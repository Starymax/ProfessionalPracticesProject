package mx.fei.logic.idao;

import mx.fei.logic.dto.Activity;
import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.WeeklyLog;
import mx.fei.logic.exceptions.DataOperationException;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public interface IDAOActivity {
    Activity getActivityById(int activityId) throws DataOperationException;

    List<Activity> getActivitiesByProjectId(int idActivity) throws DataOperationException;

    boolean insertActivity(Activity activity, Project project, ArrayList<WeeklyLog> weeklyLogs) throws DataOperationException;

    boolean insertWeeklyLogs(Connection connection, List<WeeklyLog> logs, int activityId) throws DataOperationException;

    WeeklyLog getWeeklyLogById(int weeklyLogId) throws DataOperationException;

    List<WeeklyLog> getWeeklyLogsByActivityId(int activityId) throws DataOperationException;
}