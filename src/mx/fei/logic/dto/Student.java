package mx.fei.logic.dto;

public class Student extends User{
    private int userId;
    private String enrollment;
    private String period;
    private boolean indigenousLanguage;
    private float grade;
    private Project assignedProject;
    private EducationalExperience educationalExperience;

    public Student(int userId, String name, String lastName, String email, String password, String gender, boolean active_status, int userId1, String enrollment, String period, boolean indigenousLanguage, float grade, Project asignedProject) {
        super(userId, name, lastName, email, password, gender, active_status);
        this.userId = userId1;
        this.enrollment = enrollment;
        this.period = period;
        this.indigenousLanguage = indigenousLanguage;
        this.grade = grade;
        this.assignedProject = asignedProject;
    }

    public String getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(String enrollment) {
        this.enrollment = enrollment;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public boolean isIndigenousLanguage() {
        return indigenousLanguage;
    }

    public void setIndigenousLanguage(boolean indigenousLanguage) {
        this.indigenousLanguage = indigenousLanguage;
    }

    public float getGrade() {
        return grade;
    }

    public void setGrade(float grade) {
        this.grade = grade;
    }

    public Project getAssignedProject() {
        return assignedProject;
    }

    public void setAssignedProject(Project assignedProject) {
        this.assignedProject = assignedProject;
    }
}
