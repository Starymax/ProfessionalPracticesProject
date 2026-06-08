package mx.fei.logic.dto;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Report {
    private int id;
    private String reportType;
    private Date reportDate;
    private String observations;
    private String resultsObtained;
    private Student student;
    private String nrc;
    private int reportNumber;
    private String month;
    private float workedHours;
    private float accumulatedHours;
    private List<ReportActivityProgress> reportActivityProgressList;

    public Report(int reportId, String reportType, Date reportDate, String observations, String resultsObtained, Student student, String nrc) {
        this.id = reportId;
        this.reportType = reportType;
        this.reportDate = reportDate;
        this.observations = observations;
        this.resultsObtained = resultsObtained;
        this.student = student;
        this.nrc = nrc;
    }

    public int getReportId() {
        return id;
    }

    public void setReportId(int reportId) {
        this.id = reportId;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getResultsObtained() {
        return resultsObtained;
    }

    public void setResultsObtained(String resultsObtained) {
        this.resultsObtained = resultsObtained;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getNrc() {
        return nrc;
    }

    public void setNrc(String nrc) {
        this.nrc = nrc;
    }

    public int getReportNumber() {
        return reportNumber;
    }

    public void setReportNumber(int reportNumber) {
        this.reportNumber = reportNumber;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public float getWorkedHours() {
        return workedHours;
    }

    public void setWorkedHours(float workedHours) {
        this.workedHours = workedHours;
    }

    public float getAccumulatedHours() {
        return accumulatedHours;
    }

    public void setAccumulatedHours(float accumulatedHours) {
        this.accumulatedHours = accumulatedHours;
    }

    public List<ReportActivityProgress> getActivityProgressList() {
        return reportActivityProgressList;
    }

    public void setActivityProgressList(List<ReportActivityProgress> reportActivityProgressList) {
        this.reportActivityProgressList = reportActivityProgressList;
    }

    public Date getDate() {
        return reportDate;
    }

    public void setDate(Date date) {
        this.reportDate = date;
    }

    public String getObservationsReport() {
        return observations;
    }

    public void setObservationsReport(String observationsReport) {
        this.observations = observationsReport;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report that = (Report) o;
        return id == that.id &&
                Objects.equals(reportType, that.reportType) &&
                Objects.equals(reportDate, that.reportDate) &&
                Objects.equals(observations, that.observations) &&
                Objects.equals(resultsObtained, that.resultsObtained) &&
                Objects.equals(student, that.student) &&
                Objects.equals(nrc, that.nrc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reportType, reportDate, observations, resultsObtained, student, nrc);
    }
}
