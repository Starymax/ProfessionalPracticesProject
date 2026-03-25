package mx.fei.logic.dto;

public class Activity {
    private int activityId;
    private String nameActivity;
    private String observationsActivity;
    private Project project;

    public Activity(int activityId, String nameActivity, String observationsActivity, Project project) {
        this.activityId = activityId;
        this.nameActivity = nameActivity;
        this.observationsActivity = observationsActivity;
        this.project = project;
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public String getNameActivity() {
        return nameActivity;
    }

    public void setNameActivity(String nameActivity) {
        this.nameActivity = nameActivity;
    }

    public String getObservationsActivity() {
        return observationsActivity;
    }

    public void setObservationsActivity(String observationsActivity) {
        this.observationsActivity = observationsActivity;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
