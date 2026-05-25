package mx.fei.logic.dto;

public enum ReportType {
    MONTHLY_REPORT ("MENSUAL"),
    PARTIAL_REPORT ("PARCIAL"),
    FINAL_REPORT ("FINAL");
    private final String reportType;

    private ReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getReportType() {
        return reportType;
    }
}
