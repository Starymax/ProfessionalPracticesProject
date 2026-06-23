package mx.fei.logic.idao;

import mx.fei.logic.dto.Report;
import mx.fei.logic.dto.ReportActivityProgress;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataOperationException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface IDAOReport {
    Report buildReportFromResultSet(ResultSet resultSet, Student student) throws SQLException;

    int createReport(Report report) throws DataOperationException;

    boolean createMonthlyReport(Report report) throws DataOperationException;

    void requireReport(Report report);

    boolean persistReportWithWeeklyDetail(Report report, String errorMessage) throws DataOperationException;

    void insertActivitiesWithWeeklyDetail(Connection connection, Report report, int reportId) throws SQLException;

    void bindActivityProgress(PreparedStatement preparedStatement, ReportActivityProgress activityProgress, int reportId) throws SQLException;

    void insertWeeklyDetail(Connection connection, PreparedStatement activityStatement, ReportActivityProgress activityProgress) throws SQLException;

    boolean createPartialReport(Report report) throws DataOperationException;

    boolean createFinalReport(Report report) throws DataOperationException;

    Report getReportById(int reportId) throws DataOperationException;

    List<Report> getReportsByStudentEnrollment(String enrollment) throws DataOperationException;

    int countReportsByTypeAndStudent(String reportType, int studentId) throws DataOperationException;

    boolean setObservations(int reportId, String Observations) throws DataOperationException;

    boolean updateActivityObservations(int reportId, List<ReportActivityProgress> activityProgressList) throws DataOperationException;
}
