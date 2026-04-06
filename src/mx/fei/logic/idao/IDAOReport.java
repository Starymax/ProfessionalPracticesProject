package mx.fei.logic.idao;

import mx.fei.logic.dto.Report;

import java.util.List;

public interface IDAOReport {
    boolean createReport(Report report);

    List<Report> getReportsByStudentEnrollment(String enrollment);

    Report getReportById(int reportId);

    void setObservations(int reportId, String Observations);
}
