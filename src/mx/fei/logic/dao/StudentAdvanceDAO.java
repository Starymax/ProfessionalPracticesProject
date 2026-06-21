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
        String query = "INSERT INTO avance_alumno (horas_realizadas, id_registro, id_alumno) VALUES (?,?,?) ON DUPLICATE KEY UPDATE horas_realizadas = VALUES(horas_realizadas)";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setFloat(1, advance.getRealizedHours());
            preparedStatement.setInt(2, advance.getWeeklyLog().getWeeklyLogId());
            preparedStatement.setInt(3, advance.getStudent().getUserId());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al insertar o actualizar el avance del alumno", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al insertar o actualizar el avance del alumno");
        }
    }

    @Override
    public StudentAdvance getAdvanceById(int advanceId) throws DataOperationException {
        String query = "SELECT * FROM avance_alumno WHERE id_avance = ?";
        StudentAdvance advance = null;
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, advanceId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
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
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener el avance del alumno", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener el avance del alumno");
        }
        return advance;
    }

    @Override
    public List<StudentAdvance> getAdvancesByStudentId(int studentId) throws DataOperationException {
        String query = "SELECT id_avance, horas_realizadas, id_registro, id_alumno FROM avance_alumno WHERE id_alumno = ?";
        List<StudentAdvance> advances = new ArrayList<>();
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, studentId);
            List<int[]> rows = new ArrayList<>();
            List<Float> hours = new ArrayList<>();
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    rows.add(new int[]{resultSet.getInt("id_avance"), resultSet.getInt("id_registro"), resultSet.getInt("id_alumno")});
                    hours.add(resultSet.getFloat("horas_realizadas"));
                }
            }
            ActivityDAO activityDAO = new ActivityDAO();
            StudentDAO studentDAO = new StudentDAO();
            for (int i = 0; i < rows.size(); i++) {
                int[] row = rows.get(i);
                WeeklyLog weeklyLog = activityDAO.getWeeklyLogById(row[1]);
                Student student = studentDAO.getStudentById(row[2]);
                advances.add(new StudentAdvance(row[0], hours.get(i), weeklyLog, student));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener los avances del alumno", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener los avances del alumno");
        }
        return advances;
    }

    @Override
    public List<StudentAdvance> getAdvancesByWeeklyLogId(int weeklyLogId) throws DataOperationException {
        String query = "SELECT id_avance, horas_realizadas, id_registro, id_alumno FROM avance_alumno WHERE id_registro = ?";
        List<StudentAdvance> advances = new ArrayList<>();
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, weeklyLogId);
            List<int[]> rows = new ArrayList<>();
            List<Float> hours = new ArrayList<>();
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    rows.add(new int[]{resultSet.getInt("id_avance"), resultSet.getInt("id_registro"), resultSet.getInt("id_alumno")});
                    hours.add(resultSet.getFloat("horas_realizadas"));
                }
            }
            ActivityDAO activityDAO = new ActivityDAO();
            StudentDAO studentDAO = new StudentDAO();
            for (int i = 0; i < rows.size(); i++) {
                int[] row = rows.get(i);
                WeeklyLog weeklyLog = activityDAO.getWeeklyLogById(row[1]);
                Student student = studentDAO.getStudentById(row[2]);
                advances.add(new StudentAdvance(row[0], hours.get(i), weeklyLog, student));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener los avances por registro semanal", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener los avances por registro semanal");
        }
        return advances;
    }

    @Override
    public boolean updateRealizedHours(int advanceId, float realizedHours) throws DataOperationException {
        boolean updated = false;
        String query = "UPDATE avance_alumno SET horas_realizadas = ? WHERE id_avance = ?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setFloat(1, realizedHours);
            preparedStatement.setInt(2, advanceId);
            updated = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al actualizar las horas realizadas del avance", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al actualizar las horas realizadas del avance");
        }
        return updated;
    }

    @Override
    public List<StudentAdvance> getAdvancesByStudentAndWeeklyLog(int studentId, int weeklyLogId) throws DataOperationException {
        List<StudentAdvance> advances = new ArrayList<>();
        String query = "SELECT id_avance, horas_realizadas FROM avance_alumno WHERE id_alumno = ? AND id_registro = ?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, studentId);
            preparedStatement.setInt(2, weeklyLogId);
            List<Integer> ids = new ArrayList<>();
            List<Float> hours = new ArrayList<>();
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    ids.add(resultSet.getInt("id_avance"));
                    hours.add(resultSet.getFloat("horas_realizadas"));
                }
            }
            if (!ids.isEmpty()) {
                ActivityDAO activityDAO = new ActivityDAO();
                StudentDAO studentDAO = new StudentDAO();
                WeeklyLog weeklyLog = activityDAO.getWeeklyLogById(weeklyLogId);
                Student student = studentDAO.getStudentById(studentId);
                for (int i = 0; i < ids.size(); i++) {
                    advances.add(new StudentAdvance(ids.get(i), hours.get(i), weeklyLog, student));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener los avances del alumno", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error obteniendo los avances del alumno");
        }
        return advances;
    }

    public float getTotalHoursByIdStudent(int studentId) throws DataOperationException {
        String query = "SELECT SUM(horas_realizadas) FROM avance_alumno WHERE id_alumno = ?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, studentId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getFloat(1);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al sumar horas del estudiante", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener horas totales");
        }
        return 0f;
    }
}
