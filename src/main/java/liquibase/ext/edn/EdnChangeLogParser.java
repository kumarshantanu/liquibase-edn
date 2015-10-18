package liquibase.ext.edn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import us.bpsm.edn.Keyword;
import us.bpsm.edn.Symbol;
import us.bpsm.edn.parser.Parseable;
import us.bpsm.edn.parser.Parser;
import us.bpsm.edn.parser.Parsers;

public class EdnChangeLogParser extends AbstractMapChangeLogParser {

    @Override
    public String getSupportedFileExtension() {
        return "edn";
    }

    @Override
    public Map<String, ?> parseAsMap(InputStream changeLogStream) throws IOException {
        String clob = slurp(changeLogStream);
        Parseable pbr = Parsers.newParseable(clob);
        Parser p = Parsers.newParser(Parsers.defaultConfiguration());
        Map<?, ?> m = (Map<?, ?>) p.nextValue(pbr);
        return mapKeywordToCamelString(m);
    }

    // ----- utility methods -----

    public static String slurp(InputStream in) throws IOException {
        InputStreamReader is = new InputStreamReader(in);
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(is);
        String read = br.readLine();

        while (read != null) {
            sb.append(read);
            sb.append('\n');
            read = br.readLine();
        }

        return sb.toString();
    }

    public static Object normalizeValue(Object v) {
        Object value = null;
        if (v instanceof List<?>) {
            value = mapKeywordToCamelString((List<?>) v);
        } else if (v instanceof Map<?, ?>) {
            value = mapKeywordToCamelString((Map<?, ?>) v);
        } else if (v instanceof Keyword) {
            value = toCamelCaseString((Keyword) v);
        } else if (v instanceof Symbol) {
            value = toCamelCaseString((Symbol) v);
        } else {
            value = v;
        }
        return value;
    }

    public static String toCamelCase(String name) {
        StringBuilder sb = new StringBuilder(name.length());
        boolean upper = false;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c == '-') {
                upper = true;
            } else {
                sb.append(upper? Character.toUpperCase(c): c);
                upper = false;
            }
        }
        return sb.toString();
    }

    public static String toCamelCaseString(Keyword k) {
        return toCamelCase(k.getName());
    }

    public static String toCamelCaseString(Symbol s) {
        return toCamelCase(s.getName());
    }

    public static List<?> mapKeywordToCamelString(List<?> data) {
        List<Object> result = new ArrayList<Object>();
        for (Object each: data) {
            result.add(normalizeValue(each));
        }
        return result;
    }

    public static Map<String, ?> mapKeywordToCamelString(Map<?, ?> data) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        for (Map.Entry<?, ?> pair: data.entrySet()) {
            Object k = pair.getKey();
            Object value = normalizeValue(pair.getValue());
            if (k instanceof Keyword) {
                result.put(toCamelCaseString((Keyword) k), value);
            } else if (k instanceof String) {
                result.put((String) k, value);
            } else if (k instanceof Symbol) {
                result.put(toCamelCaseString((Symbol) k), value);
            } else {
                throw new IllegalArgumentException(
                        "Expected map-key to be keyword, string or symbol, but found: " + String.valueOf(k));
            }
        }
        return result;
    }

}
