package mx.fei.logic.dto;

import java.util.ArrayList;
import java.util.List;

public class FinalReport {

    private String career;
    private String nrc;
    private String professor;
    private String period;
    private String student;
    private String enterprise;
    private String project;
    private String hoursRealized;
    private String reportDate;
    private String generalObjectives;
    private String methodology;
    private String observations;
    private List<FinalReportRow> finalReportRows;

    public FinalReport() {
        this.career = "";
        this.nrc = "";
        this.professor = "";
        this.period = "";
        this.student = "";
        this.enterprise = "";
        this.project = "";
        this.hoursRealized = "0";
        this.reportDate = "";
        this.generalObjectives = "";
        this.methodology = "";
        this.observations = "";
        this.finalReportRows = new ArrayList<>();
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public String getNrc() {
        return nrc;
    }

    public void setNrc(String nrc) {
        this.nrc = nrc;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public String getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(String enterprise) {
        this.enterprise = enterprise;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getHoursRealized() {
        return hoursRealized;
    }

    public void setHoursRealized(String hoursRealized) {
        this.hoursRealized = hoursRealized;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    public String getGeneralObjectives() {
        return generalObjectives;
    }

    public void setGeneralObjectives(String generalObjectives) {
        this.generalObjectives = generalObjectives;
    }

    public String getMethodology() {
        return methodology;
    }

    public void setMethodology(String methodology) {
        this.methodology = methodology;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public List<FinalReportRow> getFinalReportRows() {
        return finalReportRows;
    }

    public void setFinalReportRows(List<FinalReportRow> finalReportRows) {
        this.finalReportRows = finalReportRows != null ? finalReportRows : new ArrayList<>();
    }

    public void initializeRows(int count) {
        finalReportRows = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            finalReportRows.add(new FinalReportRow("", "", "", "", "", ""));
        }
    }
}
