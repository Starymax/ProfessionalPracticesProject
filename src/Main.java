import mx.fei.dataaccess.DatabaseConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOG = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) {
        try (Connection connection = DatabaseConnectionManager.getConnection()) {
            LOG.log(Level.INFO,"Connected to database.");
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }
}