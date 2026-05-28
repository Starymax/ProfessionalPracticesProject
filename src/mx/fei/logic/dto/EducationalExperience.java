package mx.fei.logic.dto;

public class EducationalExperience {
    private String nrc;
    private String name;
    private String educationalProgram;
    private Professor professor;
    private String period;

    public EducationalExperience(String nrc, String name, String educationalProgram, Professor professor) {
        this(nrc, name, educationalProgram, professor, "");
    }

    public EducationalExperience(String nrc, String name, String educationalProgram, Professor professor, String period) {
        this.nrc = nrc;
        this.name = name;
        this.educationalProgram = educationalProgram;
        this.professor = professor;
        this.period = period;
    }

    public EducationalExperience() {
    }

    public String getNrc() {
        return nrc;
    }

    public void setNrc(String nrc) {
        this.nrc = nrc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEducationalProgram() {
        return educationalProgram;
    }

    public void setEducationalProgram(String educationalProgram) {
        this.educationalProgram = educationalProgram;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }
}
