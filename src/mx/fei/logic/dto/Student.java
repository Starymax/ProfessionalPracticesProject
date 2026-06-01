package mx.fei.logic.dto;

public class Student extends User{
    private String enrollment;
    private boolean indigenousLanguage;
    private Project assignedProject;
    private float grade;

    public Student(int userId, String name, String lastName, String email, String password, String gender, boolean activeStatus,  String enrollment, boolean indigenousLanguage, Project assignedProject, float grade) {
        super(userId, name, lastName, email, password, gender, activeStatus);
        this.enrollment = enrollment;
        this.indigenousLanguage = indigenousLanguage;
        this.assignedProject = assignedProject;
        this.grade = grade;
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

    public Project getAssignedProject() {
        return assignedProject;
    }

    public void setAssignedProject(Project assignedProject) {
        this.assignedProject = assignedProject;
    }

    public float getGrade() {
        return grade;
    }

    public void setGrade(float grade) {
        this.grade = grade;
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