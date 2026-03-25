package mx.fei.logic.dto;

import java.util.Date;

public class Report {
    private int reportId;
    private float workedHours;
    private String reportType;
    private Date date;
    private String observationsReport;
    private Student student;

    public Report(int reportId, float workedHours, String reportType, Date date, String observationsReport, Student student) {
        this.reportId = reportId;
        this.workedHours = workedHours;
        this.reportType = reportType;
        this.date = date;
        this.observationsReport = observationsReport;
        this.student = student;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public float getWorkedHours() {
        return workedHours;
    }

    public void setWorkedHours(float workedHours) {
        this.workedHours = workedHours;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getObservationsReport() {
        return observationsReport;
    }

    public void setObservationsReport(String observationsReport) {
        this.observationsReport = observationsReport;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
