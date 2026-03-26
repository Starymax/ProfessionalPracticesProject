package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.Student;
import mx.fei.logic.dto.User;
import mx.fei.logic.idao.IDAOUser;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO implements IDAOUser {
    private Logger logger = Logger.getLogger(UserDAO.class.getName());
    @Override
    public boolean userExist(int idUser) {
        try {
            DatabaseConnectionManager connection = DatabaseConnectionManager.buildConnection();
            String queryUserExist = "SELECT id_usuario FROM  usuario where id_usuario=?;";
            PreparedStatement preparedStatement = connection.preparedStatement(queryUserExist);
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
    public int registerUser(Student student) {
        int generatedID = -1;
        try {
            DatabaseConnectionManager connection = DatabaseConnectionManager.buildConnection();
            String query = "INSERT INTO usuario (nombre,apellidos,correo,contrasena,estado_activo,genero) VALUES (?,?,?,?,?,?);";
            PreparedStatement preparedStatement = connection.preparedStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, student.getName());
            preparedStatement.setString(2, student.getLastName());
            preparedStatement.setString(3, student.getEmail());
            preparedStatement.setString(4, student.getPassword());
            preparedStatement.setBoolean(5, student.isActive());
            preparedStatement.setString(6, student.getGender());
            preparedStatement.executeUpdate();
            ResultSet keys = preparedStatement.getGeneratedKeys();
            if (keys.next()) {
                generatedID = keys.getInt(1);
            }
            connection.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE,e.getMessage());
        }
        return  generatedID;
    }

    @Override
    public boolean updateUser(Student student) {
        boolean updated = false;
        if (student == null) {
            return false;
        }
        try {
            DatabaseConnectionManager connection = DatabaseConnectionManager.buildConnection();
            String queryModifyStudent = "UPDATE usuario SET nombre=?, apellidos=?, correo=?, contrasena=?, estado_activo=?, genero=? WHERE id_usuario=?;";
            PreparedStatement preparedStatement = connection.preparedStatement(queryModifyStudent);
            preparedStatement.setString(1, student.getName());
            preparedStatement.setString(2, student.getLastName());
            preparedStatement.setString(3, student.getEmail());
            preparedStatement.setString(4, student.getPassword());
            preparedStatement.setBoolean(5, student.isActive());
            preparedStatement.setString(6, student.getGender());
            preparedStatement.setInt(7, student.getUserId());
            updated = preparedStatement.executeUpdate()>0;
            connection.close();
        } catch (SQLException e) {
            logger.log(Level.SEVERE,e.getMessage());
        }
        return updated;
    }
}
