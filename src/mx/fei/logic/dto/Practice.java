package mx.fei.logic.dto;

public class Practice {
    private Student student;
    private EducationalExperience educationalExperience;
    private String period;

    public Practice(Student student, EducationalExperience educationalExperience, String period) {
        this.student = student;
        this.educationalExperience = educationalExperience;
        this.period = period;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public EducationalExperience getEducationalExperience() {
        return educationalExperience;
    }

    public void setEducationalExperience(EducationalExperience educationalExperience) {
        this.educationalExperience = educationalExperience;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }
}
