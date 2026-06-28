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

/**
 * Data Access Object for Professor.
 * Provides persistence and retrieval operations on the profesor table
 * and the vw_profesor view.
 */
public class ProfessorDAO implements IDAOProfessor {
    private static final Logger LOGGER = Logger.getLogger(ProfessorDAO.class.getName());

    /**
     * Builds a Professor from the current row of the given result set.
     *
     * @param resultSet a result set positioned on a valid row
     * @return the Professor represented by the current row
     * @throws SQLException if a column cannot be read from the result set
     */
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

    /**
     * Retrieves a professor by their personal number.
     *
     * @param personalNumber the professor's personal number
     * @return the matching Professor, or null if none was found
     * @throws DataOperationException if a database error occurs
     */
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
            LOGGER.log(Level.SEVERE, "Error al obtener el profesor", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener el profesor");
        }
        return professor;
    }

    /**
     * Retrieves a professor by their user identifier.
     *
     * @param idProfessor the professor's user identifier
     * @return the matching Professor, or null if none was found
     * @throws DataOperationException if a database error occurs
     */
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
            LOGGER.log(Level.SEVERE, "Error al obtener el profesor", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener el profesor");
        }
        return professor;
    }

    /**
     * Registers a new professor, creating the underlying user record first.
     *
     * @param professor the professor to register, must not be null
     * @return true if the professor was registered successfully
     * @throws IllegalArgumentException if professor is null
     * @throws IllegalStateException if a professor with the same personal number already exists
     * @throws DataOperationException if the user could not be created or a database error occurs
     */
    @Override
    public boolean registerProfessor(Professor professor) throws DataOperationException {
        requireProfessor(professor);
        ensurePersonalNumberAvailable(professor.getPersonalNumber());
        int idUser = createUserFor(professor);
        return insertProfessorRecord(professor, idUser);
    }

    /**
     * Validates that the given professor is present.
     *
     * @param professor the professor to validate
     * @throws IllegalArgumentException if professor is null
     */
    @Override
    public void requireProfessor(Professor professor) {
        if (professor == null) {
            LOGGER.log(Level.WARNING, "El profesor es nulo");
            throw new IllegalArgumentException("El profesor es nulo");
        }
    }

    /**
     * Ensures no professor is already registered with the given personal number.
     *
     * @param personalNumber the personal number to check
     * @throws IllegalStateException if a professor with the same personal number already exists
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public void ensurePersonalNumberAvailable(int personalNumber) throws DataOperationException {
        if (getProfessorByPersonalNumber(personalNumber) != null) {
            LOGGER.log(Level.WARNING, "El profesor con el numero de personal ya existe");
            throw new IllegalStateException("El profesor con el numero de personal ya existe");
        }
    }

    /**
     * Creates the underlying user record for a professor.
     *
     * @param professor the professor whose user record is created
     * @return the generated user identifier
     * @throws DataOperationException if the user could not be created
     */
    @Override
    public int createUserFor(Professor professor) throws DataOperationException {
        UserDAO userDAO = new UserDAO();
        int idUser = userDAO.registerUser(professor);
        if (idUser == RegistrationStatus.FAILURE.getValue()) {
            LOGGER.log(Level.WARNING, "No se logro registrar el profesor");
            throw new DataOperationException("No se logro registrar el profesor");
        }
        return idUser;
    }

    /**
     * Inserts the professor-specific record linked to an existing user.
     *
     * @param professor the professor to insert
     * @param idUser the identifier of the previously created user
     * @return true if the professor record was inserted
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public boolean insertProfessorRecord(Professor professor, int idUser) throws DataOperationException {
        String query = "INSERT INTO profesor (id_usuario, numero_de_personal, es_coordinador, es_administrador, turno) VALUES (?, ?, ?, ?, ?);";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, idUser);
            preparedStatement.setInt(2, professor.getPersonalNumber());
            preparedStatement.setBoolean(3, professor.isCoordinator());
            preparedStatement.setBoolean(4, professor.isAdmin());
            preparedStatement.setString(5, professor.getShift());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error registrando el profesor", e);
            throw DAOUtils.convertSQLExceptiontoDataOperationException(e, "Error al registrar el profesor");
        }
    }

    /**
     * Retrieves all professors.
     *
     * @return a list of all professors, empty if there are none
     * @throws DataOperationException if a database error occurs
     */
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
            LOGGER.log(Level.SEVERE, "Error obteniendo todos los profesores", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener a los profesores");
        }
        return professors;
    }

    /**
     * Updates the professor-specific data (coordinator flag, admin flag and shift).
     *
     * @param professor the professor carrying the updated data, ignored if null
     * @return true if at least one row was updated
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public boolean modifyProfessor(Professor professor) throws DataOperationException {
        boolean professorUpdated = false;
        if (professor != null) {
            String query = "UPDATE profesor SET es_coordinador=?, es_administrador=?, turno=? WHERE numero_de_personal=?;";
            try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setBoolean(1, professor.isCoordinator());
                preparedStatement.setBoolean(2, professor.isAdmin());
                preparedStatement.setString(3, professor.getShift());
                preparedStatement.setInt(4, professor.getPersonalNumber());
                professorUpdated = preparedStatement.executeUpdate() > 0;
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error modificando los datos del profesor", e);
                if (DAOUtils.isConnectionError(e)) {
                    throw new DataOperationException("Error de conexión. Intente más tarde.");
                }
                throw new DataOperationException("Error al modificar los datos del profesor");
            }
        }
        return professorUpdated;
    }

    /**
     * Checks whether an active coordinator currently exists.
     *
     * @return true if there is at least one active professor flagged as coordinator
     * @throws DataOperationException if a database error occurs
     */
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
            LOGGER.log(Level.SEVERE, "Error al verificar si existe un coordinador", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al verificar si existe un coordinador");
        }
        return exists;
    }
}
