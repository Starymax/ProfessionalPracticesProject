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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doThrow;

public class ReportDAOTest {

    private ReportDAO reportDAO;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private MockedStatic<DatabaseConnectionManager> databaseConnectionManager;

    @BeforeEach
    void setUp() throws SQLException {
        reportDAO = new ReportDAO();
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);
        databaseConnectionManager = Mockito.mockStatic(DatabaseConnectionManager.class);
        databaseConnectionManager.when(DatabaseConnectionManager::getConnection).thenReturn(connection);
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
    void createReport_ReportNull_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> reportDAO.createReport(null));
    }

    @Test
    void createReport_Successful_ReturnsGeneratedId() throws SQLException, DataOperationException {
        Report report = buildMockReport();
        ResultSet generatedKeys = mock(ResultSet.class);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(42);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        int result = reportDAO.createReport(report);
        assertEquals(42, result);
        verify(report).setReportId(42);
    }

    @Test
    void createReport_NoGeneratedKeys_ReturnsZero() throws SQLException, DataOperationException {
        Report report = buildMockReport();
        ResultSet generatedKeys = mock(ResultSet.class);
        when(generatedKeys.next()).thenReturn(false);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        int result = reportDAO.createReport(report);
        assertEquals(0, result);
    }

    @Test
    void createReport_SQLException_ThrowsDataOperationException() throws SQLException {
        Report report = buildMockReport();
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de insercion"));
        assertThrows(DataOperationException.class, () -> reportDAO.createReport(report));
    }

    @Test
    void createMonthlyReport_ReportNull_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> reportDAO.createMonthlyReport(null));
    }

    @Test
    void createMonthlyReport_CreateReportReturnsZero_ReturnsFalse() throws SQLException, DataOperationException {
        Report report = buildMockReport();
        when(report.getActivityProgressList()).thenReturn(new ArrayList<>());
        ReportDAO spyReportDAO = spy(reportDAO);
        doReturn(0).when(spyReportDAO).createReport(report);
        boolean result = spyReportDAO.createMonthlyReport(report);
        assertFalse(result);
        verify(connection).rollback();
    }

    @Test
    void createMonthlyReport_Successful_ReturnsTrue() throws SQLException, DataOperationException {
        Report report = buildMockReport();
        WeeklyLog weeklyLog = mock(WeeklyLog.class);
        when(weeklyLog.getWeek()).thenReturn(1);
        when(weeklyLog.getPlannedHours()).thenReturn(8);
        when(weeklyLog.getWorkedHours()).thenReturn(7);
        List<WeeklyLog> weeklyLogs = List.of(weeklyLog);
        ReportActivityProgress progress = buildMockActivityProgress(weeklyLogs);
        when(report.getActivityProgressList()).thenReturn(List.of(progress));
        PreparedStatement psWeekly = mock(PreparedStatement.class);
        ResultSet generatedKeys = mock(ResultSet.class);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(5);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        when(connection.prepareStatement(anyString())).thenReturn(psWeekly);
        when(connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);
        ReportDAO spyReportDAO = spy(reportDAO);
        doReturn(1).when(spyReportDAO).createReport(report);
        boolean result = spyReportDAO.createMonthlyReport(report);
        assertTrue(result);
        verify(connection).commit();
    }

    @Test
    void createMonthlyReport_EmptyActivityList_CommitsAndReturnsTrue() throws SQLException, DataOperationException {
        Report report = buildMockReport();
        when(report.getActivityProgressList()).thenReturn(new ArrayList<>());
        ReportDAO spyReportDAO = spy(reportDAO);
        doReturn(1).when(spyReportDAO).createReport(report);
        boolean result = spyReportDAO.createMonthlyReport(report);
        assertTrue(result);
        verify(connection).commit();
    }

    @Test
    void createMonthlyReport_NoGeneratedKeyForActivity_SkipsWeeklyLogs() throws SQLException, DataOperationException {
        Report report = buildMockReport();
        ReportActivityProgress progress = buildMockActivityProgress(new ArrayList<>());
        when(report.getActivityProgressList()).thenReturn(List.of(progress));
        ResultSet generatedKeys = mock(ResultSet.class);
        when(generatedKeys.next()).thenReturn(false);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        when(connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);
        ReportDAO spyReportDAO = spy(reportDAO);
        doReturn(1).when(spyReportDAO).createReport(report);
        boolean result = spyReportDAO.createMonthlyReport(report);
        assertTrue(result);
        verify(connection).commit();
    }

    @Test
    void createMonthlyReport_EmptyWeeklyProgressList_ExecutesEmptyBatch() throws SQLException, DataOperationException {
        Report report = buildMockReport();
        ReportActivityProgress progress = buildMockActivityProgress(new ArrayList<>());
        when(report.getActivityProgressList()).thenReturn(List.of(progress));
        PreparedStatement psWeekly = mock(PreparedStatement.class);
        ResultSet generatedKeys = mock(ResultSet.class);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(5);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        when(connection.prepareStatement(anyString())).thenReturn(psWeekly);
        when(connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);
        ReportDAO spyReportDAO = spy(reportDAO);
        doReturn(1).when(spyReportDAO).createReport(report);
        boolean result = spyReportDAO.createMonthlyReport(report);
        assertTrue(result);
        verify(psWeekly, never()).addBatch();
        verify(psWeekly).executeBatch();
    }

    @Test
    void createMonthlyReport_SQLException_ThrowsDataOperationException() throws SQLException {
        Report report = buildMockReport();
        when(report.getActivityProgressList()).thenReturn(new ArrayList<>());
        when(connection.prepareStatement(anyString(), anyInt())).thenThrow(new SQLException("Error de conexion"));
        ReportDAO spyReportDAO = spy(reportDAO);
        doThrow(new DataOperationException("Error al crear el reporte.")).when(spyReportDAO).createReport(report);

        assertThrows(DataOperationException.class, () -> spyReportDAO.createMonthlyReport(report));
    }

    @Test
    void createPartialReport_ReportNull_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> reportDAO.createPartialReport(null));
    }

    @Test
    void createPartialReport_CreateReportReturnsZero_ReturnsFalse() throws SQLException, DataOperationException {
        Report report = buildMockReport();
        when(report.getActivityProgressList()).thenReturn(new ArrayList<>());
        ReportDAO spyReportDAO = spy(reportDAO);
        doReturn(0).when(spyReportDAO).createReport(report);
        boolean result = spyReportDAO.createPartialReport(report);
        assertFalse(result);
        verify(connection).rollback();
    }

    @Test
    void createPartialReport_Successful_ReturnsTrue() throws SQLException, DataOperationException {
        Report report = buildMockReport();
        WeeklyLog weeklyLog = mock(WeeklyLog.class);
        when(weeklyLog.getWeek()).thenReturn(2);
        when(weeklyLog.getPlannedHours()).thenReturn(10);
        when(weeklyLog.getWorkedHours()).thenReturn(9);
        List<WeeklyLog> weeklyLogs = List.of(weeklyLog);
        ReportActivityProgress progress = buildMockActivityProgress(weeklyLogs);
        when(report.getActivityProgressList()).thenReturn(List.of(progress));
        PreparedStatement psWeekly = mock(PreparedStatement.class);
        ResultSet generatedKeys = mock(ResultSet.class);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(7);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        when(connection.prepareStatement(anyString())).thenReturn(psWeekly);
        when(connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);
        ReportDAO spyReportDAO = spy(reportDAO);
        doReturn(2).when(spyReportDAO).createReport(report);
        boolean result = spyReportDAO.createPartialReport(report);
        assertTrue(result);
        verify(connection).commit();
    }

    @Test
    void createPartialReport_EmptyActivityList_CommitsAndReturnsTrue() throws SQLException, DataOperationException {
        Report report = buildMockReport();
        when(report.getActivityProgressList()).thenReturn(new ArrayList<>());
        ReportDAO spyReportDAO = spy(reportDAO);
        doReturn(2).when(spyReportDAO).createReport(report);
        boolean result = spyReportDAO.createPartialReport(report);
        assertTrue(result);
        verify(connection).commit();
    }

    @Test
    void createPartialReport_NoGeneratedKeyForActivity_SkipsWeeklyLogs() throws SQLException, DataOperationException {
        Report report = buildMockReport();
        ReportActivityProgress progress = buildMockActivityProgress(new ArrayList<>());
        when(report.getActivityProgressList()).thenReturn(List.of(progress));
        ResultSet generatedKeys = mock(ResultSet.class);
        when(generatedKeys.next()).thenReturn(false);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        when(connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);
        ReportDAO spyReportDAO = spy(reportDAO);
        doReturn(2).when(spyReportDAO).createReport(report);
        boolean result = spyReportDAO.createPartialReport(report);
        assertTrue(result);
        verify(connection).commit();
    }

    @Test
    void createPartialReport_EmptyWeeklyProgressList_ExecutesEmptyBatch() throws SQLException, DataOperationException {
        Report report = buildMockReport();
        ReportActivityProgress progress = buildMockActivityProgress(new ArrayList<>());
        when(report.getActivityProgressList()).thenReturn(List.of(progress));
        PreparedStatement psWeekly = mock(PreparedStatement.class);
        ResultSet generatedKeys = mock(ResultSet.class);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(7);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        when(connection.prepareStatement(anyString())).thenReturn(psWeekly);
        when(connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);
        ReportDAO spyReportDAO = spy(reportDAO);
        doReturn(2).when(spyReportDAO).createReport(report);
        boolean result = spyReportDAO.createPartialReport(report);
        assertTrue(result);
        verify(psWeekly, never()).addBatch();
        verify(psWeekly).executeBatch();
    }

    @Test
    void createPartialReport_SQLException_ThrowsDataOperationException() throws SQLException {
        Report report = buildMockReport();
        when(report.getActivityProgressList()).thenReturn(new ArrayList<>());
        ReportDAO spyReportDAO = spy(reportDAO);
        doThrow(new DataOperationException("Error al crear el reporte.")).when(spyReportDAO).createReport(report);
        assertThrows(DataOperationException.class, () -> spyReportDAO.createPartialReport(report));
    }

    @Test
    void createFinalReport_ReportNull_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> reportDAO.createFinalReport(null));
    }

    @Test
    void createFinalReport_CreateReportReturnsZero_ReturnsFalse() throws SQLException, DataOperationException {
        Report report = buildMockReport();
        when(report.getActivityProgressList()).thenReturn(new ArrayList<>());
        ReportDAO spyReportDAO = spy(reportDAO);
        doReturn(0).when(spyReportDAO).createReport(report);
        boolean result = spyReportDAO.createFinalReport(report);
        assertFalse(result);
        verify(connection).rollback();
    }

    @Test
    void createFinalReport_Successful_ReturnsTrue() throws SQLException, DataOperationException {
        Report report = buildMockReport();
        ReportActivityProgress progress = buildMockActivityProgress(new ArrayList<>());
        when(report.getActivityProgressList()).thenReturn(List.of(progress));
        PreparedStatement psActivity = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(psActivity);
        ReportDAO spyReportDAO = spy(reportDAO);
        doReturn(3).when(spyReportDAO).createReport(report);
        boolean result = spyReportDAO.createFinalReport(report);
        assertTrue(result);
        verify(connection).commit();
        verify(psActivity).addBatch();
    }

    @Test
    void createFinalReport_EmptyActivityList_CommitsAndReturnsTrue() throws SQLException, DataOperationException {
        Report report = buildMockReport();
        when(report.getActivityProgressList()).thenReturn(new ArrayList<>());
        ReportDAO spyReportDAO = spy(reportDAO);
        doReturn(3).when(spyReportDAO).createReport(report);
        boolean result = spyReportDAO.createFinalReport(report);
        assertTrue(result);
        verify(connection).commit();
    }

    @Test
    void createFinalReport_SQLException_ThrowsDataOperationException() throws SQLException {
        Report report = buildMockReport();
        when(report.getActivityProgressList()).thenReturn(new ArrayList<>());
        ReportDAO spyReportDAO = spy(reportDAO);
        doThrow(new DataOperationException("Error al crear el reporte.")).when(spyReportDAO).createReport(report);
        assertThrows(DataOperationException.class, () -> spyReportDAO.createFinalReport(report));
    }

    @Test
    void getReportById_Found_ReturnsReport() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("tipo_reporte")).thenReturn("Mensual");
        when(resultSet.getDate("fecha_reporte")).thenReturn(new java.sql.Date(System.currentTimeMillis()));
        when(resultSet.getString("observaciones")).thenReturn("Obs");
        when(resultSet.getString("resultados_obtenidos")).thenReturn("Resultados");
        when(resultSet.getInt("id_alumno")).thenReturn(1);
        when(resultSet.getString("nrc")).thenReturn("12345");
        Student mockStudent = mock(Student.class);
        try (MockedConstruction<StudentDAO> mockedStudentDAO = mockConstruction(StudentDAO.class,
                (mock, context) -> when(mock.getStudentById(1)).thenReturn(mockStudent))) {
            Report result = reportDAO.getReportById(10);
            assertNotNull(result);
            assertEquals("Mensual", result.getReportType());
            verify(preparedStatement).setInt(1, 10);
        }
    }

    @Test
    void getReportById_NotFound_ReturnsNull() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        Report result = reportDAO.getReportById(99);
        assertNull(result);
        verify(preparedStatement).setInt(1, 99);
    }

    @Test
    void getReportById_SQLException_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de lectura"));
        assertThrows(DataOperationException.class, () -> reportDAO.getReportById(1));
    }

    @Test
    void getReportsByStudentEnrollment_WithReports_ReturnsList() throws SQLException, DataOperationException {
        ResultSet listResultSet = mock(ResultSet.class);
        when(listResultSet.next()).thenReturn(true, false);
        when(listResultSet.getInt("id_reporte")).thenReturn(5);
        ResultSet detailResultSet = mock(ResultSet.class);
        when(detailResultSet.next()).thenReturn(true);
        when(detailResultSet.getString("tipo_reporte")).thenReturn("Parcial");
        when(detailResultSet.getDate("fecha_reporte")).thenReturn(new java.sql.Date(System.currentTimeMillis()));
        when(detailResultSet.getString("observaciones")).thenReturn("Obs");
        when(detailResultSet.getString("resultados_obtenidos")).thenReturn("Res");
        when(detailResultSet.getInt("id_alumno")).thenReturn(2);
        when(detailResultSet.getString("nrc")).thenReturn("99999");
        PreparedStatement detailStatement = mock(PreparedStatement.class);
        when(detailStatement.executeQuery()).thenReturn(detailResultSet);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement).thenReturn(detailStatement);
        when(preparedStatement.executeQuery()).thenReturn(listResultSet);
        Student mockStudent = mock(Student.class);
        try (MockedConstruction<StudentDAO> mockedStudentDAO = mockConstruction(StudentDAO.class,
                (mock, context) -> when(mock.getStudentById(anyInt())).thenReturn(mockStudent))) {
            List<Report> result = reportDAO.getReportsByStudentEnrollment("S123456");
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(preparedStatement).setString(1, "S123456");
        }
    }

    @Test
    void getReportsByStudentEnrollment_NoReports_ReturnsEmptyList() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<Report> result = reportDAO.getReportsByStudentEnrollment("S000000");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getReportsByStudentEnrollment_SQLException_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de red"));
        assertThrows(DataOperationException.class, () -> reportDAO.getReportsByStudentEnrollment("S123456"));
    }

    @Test
    void countReportsByTypeAndStudent_Found_ReturnsCount() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(3);
        int result = reportDAO.countReportsByTypeAndStudent("Mensual", 1);
        assertEquals(3, result);
        verify(preparedStatement).setString(1, "Mensual");
        verify(preparedStatement).setInt(2, 1);
    }

    @Test
    void countReportsByTypeAndStudent_NotFound_ReturnsZero() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        int result = reportDAO.countReportsByTypeAndStudent("Final", 99);
        assertEquals(0, result);
    }

    @Test
    void countReportsByTypeAndStudent_SQLException_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Timeout"));
        assertThrows(DataOperationException.class, () -> reportDAO.countReportsByTypeAndStudent("Parcial", 1));
    }

    @Test
    void setObservations_Successful_ReturnsTrue() throws SQLException, DataOperationException {
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = reportDAO.setObservations(1, "Nueva observacion");
        assertTrue(result);
        verify(preparedStatement).setString(1, "Nueva observacion");
        verify(preparedStatement).setInt(2, 1);
    }

    @Test
    void setObservations_SQLException_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de escritura"));
        assertThrows(DataOperationException.class, () -> reportDAO.setObservations(1, "Obs"));
    }

    @Test
    void updateActivityObservations_WithActivities_ReturnsTrue() throws SQLException, DataOperationException {
        ReportActivityProgress progress = mock(ReportActivityProgress.class);
        Activity activity = mock(Activity.class);
        when(activity.getActivityId()).thenReturn(10);
        when(progress.getActivity()).thenReturn(activity);
        when(progress.getObservations()).thenReturn("Nueva obs");
        boolean result = reportDAO.updateActivityObservations(1, List.of(progress));
        assertTrue(result);
        verify(preparedStatement).setString(1, "Nueva obs");
        verify(preparedStatement).setInt(2, 1);
        verify(preparedStatement).setInt(3, 10);
        verify(preparedStatement).addBatch();
        verify(preparedStatement).executeBatch();
    }

    @Test
    void updateActivityObservations_EmptyList_ReturnsTrue() throws SQLException, DataOperationException {
        boolean result = reportDAO.updateActivityObservations(1, new ArrayList<>());
        assertTrue(result);
        verify(preparedStatement, never()).addBatch();
        verify(preparedStatement).executeBatch();
    }

    @Test
    void updateActivityObservations_SQLException_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeBatch()).thenThrow(new SQLException("Error de batch"));
        assertThrows(DataOperationException.class, () -> reportDAO.updateActivityObservations(1, new ArrayList<>()));
    }
}