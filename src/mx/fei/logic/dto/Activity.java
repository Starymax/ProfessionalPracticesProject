package mx.fei.logic.dto;

import java.util.Objects;

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

    public Activity() {
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

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object object) {
        boolean isEqual = false;
        if (this == object) {
            isEqual = true;
        } else if (object != null && getClass() == object.getClass()) {
            Activity activity = (Activity) object;
            isEqual = id == activity.id
                    && Objects.equals(name, activity.name)
                    && Objects.equals(observationsActivity, activity.observationsActivity);
        }
        return isEqual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, observationsActivity);
    }
}
