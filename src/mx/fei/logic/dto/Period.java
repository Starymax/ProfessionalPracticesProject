package mx.fei.logic.dto;

public class Period {

    private int periodId;
    private int year;
    private int number;
    private String name;
    private boolean active;

    public Period(int periodId, int year, int number, String name, boolean active) {
        this.periodId = periodId;
        this.year = year;
        this.number = number;
        this.name = name;
        this.active = active;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getPeriodId() {
        return periodId;
    }

    public int getYear() {
        return year;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }
}