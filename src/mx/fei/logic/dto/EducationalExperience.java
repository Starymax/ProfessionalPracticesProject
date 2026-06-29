package mx.fei.logic.dto;

import java.util.Objects;

public class EducationalExperience {
    private String nrc;
    private String name;
    private String educationalProgram;
    private Professor professor;
    private String period;
    private boolean activeStatus;

    public EducationalExperience(String nrc, String name, String educationalProgram, Professor professor, boolean activeStatus) {
        this(nrc, name, educationalProgram, professor, "", true);
    }

    public EducationalExperience(String nrc, String name, String educationalProgram, Professor professor, String period, boolean activeStatus) {
        this.nrc = nrc;
        this.name = name;
        this.educationalProgram = educationalProgram;
        this.professor = professor;
        this.period = period;
        this.activeStatus = activeStatus;
    }

    public EducationalExperience(String period, String name, String nrc) {
        this.period = period;
        this.name = name;
        this.nrc = nrc;
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

    public boolean isActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(boolean activeStatus) {
        this.activeStatus = activeStatus;
    }

    @Override
    public boolean equals(Object object) {
        boolean isEqual = false;
        if (this == object) {
            isEqual = true;
        } else if (object != null && getClass() == object.getClass()) {
            EducationalExperience that = (EducationalExperience) object;
            isEqual = Objects.equals(nrc, that.nrc)
                    && Objects.equals(name, that.name)
                    && Objects.equals(educationalProgram, that.educationalProgram)
                    && Objects.equals(professor, that.professor)
                    && Objects.equals(period, that.period);
        }
        return isEqual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nrc, name, educationalProgram, professor, period);
    }
}
