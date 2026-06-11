package mx.fei.logic.dto;

import java.util.Date;
import java.util.List;

public class PartialReport extends Report {
    private String career;
    private String professorName;
    private String period;
    private String organization;
    private String projectObjective;
    private String methodology;
    private List<PartialActivityRow> activityRows;

    public PartialReport(int reportId, String reportType, Date reportDate, String observations, String resultsObtained, Student student, String nrc) {
        super(reportId, reportType, reportDate, observations, resultsObtained, student, nrc);
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getProjectObjective() {
        return projectObjective;
    }

    public void setProjectObjective(String projectObjective) {
        this.projectObjective = projectObjective;
    }

    public String getMethodology() {
        return methodology;
    }

    public void setMethodology(String methodology) {
        this.methodology = methodology;
    }

    public List<PartialActivityRow> getActivityRows() {
        return activityRows;
    }

    public void setActivityRows(List<PartialActivityRow> activityRows) {
        this.activityRows = activityRows;
    }
}