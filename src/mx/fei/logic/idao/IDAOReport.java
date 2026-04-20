package mx.fei.logic.idao;

import mx.fei.logic.dto.Report;
import mx.fei.logic.exceptions.DataOperationException;

import java.util.List;

public interface IDAOReport {
    boolean createReport(Report report) throws DataOperationException;

    List<Report> getReportsByStudentEnrollment(String enrollment) throws DataOperationException;

    Report getReportById(int reportId) throws DataOperationException;

    boolean setObservations(int reportId, String Observations) throws DataOperationException;
}
