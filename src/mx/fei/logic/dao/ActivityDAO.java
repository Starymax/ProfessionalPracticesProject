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

public class ActivityDAO implements IDAOActivity {
    private static final Logger logger = Logger.getLogger(ActivityDAO.class.getName());

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
            try (Connection connection = DatabaseConnectionManager.getConnection();
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
                throw new DataOperationException("Error al insertar actividad");
            }
            return success;
        }
    }

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
            throw new DataOperationException("Error al insertar el horario a la actividad");
        }
        return success;
    }

    @Override
    public Activity getActivityById(int activityId) throws DataOperationException {
        String query = "SELECT id_actividad, nombre_actividad, observaciones_actividad, id_proyecto FROM actividad WHERE id_actividad = ?";
        Activity activity = null;
        try (Connection connection = DatabaseConnectionManager.getConnection();
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
            throw new DataOperationException("Error al obtener la actividad");
        }
        return activity;
    }

    @Override
    public List<Activity> getActivitiesByProjectId(int projectId) throws DataOperationException {
        String query = "SELECT id_actividad FROM actividad WHERE id_proyecto = ? ORDER BY id_actividad ASC";
        List<Activity> activities = new ArrayList<>();
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, projectId);
            List<Integer> activitiesIds = new ArrayList<>();
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    activitiesIds.add(resultSet.getInt("id_actividad"));
                }
            }
            for (int idActivity : activitiesIds) {
                activities.add(getActivityById(idActivity));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener las actividades",e);
            throw new DataOperationException("Error al obtener las actividades");
        }
        return activities;
    }

    @Override
    public WeeklyLog getWeeklyLogById(int weeklyLogId) throws DataOperationException {
        String query = "SELECT semana, horas_planificadas, id_actividad FROM registro_semanal WHERE id_registro = ?";
        WeeklyLog weeklyLog = null;
        try (Connection connection = DatabaseConnectionManager.getConnection();
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
            throw new DataOperationException("Error al obtener el horario de la actividad");
        }
        return weeklyLog;
    }

    @Override
    public List<WeeklyLog> getWeeklyLogsByActivityId(int activityId) throws DataOperationException {
        String query = "SELECT id_registro FROM registro_semanal WHERE id_actividad = ?";
        List<WeeklyLog> weeklyLogs = new ArrayList<>();
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, activityId);
            List<Integer> weeklyLogIds = new ArrayList<>();
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    weeklyLogIds.add(resultSet.getInt("id_registro"));
                }
            }
            for (Integer weeklyLogId: weeklyLogIds) {
                weeklyLogs.add(getWeeklyLogById(weeklyLogId));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener los horarios de la actividad",e);
            throw new DataOperationException("Error al obtener los horarios de la actividad");
        }
        return weeklyLogs;
    }
}
