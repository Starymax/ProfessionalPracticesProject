package mx.fei.logic.dto;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ActivityRow {
    private final StringProperty name;
    private final StringProperty progress;
    private final StringProperty workedHours;
    private final StringProperty observations;
    private final ReportActivityProgress activityProgress;

    public ActivityRow(String name, String progress, String workedHours, String observations, ReportActivityProgress activityProgress) {
        this.name = new SimpleStringProperty(name);
        this.progress = new SimpleStringProperty(progress);
        this.workedHours = new SimpleStringProperty(workedHours);
        this.observations = new SimpleStringProperty(observations);
        this.activityProgress = activityProgress;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty progressProperty() {
        return progress;
    }

    public String getProgress() {
        return progress.get();
    }

    public void setProgress(String progress) {
        this.progress.set(progress);
    }

    public StringProperty workedHoursProperty() {
        return workedHours;
    }

    public String getWorkedHours() {
        return workedHours.get();
    }

    public void setWorkedHours(String workedHours) {
        this.workedHours.set(workedHours);
    }

    public StringProperty observationsProperty() {
        return observations;
    }

    public String getObservations() {
        return observations.get();
    }

    public void setObservations(String observations) {
        this.observations.set(observations);
    }

    public ReportActivityProgress getActivityProgress() {
        return activityProgress;
    }
}
