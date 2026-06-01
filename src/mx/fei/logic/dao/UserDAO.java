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

public class UserDAO implements IDAOUser {
    private static final Logger logger = Logger.getLogger(UserDAO.class.getName());
    @Override
    public boolean userExist(int idUser) throws DataOperationException {
        String query = "SELECT id_usuario FROM  usuario where id_usuario=?;";
        boolean exist;
        try (Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1,idUser);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                exist = resultSet.next();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Error al verificar si existe un usuario",e);
            throw new DataOperationException("Error al verificar si existe un usuario ");
        }
        return exist;
    }

    @Override
    public int registerUser(User user) throws DataOperationException {
        if (user == null) {
            logger.log(Level.WARNING,"El usuario es nulo");
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        int generatedID = -1;
        String query = "INSERT INTO usuario (nombre,apellidos,correo,contrasena,estado_activo,genero) VALUES (?,?,?,?,?,?);";
        try (Connection connection = DatabaseConnectionManager.getConnection();
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
            throw new DataOperationException("Error al registrar el usuario");
        }
        return generatedID;
    }

    @Override
    public boolean updateUser(User user) throws DataOperationException {
        boolean updated = false;
        if (user != null) {
            String query = "UPDATE usuario SET nombre=?, apellidos=?, correo=?, contrasena=?, estado_activo=?, genero=? WHERE id_usuario=?;";
            try (Connection connection = DatabaseConnectionManager.getConnection();
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
                throw new DataOperationException("Error al actualizar el usuario");
            }
        }
        return updated;
    }

    @Override
    public User getUserByEmail(String email) throws DataOperationException {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El correo no puede estar vacio");
        }
        StudentDAO studentDAO = new StudentDAO();
        ProfessorDAO professorDAO = new ProfessorDAO();
        String query = "SELECT id_usuario FROM usuario WHERE correo = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int idUser = resultSet.getInt("id_usuario");
                    if (isStudent(idUser)) {
                        return studentDAO.getStudentById(idUser);
                    } else {
                        return professorDAO.getProfessorById(idUser);
                    }
                }
            }
            logger.log(Level.WARNING, "Error al obtener el usuario");
            throw new NoSuchElementException("Error al obtener el usuario");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener el usuario");
            throw new DataOperationException("Error al buscar el usuario");
        }
    }

    @Override
    public boolean isStudent(int idUser) throws DataOperationException {
        String query = "SELECT COUNT(*) FROM alumno WHERE id_usuario = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, idUser);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DataOperationException("Error al obtener los datos del usuario");
        }
    }

    @Override
    public void logInByRole(UserRole role) throws DataOperationException {
        try {
            DatabaseConnectionManager.loadProperties(role.getPropertiesKey());
        } catch (IOException e) {
            throw new DataOperationException("Error al iniciar sesión");
        }
    }
}