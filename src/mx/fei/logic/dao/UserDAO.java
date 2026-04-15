package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.User;
import mx.fei.logic.exceptions.DataBaseConnectionException;
import mx.fei.logic.idao.IDAOUser;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO implements IDAOUser {
    private Logger logger = Logger.getLogger(UserDAO.class.getName());
    @Override
    public boolean userExist(int idUser) throws DataBaseConnectionException {
        String queryUserExist = "SELECT id_usuario FROM  usuario where id_usuario=?;";
        boolean exist;
        try (Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(queryUserExist)) {
            preparedStatement.setInt(1,idUser);
            ResultSet resultSet = preparedStatement.executeQuery();
            exist = resultSet.next();
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Error al verificar si existe un usuario en la base de datos");
            throw new DataBaseConnectionException("Error al verificar si existe un usuario en la base de datos");
        }
        return exist;
    }

    @Override
    public int registerUser(User user) throws DataBaseConnectionException {
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
            ResultSet keys = preparedStatement.getGeneratedKeys();
            if (keys.next()) {
                generatedID = keys.getInt(1);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Error al registrar un usuario en la base de datos");
            throw new DataBaseConnectionException("Error al registrar un usuario en la base de datos");
        }
        return generatedID;
    }

    @Override
    public boolean updateUser(User user) throws DataBaseConnectionException {
        boolean updated = false;
        if (user != null) {
            String queryModifyStudent = "UPDATE usuario SET nombre=?, apellidos=?, correo=?, contrasena=?, estado_activo=?, genero=? WHERE id_usuario=?;";
            try (Connection connection = DatabaseConnectionManager.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(queryModifyStudent);) {
                preparedStatement.setString(1, user.getName());
                preparedStatement.setString(2, user.getLastName());
                preparedStatement.setString(3, user.getEmail());
                preparedStatement.setString(4, user.getPassword());
                preparedStatement.setBoolean(5, user.isActive());
                preparedStatement.setString(6, user.getGender());
                preparedStatement.setInt(7, user.getUserId());
                updated = preparedStatement.executeUpdate() > 0;
            } catch (SQLException e) {
                logger.log(Level.SEVERE,"Error al actualizar un usuario de la base de datos");
                throw new DataBaseConnectionException("Error al actualizar un usuario de la base de datos");
            }
        }
        return updated;
    }
}
