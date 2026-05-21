package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.StudentAdvance;
import mx.fei.logic.dto.Student;
import mx.fei.logic.dto.WeeklyLog;
import mx.fei.logic.exceptions.DataOperationException;
import mx.fei.logic.idao.IDAOStudentAdvance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentAdvanceDAO implements IDAOStudentAdvance {
    private static final Logger logger = Logger.getLogger(StudentAdvanceDAO.class.getName());

    @Override
    public boolean createAdvance(StudentAdvance advance) throws DataOperationException {
        if (advance == null) {
            logger.log(Level.WARNING, "El avance del alumno es nulo");
            throw new IllegalArgumentException("El avance del alumno no puede ser nulo");
        }
        String query = "INSERT INTO avance_alumno (horas_realizadas, id_registro, id_alumno) VALUES (?,?,?) " +
                "ON DUPLICATE KEY UPDATE horas_realizadas = VALUES(horas_realizadas)";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setFloat(1, advance.getRealizedHours());
            preparedStatement.setInt(2, advance.getWeeklyLog().getWeeklyLogId());
            preparedStatement.setInt(3, advance.getStudent().getUserId());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al insertar o actualizar el avance del alumno", e);
            throw new DataOperationException("Error al insertar o actualizar el avance del alumno");
        }
    }

    @Override
    public StudentAdvance getAdvanceById(int advanceId) throws DataOperationException {
        String query = "SELECT * FROM avance_alumno WHERE id_avance = ?";
        StudentAdvance advance = null;
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, advanceId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                float realizedHours = resultSet.getFloat("horas_realizadas");
                int weeklyLogId = resultSet.getInt("id_registro");
                int studentId = resultSet.getInt("id_alumno");
                ActivityDAO activityDAO = new ActivityDAO();
                WeeklyLog weeklyLog = activityDAO.getWeeklyLogById(weeklyLogId);
                StudentDAO studentDAO = new StudentDAO();
                Student student = studentDAO.getStudentById(studentId);
                advance = new StudentAdvance(advanceId, realizedHours, weeklyLog, student);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener el avance del alumno", e);
            throw new DataOperationException("Error al obtener el avance del alumno");
        }
        return advance;
    }

    @Override
    public List<StudentAdvance> getAdvancesByStudentId(int studentId) throws DataOperationException {
        String query = "SELECT id_avance FROM avance_alumno WHERE id_alumno = ?";
        List<StudentAdvance> advances = new ArrayList<>();
        List<Integer> advanceIds = new ArrayList<>();
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, studentId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                advanceIds.add(resultSet.getInt("id_avance"));
            }
            resultSet.close();
            for (int id : advanceIds) {
                advances.add(getAdvanceById(id));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener los avances del alumno", e);
            throw new DataOperationException("Error al obtener los avances del alumno");
        }
        return advances;
    }

    @Override
    public List<StudentAdvance> getAdvancesByWeeklyLogId(int weeklyLogId) throws DataOperationException {
        String query = "SELECT id_avance FROM avance_alumno WHERE id_registro = ?";
        List<StudentAdvance> advances = new ArrayList<>();
        List<Integer> advanceIds = new ArrayList<>();
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, weeklyLogId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                advanceIds.add(resultSet.getInt("id_avance"));
            }
            resultSet.close();
            for (int id : advanceIds) {
                advances.add(getAdvanceById(id));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener los avances por registro semanal", e);
            throw new DataOperationException("Error al obtener los avances por registro semanal");
        }
        return advances;
    }

    @Override
    public boolean updateRealizedHours(int advanceId, float realizedHours) throws DataOperationException {
        boolean updated = false;
        String query = "UPDATE avance_alumno SET horas_realizadas = ? WHERE id_avance = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setFloat(1, realizedHours);
            preparedStatement.setInt(2, advanceId);
            updated = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al actualizar las horas realizadas del avance", e);
            throw new DataOperationException("Error al actualizar las horas realizadas del avance");
        }
        return updated;
    }
}
