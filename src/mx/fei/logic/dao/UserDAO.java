package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.User;
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
    public boolean userExist(int idUser) {
        String queryUserExist = "SELECT id_usuario FROM  usuario where id_usuario=?;";
        try (Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(queryUserExist);) {
            preparedStatement.setInt(1,idUser);
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean exist = resultSet.next();
            connection.close();
            return exist;
        } catch (SQLException e) {
            logger.log(Level.SEVERE,e.getMessage());
        }
        return false;
    }

    @Override
    public int registerUser(User user) {
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
            logger.log(Level.SEVERE,e.getMessage());
        }
        return  generatedID;
    }

    @Override
    public boolean updateUser(User user) {
        boolean updated = false;
        if (user == null) {
            return false;
        }
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
            updated = preparedStatement.executeUpdate()>0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE,e.getMessage());
        }
        return updated;
    }
}
