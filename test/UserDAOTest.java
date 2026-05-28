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
import java.sql.*;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class UserDAOTest {
    private UserDAO userDAO;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private MockedStatic<DatabaseConnectionManager>  databaseConnectionManager;

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
    void userExist_UserFound_ReturnsTrue() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        boolean result = userDAO.userExist(1);
        assertTrue(result);
        verify(preparedStatement).setInt(1, 1);
    }

    @Test
    void userExist_UserNotFound_ReturnsFalse() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        boolean result = userDAO.userExist(400);
        assertFalse(result);
        verify(preparedStatement).setInt(1, 400);
    }

    @Test
    void userExist_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(SQLException.class);
        DataOperationException dataOperationException = assertThrows(DataOperationException.class, () -> userDAO.userExist(400));
        assertEquals(dataOperationException.getMessage(), dataOperationException.getMessage());
    }

    @Test
    void registerUser_UserNull_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            userDAO.registerUser(null);
        });
    }

    @Test
    void registerUser_Successful_ReturnsGeneratedId() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        Student student = mock(Student.class);
        student.setName("Diego");
        student.setLastName("Perez");
        student.setEmail("diego@example.com");
        student.setPassword("password123");
        student.setActiveStatus(true);
        student.setGender("M");
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(50);
        int result = userDAO.registerUser(student);
        assertEquals(50, result);
    }

    @Test
    void registerUser_NoKeysGenerated_ReturnsMinusOne() throws SQLException {
        Professor user = mock(Professor.class);
        user.setName("Test");
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(preparedStatement);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        int result = userDAO.registerUser(user);
        assertEquals(-1, result);
    }

    @Test
    void registerUser_SQLException_ThrowsDataOperationException() throws SQLException {
        User user = mock(User.class);
        user.setName("Error User");
        when(connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de inserción"));
        assertThrows(DataOperationException.class, () -> {userDAO.registerUser(user);});
    }

    @Test
    void updateUser_UserNull_ReturnsFalse() throws SQLException {
        boolean result = userDAO.updateUser(null);
        assertFalse(result);
    }

    @Test
    void updateUser_Successful_ReturnsTrue() throws SQLException {
        Student student = mock(Student.class);
        when(student.getUserId()).thenReturn(1);
        when(student.getName()).thenReturn("Carlos");
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = userDAO.updateUser(student);
        assertTrue(result);
        verify(preparedStatement).setInt(7, 1);
        verify(preparedStatement).setString(1, "Carlos");
    }

    @Test
    void updateUser_UserNotFound_ReturnsFalse() throws SQLException {
        Student mockStudent = mock(Student.class);
        when(mockStudent.getUserId()).thenReturn(999);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);
        boolean result = userDAO.updateUser(mockStudent);
        assertFalse(result);
    }

    @Test
    void updateUser_SQLException_ThrowsDataOperationException() throws SQLException {
        Student student = mock(Student.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de red"));
        assertThrows(DataOperationException.class, () -> {userDAO.updateUser(student);});
    }

    @Test
    void getUserByEmail_EmailInvalid_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> userDAO.getUserByEmail(""));
        assertThrows(IllegalArgumentException.class, () -> userDAO.getUserByEmail(null));
    }

    @Test
    void getUserByEmail_NotFound_ThrowsNoSuchElementException() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        assertThrows(NoSuchElementException.class, () -> {userDAO.getUserByEmail("test@uv.mx");});
    }

    @Test
    void getUserByEmail_ReturnsStudent() throws SQLException {
        String email = "estudiante@uv.mx";
        int studentId = 10;
        Student mockStudent = mock(Student.class);
        try (MockedConstruction<StudentDAO> mocked = mockConstruction(StudentDAO.class, (mock, context) -> {when(mock.getStudentById(studentId)).thenReturn(mockStudent);})) {
            when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
            when(preparedStatement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true);
            when(resultSet.getInt("id_usuario")).thenReturn(studentId);
            UserDAO spyDAO = spy(userDAO);
            doReturn(true).when(spyDAO).isStudent(studentId);
            User result = spyDAO.getUserByEmail(email);
            assertEquals(mockStudent, result);
        }
    }

    @Test
    void getUserByEmail_SQLException_ThrowsDataOperationException() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Timeout"));
        assertThrows(DataOperationException.class, () -> {userDAO.getUserByEmail("error@test.com");});
    }

    @Test
    void isStudent_Exists_ReturnsTrue() throws SQLException {
        int studentId = 10;
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);
        boolean result = userDAO.isStudent(studentId);
        assertTrue(result);
        verify(preparedStatement).setInt(1, studentId);
    }

    @Test
    void isStudent_NotExists_ReturnsFalse() throws SQLException {
        int studentId = 20;
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(0);
        boolean result = userDAO.isStudent(studentId);
        assertFalse(result);
        verify(preparedStatement).setInt(1, studentId);
    }

    @Test
    void isStudent_SQLException_ThrowsDataOperationException() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de lectura"));
        assertThrows(DataOperationException.class, () -> {userDAO.isStudent(1);});
    }

    @Test
    void logInByRole_Successful() throws Exception {
        UserRole role = mock(UserRole.class);
        when(role.getPropertiesKey()).thenReturn("admin_config");
        assertDoesNotThrow(() -> {userDAO.logInByRole(role);});
        databaseConnectionManager.verify(() -> DatabaseConnectionManager.loadProperties("admin_config"));
    }

    @Test
    void logInByRole_IOException_ThrowsDataOperationException() throws SQLException {
        UserRole role = mock(UserRole.class);
        when(role.getPropertiesKey()).thenReturn("root_config");
        databaseConnectionManager.when(() -> DatabaseConnectionManager.loadProperties("root_config")).thenThrow(new IOException("Archivo no encontrado"));
        assertThrows(DataOperationException.class, () -> {userDAO.logInByRole(role);});
    }
}
