package mx.fei.dataaccess;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnectionManager {
    private static final Logger logger = Logger.getLogger(DatabaseConnectionManager.class.getName());
    private  static DatabaseConnectionManager dbManager;
    private Connection connection;
    private String url;
    private String username;
    private String password;

    private DatabaseConnectionManager() throws SQLException {
        loadProperties();
    }

    private void loadProperties() throws SQLException {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new SQLException("Archivo db.properties no encontrado");
            }
            properties.load(input);
            username = properties.getProperty("db.username");
            password = properties.getProperty("db.password");
            String host = properties.getProperty("db.host");
            String port = properties.getProperty("db.port");
            String dataBase = properties.getProperty("db.name");
            url = "jdbc:mysql://" + host + ":" + port + "/" + dataBase + "?useTimezone=true&serverTimezone=UTC";
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al leer db.properties", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dbManager == null) {
            dbManager = new DatabaseConnectionManager();
        }
        return dbManager.connect();
    }

    private Connection connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url, username, password);
            logger.info("Conexion establecida");
        }
        return connection;
    }
}