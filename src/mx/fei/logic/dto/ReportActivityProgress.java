package mx.fei.logic.dto;

import java.util.List;

public class ReportActivityProgress {
    private float progressPercentage;
    private String observations;
    private Activity activity;
    private List<WeeklyLog> weeklyProgressList;

    public ReportActivityProgress(float progressPercentage, String observations, Activity activity, List<WeeklyLog> weeklyProgressList) {
        this.progressPercentage = progressPercentage;
        this.observations = observations;
        this.activity = activity;
        this.weeklyProgressList = weeklyProgressList;
    }

    public float getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(float progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public List<WeeklyLog> getWeeklyProgressList() {
        return weeklyProgressList;
    }

    public void setWeeklyProgressList(List<WeeklyLog> weeklyProgressList) {
        this.weeklyProgressList = weeklyProgressList;
    }
}
