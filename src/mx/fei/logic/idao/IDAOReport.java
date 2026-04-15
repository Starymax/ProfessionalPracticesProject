package mx.fei.logic.idao;

import mx.fei.logic.dto.Report;
import mx.fei.logic.exceptions.DataBaseConnectionException;

import java.util.List;

public interface IDAOReport {
    boolean createReport(Report report) throws DataBaseConnectionException;

    List<Report> getReportsByStudentEnrollment(String enrollment) throws DataBaseConnectionException;

    Report getReportById(int reportId) throws DataBaseConnectionException;

    boolean setObservations(int reportId, String Observations) throws DataBaseConnectionException;
}
