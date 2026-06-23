package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.User;
import mx.fei.logic.dto.UserRole;
import mx.fei.logic.exceptions.DataOperationException;
import mx.fei.logic.idao.IDAOUser;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for User.
 * Provides persistence and retrieval operations on the usuario table,
 * as well as role-based session management.
 */
public class UserDAO implements IDAOUser {
    private static final Logger logger = Logger.getLogger(UserDAO.class.getName());

    /**
     * Checks whether a user with the given identifier exists.
     *
     * @param idUser the user identifier to check
     * @return true if a matching user exists
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public boolean userExist(int idUser) throws DataOperationException {
        String query = "SELECT id_usuario FROM  usuario where id_usuario=?;";
        boolean exist;
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1,idUser);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                exist = resultSet.next();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Error al verificar si existe un usuario",e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al verificar si existe un usuario ");
        }
        return exist;
    }

    /**
     * Registers the base user record shared by all roles.
     *
     * @param user the user to register, must not be null
     * @return the generated identifier of the new user, or -1 if none was generated
     * @throws IllegalArgumentException if user is null
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public int registerUser(User user) throws DataOperationException {
        if (user == null) {
            logger.log(Level.WARNING,"El usuario es nulo");
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        int generatedID = -1;
        String query = "INSERT INTO usuario (nombre,apellidos,correo,contrasena,estado_activo,genero) VALUES (?,?,?,?,?,?);";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getPassword());
            preparedStatement.setBoolean(5, user.isActive());
            preparedStatement.setString(6, user.getGender());
            preparedStatement.executeUpdate();
            try (ResultSet keys = preparedStatement.getGeneratedKeys()) {
                if (keys.next()) {
                    generatedID = keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Error al registrar el usuario",e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al registrar el usuario");
        }
        return generatedID;
    }

    /**
     * Updates the base data of an existing user.
     *
     * @param user the user carrying the updated data, ignored if null
     * @return true if at least one row was updated
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public boolean updateUser(User user) throws DataOperationException {
        boolean updated = false;
        if (user != null) {
            String query = "UPDATE usuario SET nombre=?, apellidos=?, correo=?, contrasena=?, estado_activo=?, genero=? WHERE id_usuario=?;";
            try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query);) {
                preparedStatement.setString(1, user.getName());
                preparedStatement.setString(2, user.getLastName());
                preparedStatement.setString(3, user.getEmail());
                preparedStatement.setString(4, user.getPassword());
                preparedStatement.setBoolean(5, user.isActive());
                preparedStatement.setString(6, user.getGender());
                preparedStatement.setInt(7, user.getUserId());
                updated = preparedStatement.executeUpdate() > 0;
            } catch (SQLException e) {
                logger.log(Level.SEVERE,"Error al actualizar el usuario",e);
                if (DAOUtils.isConnectionError(e)) {
                    throw new DataOperationException("Error de conexión. Intente más tarde.");
                }
                throw new DataOperationException("Error al actualizar el usuario");
            }
        }
        return updated;
    }

    /**
     * Retrieves a user by email, returning the concrete role (student or professor).
     *
     * @param email the email to search for, must not be null or blank
     * @return the matching User, as a Student or Professor
     * @throws IllegalArgumentException if email is null or blank
     * @throws java.util.NoSuchElementException if no user exists with the given email
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public User getUserByEmail(String email) throws DataOperationException {
        requireEmail(email);
        int idUser = findUserIdByEmail(email);
        return loadUserByRole(idUser);
    }

    /**
     * Validates that the given email is present.
     *
     * @param email the email to validate
     * @throws IllegalArgumentException if email is null or blank
     */
    @Override
    public void requireEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El correo no puede estar vacio");
        }
    }

    /**
     * Looks up the identifier of the user registered with the given email.
     *
     * @param email the email to search for
     * @return the matching user identifier
     * @throws NoSuchElementException if no user exists with the given email
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public int findUserIdByEmail(String email) throws DataOperationException {
        String query = "SELECT id_usuario FROM usuario WHERE correo = ?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id_usuario");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener el usuario", e);
            throw DAOUtils.convertSQLExceptiontoDataOperationException(e, "Error al buscar el usuario");
        }
        logger.log(Level.WARNING, "Error al obtener el usuario");
        throw new NoSuchElementException("Error al obtener el usuario");
    }

    /**
     * Loads the concrete user (student or professor) for the given identifier.
     *
     * @param idUser the user identifier
     * @return the matching User, as a Student or Professor
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public User loadUserByRole(int idUser) throws DataOperationException {
        return isStudent(idUser) ? new StudentDAO().getStudentById(idUser) : new ProfessorDAO().getProfessorById(idUser);
    }

    /**
     * Determines whether the given user is a student.
     *
     * @param idUser the user identifier to check
     * @return true if the user has a student record
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public boolean isStudent(int idUser) throws DataOperationException {
        String query = "SELECT COUNT(*) FROM alumno WHERE id_usuario = ?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, idUser);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener los datos del usuario");
        }
    }

    /**
     * Opens the database connection using the credentials associated with the given role.
     *
     * @param role the role whose connection properties should be used
     * @throws DataOperationException if the session could not be started
     */
    @Override
    public void logInByRole(UserRole role) throws DataOperationException {
        try {
            DatabaseConnectionManager.getInstance(role.getPropertiesKey());
        } catch (IOException e) {
            throw new DataOperationException("Error al iniciar sesión");
        }
    }

    /**
     * Closes the current database connection, ending the user session.
     */
    @Override
    public void logout() {
        DatabaseConnectionManager.getInstance().closeConnection();
    }
}