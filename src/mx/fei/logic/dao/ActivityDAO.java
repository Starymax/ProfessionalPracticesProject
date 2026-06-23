package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.Activity;
import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.WeeklyLog;
import mx.fei.logic.exceptions.DataOperationException;
import mx.fei.logic.idao.IDAOActivity;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Activity and its associated WeeklyLog records.
 * Provides persistence and retrieval operations on the actividad and
 * registro_semanal tables.
 */
public class ActivityDAO implements IDAOActivity {
    private static final Logger logger = Logger.getLogger(ActivityDAO.class.getName());

    /**
     * Inserts an activity together with its weekly logs.
     *
     * @param activity the activity to persist, must not be null
     * @param project the project the activity belongs to, must not be null
     * @param weeklyLogs the weekly logs to associate with the new activity
     * @return true if the activity and its weekly logs were inserted successfully
     * @throws DataOperationException if the activity or project is null, or a database error occurs
     */
    @Override
    public boolean insertActivity(Activity activity, Project project, ArrayList<WeeklyLog> weeklyLogs) throws DataOperationException {
        if (activity == null) {
            logger.log(Level.WARNING,"La actividad esta vacia");
            throw new DataOperationException("La actividad no puede estar vacía");
        } else if (project == null) {
            logger.log(Level.WARNING,"Error al guardar el proyecto");
            throw new DataOperationException("Error al guardar el proyecto");
        } else {
            boolean success = false;
            String query = "INSERT INTO actividad (nombre_actividad, observaciones_actividad, id_proyecto) VALUES (?,?,?)";
            try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
                preparedStatement.setString(1, activity.getName());
                preparedStatement.setString(2, activity.getObservationsActivity());
                preparedStatement.setInt(3, project.getProjectId());
                preparedStatement.executeUpdate();
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int activityId = generatedKeys.getInt(1);
                        success = insertWeeklyLogs(connection, weeklyLogs, activityId);
                    }
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error al insertar actividad", e);
                if (DAOUtils.isConnectionError(e)) {
                    throw new DataOperationException("Error de conexión. Intente más tarde.");
                }
                throw new DataOperationException("Error al insertar actividad");
            }
            return success;
        }
    }

    /**
     * Inserts the given weekly logs for an activity using an existing connection.
     * This method does not manage the connection lifecycle, allowing it to participate
     * in a wider transaction.
     *
     * @param connection an open database connection
     * @param logs the weekly logs to insert
     * @param activityId the identifier of the activity the logs belong to
     * @return true if the weekly logs were inserted successfully
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public boolean insertWeeklyLogs(Connection connection, List<WeeklyLog> logs, int activityId) throws DataOperationException {
        boolean success = false;
        String query = "INSERT INTO registro_semanal (semana, horas_planificadas, id_actividad) VALUES (?,?,?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            for (WeeklyLog log : logs) {
                preparedStatement.setInt(1, log.getWeek());
                preparedStatement.setInt(2, log.getPlannedHours());
                preparedStatement.setInt(3, activityId);
                preparedStatement.executeUpdate();
            }
            success = true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al insertar el horario a la actividad",e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al insertar el horario a la actividad");
        }
        return success;
    }

    /**
     * Retrieves an activity by its identifier, including its associated project.
     *
     * @param activityId the identifier of the activity to retrieve
     * @return the matching Activity, or null if none was found
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public Activity getActivityById(int activityId) throws DataOperationException {
        String query = "SELECT id_actividad, nombre_actividad, observaciones_actividad, id_proyecto FROM actividad WHERE id_actividad = ?";
        Activity activity = null;
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, activityId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String nameActivity = resultSet.getString("nombre_actividad");
                    String observationsActivity = resultSet.getString("observaciones_actividad");
                    int projectId = resultSet.getInt("id_proyecto");
                    ProjectDAO projectDAO = new ProjectDAO();
                    Project project = projectDAO.getProjectById(projectId);
                    activity = new Activity(activityId, nameActivity, observationsActivity, project);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener la actividad",e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener la actividad");
        }
        return activity;
    }

    /**
     * Retrieves all activities that belong to a given project, ordered by activity id.
     *
     * @param projectId the identifier of the project whose activities are requested
     * @return a list of activities for the project, empty if the project has none
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public List<Activity> getActivitiesByProjectId(int projectId) throws DataOperationException {
        String query = "SELECT id_actividad, nombre_actividad, observaciones_actividad FROM actividad WHERE id_proyecto = ? ORDER BY id_actividad ASC";
        List<Activity> activities = new ArrayList<>();
        try {
            ProjectDAO projectDAO = new ProjectDAO();
            Project project = projectDAO.getProjectById(projectId);
            try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, projectId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int id = resultSet.getInt("id_actividad");
                        String name = resultSet.getString("nombre_actividad");
                        String observations = resultSet.getString("observaciones_actividad");
                        activities.add(new Activity(id, name, observations, project));
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener las actividades", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener las actividades");
        }
        return activities;
    }

    /**
     * Retrieves a weekly log by its identifier, including its associated activity.
     *
     * @param weeklyLogId the identifier of the weekly log to retrieve
     * @return the matching WeeklyLog, or null if none was found
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public WeeklyLog getWeeklyLogById(int weeklyLogId) throws DataOperationException {
        String query = "SELECT semana, horas_planificadas, id_actividad FROM registro_semanal WHERE id_registro = ?";
        WeeklyLog weeklyLog = null;
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, weeklyLogId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int week = resultSet.getInt("semana");
                    int plannedHours = resultSet.getInt("horas_planificadas");
                    Activity activity = getActivityById(resultSet.getInt("id_actividad"));
                    weeklyLog = new WeeklyLog(weeklyLogId, week, 0, plannedHours, activity);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener el horario de la actividad",e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener el horario de la actividad");
        }
        return weeklyLog;
    }

    /**
     * Retrieves all weekly logs associated with a given activity.
     *
     * @param activityId the identifier of the activity whose weekly logs are requested
     * @return a list of weekly logs for the activity, empty if it has none
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public List<WeeklyLog> getWeeklyLogsByActivityId(int activityId) throws DataOperationException {
        String query = "SELECT id_registro, semana, horas_planificadas FROM registro_semanal WHERE id_actividad = ?";
        List<WeeklyLog> weeklyLogs = new ArrayList<>();
        try {
            Activity activity = getActivityById(activityId);
            try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, activityId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int id = resultSet.getInt("id_registro");
                        int week = resultSet.getInt("semana");
                        int plannedHours = resultSet.getInt("horas_planificadas");
                        weeklyLogs.add(new WeeklyLog(id, week, 0, plannedHours, activity));
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener los horarios de la actividad", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener los horarios de la actividad");
        }
        return weeklyLogs;
    }
}
