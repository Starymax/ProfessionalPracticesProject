package mx.fei.logic.idao;

import mx.fei.logic.dto.Report;
import mx.fei.logic.exceptions.DataOperationException;

import java.util.List;

public interface IDAOReport {
    int createReport(Report report) throws DataOperationException;

    boolean createMonthlyReport(Report report) throws DataOperationException;

    boolean createPartialReport(Report report) throws DataOperationException;

    boolean createFinalReport(Report report) throws DataOperationException;

    Report getReportById(int reportId) throws DataOperationException;

    List<Report> getReportsByStudentEnrollment(String enrollment) throws DataOperationException;

    int countReportsByTypeAndStudent(String reportType, int studentId) throws DataOperationException;

    boolean setObservations(int reportId, String Observations) throws DataOperationException;
}
