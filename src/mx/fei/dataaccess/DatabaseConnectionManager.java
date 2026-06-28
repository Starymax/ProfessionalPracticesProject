package mx.fei.dataaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import java.io.InputStream;

public class DatabaseConnectionManager {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnectionManager.class.getName());

    private static volatile DatabaseConnectionManager instance;
    private Connection connection;
    private String url;
    private String username;
    private String password;
    private String currentRole;
    private final int TIME_OUT = 2;

    private DatabaseConnectionManager() {}

    public static synchronized DatabaseConnectionManager getInstance(String role) throws IOException {
        if (instance != null && !role.equals(instance.currentRole)) {
            instance.closeConnection();
        }
        if (instance == null) {
            DatabaseConnectionManager newInstance = new DatabaseConnectionManager();
            try {
                newInstance.loadProperties(role);
            } catch (IOException e) {
                instance = null;
                throw e;
            }
            newInstance.currentRole = role;
            instance = newInstance;
        }
        return instance;
    }

    public static DatabaseConnectionManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("La conexión no ha sido inicializado. Intente iniciar sesión nuevamente");
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (url == null) {
            throw new SQLException("Sistema no disponible, inténtelo de nuevo");
        }
        if (connection == null || connection.isClosed() || !connection.isValid(TIME_OUT)) {
            LOGGER.log(Level.INFO, "Estableciendo nueva conexión a la base de datos");
            try {
                connection = DriverManager.getConnection(url, username, password);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "No se pudo conectar a la base de datos: " + url, e);
                throw e;
            }
        }

        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                LOGGER.log(Level.INFO, "Conexión cerrada correctamente");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error al cerrar la conexión: " + e.getMessage());
        } finally {
            connection = null;
            url = null;
            username = null;
            password = null;
            currentRole = null;
            instance = null;
        }
    }

    private void loadProperties(String role) throws IOException {
        String fileName = "db_" + role + ".properties";
        InputStream input = DatabaseConnectionManager.class.getClassLoader().getResourceAsStream(fileName);
        if (input == null) {
            LOGGER.log(Level.SEVERE, "No se encontró el archivo de propiedades: " + fileName);
            throw new IOException("Error al iniciar sesión: archivo de configuracion no encontrado");
        }
        try (input) {
            Properties properties = new Properties();
            properties.load(input);
            this.username = properties.getProperty("db.username");
            this.password = properties.getProperty("db.password");
            String host = properties.getProperty("db.host");
            String port = properties.getProperty("db.port");
            String name = properties.getProperty("db.name");
            if (isBlank(username) || isBlank(password) || isBlank(host) || isBlank(port) || isBlank(name)) {
                LOGGER.log(Level.SEVERE, "Archivo de propiedades incompleto o mal formado: " + fileName);
                throw new IOException("Error al iniciar sesión: configuración de conexión inválida");
            }
            this.url = "jdbc:mysql://" + host + ":" + port + "/" + name;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}