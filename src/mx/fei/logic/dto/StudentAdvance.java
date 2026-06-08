package mx.fei.logic.dto;

import java.util.Objects;

public class StudentAdvance {
    private int advanceId;
    private float realizedHours;
    private WeeklyLog weeklyLog;
    private Student student;

    public StudentAdvance(int advanceId, float realizedHours, WeeklyLog weeklyLog, Student student) {
        this.advanceId = advanceId;
        this.realizedHours = realizedHours;
        this.weeklyLog = weeklyLog;
        this.student = student;
    }

    public int getAdvanceId() {
        return advanceId;
    }

    public void setAdvanceId(int advanceId) {
        this.advanceId = advanceId;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentAdvance that = (StudentAdvance) o;
        return advanceId == that.advanceId
                && Float.compare(realizedHours, that.realizedHours) == 0
                && Objects.equals(weeklyLog, that.weeklyLog)
                && Objects.equals(student, that.student);
    }

    @Override
    public int hashCode() {
        return Objects.hash(advanceId, realizedHours, weeklyLog, student);
    }
}
