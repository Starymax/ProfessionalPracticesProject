import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dao.NotificationDAO;
import mx.fei.logic.dto.Notification;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataOperationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NotificationDAOTest {

    private NotificationDAO notificationDAO;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private MockedStatic<DatabaseConnectionManager> databaseConnectionManager;
    private DatabaseConnectionManager mockManager;

    @BeforeEach
    void setUp() throws SQLException {
        notificationDAO = new NotificationDAO();
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);
        mockManager = mock(DatabaseConnectionManager.class);
        databaseConnectionManager = Mockito.mockStatic(DatabaseConnectionManager.class);
        databaseConnectionManager.when(DatabaseConnectionManager::getInstance).thenReturn(mockManager);
        when(mockManager.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    @AfterEach
    void tearDown() {
        if (databaseConnectionManager != null) {
            databaseConnectionManager.close();
        }
    }

    @Test
    void sendNotification_InsertAffectsOneRow_ReturnsTrue() throws SQLException {
        Student student = mock(Student.class);
        when(student.getUserId()).thenReturn(1);
        Notification notification = new Notification(0, "Titulo", "Mensaje", null, false, student);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = notificationDAO.sendNotification(notification);
        assertTrue(result);
    }

    @Test
    void sendNotification_InsertAffectsZeroRows_ReturnsFalse() throws SQLException {
        Student student = mock(Student.class);
        when(student.getUserId()).thenReturn(1);
        Notification notification = new Notification(0, "Titulo", "Mensaje", null, false, student);
        when(preparedStatement.executeUpdate()).thenReturn(0);
        boolean result = notificationDAO.sendNotification(notification);
        assertFalse(result);
    }

    @Test
    void sendNotification_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        Student student = mock(Student.class);
        when(student.getUserId()).thenReturn(1);
        Notification notification = new Notification(0, "Titulo", "Mensaje", null, false, student);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de conexion"));
        assertThrows(DataOperationException.class, () -> notificationDAO.sendNotification(notification));
    }

    @Test
    void getNotificationsByStudentId_StudentHasOneNotification_ReturnsListWithOneNotification() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("id_notificacion")).thenReturn(10);
        when(resultSet.getString("titulo")).thenReturn("Asignacion");
        when(resultSet.getString("mensaje")).thenReturn("Fue asignado");
        when(resultSet.getTimestamp("fecha_emision")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(resultSet.getBoolean("leida")).thenReturn(false);
        List<Notification> result = notificationDAO.getNotificationsByStudentId(1);
        assertEquals(1, result.size());
    }

    @Test
    void getNotificationsByStudentId_StudentHasOneNotification_ReturnsNotificationWithExpectedTitle() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("id_notificacion")).thenReturn(7);
        when(resultSet.getString("titulo")).thenReturn("Proyecto asignado");
        when(resultSet.getString("mensaje")).thenReturn("Se le asigno un proyecto");
        when(resultSet.getTimestamp("fecha_emision")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(resultSet.getBoolean("leida")).thenReturn(true);
        List<Notification> result = notificationDAO.getNotificationsByStudentId(2);
        assertEquals("Proyecto asignado", result.get(0).getTitle());
    }

    @Test
    void getNotificationsByStudentId_ReadNotification_ReturnsNotificationMarkedAsRead() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("id_notificacion")).thenReturn(3);
        when(resultSet.getString("titulo")).thenReturn("Aviso");
        when(resultSet.getString("mensaje")).thenReturn("Mensaje de prueba");
        when(resultSet.getTimestamp("fecha_emision")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(resultSet.getBoolean("leida")).thenReturn(true);
        List<Notification> result = notificationDAO.getNotificationsByStudentId(1);
        assertTrue(result.get(0).isRead());
    }

    @Test
    void getNotificationsByStudentId_StudentHasNoNotifications_ReturnsEmptyList() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<Notification> result = notificationDAO.getNotificationsByStudentId(1);
        assertTrue(result.isEmpty());
    }

    @Test
    void getNotificationsByStudentId_StudentHasNoNotifications_ReturnsNonNullList() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<Notification> result = notificationDAO.getNotificationsByStudentId(1);
        assertNotNull(result);
    }

    @Test
    void getNotificationsByStudentId_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de red"));
        assertThrows(DataOperationException.class, () -> notificationDAO.getNotificationsByStudentId(1));
    }

    @Test
    void markAsRead_UpdateAffectsOneRow_ReturnsTrue() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = notificationDAO.markAsRead(5);
        assertTrue(result);
    }

    @Test
    void markAsRead_UpdateAffectsZeroRows_ReturnsFalse() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(0);
        boolean result = notificationDAO.markAsRead(999);
        assertFalse(result);
    }

    @Test
    void markAsRead_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de escritura"));
        assertThrows(DataOperationException.class, () -> notificationDAO.markAsRead(5));
    }

    @Test
    void countUnreadNotifications_StudentHasThreeUnread_ReturnsThree() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(3);
        int result = notificationDAO.countUnreadNotifications(1);
        assertEquals(3, result);
    }

    @Test
    void countUnreadNotifications_StudentHasNoUnread_ReturnsZero() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        int result = notificationDAO.countUnreadNotifications(1);
        assertEquals(0, result);
    }

    @Test
    void countUnreadNotifications_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de conexion"));
        assertThrows(DataOperationException.class, () -> notificationDAO.countUnreadNotifications(1));
    }
}
