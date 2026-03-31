package mx.fei.logic.dto;

public class EducationalExperience {
    private String nrc;
    private String name;
    private String educationalProgram;
    private String escolarPeriod;
    private Professor professor;

    public EducationalExperience(String nrc, String name, String educationalProgram, String escolarPeriod, Professor professor) {
        this.nrc = nrc;
        this.name = name;
        this.educationalProgram = educationalProgram;
        this.escolarPeriod = escolarPeriod;
        this.professor = professor;
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

    public String getEscolarPeriod() {
        return escolarPeriod;
    }

    public void setEscolarPeriod(String escolarPeriod) {
        this.escolarPeriod = escolarPeriod;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }
}
