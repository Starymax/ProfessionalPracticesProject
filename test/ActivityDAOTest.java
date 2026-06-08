import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dao.ActivityDAO;
import mx.fei.logic.dao.ProjectDAO;
import mx.fei.logic.dto.Activity;
import mx.fei.logic.dto.Project;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mockConstruction;

public class ActivityDAOTest {

    private ActivityDAO activityDAO;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private MockedStatic<DatabaseConnectionManager> databaseConnectionManager;
    private DatabaseConnectionManager mockManager;

    @BeforeEach
    void setUp() throws SQLException {
        activityDAO = new ActivityDAO();
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
            databaseConnectionManager = null;
        }
    }

    @Test
    void insertActivity_ActivityIsNull_ThrowsDataOperationException() {
        assertThrows(DataOperationException.class, () -> activityDAO.insertActivity(null, mock(Project.class), new ArrayList<>()));
    }

    @Test
    void insertActivity_ProjectIsNull_ThrowsDataOperationException() {
        assertThrows(DataOperationException.class, () -> activityDAO.insertActivity(mock(Activity.class), null, new ArrayList<>()));
    }

    @Test
    void insertActivity_InsertReturnsGeneratedKey_ReturnsTrue() throws SQLException {
        Activity activity = mock(Activity.class);
        Project project = mock(Project.class);
        when(project.getProjectId()).thenReturn(1);
        ResultSet generatedKeys = mock(ResultSet.class);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(10);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        PreparedStatement logsStatement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(logsStatement);
        when(connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);
        WeeklyLog weeklyLog = mock(WeeklyLog.class);
        when(weeklyLog.getWeek()).thenReturn(1);
        when(weeklyLog.getPlannedHours()).thenReturn(8);
        ArrayList<WeeklyLog> weeklyLogs = new ArrayList<>();
        weeklyLogs.add(weeklyLog);
        boolean result = activityDAO.insertActivity(activity, project, weeklyLogs);
        assertTrue(result);
    }

    @Test
    void insertActivity_InsertReturnsNoGeneratedKey_ReturnsFalse() throws SQLException {
        Activity activity = mock(Activity.class);
        Project project = mock(Project.class);
        when(project.getProjectId()).thenReturn(1);
        ResultSet generatedKeys = mock(ResultSet.class);
        when(generatedKeys.next()).thenReturn(false);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        boolean result = activityDAO.insertActivity(activity, project, new ArrayList<>());
        assertFalse(result);
    }

    @Test
    void insertActivity_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        Activity activity = mock(Activity.class);
        Project project = mock(Project.class);
        when(project.getProjectId()).thenReturn(1);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de insercion"));
        assertThrows(DataOperationException.class, () -> activityDAO.insertActivity(activity, project, new ArrayList<>()));
    }

    @Test
    void insertWeeklyLogs_ListHasOneLog_ReturnsTrue() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        WeeklyLog log = mock(WeeklyLog.class);
        when(log.getWeek()).thenReturn(1);
        when(log.getPlannedHours()).thenReturn(10);
        List<WeeklyLog> logs = new ArrayList<>();
        logs.add(log);
        boolean result = activityDAO.insertWeeklyLogs(connection, logs, 5);
        assertTrue(result);
    }

    @Test
    void insertWeeklyLogs_ListIsEmpty_ReturnsTrue() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        boolean result = activityDAO.insertWeeklyLogs(connection, new ArrayList<>(), 5);
        assertTrue(result);
    }

    @Test
    void insertWeeklyLogs_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de insercion"));
        WeeklyLog log = mock(WeeklyLog.class);
        when(log.getWeek()).thenReturn(1);
        when(log.getPlannedHours()).thenReturn(5);
        List<WeeklyLog> logs = new ArrayList<>();
        logs.add(log);
        assertThrows(DataOperationException.class, () -> activityDAO.insertWeeklyLogs(connection, logs, 5));
    }

    @Test
    void getActivityById_ActivityExists_ReturnsExpectedActivity() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("nombre_actividad")).thenReturn("Actividad A");
        when(resultSet.getString("observaciones_actividad")).thenReturn("Observaciones");
        when(resultSet.getInt("id_proyecto")).thenReturn(2);
        Project project = mock(Project.class);
        Activity expectedActivity = new Activity();
        expectedActivity.setActivityId(1);
        expectedActivity.setName("Actividad A");
        expectedActivity.setObservationsActivity("Observaciones");
        expectedActivity.setProject(project);
        try (MockedConstruction<ProjectDAO> mockedProjectDAO = mockConstruction(ProjectDAO.class,
                (mock, context) -> when(mock.getProjectById(2)).thenReturn(project))) {
            Activity result = activityDAO.getActivityById(1);
            assertEquals(expectedActivity, result);
        }
    }

    @Test
    void getActivityById_ActivityDoesNotExist_ReturnsNull() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        Activity result = activityDAO.getActivityById(99);
        assertNull(result);
    }

    @Test
    void getActivityById_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de lectura"));
        assertThrows(DataOperationException.class, () -> activityDAO.getActivityById(1));
    }

    @Test
    void getWeeklyLogsByActivityId_ActivityHasOneLog_ReturnsListWithExpectedLog() throws SQLException {
        Connection activityConnection = mock(Connection.class);
        PreparedStatement activityStatement = mock(PreparedStatement.class);
        ResultSet activityResultSet = mock(ResultSet.class);
        when(activityResultSet.next()).thenReturn(true, false);
        when(activityResultSet.getString("nombre_actividad")).thenReturn("Act");
        when(activityResultSet.getString("observaciones_actividad")).thenReturn("Obs");
        when(activityResultSet.getInt("id_proyecto")).thenReturn(1);
        when(activityStatement.executeQuery()).thenReturn(activityResultSet);
        when(activityConnection.prepareStatement(anyString())).thenReturn(activityStatement);
        ResultSet listResultSet = mock(ResultSet.class);
        when(listResultSet.next()).thenReturn(true, false);
        when(listResultSet.getInt("id_registro")).thenReturn(20);
        when(listResultSet.getInt("semana")).thenReturn(2);
        when(listResultSet.getInt("horas_planificadas")).thenReturn(15);
        when(preparedStatement.executeQuery()).thenReturn(listResultSet);
        when(mockManager.getConnection()).thenReturn(activityConnection).thenReturn(connection);
        Project project = mock(Project.class);
        Activity expectedActivity = new Activity(5, "Act", "Obs", project);
        WeeklyLog expectedLog = new WeeklyLog(20, 2, 0, 15, expectedActivity);
        List<WeeklyLog> expectedList = List.of(expectedLog);
        try (MockedConstruction<ProjectDAO> mockedProjectDAO = mockConstruction(ProjectDAO.class,
                (mock, context) -> when(mock.getProjectById(anyInt())).thenReturn(project))) {
            List<WeeklyLog> result = activityDAO.getWeeklyLogsByActivityId(5);
            assertEquals(expectedList, result);
        }
    }

    @Test
    void getActivitiesByProjectId_ProjectHasNoActivities_ReturnsEmptyList() throws SQLException, DataOperationException {
        Project project = mock(Project.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        try (MockedConstruction<ProjectDAO> mockedProjectDAO = mockConstruction(ProjectDAO.class,
                (mock, context) -> when(mock.getProjectById(anyInt())).thenReturn(project))) {
            List<Activity> result = activityDAO.getActivitiesByProjectId(1);
            assertTrue(result.isEmpty());
        }
    }

    @Test
    void getActivitiesByProjectId_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        Project project = mock(Project.class);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de red"));
        try (MockedConstruction<ProjectDAO> mockedProjectDAO = mockConstruction(ProjectDAO.class,
                (mock, context) -> when(mock.getProjectById(anyInt())).thenReturn(project))) {
            assertThrows(DataOperationException.class, () -> activityDAO.getActivitiesByProjectId(1));
        }
    }

    @Test
    void getWeeklyLogById_WeeklyLogExists_ReturnsExpectedWeeklyLog() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("semana")).thenReturn(3);
        when(resultSet.getInt("horas_planificadas")).thenReturn(20);
        when(resultSet.getInt("id_actividad")).thenReturn(5);
        when(resultSet.getInt("horas_trabajadas")).thenReturn(0);
        Connection secondConnection = mock(Connection.class);
        PreparedStatement secondStatement = mock(PreparedStatement.class);
        ResultSet secondResultSet = mock(ResultSet.class);
        when(secondResultSet.next()).thenReturn(true);
        when(secondResultSet.getString("nombre_actividad")).thenReturn("Act");
        when(secondResultSet.getString("observaciones_actividad")).thenReturn("Obs");
        when(secondResultSet.getInt("id_proyecto")).thenReturn(1);
        when(secondStatement.executeQuery()).thenReturn(secondResultSet);
        when(secondConnection.prepareStatement(anyString())).thenReturn(secondStatement);
        when(mockManager.getConnection()).thenReturn(connection).thenReturn(secondConnection);
        Project project = mock(Project.class);
        Activity expectedActivity = new Activity(5, "Act", "Obs", project);
        WeeklyLog expectedLog = new WeeklyLog(7, 3, 0, 20, expectedActivity);
        try (MockedConstruction<ProjectDAO> mockedProjectDAO = mockConstruction(ProjectDAO.class,
                (mock, context) -> when(mock.getProjectById(anyInt())).thenReturn(project))) {
            WeeklyLog result = activityDAO.getWeeklyLogById(7);
            assertEquals(expectedLog, result);
        }
    }

    @Test
    void getWeeklyLogById_WeeklyLogDoesNotExist_ReturnsNull() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        WeeklyLog result = activityDAO.getWeeklyLogById(99);
        assertNull(result);
    }

    @Test
    void getWeeklyLogById_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de lectura"));
        assertThrows(DataOperationException.class, () -> activityDAO.getWeeklyLogById(1));
    }

    @Test
    void getWeeklyLogsByActivityId_ActivityHasOneLog_ReturnsListWithOneLog() throws SQLException {
        Connection activityConnection = mock(Connection.class);
        PreparedStatement activityStatement = mock(PreparedStatement.class);
        ResultSet activityResultSet = mock(ResultSet.class);
        when(activityResultSet.next()).thenReturn(true, false);
        when(activityResultSet.getString("nombre_actividad")).thenReturn("Act");
        when(activityResultSet.getString("observaciones_actividad")).thenReturn("Obs");
        when(activityResultSet.getInt("id_proyecto")).thenReturn(1);
        when(activityStatement.executeQuery()).thenReturn(activityResultSet);
        when(activityConnection.prepareStatement(anyString())).thenReturn(activityStatement);
        ResultSet listResultSet = mock(ResultSet.class);
        when(listResultSet.next()).thenReturn(true, false);
        when(listResultSet.getInt("id_registro")).thenReturn(30);
        when(listResultSet.getInt("semana")).thenReturn(4);
        when(listResultSet.getInt("horas_planificadas")).thenReturn(20);
        when(preparedStatement.executeQuery()).thenReturn(listResultSet);
        when(mockManager.getConnection()).thenReturn(activityConnection).thenReturn(connection);
        Project project = mock(Project.class);
        Activity expectedActivity = new Activity(5, "Act", "Obs", project);
        WeeklyLog expectedLog = new WeeklyLog(30, 4, 0, 20, expectedActivity);
        List<WeeklyLog> expectedList = List.of(expectedLog);
        try (MockedConstruction<ProjectDAO> mockedProjectDAO = mockConstruction(ProjectDAO.class,
                (mock, context) -> when(mock.getProjectById(anyInt())).thenReturn(project))) {
            List<WeeklyLog> result = activityDAO.getWeeklyLogsByActivityId(5);
            assertEquals(expectedList, result);
        }
    }

    @Test
    void getWeeklyLogsByActivityId_ActivityHasNoLogs_ReturnsEmptyList() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<WeeklyLog> result = activityDAO.getWeeklyLogsByActivityId(5);
        assertTrue(result.isEmpty());
    }

    @Test
    void getWeeklyLogsByActivityId_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de red"));
        assertThrows(DataOperationException.class, () -> activityDAO.getWeeklyLogsByActivityId(5));
    }
}
