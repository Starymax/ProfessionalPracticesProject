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

/**
 * Data Access Object for StudentAdvance (a student's reported hours per weekly log).
 * Provides persistence and retrieval operations on the avance_alumno table.
 */
public class StudentAdvanceDAO implements IDAOStudentAdvance {
    private static final Logger logger = Logger.getLogger(StudentAdvanceDAO.class.getName());

    /**
     * Inserts a student advance, or updates the realized hours if one already exists for the same key.
     *
     * @param advance the advance to create or update, must not be null
     * @return true if the row was inserted or updated
     * @throws IllegalArgumentException if advance is null
     * @throws DataOperationException if a database error occurs
     */
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

    /**
     * Retrieves a student advance by his identifier, resolving his weekly log and student.
     *
     * @param advanceId the identifier of the advance to retrieve
     * @return the matching StudentAdvance, or null if none was found
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public StudentAdvance getAdvanceById(int advanceId) throws DataOperationException {
        String query = "SELECT * FROM avance_alumno WHERE id_avance = ?";
        float realizedHours = 0;
        int weeklyLogId = 0;
        int studentId = 0;
        boolean found = false;
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, advanceId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    realizedHours = resultSet.getFloat("horas_realizadas");
                    weeklyLogId = resultSet.getInt("id_registro");
                    studentId = resultSet.getInt("id_alumno");
                    found = true;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener el avance del alumno", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener el avance del alumno");
        }
        if (!found) {
            return null;
        }
        ActivityDAO activityDAO = new ActivityDAO();
        WeeklyLog weeklyLog = activityDAO.getWeeklyLogById(weeklyLogId);
        StudentDAO studentDAO = new StudentDAO();
        Student student = studentDAO.getStudentById(studentId);
        return new StudentAdvance(advanceId, realizedHours, weeklyLog, student);
    }

    /**
     * Retrieves all advances reported by a given student.
     *
     * @param studentId the student's identifier
     * @return a list of the student's advances, empty if there are none
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public List<StudentAdvance> getAdvancesByStudentId(int studentId) throws DataOperationException {
        String query = "SELECT id_avance, horas_realizadas, id_registro, id_alumno FROM avance_alumno WHERE id_alumno = ?";
        List<StudentAdvance> advances = new ArrayList<>();
        List<int[]> rows = new ArrayList<>();
        List<Float> hours = new ArrayList<>();
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, studentId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    rows.add(new int[]{resultSet.getInt("id_avance"), resultSet.getInt("id_registro"), resultSet.getInt("id_alumno")});
                    hours.add(resultSet.getFloat("horas_realizadas"));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener los avances del alumno", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener los avances del alumno");
        }
        ActivityDAO activityDAO = new ActivityDAO();
        StudentDAO studentDAO = new StudentDAO();
        for (int i = 0; i < rows.size(); i++) {
            int[] row = rows.get(i);
            WeeklyLog weeklyLog = activityDAO.getWeeklyLogById(row[1]);
            Student student = studentDAO.getStudentById(row[2]);
            advances.add(new StudentAdvance(row[0], hours.get(i), weeklyLog, student));
        }
        return advances;
    }

    /**
     * Retrieves all advances associated with a given weekly log.
     *
     * @param weeklyLogId the identifier of the weekly log
     * @return a list of advances for the weekly log, empty if there are none
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public List<StudentAdvance> getAdvancesByWeeklyLogId(int weeklyLogId) throws DataOperationException {
        String query = "SELECT id_avance, horas_realizadas, id_registro, id_alumno FROM avance_alumno WHERE id_registro = ?";
        List<StudentAdvance> advances = new ArrayList<>();
        List<int[]> rows = new ArrayList<>();
        List<Float> hours = new ArrayList<>();
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, weeklyLogId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    rows.add(new int[]{resultSet.getInt("id_avance"), resultSet.getInt("id_registro"), resultSet.getInt("id_alumno")});
                    hours.add(resultSet.getFloat("horas_realizadas"));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener los avances por registro semanal", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener los avances por registro semanal");
        }
        ActivityDAO activityDAO = new ActivityDAO();
        StudentDAO studentDAO = new StudentDAO();
        for (int i = 0; i < rows.size(); i++) {
            int[] row = rows.get(i);
            WeeklyLog weeklyLog = activityDAO.getWeeklyLogById(row[1]);
            Student student = studentDAO.getStudentById(row[2]);
            advances.add(new StudentAdvance(row[0], hours.get(i), weeklyLog, student));
        }
        return advances;
    }

    /**
     * Updates the realized hours of an existing advance.
     *
     * @param advanceId the identifier of the advance to update
     * @param realizedHours the new realized hours value
     * @return true if at least one row was updated
     * @throws DataOperationException if a database error occurs
     */
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

    /**
     * Retrieves the advances of a student for a specific weekly log.
     *
     * @param studentId the student's identifier
     * @param weeklyLogId the identifier of the weekly log
     * @return a list of matching advances, empty if there are none
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public List<StudentAdvance> getAdvancesByStudentAndWeeklyLog(int studentId, int weeklyLogId) throws DataOperationException {
        List<StudentAdvance> advances = new ArrayList<>();
        String query = "SELECT id_avance, horas_realizadas FROM avance_alumno WHERE id_alumno = ? AND id_registro = ?";
        List<Integer> ids = new ArrayList<>();
        List<Float> hours = new ArrayList<>();
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, studentId);
            preparedStatement.setInt(2, weeklyLogId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    ids.add(resultSet.getInt("id_avance"));
                    hours.add(resultSet.getFloat("horas_realizadas"));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener los avances del alumno", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error obteniendo los avances del alumno");
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
        return advances;
    }

    /**
     * Returns the total realized hours reported by a student across all their advances.
     *
     * @param studentId the student's identifier
     * @return the sum of realized hours, or 0 if the student has no advances
     * @throws DataOperationException if a database error occurs
     */
    @Override
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
