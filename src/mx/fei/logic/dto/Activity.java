package mx.fei.logic.dto;

public class Activity {
    private int id;
    private String name;
    private String observationsActivity;
    private Project project;

    public Activity(int activityId, String name, String observationsActivity, Project project) {
        this.id = activityId;
        this.name = name;
        this.observationsActivity = observationsActivity;
        this.project = project;
    }

    public int getActivityId() {
        return id;
    }

    public void setActivityId(int activityId) {
        this.id = activityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
