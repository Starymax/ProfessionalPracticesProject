package mx.fei.logic.idao;

import mx.fei.logic.dto.Activity;
import mx.fei.logic.dto.WeeklyLog;
import mx.fei.logic.exceptions.DataBaseConnectionException;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public interface IDAOActivity {
    Activity getActivityById(int activityId) throws DataBaseConnectionException;

    List<Activity> getActivitiesByProjectId(int idActivity) throws DataBaseConnectionException;

    boolean insertActivity(Activity activity, int projectId, ArrayList<WeeklyLog> weeklyLogs) throws DataBaseConnectionException;

    boolean insertWeeklyLogs(Connection connection, List<WeeklyLog> logs, int activityId) throws DataBaseConnectionException;

    WeeklyLog getWeeklyLogById(int weeklyLogId) throws DataBaseConnectionException;

    List<WeeklyLog> getWeeklyLogsByActivityId(int activityId) throws DataBaseConnectionException;
}