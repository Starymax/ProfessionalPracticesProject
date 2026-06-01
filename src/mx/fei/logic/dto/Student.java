package mx.fei.logic.dto;

public class Student extends User{
    private String enrollment;
    private boolean indigenousLanguage;
    private Project assignedProject;

    public Student(int userId, String name, String lastName, String email, String password, String gender, boolean activeStatus,  String enrollment, boolean indigenousLanguage, Project assignedProject) {
        super(userId, name, lastName, email, password, gender, activeStatus);
        this.enrollment = enrollment;
        this.indigenousLanguage = indigenousLanguage;
        this.assignedProject = assignedProject;
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