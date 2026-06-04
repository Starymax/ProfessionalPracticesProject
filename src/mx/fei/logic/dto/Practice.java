package mx.fei.logic.dto;

public class Practice {
    private int id;
    private Student student;
    private EducationalExperience educationalExperience;
    private String period;
    private float grade;

    public Practice(int id ,Student student, EducationalExperience educationalExperience, String period, float grade) {
        this.id = id;
        this.student = student;
        this.educationalExperience = educationalExperience;
        this.period = period;
        this.grade = grade;
    }

    public Practice(Student student, EducationalExperience educationalExperience, String period, float grade) {
        this.student = student;
        this.educationalExperience = educationalExperience;
        this.period = period;
        this.grade = grade;
    }

    public Practice(Student student, EducationalExperience educationalExperience) {
        this.student = student;
        this.educationalExperience = educationalExperience;
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

    public float getGrade() {
        return grade;
    }

    public void setGrade(float grade) {
        this.grade = grade;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
