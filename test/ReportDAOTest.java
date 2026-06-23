import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dao.ReportDAO;
import mx.fei.logic.dao.StudentDAO;
import mx.fei.logic.dto.Report;
import mx.fei.logic.dto.Student;
import mx.fei.logic.dto.ReportActivityProgress;
import mx.fei.logic.dto.Activity;
import mx.fei.logic.dto.WeeklyLog;
import mx.fei.logic.exceptions.DataOperationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

public class ReportDAOTest {

    private ReportDAO reportDAO;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private MockedStatic<DatabaseConnectionManager> databaseConnectionManager;
    private DatabaseConnectionManager mockManager;

    @BeforeEach
    void setUp() throws SQLException {
        reportDAO = new ReportDAO();
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);
        mockManager = mock(DatabaseConnectionManager.class);
        databaseConnectionManager = Mockito.mockStatic(DatabaseConnectionManager.class);
        databaseConnectionManager.when(DatabaseConnectionManager::getInstance).thenReturn(mockManager);
        when(mockManager.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);
    }

    @AfterEach
    void tearDown() {
        if (databaseConnectionManager != null) {
            databaseConnectionManager.close();
        }
    }

    private Report buildMockReport() {
        Report report = mock(Report.class);
        Student student = mock(Student.class);
        when(student.getUserId()).thenReturn(1);
        when(report.getStudent()).thenReturn(student);
        when(report.getReportType()).thenReturn("Mensual");
        when(report.getReportDate()).thenReturn(new Date());
        when(report.getObservations()).thenReturn("Obs");
        when(report.getResultsObtained()).thenReturn("Resultados");
        when(report.getNrc()).thenReturn("12345");
        return report;
    }

    private ReportActivityProgress buildMockActivityProgress(List<WeeklyLog> weeklyLogs) {
        ReportActivityProgress progress = mock(ReportActivityProgress.class);
        Activity activity = mock(Activity.class);
        when(activity.getActivityId()).thenReturn(10);
        when(progress.getActivity()).thenReturn(activity);
        when(progress.getProgressPercentage()).thenReturn(75.0f);
        when(progress.getObservations()).thenReturn("Obs actividad");
        when(progress.getWeeklyProgressList()).thenReturn(weeklyLogs);
        return progress;
    }

    @Test
    void createReport_ReportIsNull_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> reportDAO.createReport(null));
    }

    @Test
    void createReport_InsertGeneratesKey_ReturnsGeneratedId() throws SQLException {
        Report report = buildMockReport();
        ResultSet generatedKeys = mock(ResultSet.class);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(42);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        int result = reportDAO.createReport(report);
        assertEquals(42, result);
    }

    @Test
    void createReport_InsertGeneratesNoKey_ReturnsZero() throws SQLException {
        Report report = buildMockReport();
        ResultSet generatedKeys = mock(ResultSet.class);
        when(generatedKeys.next()).thenReturn(false);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        int result = reportDAO.createReport(report);
        assertEquals(0, result);
    }

    @Test
    void createReport_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        Report report = buildMockReport();
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de insercion"));
        assertThrows(DataOperationException.class, () -> reportDAO.createReport(report));
    }

    @Test
    void createMonthlyReport_ReportIsNull_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> reportDAO.createMonthlyReport(null));
    }

    @Test
    void createMonthlyReport_CreateReportReturnsZero_ReturnsFalse() throws SQLException {
        Report report = buildMockReport();
        ResultSet generatedKeys = mock(ResultSet.class);
        when(generatedKeys.next()).thenReturn(false);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        boolean result = reportDAO.createMonthlyReport(report);
        assertFalse(result);
    }

    @Test
    void createMonthlyReport_ActivitiesPersisted_ReturnsTrue() throws SQLException {
        Report report = buildMockReport();
        WeeklyLog weeklyLog = mock(WeeklyLog.class);
        when(weeklyLog.getWeek()).thenReturn(1);
        when(weeklyLog.getPlannedHours()).thenReturn(8);
        when(weeklyLog.getWorkedHours()).thenReturn(7);
        ReportActivityProgress progress = buildMockActivityProgress(List.of(weeklyLog));
        when(report.getActivityProgressList()).thenReturn(List.of(progress));
        PreparedStatement weeklyStatement = mock(PreparedStatement.class);
        ResultSet generatedKeys = mock(ResultSet.class);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(5);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        when(connection.prepareStatement(anyString())).thenReturn(weeklyStatement);
        when(connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);
        ReportDAO secondReportDAO = spy(reportDAO);
        doReturn(1).when(secondReportDAO).createReport(report);
        boolean result = secondReportDAO.createMonthlyReport(report);
        assertTrue(result);
    }

    @Test
    void createMonthlyReport_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException, DataOperationException {
        Report report = buildMockReport();
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de insercion"));
        assertThrows(DataOperationException.class, () -> reportDAO.createMonthlyReport(report));
    }

    @Test
    void createPartialReport_ReportIsNull_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> reportDAO.createPartialReport(null));
    }

    @Test
    void createPartialReport_CreateReportReturnsZero_ReturnsFalse() throws SQLException, DataOperationException {
        Report report = buildMockReport();
        ResultSet generatedKeys = mock(ResultSet.class);
        when(generatedKeys.next()).thenReturn(false);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        boolean result = reportDAO.createPartialReport(report);
        assertFalse(result);
    }

    @Test
    void createPartialReport_ActivitiesPersisted_ReturnsTrue() throws SQLException {
        Report report = buildMockReport();
        WeeklyLog weeklyLog = mock(WeeklyLog.class);
        when(weeklyLog.getWeek()).thenReturn(2);
        when(weeklyLog.getPlannedHours()).thenReturn(10);
        when(weeklyLog.getWorkedHours()).thenReturn(9);
        ReportActivityProgress progress = buildMockActivityProgress(List.of(weeklyLog));
        when(report.getActivityProgressList()).thenReturn(List.of(progress));
        PreparedStatement weeklyStatement = mock(PreparedStatement.class);
        ResultSet generatedKeys = mock(ResultSet.class);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(7);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        when(connection.prepareStatement(anyString())).thenReturn(weeklyStatement);
        when(connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);
        ReportDAO secondReportDAO = spy(reportDAO);
        doReturn(2).when(secondReportDAO).createReport(report);
        boolean result = secondReportDAO.createPartialReport(report);
        assertTrue(result);
    }

    @Test
    void createPartialReport_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException, DataOperationException {
        Report report = buildMockReport();
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de insercion"));
        assertThrows(DataOperationException.class, () -> reportDAO.createPartialReport(report));
    }

    @Test
    void createFinalReport_ReportIsNull_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> reportDAO.createFinalReport(null));
    }

    @Test
    void createFinalReport_CreateReportReturnsZero_ReturnsFalse() throws SQLException {
        Report report = buildMockReport();
        ResultSet generatedKeys = mock(ResultSet.class);
        when(generatedKeys.next()).thenReturn(false);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        boolean result = reportDAO.createFinalReport(report);
        assertFalse(result);
    }

    @Test
    void createFinalReport_ActivitiesPersisted_ReturnsTrue() throws SQLException {
        Report report = buildMockReport();
        ReportActivityProgress progress = buildMockActivityProgress(new ArrayList<>());
        when(report.getActivityProgressList()).thenReturn(List.of(progress));
        ResultSet generatedKeys = mock(ResultSet.class);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(3);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        PreparedStatement activityStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(activityStatement);
        boolean result = reportDAO.createFinalReport(report);
        assertTrue(result);
    }

    @Test
    void createFinalReport_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException, DataOperationException {
        Report report = buildMockReport();
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de insercion"));
        assertThrows(DataOperationException.class, () -> reportDAO.createFinalReport(report));
    }

    @Test
    void getReportById_ReportExists_ReturnsExpectedReport() throws SQLException {
        Date date = new java.sql.Date(1000000000L);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("id_reporte")).thenReturn(10);
        when(resultSet.getString("tipo_reporte")).thenReturn("Mensual");
        when(resultSet.getDate("fecha_reporte")).thenReturn((java.sql.Date) date);
        when(resultSet.getString("observaciones")).thenReturn("Obs");
        when(resultSet.getString("resultados_obtenidos")).thenReturn("Resultados");
        when(resultSet.getInt("id_alumno")).thenReturn(1);
        when(resultSet.getString("nrc")).thenReturn("12345");
        Student student = mock(Student.class);
        Report expectedReport = new Report(10, "Mensual", date, "Obs", "Resultados", student, "12345");
        try (MockedConstruction<StudentDAO> mockedStudentDAO = mockConstruction(StudentDAO.class,
                (mock, context) -> when(mock.getStudentById(1)).thenReturn(student))) {
            Report result = reportDAO.getReportById(10);
            assertEquals(expectedReport, result);
        }
    }

    @Test
    void getReportById_ReportDoesNotExist_ReturnsNull() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        Report result = reportDAO.getReportById(99);
        assertNull(result);
    }

    @Test
    void getReportById_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de lectura"));
        assertThrows(DataOperationException.class, () -> reportDAO.getReportById(1));
    }

    @Test
    void getReportsByStudentEnrollment_StudentHasTwoReports_ReturnsListWithTwoReports() throws SQLException {
        java.sql.Date reportDate = new java.sql.Date(1000000000L);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getInt("id_reporte")).thenReturn(5, 6);
        when(resultSet.getString("tipo_reporte")).thenReturn("Parcial", "Final");
        when(resultSet.getDate("fecha_reporte")).thenReturn(reportDate, reportDate);
        when(resultSet.getString("observaciones")).thenReturn("Obs", "Obs2");
        when(resultSet.getString("resultados_obtenidos")).thenReturn("Res", "Res2");
        when(resultSet.getString("nrc")).thenReturn("99999", "99999");
        Student student = mock(Student.class);
        Report expectedReport1 = new Report(5, "Parcial", reportDate, "Obs", "Res", student, "99999");
        Report expectedReport2 = new Report(6, "Final", reportDate, "Obs2", "Res2", student, "99999");
        try (MockedConstruction<StudentDAO> mockedStudentDAO = mockConstruction(StudentDAO.class,
                (mock, context) -> when(mock.getStudentByEnrollment("S123456")).thenReturn(student))) {
            List<Report> result = reportDAO.getReportsByStudentEnrollment("S123456");
            assertEquals(List.of(expectedReport1, expectedReport2), result);
        }
    }

    @Test
    void getReportsByStudentEnrollment_StudentHasNoReports_ReturnsEmptyList() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<Report> result = reportDAO.getReportsByStudentEnrollment("S000000");
        assertTrue(result.isEmpty());
    }

    @Test
    void getReportsByStudentEnrollment_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de red"));
        assertThrows(DataOperationException.class, () -> reportDAO.getReportsByStudentEnrollment("S123456"));
    }

    @Test
    void countReportsByTypeAndStudent_ReportsExist_ReturnsCount() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(3);
        int result = reportDAO.countReportsByTypeAndStudent("Mensual", 1);
        assertEquals(3, result);
    }

    @Test
    void countReportsByTypeAndStudent_NoReportsExist_ReturnsZero() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        int result = reportDAO.countReportsByTypeAndStudent("Final", 99);
        assertEquals(0, result);
    }

    @Test
    void countReportsByTypeAndStudent_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Timeout"));
        assertThrows(DataOperationException.class, () -> reportDAO.countReportsByTypeAndStudent("Parcial", 1));
    }

    @Test
    void setObservations_UpdateAffectsOneRow_ReturnsTrue() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = reportDAO.setObservations(1, "Nueva observacion");
        assertTrue(result);
    }

    @Test
    void setObservations_UpdateAffectsZeroRows_ReturnsFalse() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(0);
        boolean result = reportDAO.setObservations(1, "Nueva observacion");
        assertFalse(result);
    }

    @Test
    void setObservations_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de escritura"));
        assertThrows(DataOperationException.class, () -> reportDAO.setObservations(1, "Obs"));
    }

    @Test
    void updateActivityObservations_ListHasOneActivity_ReturnsTrue() throws SQLException {
        ReportActivityProgress progress = mock(ReportActivityProgress.class);
        Activity activity = mock(Activity.class);
        when(activity.getActivityId()).thenReturn(10);
        when(progress.getActivity()).thenReturn(activity);
        when(progress.getObservations()).thenReturn("Nueva obs");
        boolean result = reportDAO.updateActivityObservations(1, List.of(progress));
        assertTrue(result);
    }

    @Test
    void updateActivityObservations_ListIsEmpty_ReturnsTrue() throws SQLException {
        boolean result = reportDAO.updateActivityObservations(1, new ArrayList<>());
        assertTrue(result);
    }

    @Test
    void updateActivityObservations_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeBatch()).thenThrow(new SQLException("Error de batch"));
        assertThrows(DataOperationException.class, () -> reportDAO.updateActivityObservations(1, new ArrayList<>()));
    }
}
