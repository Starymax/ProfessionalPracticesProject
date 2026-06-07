import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dao.ActivityDAO;
import mx.fei.logic.dao.StudentAdvanceDAO;
import mx.fei.logic.dao.StudentDAO;
import mx.fei.logic.dto.Student;
import mx.fei.logic.dto.StudentAdvance;
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

public class StudentAdvanceDAOTest {

    private StudentAdvanceDAO studentAdvanceDAO;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private MockedStatic<DatabaseConnectionManager> databaseConnectionManager;
    private DatabaseConnectionManager mockManager;

    @BeforeEach
    void setUp() throws SQLException {
        studentAdvanceDAO = new StudentAdvanceDAO();
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
    void createAdvance_AdvanceIsNull_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> studentAdvanceDAO.createAdvance(null));
    }

    @Test
    void createAdvance_InsertAffectsOneRow_ReturnsTrue() throws SQLException, DataOperationException {
        WeeklyLog weeklyLog = mock(WeeklyLog.class);
        when(weeklyLog.getWeeklyLogId()).thenReturn(1);
        Student student = mock(Student.class);
        when(student.getUserId()).thenReturn(10);
        StudentAdvance advance = mock(StudentAdvance.class);
        when(advance.getRealizedHours()).thenReturn(5f);
        when(advance.getWeeklyLog()).thenReturn(weeklyLog);
        when(advance.getStudent()).thenReturn(student);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = studentAdvanceDAO.createAdvance(advance);
        assertTrue(result);
    }

    @Test
    void createAdvance_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        WeeklyLog weeklyLog = mock(WeeklyLog.class);
        when(weeklyLog.getWeeklyLogId()).thenReturn(1);
        Student student = mock(Student.class);
        when(student.getUserId()).thenReturn(10);
        StudentAdvance advance = mock(StudentAdvance.class);
        when(advance.getWeeklyLog()).thenReturn(weeklyLog);
        when(advance.getStudent()).thenReturn(student);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de insercion"));
        assertThrows(DataOperationException.class, () -> studentAdvanceDAO.createAdvance(advance));
    }

    @Test
    void getAdvanceById_AdvanceExists_ReturnsAdvanceWithExpectedRealizedHours() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getFloat("horas_realizadas")).thenReturn(8f);
        when(resultSet.getInt("id_registro")).thenReturn(3);
        when(resultSet.getInt("id_alumno")).thenReturn(5);
        WeeklyLog weeklyLog = mock(WeeklyLog.class);
        Student student = mock(Student.class);
        try (MockedConstruction<ActivityDAO> mockedActivityDAO = mockConstruction(ActivityDAO.class,
                (mock, context) -> when(mock.getWeeklyLogById(3)).thenReturn(weeklyLog));
             MockedConstruction<StudentDAO> mockedStudentDAO = mockConstruction(StudentDAO.class,
                     (mock, context) -> when(mock.getStudentById(5)).thenReturn(student))) {
            StudentAdvance result = studentAdvanceDAO.getAdvanceById(1);
            assertEquals(8f, result.getRealizedHours());
        }
    }

    @Test
    void getAdvanceById_AdvanceDoesNotExist_ReturnsNull() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        StudentAdvance result = studentAdvanceDAO.getAdvanceById(99);
        assertNull(result);
    }

    @Test
    void getAdvanceById_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de lectura"));
        assertThrows(DataOperationException.class, () -> studentAdvanceDAO.getAdvanceById(1));
    }

    @Test
    void getAdvancesByStudentId_StudentHasOneAdvance_ReturnsListWithOneAdvance() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("id_avance")).thenReturn(7);
        when(resultSet.getFloat("horas_realizadas")).thenReturn(4f);
        when(resultSet.getInt("id_registro")).thenReturn(2);
        when(resultSet.getInt("id_alumno")).thenReturn(10);
        WeeklyLog weeklyLog = mock(WeeklyLog.class);
        Student student = mock(Student.class);
        try (MockedConstruction<ActivityDAO> mockedActivityDAO = mockConstruction(ActivityDAO.class,
                (mock, context) -> when(mock.getWeeklyLogById(anyInt())).thenReturn(weeklyLog));
             MockedConstruction<StudentDAO> mockedStudentDAO = mockConstruction(StudentDAO.class,
                     (mock, context) -> when(mock.getStudentById(anyInt())).thenReturn(student))) {
            List<StudentAdvance> result = studentAdvanceDAO.getAdvancesByStudentId(10);
            assertEquals(1, result.size());
        }
    }

    @Test
    void getAdvancesByStudentId_StudentHasNoAdvances_ReturnsEmptyList() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<StudentAdvance> result = studentAdvanceDAO.getAdvancesByStudentId(10);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAdvancesByStudentId_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de red"));
        assertThrows(DataOperationException.class, () -> studentAdvanceDAO.getAdvancesByStudentId(10));
    }

    @Test
    void getAdvancesByWeeklyLogId_WeeklyLogHasOneAdvance_ReturnsListWithOneAdvance() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("id_avance")).thenReturn(9);
        when(resultSet.getFloat("horas_realizadas")).thenReturn(6f);
        when(resultSet.getInt("id_registro")).thenReturn(3);
        when(resultSet.getInt("id_alumno")).thenReturn(11);
        WeeklyLog weeklyLog = mock(WeeklyLog.class);
        Student student = mock(Student.class);
        try (MockedConstruction<ActivityDAO> mockedActivityDAO = mockConstruction(ActivityDAO.class,
                (mock, context) -> when(mock.getWeeklyLogById(anyInt())).thenReturn(weeklyLog));
             MockedConstruction<StudentDAO> mockedStudentDAO = mockConstruction(StudentDAO.class,
                     (mock, context) -> when(mock.getStudentById(anyInt())).thenReturn(student))) {
            List<StudentAdvance> result = studentAdvanceDAO.getAdvancesByWeeklyLogId(3);
            assertEquals(1, result.size());
        }
    }

    @Test
    void getAdvancesByWeeklyLogId_WeeklyLogHasNoAdvances_ReturnsEmptyList() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<StudentAdvance> result = studentAdvanceDAO.getAdvancesByWeeklyLogId(3);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAdvancesByWeeklyLogId_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de red"));
        assertThrows(DataOperationException.class, () -> studentAdvanceDAO.getAdvancesByWeeklyLogId(3));
    }

    @Test
    void updateRealizedHours_UpdateAffectsOneRow_ReturnsTrue() throws SQLException, DataOperationException {
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = studentAdvanceDAO.updateRealizedHours(1, 10f);
        assertTrue(result);
    }

    @Test
    void updateRealizedHours_UpdateAffectsZeroRows_ReturnsFalse() throws SQLException, DataOperationException {
        when(preparedStatement.executeUpdate()).thenReturn(0);
        boolean result = studentAdvanceDAO.updateRealizedHours(1, 10f);
        assertFalse(result);
    }

    @Test
    void updateRealizedHours_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de escritura"));
        assertThrows(DataOperationException.class, () -> studentAdvanceDAO.updateRealizedHours(1, 10f));
    }

    @Test
    void getAdvancesByStudentAndWeeklyLog_HasOneAdvance_ReturnsListWithOneAdvance() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("id_avance")).thenReturn(15);
        when(resultSet.getFloat("horas_realizadas")).thenReturn(3f);
        WeeklyLog weeklyLog = mock(WeeklyLog.class);
        Student student = mock(Student.class);
        try (MockedConstruction<ActivityDAO> mockedActivityDAO = mockConstruction(ActivityDAO.class,
                (mock, context) -> when(mock.getWeeklyLogById(anyInt())).thenReturn(weeklyLog));
             MockedConstruction<StudentDAO> mockedStudentDAO = mockConstruction(StudentDAO.class,
                     (mock, context) -> when(mock.getStudentById(anyInt())).thenReturn(student))) {
            List<StudentAdvance> result = studentAdvanceDAO.getAdvancesByStudentAndWeeklyLog(12, 4);
            assertEquals(1, result.size());
        }
    }

    @Test
    void getAdvancesByStudentAndWeeklyLog_HasNoAdvances_ReturnsEmptyList() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<StudentAdvance> result = studentAdvanceDAO.getAdvancesByStudentAndWeeklyLog(12, 4);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAdvancesByStudentAndWeeklyLog_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de red"));
        assertThrows(DataOperationException.class, () -> studentAdvanceDAO.getAdvancesByStudentAndWeeklyLog(12, 4));
    }

    @Test
    void getTotalHoursByIdStudent_StudentHasRegisteredHours_ReturnsTotalHours() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getFloat(1)).thenReturn(120f);
        float result = studentAdvanceDAO.getTotalHoursByIdStudent(10);
        assertEquals(120f, result);
    }

    @Test
    void getTotalHoursByIdStudent_StudentHasNoRegisteredHours_ReturnsZero() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        float result = studentAdvanceDAO.getTotalHoursByIdStudent(10);
        assertEquals(0f, result);
    }

    @Test
    void getTotalHoursByIdStudent_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de red"));
        assertThrows(DataOperationException.class, () -> studentAdvanceDAO.getTotalHoursByIdStudent(10));
    }
}
