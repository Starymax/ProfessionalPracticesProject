import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dao.StudentDAO;
import mx.fei.logic.dao.UserDAO;
import mx.fei.logic.dto.Professor;
import mx.fei.logic.dto.Student;
import mx.fei.logic.dto.User;
import mx.fei.logic.dto.UserRole;
import mx.fei.logic.exceptions.DataOperationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.doReturn;

public class UserDAOTest {
    private UserDAO userDAO;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private MockedStatic<DatabaseConnectionManager> databaseConnectionManager;

    @BeforeEach
    public void setUp() throws SQLException {
        userDAO = new UserDAO();
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
    void userExist_UserExists_ReturnsTrue() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        boolean result = userDAO.userExist(1);
        assertTrue(result);
    }

    @Test
    void userExist_UserDoesNotExist_ReturnsFalse() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        boolean result = userDAO.userExist(400);
        assertFalse(result);
    }

    @Test
    void userExist_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de lectura"));
        assertThrows(DataOperationException.class, () -> userDAO.userExist(400));
    }

    @Test
    void registerUser_UserIsNull_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> userDAO.registerUser(null));
    }

    @Test
    void registerUser_InsertGeneratesKey_ReturnsGeneratedId() throws SQLException {
        Student student = mock(Student.class);
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(50);
        int result = userDAO.registerUser(student);
        assertEquals(50, result);
    }

    @Test
    void registerUser_InsertGeneratesNoKey_ReturnsMinusOne() throws SQLException {
        Professor professor = mock(Professor.class);
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(preparedStatement);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        int result = userDAO.registerUser(professor);
        assertEquals(-1, result);
    }

    @Test
    void registerUser_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        User user = mock(User.class);
        when(connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de inserción"));
        assertThrows(DataOperationException.class, () -> userDAO.registerUser(user));
    }

    @Test
    void updateUser_UserIsNull_ReturnsFalse() throws SQLException {
        boolean result = userDAO.updateUser(null);
        assertFalse(result);
    }

    @Test
    void updateUser_UpdateAffectsOneRow_ReturnsTrue() throws SQLException {
        Student student = mock(Student.class);
        when(student.getUserId()).thenReturn(1);
        when(student.getName()).thenReturn("Carlos");
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = userDAO.updateUser(student);
        assertTrue(result);
    }

    @Test
    void updateUser_UpdateAffectsZeroRows_ReturnsFalse() throws SQLException {
        Student student = mock(Student.class);
        when(student.getUserId()).thenReturn(999);
        when(preparedStatement.executeUpdate()).thenReturn(0);
        boolean result = userDAO.updateUser(student);
        assertFalse(result);
    }

    @Test
    void updateUser_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        Student student = mock(Student.class);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de red"));
        assertThrows(DataOperationException.class, () -> userDAO.updateUser(student));
    }

    @Test
    void getUserByEmail_EmailIsEmpty_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> userDAO.getUserByEmail(""));
    }

    @Test
    void getUserByEmail_EmailIsNull_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> userDAO.getUserByEmail(null));
    }

    @Test
    void getUserByEmail_UserDoesNotExist_ThrowsNoSuchElementException() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        assertThrows(NoSuchElementException.class, () -> userDAO.getUserByEmail("test@uv.mx"));
    }

    @Test
    void getUserByEmail_UserIsStudent_ReturnsStudent() throws SQLException {
        String email = "estudiante@uv.mx";
        int studentId = 10;
        Student student = mock(Student.class);
        try (MockedConstruction<StudentDAO> mockedStudentDAO = mockConstruction(StudentDAO.class, (mock, context) -> when(mock.getStudentById(studentId)).thenReturn(student))) {
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true);
            when(resultSet.getInt("id_usuario")).thenReturn(studentId);
            UserDAO userDAO1 = spy(userDAO);
            doReturn(true).when(userDAO1).isStudent(studentId);
            User result = userDAO1.getUserByEmail(email);
            assertEquals(student, result);
        }
    }

    @Test
    void getUserByEmail_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Timeout"));
        assertThrows(DataOperationException.class, () -> userDAO.getUserByEmail("error@test.com"));
    }

    @Test
    void isStudent_UserIsStudent_ReturnsTrue() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);
        boolean result = userDAO.isStudent(10);
        assertTrue(result);
    }

    @Test
    void isStudent_UserIsNotStudent_ReturnsFalse() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(0);
        boolean result = userDAO.isStudent(20);
        assertFalse(result);
    }

    @Test
    void isStudent_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de lectura"));
        assertThrows(DataOperationException.class, () -> userDAO.isStudent(1));
    }

    @Test
    void logInByRole_PropertiesLoadSucceeds_DoesNotThrow() {
        UserRole role = mock(UserRole.class);
        when(role.getPropertiesKey()).thenReturn("admin_config");
        assertDoesNotThrow(() -> userDAO.logInByRole(role));
    }

    @Test
    void logInByRole_PropertiesLoadThrowsIOException_ThrowsDataOperationException() {
        UserRole role = mock(UserRole.class);
        when(role.getPropertiesKey()).thenReturn("root_config");
        databaseConnectionManager.when(() -> DatabaseConnectionManager.loadProperties("root_config")).thenThrow(new IOException("Archivo no encontrado"));
        assertThrows(DataOperationException.class, () -> userDAO.logInByRole(role));
    }
}
