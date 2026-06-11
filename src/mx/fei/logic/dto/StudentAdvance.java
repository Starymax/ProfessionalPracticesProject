package mx.fei.logic.dto;

import java.util.Objects;

public class StudentAdvance {
    private int id;
    private float realizedHours;
    private WeeklyLog weeklyLog;
    private Student student;

    public StudentAdvance(int id, float realizedHours, WeeklyLog weeklyLog, Student student) {
        this.id = id;
        this.realizedHours = realizedHours;
        this.weeklyLog = weeklyLog;
        this.student = student;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getRealizedHours() {
        return realizedHours;
    }

    public void setRealizedHours(float realizedHours) {
        this.realizedHours = realizedHours;
    }

    public WeeklyLog getWeeklyLog() {
        return weeklyLog;
    }

    public void setWeeklyLog(WeeklyLog weeklyLog) {
        this.weeklyLog = weeklyLog;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public boolean equals(Object object) {
        boolean isEqual = false;
        if (this == object) {
            isEqual = true;
        } else if (object == null || getClass() != object.getClass()) {
            StudentAdvance that = (StudentAdvance) object;
            isEqual = id == that.id
                    && Float.compare(realizedHours, that.realizedHours) == 0
                    && Objects.equals(weeklyLog, that.weeklyLog)
                    && Objects.equals(student, that.student);
        }
        return isEqual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, realizedHours, weeklyLog, student);
    }
}
