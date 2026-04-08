package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.Professor;
import mx.fei.logic.idao.IDAOProfessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProfessorDAO implements IDAOProfessor {
    private final Logger logger = Logger.getLogger(ProfessorDAO.class.getName());
    @Override
    public Professor getProfessorByPersonalNumber(int personalNumber) {
        Professor professor = null;
        String queryProfessor = "SELECT * FROM vw_profesor WHERE numero_de_personal = ?;";
        try (Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(queryProfessor)) {
            preparedStatement.setInt(1, personalNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int idUser = resultSet.getInt("id_usuario");
                String name = resultSet.getString("nombre");
                String lastName = resultSet.getString("apellidos");
                String mail = resultSet.getString("correo");
                String password = resultSet.getString("contrasena");
                boolean activeStatus = resultSet.getBoolean("estado_activo");
                String gender = resultSet.getString("genero");
                boolean isCoordinator = resultSet.getBoolean("es_coordinador");
                boolean isAdmin = resultSet.getBoolean("es_administrador");
                String shift = resultSet.getString("turno");
                professor = new Professor(idUser, name, lastName, mail, password, gender, activeStatus, personalNumber, isCoordinator, isAdmin, shift);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener el profesor: ", e);
        }
        return professor;
    }

    @Override
    public boolean registerProfessor(Professor professor) {
        if (professor == null) {
            return false;
        }
        if (this.getProfessorByPersonalNumber(professor.getPersonalNumber()) != null) {
            logger.log(Level.WARNING, "El profesor con el número de personal ingresado ya existe");
            return false;
        }
        try {
            UserDAO userDAO = new UserDAO();
            int idUser = userDAO.registerUser(professor);
            if (idUser == -1) {
                logger.log(Level.SEVERE, "No se logro registrar el usuario en la base");
                return false;
            }
            String queryRegisterProfessor = "INSERT INTO profesor (id_usuario, numero_de_personal, es_coordinador, es_administrador, turno) " + "VALUES (?, ?, ?, ?, ?);";
            try (Connection connection = DatabaseConnectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(queryRegisterProfessor)) {
                preparedStatement.setInt(1, idUser);
                preparedStatement.setInt(2, professor.getPersonalNumber());
                preparedStatement.setBoolean(3, professor.isCoordinator());
                preparedStatement.setBoolean(4, professor.isAdmin());
                preparedStatement.setString(5, professor.getShift());
                preparedStatement.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return false;
    }

    @Override
    public List<Professor> getProfessors() {
        List<Professor> professors = new ArrayList<>();
        String queryRegisterProfessor = "SELECT numero_de_personal FROM profesor;";
        try (Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(queryRegisterProfessor)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Integer> personalNumbers = new ArrayList<>();
            while (resultSet.next()) {
                personalNumbers.add(resultSet.getInt("numero_de_personal"));
            }
            resultSet.close();
            for (Integer personalNumer : personalNumbers) {
                professors.add(getProfessorByPersonalNumber(personalNumer));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return professors;
    }

    @Override
    public boolean modifyProfessor(Professor professor) {
        boolean updated = false;
        String queryModifyProfessor = "UPDATE profesor set es_coordinador=?, es_administrador=?, turno=? WHERE numero_de_personal=?;";
        if (professor == null) {
            updated = false;
        }
        try (Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(queryModifyProfessor)) {
            preparedStatement.setBoolean(1,professor.isCoordinator());
            preparedStatement.setBoolean(2,professor.isAdmin());
            preparedStatement.setString(3,professor.getShift());
            updated = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return updated;
    }

}
