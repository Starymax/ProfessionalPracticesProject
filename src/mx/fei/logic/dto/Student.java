package mx.fei.logic.dto;

public class Student extends User{
    private String enrollment;
    private boolean indigenousLanguage;
    private float grade;
    private Project assignedProject;
    private Practice practice;

    public Student(int userId, String name, String lastName, String email, String password, String gender, boolean active_status,  String enrollment, boolean indigenousLanguage, float grade, Project asignedProject, Practice practice) {
        super(userId, name, lastName, email, password, gender, active_status);
        this.enrollment = enrollment;
        this.indigenousLanguage = indigenousLanguage;
        this.grade = grade;
        this.assignedProject = asignedProject;
        this.practice = practice;
    }

    public String getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(String enrollment) {
        this.enrollment = enrollment;
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

    public Practice getPractice() {
        return practice;
    }

    public void setPractice(Practice practice) {
        this.practice = practice;
    }

    public EducationalExperience getEducationalExperience() {
        return practice != null ? practice.getEducationalExperience() : null;
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;
        if (this == object) {
            result = true;
        } else if (object instanceof Student) {
            Student otherStudent = (Student) object;
            result = this.enrollment.equals(otherStudent.enrollment);
        }
        return result;
    }
}