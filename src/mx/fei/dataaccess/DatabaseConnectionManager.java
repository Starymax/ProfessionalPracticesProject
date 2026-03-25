package mx.fei.dataaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnectionManager {
    private static final Logger logger = Logger.getLogger(DatabaseConnectionManager.class.getName());
    private  static DatabaseConnectionManager connSingleton;
    private Connection connection;
    private String driver;
    private String url;
    private String host = "localhost";
    private String port = "3306";
    private String dataBase = "practicas_profesionales";
    private String username = "administradorGeneral";
    private String password = "Admin12345";

    private DatabaseConnectionManager() throws SQLException {
        driver = "com.mysql.cj.jdbc.Driver";
        url = "jdbc:mysql://" + host + ":" + port + "/" + dataBase + "?useTimezone=true&serverTimezone=UTC";
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE,"No se encontro el driver JDBC" + driver,e);
        }
        connect();
    }

    private void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url,username,password);
            logger.log(Level.INFO,"Conexion establecida con la base de datos");
        }
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
            logger.log(Level.INFO,"Conexion cerrada");
        }
    }

    public PreparedStatement preparedStatement(String query) throws SQLException {
        connect();
        return connection.prepareStatement(query);
    }

    public static DatabaseConnectionManager buildConnection() throws SQLException {
        if (connSingleton == null) {
            connSingleton = new DatabaseConnectionManager();
        }
        connSingleton.connect();
        return connSingleton;
    }
}