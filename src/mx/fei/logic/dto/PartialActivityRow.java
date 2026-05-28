package mx.fei.logic.dto;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PartialActivityRow {
    private final StringProperty activityName;
    private final StringProperty plannedTime;
    private final StringProperty week1Plan;
    private final StringProperty week1Real;
    private final StringProperty week2Plan;
    private final StringProperty week2Real;
    private final StringProperty week3Plan;
    private final StringProperty week3Real;
    private final StringProperty week4Plan;
    private final StringProperty week4Real;
    private final StringProperty week5Plan;
    private final StringProperty week5Real;
    private final StringProperty week6Plan;
    private final StringProperty week6Real;
    private final StringProperty week7Plan;
    private final StringProperty week7Real;
    private final StringProperty week8Plan;
    private final StringProperty week8Real;

    public PartialActivityRow(String activityName, String plannedTime, String week1Plan, String week1Real, String week2Plan, String week2Real, String week3Plan, String week3Real, String week4Plan, String week4Real, String week5Plan, String week5Real, String week6Plan, String week6Real, String week7Plan, String week7Real, String week8Plan, String week8Real) {
        this.activityName = new SimpleStringProperty(activityName);
        this.plannedTime = new SimpleStringProperty(plannedTime);
        this.week1Plan = new SimpleStringProperty(week1Plan);
        this.week1Real = new SimpleStringProperty(week1Real);
        this.week2Plan = new SimpleStringProperty(week2Plan);
        this.week2Real = new SimpleStringProperty(week2Real);
        this.week3Plan = new SimpleStringProperty(week3Plan);
        this.week3Real = new SimpleStringProperty(week3Real);
        this.week4Plan = new SimpleStringProperty(week4Plan);
        this.week4Real = new SimpleStringProperty(week4Real);
        this.week5Plan = new SimpleStringProperty(week5Plan);
        this.week5Real = new SimpleStringProperty(week5Real);
        this.week6Plan = new SimpleStringProperty(week6Plan);
        this.week6Real = new SimpleStringProperty(week6Real);
        this.week7Plan = new SimpleStringProperty(week7Plan);
        this.week7Real = new SimpleStringProperty(week7Real);
        this.week8Plan = new SimpleStringProperty(week8Plan);
        this.week8Real = new SimpleStringProperty(week8Real);
    }

    public String getActivityName() {
        return activityName.get();
    }

    public StringProperty activityNameProperty() {
        return activityName;
    }

    public String getPlannedTime() {
        return plannedTime.get();
    }

    public StringProperty plannedTimeProperty() {
        return plannedTime;
    }

    public String getWeek1Plan() {
        return week1Plan.get();
    }

    public StringProperty week1PlanProperty() {
        return week1Plan;
    }

    public String getWeek1Real() {
        return week1Real.get();
    }

    public StringProperty week1RealProperty() {
        return week1Real;
    }

    public String getWeek2Plan() {
        return week2Plan.get();
    }

    public StringProperty week2PlanProperty() {
        return week2Plan;
    }

    public String getWeek2Real() {
        return week2Real.get();
    }

    public StringProperty week2RealProperty() {
        return week2Real;
    }

    public String getWeek3Plan() {
        return week3Plan.get();
    }

    public StringProperty week3PlanProperty() {
        return week3Plan;
    }

    public String getWeek3Real() {
        return week3Real.get();
    }

    public StringProperty week3RealProperty() {
        return week3Real;
    }

    public String getWeek4Plan() {
        return week4Plan.get();
    }

    public StringProperty week4PlanProperty() {
        return week4Plan;
    }

    public String getWeek4Real() {
        return week4Real.get();
    }

    public StringProperty week4RealProperty() {
        return week4Real;
    }

    public String getWeek5Plan() {
        return week5Plan.get();
    }

    public StringProperty week5PlanProperty() {
        return week5Plan;
    }

    public String getWeek5Real() {
        return week5Real.get();
    }

    public StringProperty week5RealProperty() {
        return week5Real;
    }

    public String getWeek6Plan() {
        return week6Plan.get();
    }

    public StringProperty week6PlanProperty() {
        return week6Plan;
    }

    public String getWeek6Real() {
        return week6Real.get();
    }

    public StringProperty week6RealProperty() {
        return week6Real;
    }

    public String getWeek7Plan() {
        return week7Plan.get();
    }

    public StringProperty week7PlanProperty() {
        return week7Plan;
    }

    public String getWeek7Real() {
        return week7Real.get();
    }

    public StringProperty week7RealProperty() {
        return week7Real;
    }

    public String getWeek8Plan() {
        return week8Plan.get();
    }

    public StringProperty week8PlanProperty() {
        return week8Plan;
    }

    public String getWeek8Real() {
        return week8Real.get();
    }

    public StringProperty week8RealProperty() {
        return week8Real;
    }
}
