import mx.fei.dataaccess.DatabaseConnectionManager;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOG = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) {
        DatabaseConnectionManager dbManager;
        try {
            dbManager = DatabaseConnectionManager.buildConnection();
            dbManager.close();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error al conectar con la base de datos", e);
        }
    }
}