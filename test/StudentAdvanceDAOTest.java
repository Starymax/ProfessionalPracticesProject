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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mockConstruction;

public class StudentAdvanceDAOTest {

    private StudentAdvanceDAO studentAdvanceDAO;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private MockedStatic<DatabaseConnectionManager> databaseConnectionManager;

    @BeforeEach
    void setUp() throws SQLException {
        studentAdvanceDAO = new StudentAdvanceDAO();
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);
        databaseConnectionManager = Mockito.mockStatic(DatabaseConnectionManager.class);
        databaseConnectionManager.when(DatabaseConnectionManager::getConnection).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    @AfterEach
    void tearDown() {
        if (databaseConnectionManager != null) {
            databaseConnectionManager.close();
        }
    }

    @Test
    void createAdvance_AdvanceNull_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> studentAdvanceDAO.createAdvance(null));
    }

    @Test
    void createAdvance_Successful_ReturnsTrue() throws SQLException, DataOperationException {
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
        verify(preparedStatement).setFloat(1, 5f);
        verify(preparedStatement).setInt(2, 1);
        verify(preparedStatement).setInt(3, 10);
    }

    @Test
    void createAdvance_SQLException_ThrowsDataOperationException() throws SQLException {
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
    void getAdvanceById_Found_ReturnsStudentAdvance() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getFloat("horas_realizadas")).thenReturn(8f);
        when(resultSet.getInt("id_registro")).thenReturn(3);
        when(resultSet.getInt("id_alumno")).thenReturn(5);
        WeeklyLog mockLog = mock(WeeklyLog.class);
        Student mockStudent = mock(Student.class);
        try (MockedConstruction<ActivityDAO> mockedActivityDAO = mockConstruction(ActivityDAO.class,
                (mock, context) -> when(mock.getWeeklyLogById(3)).thenReturn(mockLog));
             MockedConstruction<StudentDAO> mockedStudentDAO = mockConstruction(StudentDAO.class,
                     (mock, context) -> when(mock.getStudentById(5)).thenReturn(mockStudent))) {
            StudentAdvance result = studentAdvanceDAO.getAdvanceById(1);
            assertNotNull(result);
            assertEquals(8f, result.getRealizedHours());
            verify(preparedStatement).setInt(1, 1);
        }
    }

    @Test
    void getAdvanceById_NotFound_ReturnsNull() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        StudentAdvance result = studentAdvanceDAO.getAdvanceById(99);
        assertNull(result);
        verify(preparedStatement).setInt(1, 99);
    }

    @Test
    void getAdvanceById_SQLException_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de lectura"));
        assertThrows(DataOperationException.class, () -> studentAdvanceDAO.getAdvanceById(1));
    }

    @Test
    void getAdvancesByStudentId_WithAdvances_ReturnsList() throws SQLException, DataOperationException {
        ResultSet listResultSet = mock(ResultSet.class);
        when(listResultSet.next()).thenReturn(true, false);
        when(listResultSet.getInt("id_avance")).thenReturn(7);
        Connection secondConnection = mock(Connection.class);
        PreparedStatement detailStatement = mock(PreparedStatement.class);
        ResultSet detailResultSet = mock(ResultSet.class);
        when(detailResultSet.next()).thenReturn(true);
        when(detailResultSet.getFloat("horas_realizadas")).thenReturn(4f);
        when(detailResultSet.getInt("id_registro")).thenReturn(2);
        when(detailResultSet.getInt("id_alumno")).thenReturn(10);
        when(detailStatement.executeQuery()).thenReturn(detailResultSet);
        when(secondConnection.prepareStatement(anyString())).thenReturn(detailStatement);
        databaseConnectionManager.when(DatabaseConnectionManager::getConnection).thenReturn(connection).thenReturn(secondConnection);
        when(preparedStatement.executeQuery()).thenReturn(listResultSet);
        WeeklyLog mockLog = mock(WeeklyLog.class);
        Student mockStudent = mock(Student.class);
        try (MockedConstruction<ActivityDAO> mockedActivityDAO = mockConstruction(ActivityDAO.class,
                (mock, context) -> when(mock.getWeeklyLogById(anyInt())).thenReturn(mockLog));
             MockedConstruction<StudentDAO> mockedStudentDAO = mockConstruction(StudentDAO.class,
                     (mock, context) -> when(mock.getStudentById(anyInt())).thenReturn(mockStudent))) {
            List<StudentAdvance> result = studentAdvanceDAO.getAdvancesByStudentId(10);
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(preparedStatement).setInt(1, 10);
        }
    }

    @Test
    void getAdvancesByStudentId_NoAdvances_ReturnsEmptyList() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<StudentAdvance> result = studentAdvanceDAO.getAdvancesByStudentId(10);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAdvancesByStudentId_SQLException_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de red"));
        assertThrows(DataOperationException.class, () -> studentAdvanceDAO.getAdvancesByStudentId(10));
    }

    @Test
    void getAdvancesByWeeklyLogId_WithAdvances_ReturnsList() throws SQLException, DataOperationException {
        ResultSet listResultSet = mock(ResultSet.class);
        when(listResultSet.next()).thenReturn(true, false);
        when(listResultSet.getInt("id_avance")).thenReturn(9);
        Connection secondConnection = mock(Connection.class);
        PreparedStatement detailStatement = mock(PreparedStatement.class);
        ResultSet detailResultSet = mock(ResultSet.class);
        when(detailResultSet.next()).thenReturn(true);
        when(detailResultSet.getFloat("horas_realizadas")).thenReturn(6f);
        when(detailResultSet.getInt("id_registro")).thenReturn(3);
        when(detailResultSet.getInt("id_alumno")).thenReturn(11);
        when(detailStatement.executeQuery()).thenReturn(detailResultSet);
        when(secondConnection.prepareStatement(anyString())).thenReturn(detailStatement);
        databaseConnectionManager.when(DatabaseConnectionManager::getConnection).thenReturn(connection).thenReturn(secondConnection);
        when(preparedStatement.executeQuery()).thenReturn(listResultSet);
        WeeklyLog mockLog = mock(WeeklyLog.class);
        Student mockStudent = mock(Student.class);
        try (MockedConstruction<ActivityDAO> mockedActivityDAO = mockConstruction(ActivityDAO.class,
                (mock, context) -> when(mock.getWeeklyLogById(anyInt())).thenReturn(mockLog));
             MockedConstruction<StudentDAO> mockedStudentDAO = mockConstruction(StudentDAO.class,
                     (mock, context) -> when(mock.getStudentById(anyInt())).thenReturn(mockStudent))) {
            List<StudentAdvance> result = studentAdvanceDAO.getAdvancesByWeeklyLogId(3);
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(preparedStatement).setInt(1, 3);
        }
    }

    @Test
    void getAdvancesByWeeklyLogId_NoAdvances_ReturnsEmptyList() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<StudentAdvance> result = studentAdvanceDAO.getAdvancesByWeeklyLogId(3);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAdvancesByWeeklyLogId_SQLException_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de red"));
        assertThrows(DataOperationException.class, () -> studentAdvanceDAO.getAdvancesByWeeklyLogId(3));
    }

    @Test
    void updateRealizedHours_Successful_ReturnsTrue() throws SQLException, DataOperationException {
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = studentAdvanceDAO.updateRealizedHours(1, 10f);
        assertTrue(result);
        verify(preparedStatement).setFloat(1, 10f);
        verify(preparedStatement).setInt(2, 1);
    }

    @Test
    void updateRealizedHours_SQLException_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de escritura"));
        assertThrows(DataOperationException.class, () -> studentAdvanceDAO.updateRealizedHours(1, 10f));
    }

    @Test
    void getAdvancesByStudentAndWeeklyLog_WithAdvances_ReturnsList() throws SQLException, DataOperationException {
        ResultSet listResultSet = mock(ResultSet.class);
        when(listResultSet.next()).thenReturn(true, false);
        when(listResultSet.getInt("id_avance")).thenReturn(15);
        Connection secondConnection = mock(Connection.class);
        PreparedStatement detailStatement = mock(PreparedStatement.class);
        ResultSet detailResultSet = mock(ResultSet.class);
        when(detailResultSet.next()).thenReturn(true);
        when(detailResultSet.getFloat("horas_realizadas")).thenReturn(3f);
        when(detailResultSet.getInt("id_registro")).thenReturn(4);
        when(detailResultSet.getInt("id_alumno")).thenReturn(12);
        when(detailStatement.executeQuery()).thenReturn(detailResultSet);
        when(secondConnection.prepareStatement(anyString())).thenReturn(detailStatement);
        databaseConnectionManager.when(DatabaseConnectionManager::getConnection).thenReturn(connection).thenReturn(secondConnection);
        when(preparedStatement.executeQuery()).thenReturn(listResultSet);
        WeeklyLog mockLog = mock(WeeklyLog.class);
        Student mockStudent = mock(Student.class);
        try (MockedConstruction<ActivityDAO> mockedActivityDAO = mockConstruction(ActivityDAO.class,
                (mock, context) -> when(mock.getWeeklyLogById(anyInt())).thenReturn(mockLog));
             MockedConstruction<StudentDAO> mockedStudentDAO = mockConstruction(StudentDAO.class,
                     (mock, context) -> when(mock.getStudentById(anyInt())).thenReturn(mockStudent))) {
            List<StudentAdvance> result = studentAdvanceDAO.getAdvancesByStudentAndWeeklyLog(12, 4);
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(preparedStatement).setInt(1, 12);
            verify(preparedStatement).setInt(2, 4);
        }
    }

    @Test
    void getAdvancesByStudentAndWeeklyLog_NoAdvances_ReturnsEmptyList() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<StudentAdvance> result = studentAdvanceDAO.getAdvancesByStudentAndWeeklyLog(12, 4);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAdvancesByStudentAndWeeklyLog_SQLException_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de red"));
        assertThrows(DataOperationException.class, () -> studentAdvanceDAO.getAdvancesByStudentAndWeeklyLog(12, 4));
    }
}