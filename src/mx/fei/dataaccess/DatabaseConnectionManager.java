package mx.fei.dataaccess;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

public class DatabaseConnectionManager {
    private static final Logger logger = Logger.getLogger(DatabaseConnectionManager.class.getName());
    private static DatabaseConnectionManager dbManager;
    private static Connection connection;
    private static String url;
    private static String username;
    private static String password;

    private DatabaseConnectionManager() {}

    public static void loadProperties(String role) throws IOException {
        String fileName = "db_" + role + ".properties";
        try (InputStream input = DatabaseConnectionManager.class.getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                throw new IOException("No se encontró el archivo: " + fileName);
            }
            Properties properties = new Properties();
            properties.load(input);
            username = properties.getProperty("db.username");
            password = properties.getProperty("db.password");
            String host = properties.getProperty("db.host");
            String port = properties.getProperty("db.port");
            String name = properties.getProperty("db.name");
            url = "jdbc:mysql://" + host + ":" + port + "/" + name;
        }
    }

    public static Connection getConnection() throws SQLException {
        if (url == null) {
            throw new SQLException("Sistema no disponible, intentelo de nuevo");
        }
        return DriverManager.getConnection(url, username, password);
    }
}