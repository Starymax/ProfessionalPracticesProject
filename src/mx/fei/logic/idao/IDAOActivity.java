package mx.fei.logic.idao;

import mx.fei.logic.dto.Activity;
import mx.fei.logic.dto.WeeklyLog;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public interface IDAOActivity {
    Activity getActivityById(int activityId);

    List<Activity> getActivitiesByProjectId(int idActivity);

    boolean insertActivity(Activity activity, int projectId, ArrayList<WeeklyLog> weeklyLogs);

    boolean insertWeeklyLogs(Connection connection, List<WeeklyLog> logs, int activityId);

    WeeklyLog getWeeklyLogById(int weeklyLogId);

    List<WeeklyLog> getWeeklyLogsByActivityId(int activityId);
}