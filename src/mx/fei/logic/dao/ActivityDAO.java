package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.Activity;
import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.WeeklyLog;
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
    private Logger logger = Logger.getLogger(ActivityDAO.class.getName());

    @Override
    public boolean insertActivity(Activity activity, int projectId, ArrayList<WeeklyLog> weeklyLogs) {
        boolean success = false;
        String queryActivity = "INSERT INTO actividad (nombre_actividad, observaciones_actividad, id_proyecto) VALUES (?,?,?)";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryActivity, Statement.RETURN_GENERATED_KEYS);) {
            preparedStatement.setString(1, activity.getName());
            preparedStatement.setString(2, activity.getObservationsActivity());
            preparedStatement.setInt(3, projectId);
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int activityId = generatedKeys.getInt(1);
                success = insertWeeklyLogs(connection, weeklyLogs, activityId);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error en la conexión a la base de datos", e);
        }
        return success;
    }

    @Override
    public boolean insertWeeklyLogs(Connection connection, List<WeeklyLog> logs, int activityId) {
        boolean success = false;
        String queryWeeklyLog = "INSERT INTO registro_semanal (semana, horas_realizadas, horas_planificadas, id_actividad) VALUES (?,?,?,?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(queryWeeklyLog);) {
            for (WeeklyLog log : logs) {
                preparedStatement.setInt(1, log.getWeek());
                preparedStatement.setFloat(2, log.getWorkedHours());
                preparedStatement.setFloat(3, log.getPlannedHours());
                preparedStatement.setInt(4, activityId);
                preparedStatement.executeUpdate();
            }
            success = true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return success;
    }

    @Override
    public Activity getActivityById(int activityId) {
        String query = "SELECT id_actividad, nombre_actividad, observaciones_actividad, id_proyecto FROM actividad WHERE id_actividad = ?";
        Activity activity = null;
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, activityId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String nameActivity = resultSet.getString("nombre_actividad");
                String observationsActivity = resultSet.getString("observaciones_actividad");
                int projectId = resultSet.getInt("id_proyecto");
                ProjectDAO projectDAO = new ProjectDAO();
                Project project = projectDAO.getProjectById(projectId);
                activity = new Activity(activityId, nameActivity, observationsActivity, project);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return activity;
    }

    @Override
    public List<Activity> getActivitiesByProjectId(int projectId) {
        String queryActivities = "SELECT id_actividad FROM actividad WHERE id_proyecto = ?";
        List<Activity> activities = new ArrayList<>();
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryActivities)) {
            preparedStatement.setInt(1, projectId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                activities.add(getActivityById(resultSet.getInt("id_actividad")));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return activities;
    }

    @Override
    public WeeklyLog getWeeklyLogById(int weeklyLogId) {
        String queryWeeklyLog = "SELECT semana, horas_realizadas, horas_planificadas, id_actividad FROM registro_semanal WHERE id_registro = ?";
        WeeklyLog weeklyLog = null;
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryWeeklyLog)) {
            preparedStatement.setInt(1, weeklyLogId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int week = resultSet.getInt("semana");
                float workedHours = resultSet.getFloat("horas_realizadas");
                float plannedHours = resultSet.getFloat("horas_planificadas");
                Activity activity = getActivityById(resultSet.getInt("id_actividad"));
                weeklyLog = new WeeklyLog(weeklyLogId, week, workedHours, plannedHours, activity);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return weeklyLog;
    }

    @Override
    public List<WeeklyLog> getWeeklyLogsByActivityId(int activityId) {
        String queryWeeklyLogs = "SELECT id_registro FROM registro_semanal WHERE id_actividad = ?";
        List<WeeklyLog> weeklyLogs = new ArrayList<>();
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryWeeklyLogs)) {
            preparedStatement.setInt(1, activityId);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Integer> weeklyLogIds = new ArrayList<>();
            while (resultSet.next()) {
                weeklyLogIds.add(resultSet.getInt("id_registro"));
            }
            resultSet.close();
            for (Integer weeklyLogId: weeklyLogIds) {
                weeklyLogs.add(getWeeklyLogById(weeklyLogId));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return weeklyLogs;
    }
}
