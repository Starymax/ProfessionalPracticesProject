package mx.fei.logic.dto;

import java.util.Objects;

public class WeeklyLog {
    private int id;
    private int week;
    private int workedHours;
    private int plannedHours;
    private Activity activity;

    public WeeklyLog(int weeklyLogId, int week, int workedHours, int plannedHours, Activity activity) {
        this.id = weeklyLogId;
        this.week = week;
        this.workedHours = workedHours;
        this.plannedHours = plannedHours;
        this.activity = activity;
    }

    public WeeklyLog() {}

    public int getWeeklyLogId() {
        return id;
    }

    public void setWeeklyLogId(int weeklyLogId) {
        this.id = weeklyLogId;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getWorkedHours() {
        return workedHours;
    }

    public void setWorkedHours(int workedHours) {
        this.workedHours = workedHours;
    }

    public int getPlannedHours() {
        return plannedHours;
    }

    public void setPlannedHours(int plannedHours) {
        this.plannedHours = plannedHours;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object object) {
        boolean isEqual = false;
        if (this == object) {
            isEqual = true;
        } else if (object != null && getClass() == object.getClass()) {
            WeeklyLog weeklyLog = (WeeklyLog) object;
            isEqual = id == weeklyLog.id
                    && week == weeklyLog.week
                    && workedHours == weeklyLog.workedHours
                    && plannedHours == weeklyLog.plannedHours
                    && Objects.equals(activity, weeklyLog.activity);
        }
        return isEqual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, week, workedHours, plannedHours, activity);
    }
}
