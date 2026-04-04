import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dao.ProfessorDAO;
import mx.fei.logic.dao.StudentDAO;
import mx.fei.logic.dto.Professor;
import mx.fei.logic.dto.Student;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOG = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) {

        try (Connection connection = DatabaseConnectionManager.getConnection()) {

        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }
}