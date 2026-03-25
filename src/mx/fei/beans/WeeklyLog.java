package mx.fei.beans;

public class WeeklyLog {
    private int weeklyLogId;
    private String week;
    private float workedHours;
    private float plannedHours;
    private Activity activity;

    public WeeklyLog(int weeklyLogId, String week, float workedHours, float plannedHours, Activity activity) {
        this.weeklyLogId = weeklyLogId;
        this.week = week;
        this.workedHours = workedHours;
        this.plannedHours = plannedHours;
        this.activity = activity;
    }

    public int getWeeklyLogId() {
        return weeklyLogId;
    }

    public void setWeeklyLogId(int weeklyLogId) {
        this.weeklyLogId = weeklyLogId;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public float getWorkedHours() {
        return workedHours;
    }

    public void setWorkedHours(float workedHours) {
        this.workedHours = workedHours;
    }

    public float getPlannedHours() {
        return plannedHours;
    }

    public void setPlannedHours(float plannedHours) {
        this.plannedHours = plannedHours;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
