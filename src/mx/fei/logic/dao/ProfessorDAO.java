package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.Professor;
import mx.fei.logic.dto.RegistrationStatus;
import mx.fei.logic.exceptions.DataOperationException;
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
    private static final Logger logger = Logger.getLogger(ProfessorDAO.class.getName());

    @Override
    public Professor buildProfessorFromResultSet(ResultSet resultSet) throws SQLException {
        int idUser = resultSet.getInt("id_usuario");
        int personalNumber = resultSet.getInt("numero_de_personal");
        String name = resultSet.getString("nombre");
        String lastName = resultSet.getString("apellidos");
        String mail = resultSet.getString("correo");
        String password = resultSet.getString("contrasena");
        boolean activeStatus = resultSet.getBoolean("estado_activo");
        String gender = resultSet.getString("genero");
        boolean isCoordinator = resultSet.getBoolean("es_coordinador");
        boolean isAdmin = resultSet.getBoolean("es_administrador");
        String shift = resultSet.getString("turno");
        return new Professor(idUser, name, lastName, mail, password, gender, activeStatus, personalNumber, isCoordinator, isAdmin, shift);
    }

    @Override
    public Professor getProfessorByPersonalNumber(int personalNumber) throws DataOperationException {
        Professor professor = null;
        String query = "SELECT * FROM vw_profesor WHERE numero_de_personal = ?;";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, personalNumber);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    professor = buildProfessorFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener el profesor", e);
            throw new DataOperationException("Error al obtener el profesor");
        }
        return professor;
    }

    @Override
    public Professor getProfessorById(int idProfessor) throws DataOperationException {
        Professor professor = null;
        String query = "SELECT * FROM vw_profesor WHERE id_usuario = ?;";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, idProfessor);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    professor = buildProfessorFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener el profesor", e);
            throw new DataOperationException("Error al obtener el profesor");
        }
        return professor;
    }

    @Override
    public boolean registerProfessor(Professor professor) throws DataOperationException {
        if (professor == null) {
            logger.log(Level.WARNING, "El profesor es nulo");
            throw new IllegalArgumentException("El profesor es nulo");
        }
        if (getProfessorByPersonalNumber(professor.getPersonalNumber()) != null) {
            logger.log(Level.WARNING, "El profesor con el numero de personal ya existe");
            throw new IllegalStateException("El profesor con el numero de personal ya existe");
        }
        boolean registered = false;
        try {
            UserDAO userDAO = new UserDAO();
            int idUser = userDAO.registerUser(professor);
            if (idUser == RegistrationStatus.FAILURE.getValue()) {
                logger.log(Level.WARNING, "No se logro registrar el profesor");
                throw new DataOperationException("No se logro registrar el profesor");
            }
            String query = "INSERT INTO profesor (id_usuario, numero_de_personal, es_coordinador, es_administrador, turno) VALUES (?, ?, ?, ?, ?);";
            try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, idUser);
                preparedStatement.setInt(2, professor.getPersonalNumber());
                preparedStatement.setBoolean(3, professor.isCoordinator());
                preparedStatement.setBoolean(4, professor.isAdmin());
                preparedStatement.setString(5, professor.getShift());
                registered = preparedStatement.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error registrando el profesor", e);
            throw new DataOperationException("Error al registrar el profesor");
        }
        return registered;
    }

    @Override
    public List<Professor> getProfessors() throws DataOperationException {
        List<Professor> professors = new ArrayList<>();
        String query = "SELECT * FROM vw_profesor;";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                professors.add(buildProfessorFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error obteniendo todos los profesores", e);
            throw new DataOperationException("Error al obtener a los profesores");
        }
        return professors;
    }

    @Override
    public boolean modifyProfessor(Professor professor) throws DataOperationException {
        boolean updated = false;
        if (professor != null) {
            String query = "UPDATE profesor SET es_coordinador=?, es_administrador=?, turno=? WHERE numero_de_personal=?;";
            try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setBoolean(1, professor.isCoordinator());
                preparedStatement.setBoolean(2, professor.isAdmin());
                preparedStatement.setString(3, professor.getShift());
                preparedStatement.setInt(4, professor.getPersonalNumber());
                updated = preparedStatement.executeUpdate() > 0;
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error modificando los datos del profesor", e);
                throw new DataOperationException("Error al modificar los datos del profesor");
            }
        }
        return updated;
    }

    @Override
    public boolean existsCoordinator() throws DataOperationException {
        boolean exists = false;
        String query = "SELECT 1 FROM profesor p JOIN usuario u ON p.id_usuario = u.id_usuario WHERE p.es_coordinador = true AND u.estado_activo = true LIMIT 1;";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                exists = true;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al verificar si existe un coordinador", e);
            throw new DataOperationException("Error al verificar si existe un coordinador");
        }
        return exists;
    }
}
