package mx.fei.logic.dto;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ActivityProgressRow {

    private final StringProperty name;
    private final StringProperty plannedHours;
    private final StringProperty realizedHours;
    private final StringProperty status;

    public ActivityProgressRow(String name, String plannedHours, String realizedHours, String status) {
        this.name = new SimpleStringProperty(name);
        this.plannedHours = new SimpleStringProperty(plannedHours);
        this.realizedHours = new SimpleStringProperty(realizedHours);
        this.status = new SimpleStringProperty(status);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty plannedHoursProperty() {
        return plannedHours;
    }

    public String getPlannedHours() {
        return plannedHours.get();
    }

    public StringProperty realizedHoursProperty() {
        return realizedHours;
    }

    public String getRealizedHours() {
        return realizedHours.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public String getStatus() {
        return status.get();
    }
}
