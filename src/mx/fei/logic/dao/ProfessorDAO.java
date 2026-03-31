package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.Professor;
import mx.fei.logic.idao.IProfessorDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProfessorDAO implements IProfessorDAO {
    private final Logger logger = Logger.getLogger(ProfessorDAO.class.getName());

    @Override
    public Professor getProfessorByPersonalNumber(int personalNumber) {
        Professor professor = null;
        String queryProfessor = "SELECT * FROM vw_profesor WHERE numero_de_personal = ?;";
        try (Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(queryProfessor);) {
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
            PreparedStatement preparedStatement = connection.prepareStatement(queryRegisterProfessor);) {
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
    public List<Professor> consultProfessors() {
        List<Professor> professors = new ArrayList<>();
        String queryRegisterProfessor = "SELECT numero_de_personal FROM profesor;";
        try (Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(queryRegisterProfessor);) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                professors.add(getProfessorByPersonalNumber(resultSet.getInt("numero_de_personal")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return professors;
    }
}
