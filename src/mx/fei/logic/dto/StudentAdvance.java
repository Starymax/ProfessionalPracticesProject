package mx.fei.logic.dto;

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
}
