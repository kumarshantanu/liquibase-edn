package liquibase.ext.edn.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.Test;

import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;

public class EdnParserTest {

    private static final String changeLogFile = "example.edn";

    private static Connection getH2Connection() {
        try {
            Class.forName("org.h2.Driver");
            return DriverManager.getConnection("jdbc:h2:mem:test");
        } catch (Exception e) {
            throw new RuntimeException("Cannot create H2 connection", e);
        }
    }

    private static void closeConnection(Connection conn) {
        try {
            conn.close();
        } catch (SQLException e) {
            System.err.println("Error closing connection");
            e.printStackTrace();
        }
    }

    @Test
    public void testEdnParser() throws LiquibaseException {
        ResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor();
        Connection connection = getH2Connection();
        try {
            DatabaseConnection databaseConnection = new JdbcConnection(connection);
            Liquibase lb = new Liquibase(changeLogFile, resourceAccessor, databaseConnection);
            lb.update("");
        } finally {
            closeConnection(connection);
        }
    }

}
