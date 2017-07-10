package liquibase.ext.edn;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import liquibase.ContextExpression;
import liquibase.Labels;
import liquibase.changelog.ChangeLogParameters;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.exception.ChangeLogParseException;
import liquibase.parser.ChangeLogParser;
import liquibase.parser.core.ParsedNode;
import liquibase.parser.core.yaml.YamlChangeLogParser;
import liquibase.resource.ResourceAccessor;
import liquibase.util.StreamUtil;

public abstract class AbstractMapChangeLogParser extends YamlChangeLogParser implements ChangeLogParser {

    public abstract Map<String, ?> parseAsMap(InputStream changeLogStream) throws IOException;

	@Override
	public int getPriority() {
		return PRIORITY_DEFAULT;
	}

	@Override
	public DatabaseChangeLog parse(String physicalChangeLogLocation, ChangeLogParameters changeLogParameters,
			ResourceAccessor resourceAccessor) throws ChangeLogParseException {
		try {
            InputStream changeLogStream = StreamUtil.singleInputStream(physicalChangeLogLocation, resourceAccessor);
            if (changeLogStream == null) {
                throw new ChangeLogParseException(physicalChangeLogLocation + " does not exist");
            }

            Map<String, ?> map = null;
            try {
                map = parseAsMap(changeLogStream);
            } catch (Exception e) {
                throw new ChangeLogParseException("Syntax error in " + getSupportedFileExtensions()[0] + ": " + e.getMessage(), e);
            }

            if (map == null || map.size() == 0) {
                throw new ChangeLogParseException("Empty file " + physicalChangeLogLocation);
            }

            DatabaseChangeLog changeLog = new DatabaseChangeLog(physicalChangeLogLocation);
            return parse(map, changeLog, changeLogParameters, resourceAccessor);
		} catch (Throwable e) {
            if (e instanceof ChangeLogParseException) {
                throw (ChangeLogParseException) e;
            }
            throw new ChangeLogParseException("Error parsing "+physicalChangeLogLocation, e);
		}
	}

	public DatabaseChangeLog parse(Map<String, ?> changeLogAsMap, DatabaseChangeLog changeLog,
			ChangeLogParameters changeLogParameters, ResourceAccessor resourceAccessor) throws Exception {
        Object rootList = changeLogAsMap.get("databaseChangeLog");
        if (rootList == null) {
            throw new ChangeLogParseException("Could not find databaseChangeLog node");
        }

        if (!(rootList instanceof List)) {
            throw new ChangeLogParseException("databaseChangeLog does not contain a list of entries. Each changeSet must begin ' - changeSet:'");
        }

        for (Object obj : (List) rootList) {
            if (obj instanceof Map && ((Map) obj).containsKey("property")) {
                Map property = (Map) ((Map) obj).get("property");
                ContextExpression context = new ContextExpression((String) property.get("context"));
                Labels labels = new Labels((String) property.get("labels"));

                Boolean global = getGlobalParam(property);

                if (property.containsKey("name")) {
                    Object value = property.get("value");
                    if (value != null) {
                        value = value.toString(); // TODO: not nice...
                    }

                    changeLogParameters.set((String) property.get("name"), (String) value, context, labels, (String) property.get("dbms"), global, changeLog);
                } else if (property.containsKey("file")) {
                    Properties props = new Properties();
                    InputStream propertiesStream = StreamUtil.singleInputStream((String) property.get("file"), resourceAccessor);
                    if (propertiesStream == null) {
                        log.info("Could not open properties file " + property.get("file"));
                    } else {
                        props.load(propertiesStream);

                        for (Map.Entry entry : props.entrySet()) {
                            changeLogParameters.set(entry.getKey().toString(), entry.getValue().toString(), context, labels, (String) property.get("dbms"), global, changeLog);
                        }
                    }
                }
            }
        }


        replaceParameters(changeLogAsMap, changeLogParameters, changeLog);

        changeLog.setChangeLogParameters(changeLogParameters);
        ParsedNode databaseChangeLogNode = new ParsedNode(null, "databaseChangeLog");
        databaseChangeLogNode.setValue(rootList);

        changeLog.load(databaseChangeLogNode, resourceAccessor);

        return changeLog;
	}

	/**
	 * Extract the global parameter from the properties.
	 * 
	 * @param property the map of props
	 * @return the global param
	 */
	private Boolean getGlobalParam(Map property) {
		Boolean global = null;
		Object globalObj = property.get("global");
		if (globalObj == null) {
			// default behaviour before liquibase 3.4
			global = true;
		} else {
			global = (Boolean) globalObj;
		}
		return global;
	}


	@Override
	public boolean supports(String changeLogFile, ResourceAccessor resourceAccessor) {
        for (String extension : getSupportedFileExtensions()) {
            if (changeLogFile.toLowerCase().endsWith("." + extension)) {
                return true;
            }
        }
        return false;
	}

}
