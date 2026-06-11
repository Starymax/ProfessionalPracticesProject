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
    void createAdvance_InsertAffectsOneRow_ReturnsTrue() throws SQLException {
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
    void getAdvanceById_AdvanceExists_ReturnsExpectedAdvance() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getFloat("horas_realizadas")).thenReturn(8f);
        when(resultSet.getInt("id_registro")).thenReturn(3);
        when(resultSet.getInt("id_alumno")).thenReturn(5);
        WeeklyLog weeklyLog = mock(WeeklyLog.class);
        Student student = mock(Student.class);
        StudentAdvance expectedAdvance = new StudentAdvance(1, 8f, weeklyLog, student);
        try (MockedConstruction<ActivityDAO> mockedActivityDAO = mockConstruction(ActivityDAO.class, (mock, context) -> when(mock.getWeeklyLogById(3)).thenReturn(weeklyLog));
             MockedConstruction<StudentDAO> mockedStudentDAO = mockConstruction(StudentDAO.class, (mock, context) -> when(mock.getStudentById(5)).thenReturn(student))) {
            StudentAdvance result = studentAdvanceDAO.getAdvanceById(1);
            assertEquals(expectedAdvance, result);
        }
    }

    @Test
    void getAdvanceById_AdvanceDoesNotExist_ReturnsNull() throws SQLException {
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
    void getAdvancesByStudentId_StudentHasTwoAdvances_ReturnsListWithTwoAdvances() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getInt("id_avance")).thenReturn(7, 8);
        when(resultSet.getFloat("horas_realizadas")).thenReturn(4f, 5f);
        when(resultSet.getInt("id_registro")).thenReturn(2, 3);
        when(resultSet.getInt("id_alumno")).thenReturn(10, 10);
        WeeklyLog weeklyLog = mock(WeeklyLog.class);
        Student student = mock(Student.class);
        StudentAdvance expectedAdvance1 = new StudentAdvance(7, 4f, weeklyLog, student);
        StudentAdvance expectedAdvance2 = new StudentAdvance(8, 5f, weeklyLog, student);
        try (MockedConstruction<ActivityDAO> mockedActivityDAO = mockConstruction(ActivityDAO.class,
                (mock, context) -> when(mock.getWeeklyLogById(anyInt())).thenReturn(weeklyLog));
             MockedConstruction<StudentDAO> mockedStudentDAO = mockConstruction(StudentDAO.class,
                     (mock, context) -> when(mock.getStudentById(anyInt())).thenReturn(student))) {
            List<StudentAdvance> result = studentAdvanceDAO.getAdvancesByStudentId(10);
            assertEquals(List.of(expectedAdvance1, expectedAdvance2), result);
        }
    }

    @Test
    void getAdvancesByStudentId_StudentHasNoAdvances_ReturnsEmptyList() throws SQLException {
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
    void getAdvancesByWeeklyLogId_WeeklyLogHasTwoAdvances_ReturnsListWithTwoAdvances() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getInt("id_avance")).thenReturn(9, 10);
        when(resultSet.getFloat("horas_realizadas")).thenReturn(6f, 7f);
        when(resultSet.getInt("id_registro")).thenReturn(3, 3);
        when(resultSet.getInt("id_alumno")).thenReturn(11, 12);
        WeeklyLog weeklyLog = mock(WeeklyLog.class);
        Student student = mock(Student.class);
        StudentAdvance expectedAdvance1 = new StudentAdvance(9, 6f, weeklyLog, student);
        StudentAdvance expectedAdvance2 = new StudentAdvance(10, 7f, weeklyLog, student);
        try (MockedConstruction<ActivityDAO> mockedActivityDAO = mockConstruction(ActivityDAO.class, (mock, context) -> when(mock.getWeeklyLogById(anyInt())).thenReturn(weeklyLog));
             MockedConstruction<StudentDAO> mockedStudentDAO = mockConstruction(StudentDAO.class, (mock, context) -> when(mock.getStudentById(anyInt())).thenReturn(student))) {
            List<StudentAdvance> result = studentAdvanceDAO.getAdvancesByWeeklyLogId(3);
            assertEquals(List.of(expectedAdvance1, expectedAdvance2), result);
        }
    }

    @Test
    void getAdvancesByWeeklyLogId_WeeklyLogHasNoAdvances_ReturnsEmptyList() throws SQLException {
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
    void updateRealizedHours_UpdateAffectsOneRow_ReturnsTrue() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = studentAdvanceDAO.updateRealizedHours(1, 10f);
        assertTrue(result);
    }

    @Test
    void updateRealizedHours_UpdateAffectsZeroRows_ReturnsFalse() throws SQLException {
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
    void getAdvancesByStudentAndWeeklyLog_HasTwoAdvances_ReturnsListWithTwoAdvances() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getInt("id_avance")).thenReturn(15, 16);
        when(resultSet.getFloat("horas_realizadas")).thenReturn(3f, 2f);
        WeeklyLog weeklyLog = mock(WeeklyLog.class);
        Student student = mock(Student.class);
        StudentAdvance expectedAdvance1 = new StudentAdvance(15, 3f, weeklyLog, student);
        StudentAdvance expectedAdvance2 = new StudentAdvance(16, 2f, weeklyLog, student);
        try (MockedConstruction<ActivityDAO> mockedActivityDAO = mockConstruction(ActivityDAO.class, (mock, context) -> when(mock.getWeeklyLogById(anyInt())).thenReturn(weeklyLog));
             MockedConstruction<StudentDAO> mockedStudentDAO = mockConstruction(StudentDAO.class, (mock, context) -> when(mock.getStudentById(anyInt())).thenReturn(student))) {
            List<StudentAdvance> result = studentAdvanceDAO.getAdvancesByStudentAndWeeklyLog(12, 4);
            assertEquals(List.of(expectedAdvance1, expectedAdvance2), result);
        }
    }

    @Test
    void getAdvancesByStudentAndWeeklyLog_HasNoAdvances_ReturnsEmptyList() throws SQLException {
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
    void getTotalHoursByIdStudent_StudentHasRegisteredHours_ReturnsTotalHours() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getFloat(1)).thenReturn(120f);
        float result = studentAdvanceDAO.getTotalHoursByIdStudent(10);
        assertEquals(120f, result);
    }

    @Test
    void getTotalHoursByIdStudent_StudentHasNoRegisteredHours_ReturnsZero() throws SQLException {
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
