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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mockConstruction;

public class ActivityDAOTest {

    private ActivityDAO activityDAO;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private MockedStatic<DatabaseConnectionManager> databaseConnectionManager;

    @BeforeEach
    void setUp() throws SQLException {
        activityDAO = new ActivityDAO();
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

    @Test
    void insertActivity_ActivityNull_ThrowsDataOperationException() {
        DataOperationException exception = assertThrows(DataOperationException.class,
                () -> activityDAO.insertActivity(null, mock(Project.class), new ArrayList<>()));
        assertEquals("La actividad no puede estar vacía", exception.getMessage());
    }

    @Test
    void insertActivity_ProjectNull_ThrowsDataOperationException() {
        DataOperationException exception = assertThrows(DataOperationException.class,
                () -> activityDAO.insertActivity(mock(Activity.class), null, new ArrayList<>()));
        assertEquals("Error al guardar el proyecto", exception.getMessage());
    }

    @Test
    void insertActivity_Successful_ReturnsTrue() throws SQLException, DataOperationException {
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
        WeeklyLog log = mock(WeeklyLog.class);
        when(log.getWeek()).thenReturn(1);
        when(log.getPlannedHours()).thenReturn(8);
        ArrayList<WeeklyLog> logs = new ArrayList<>();
        logs.add(log);
        boolean result = activityDAO.insertActivity(activity, project, logs);
        assertTrue(result);
        verify(generatedKeys).next();
    }

    @Test
    void insertActivity_NoGeneratedKeys_ReturnsFalse() throws SQLException, DataOperationException {
        Activity activity = mock(Activity.class);
        Project project = mock(Project.class);
        when(project.getProjectId()).thenReturn(1);
        ResultSet generatedKeys = mock(ResultSet.class);
        when(generatedKeys.next()).thenReturn(false);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        boolean result = activityDAO.insertActivity(activity, project, new ArrayList<>());
        assertFalse(result);
        verify(generatedKeys).next();
    }

    @Test
    void insertActivity_SQLException_ThrowsDataOperationException() throws SQLException {
        Activity activity = mock(Activity.class);
        Project project = mock(Project.class);
        when(project.getProjectId()).thenReturn(1);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de insercion"));

        assertThrows(DataOperationException.class, () -> activityDAO.insertActivity(activity, project, new ArrayList<>()));
    }

    @Test
    void insertWeeklyLogs_WithLogs_ReturnsTrue() throws SQLException, DataOperationException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        WeeklyLog log = mock(WeeklyLog.class);
        when(log.getWeek()).thenReturn(1);
        when(log.getPlannedHours()).thenReturn(10);
        List<WeeklyLog> logs = new ArrayList<>();
        logs.add(log);
        boolean result = activityDAO.insertWeeklyLogs(connection, logs, 5);
        assertTrue(result);
        verify(preparedStatement).setInt(1, 1);
        verify(preparedStatement).setInt(2, 10);
        verify(preparedStatement).setInt(3, 5);
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void insertWeeklyLogs_EmptyList_ReturnsTrue() throws SQLException, DataOperationException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        boolean result = activityDAO.insertWeeklyLogs(connection, new ArrayList<>(), 5);
        assertTrue(result);
        verify(preparedStatement, never()).executeUpdate();
    }

    @Test
    void insertWeeklyLogs_SQLException_ThrowsDataOperationException() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de insercion"));
        WeeklyLog log = mock(WeeklyLog.class);
        when(log.getWeek()).thenReturn(1);
        when(log.getPlannedHours()).thenReturn(5);
        List<WeeklyLog> logs = new ArrayList<>();
        logs.add(log);
        assertThrows(DataOperationException.class,
                () -> activityDAO.insertWeeklyLogs(connection, logs, 5));
    }

    @Test
    void getActivityById_Found_ReturnsActivity() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("nombre_actividad")).thenReturn("Actividad A");
        when(resultSet.getString("observaciones_actividad")).thenReturn("Observaciones");
        when(resultSet.getInt("id_proyecto")).thenReturn(2);
        Project mockProject = mock(Project.class);
        try (MockedConstruction<ProjectDAO> mockedProjectDAO = mockConstruction(ProjectDAO.class,
                (mock, context) -> when(mock.getProjectById(2)).thenReturn(mockProject))) {
            Activity result = activityDAO.getActivityById(1);
            assertNotNull(result);
            assertEquals("Actividad A", result.getName());
            verify(preparedStatement).setInt(1, 1);
        }
    }

    @Test
    void getActivityById_NotFound_ReturnsNull() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        Activity result = activityDAO.getActivityById(99);
        assertNull(result);
        verify(preparedStatement).setInt(1, 99);
    }

    @Test
    void getActivityById_SQLException_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de lectura"));
        assertThrows(DataOperationException.class, () -> activityDAO.getActivityById(1));
    }

    @Test
    void getActivitiesByProjectId_WithActivities_ReturnsList() throws SQLException, DataOperationException {
        ResultSet listResultSet = mock(ResultSet.class);
        when(listResultSet.next()).thenReturn(true, false);
        when(listResultSet.getInt("id_actividad")).thenReturn(10);
        ResultSet detailResultSet = mock(ResultSet.class);
        when(detailResultSet.next()).thenReturn(true);
        when(detailResultSet.getString("nombre_actividad")).thenReturn("Actividad X");
        when(detailResultSet.getString("observaciones_actividad")).thenReturn("Obs");
        when(detailResultSet.getInt("id_proyecto")).thenReturn(1);
        PreparedStatement detailStatement = mock(PreparedStatement.class);
        when(detailStatement.executeQuery()).thenReturn(detailResultSet);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement).thenReturn(detailStatement);
        when(preparedStatement.executeQuery()).thenReturn(listResultSet);
        Project mockProject = mock(Project.class);
        try (MockedConstruction<ProjectDAO> mockedProjectDAO = mockConstruction(ProjectDAO.class,
                (mock, context) -> when(mock.getProjectById(anyInt())).thenReturn(mockProject))) {
            List<Activity> result = activityDAO.getActivitiesByProjectId(1);
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Actividad X", result.get(0).getName());
        }
    }

    @Test
    void getActivitiesByProjectId_EmptyProject_ReturnsEmptyList() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<Activity> result = activityDAO.getActivitiesByProjectId(1);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getActivitiesByProjectId_SQLException_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de red"));
        assertThrows(DataOperationException.class, () -> activityDAO.getActivitiesByProjectId(1));
    }

    @Test
    void getWeeklyLogById_Found_ReturnsWeeklyLog() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("semana")).thenReturn(3);
        when(resultSet.getInt("horas_planificadas")).thenReturn(20);
        when(resultSet.getInt("id_actividad")).thenReturn(5);
        Connection secondConnection = mock(Connection.class);
        PreparedStatement secondStatement = mock(PreparedStatement.class);
        ResultSet secondResultSet = mock(ResultSet.class);
        when(secondResultSet.next()).thenReturn(true);
        when(secondResultSet.getString("nombre_actividad")).thenReturn("Act");
        when(secondResultSet.getString("observaciones_actividad")).thenReturn("Obs");
        when(secondResultSet.getInt("id_proyecto")).thenReturn(1);
        when(secondStatement.executeQuery()).thenReturn(secondResultSet);
        when(secondConnection.prepareStatement(anyString())).thenReturn(secondStatement);
        databaseConnectionManager.when(DatabaseConnectionManager::getConnection).thenReturn(connection).thenReturn(secondConnection);
        Project mockProject = mock(Project.class);
        try (MockedConstruction<ProjectDAO> mockedProjectDAO = mockConstruction(ProjectDAO.class,
                (mock, context) -> when(mock.getProjectById(anyInt())).thenReturn(mockProject))) {
            WeeklyLog result = activityDAO.getWeeklyLogById(7);
            assertNotNull(result);
            assertEquals(3, result.getWeek());
            assertEquals(20, result.getPlannedHours());
            verify(preparedStatement).setInt(1, 7);
        }
    }

    @Test
    void getWeeklyLogById_NotFound_ReturnsNull() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        WeeklyLog result = activityDAO.getWeeklyLogById(99);
        assertNull(result);
        verify(preparedStatement).setInt(1, 99);
    }

    @Test
    void getWeeklyLogById_SQLException_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de lectura"));
        assertThrows(DataOperationException.class, () -> activityDAO.getWeeklyLogById(1));
    }

    @Test
    void getWeeklyLogsByActivityId_WithLogs_ReturnsList() throws SQLException, DataOperationException {
        ResultSet listResultSet = mock(ResultSet.class);
        when(listResultSet.next()).thenReturn(true, false);
        when(listResultSet.getInt("id_registro")).thenReturn(20);
        Connection secondConnection = mock(Connection.class);
        PreparedStatement detailStatement = mock(PreparedStatement.class);
        ResultSet detailResultSet = mock(ResultSet.class);
        when(detailResultSet.next()).thenReturn(true);
        when(detailResultSet.getInt("semana")).thenReturn(2);
        when(detailResultSet.getInt("horas_planificadas")).thenReturn(15);
        when(detailResultSet.getInt("id_actividad")).thenReturn(5);
        when(detailStatement.executeQuery()).thenReturn(detailResultSet);
        when(secondConnection.prepareStatement(anyString())).thenReturn(detailStatement);
        Connection thirdConnection = mock(Connection.class);
        PreparedStatement activityStatement = mock(PreparedStatement.class);
        ResultSet activityResultSet = mock(ResultSet.class);
        when(activityResultSet.next()).thenReturn(true);
        when(activityResultSet.getString("nombre_actividad")).thenReturn("Act");
        when(activityResultSet.getString("observaciones_actividad")).thenReturn("Obs");
        when(activityResultSet.getInt("id_proyecto")).thenReturn(1);
        when(activityStatement.executeQuery()).thenReturn(activityResultSet);
        when(thirdConnection.prepareStatement(anyString())).thenReturn(activityStatement);
        databaseConnectionManager.when(DatabaseConnectionManager::getConnection).thenReturn(connection).thenReturn(secondConnection).thenReturn(thirdConnection);
        when(preparedStatement.executeQuery()).thenReturn(listResultSet);
        Project mockProject = mock(Project.class);
        try (MockedConstruction<ProjectDAO> mockedProjectDAO = mockConstruction(ProjectDAO.class,
                (mock, context) -> when(mock.getProjectById(anyInt())).thenReturn(mockProject))) {
            List<WeeklyLog> result = activityDAO.getWeeklyLogsByActivityId(5);
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(2, result.get(0).getWeek());
        }
    }

    @Test
    void getWeeklyLogsByActivityId_EmptyActivity_ReturnsEmptyList() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<WeeklyLog> result = activityDAO.getWeeklyLogsByActivityId(5);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getWeeklyLogsByActivityId_SQLException_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de red"));
        assertThrows(DataOperationException.class, () -> activityDAO.getWeeklyLogsByActivityId(5));
    }
}