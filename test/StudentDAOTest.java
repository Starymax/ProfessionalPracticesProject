import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dao.ProjectDAO;
import mx.fei.logic.dao.StudentDAO;
import mx.fei.logic.dao.UserDAO;
import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.dto.Practice;
import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataOperationException;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.eq;

public class StudentDAOTest {
    private StudentDAO studentDAO;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private MockedStatic<DatabaseConnectionManager> databaseConnectionManager;

    @BeforeEach
    public void setUp() throws SQLException {
        studentDAO = new StudentDAO();
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
    void getStudentByEnrollment_InvalidEnrollment_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> studentDAO.getStudentByEnrollment(""));
    }

    @Test
    void getStudentByEnrollment_NotFound_ThrowsNoSuchElementException() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        assertThrows(NoSuchElementException.class, () -> studentDAO.getStudentByEnrollment("S21012345"));
    }

    @Test
    void getStudentByEnrollment_Success_NoProject() throws SQLException {
        String enrollment = "S21011011";
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("id_usuario")).thenReturn(50);
        when(resultSet.getString("nombre")).thenReturn("Juan");
        when(resultSet.getString("apellidos")).thenReturn("Pérez");
        when(resultSet.getString("matricula")).thenReturn(enrollment);
        when(resultSet.getBoolean("activo")).thenReturn(true);
        when(resultSet.getInt("proyecto")).thenReturn(0);
        Student student = studentDAO.getStudentByEnrollment(enrollment);
        assertNotNull(student);
        assertEquals(enrollment, student.getEnrollment());
        assertNull(student.getAssignedProject());
    }


    @Test
    void getStudentByEnrollment_SQLException_ThrowsDataOperationException() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error en vista"));
        assertThrows(DataOperationException.class, () -> studentDAO.getStudentByEnrollment("S123"));
    }

    @Test
    void getStudentByEnrollment_Success_WithProject() throws SQLException {
        String enrollment = "S21011011";
        int projectId = 7;
        Project mockProject = mock(Project.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("id_usuario")).thenReturn(50);
        when(resultSet.getInt("proyecto")).thenReturn(projectId);
        try (MockedConstruction<ProjectDAO> mocked = mockConstruction(ProjectDAO.class, (mock, context) -> { when(mock.getProjectById(projectId)).thenReturn(mockProject);})) {
            Student student = studentDAO.getStudentByEnrollment(enrollment);
            assertNotNull(student);
            assertEquals(mockProject, student.getAssignedProject());
        }
    }

    @Test
    void getStudentById_InvalidId_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> studentDAO.getStudentById(0));
    }

    @Test
    void getStudentById_NotFound_ThrowsNoSuchElementException() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        assertThrows(NoSuchElementException.class, () -> studentDAO.getStudentById(500));
    }

    @Test
    void getStudentById_Success_NoProject() throws SQLException {
        int idTest = 10;
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("nombre")).thenReturn("Maria");
        when(resultSet.getString("apellidos")).thenReturn("Lopez");
        when(resultSet.getString("matricula")).thenReturn("S21011000");
        when(resultSet.getInt("proyecto")).thenReturn(0);
        Student result = studentDAO.getStudentById(idTest);
        assertNotNull(result);
        assertEquals(idTest, result.getUserId());
        assertNull(result.getAssignedProject());
        verify(preparedStatement).setInt(1, idTest);
    }

    @Test
    void getStudentById_SQLException_ThrowsDataOperationException() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de enlace"));
        assertThrows(DataOperationException.class, () -> studentDAO.getStudentById(1));
    }

    @Test
    void registerStudent_Null_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> studentDAO.registerStudent(null));
    }

    @Test
    void registerStudent_AlreadyExists_ThrowsIllegalStateException() throws DataOperationException {
        Student student = new Student(0, "Nombre", "Apellidos", "correo@test.com", "pass", "M", true, "S21011011", false, null, 0.0f);
        StudentDAO spyStudentDAO = spy(studentDAO);
        doReturn(student).when(spyStudentDAO).getStudentByEnrollment("S21011011");
        assertThrows(IllegalStateException.class, () -> spyStudentDAO.registerStudent(student));
    }

    @Test
    void registerStudent_UserRegistrationFails_ThrowsDataOperationException() throws DataOperationException {
        Student student = new Student(0, "Nombre", "Apellidos", "correo@test.com", "pass", "M", true, "S22012012", false, null, 0.0f);
        StudentDAO spyStudentDAO = spy(studentDAO);
        doThrow(new NoSuchElementException()).when(spyStudentDAO).getStudentByEnrollment(anyString());
        try (MockedConstruction<UserDAO> mockedUserDAO = mockConstruction(UserDAO.class, (mock, context) -> {
            when(mock.registerUser(any())).thenReturn(-1);})) {
            assertThrows(DataOperationException.class, () -> spyStudentDAO.registerStudent(student));
        }
    }

    @Test
    void registerStudent_Successful_ReturnsTrue() throws SQLException {
        String enrollment = "S23013013";
        Student student = new Student(0, "Ana", "García", "ana@test.com", "123", "F", true, enrollment, true, null, 9.5f);
        int generatedUserId = 100;
        StudentDAO spyStudentDAO = spy(studentDAO);
        doThrow(new NoSuchElementException()).when(spyStudentDAO).getStudentByEnrollment(enrollment);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        try (MockedConstruction<UserDAO> mockedUserDAO = mockConstruction(UserDAO.class, (mock, context) -> {when(mock.registerUser(any(Student.class))).thenReturn(generatedUserId);})) {
            boolean result = spyStudentDAO.registerStudent(student);
            assertTrue(result);
            verify(preparedStatement).setInt(1, generatedUserId);
            verify(preparedStatement).setString(2, enrollment);
            verify(preparedStatement).setBoolean(3, true);
            verify(preparedStatement).setFloat(4, 9.5f);
        }
    }

    @Test
    void registerStudent_SQLException_ThrowsDataOperationException() throws SQLException {
        Student student = new Student(0, "Luis", "Paz", "luis@test.com", "123", "M", true, "S24014014", false, null, 7.0f);
        StudentDAO spyStudentDAO = spy(studentDAO);
        doThrow(new NoSuchElementException()).when(spyStudentDAO).getStudentByEnrollment(anyString());
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error DB"));
        try (MockedConstruction<UserDAO> mockedUserDAO = mockConstruction(UserDAO.class, (mock, context) -> {when(mock.registerUser(any())).thenReturn(200);})) {
            assertThrows(DataOperationException.class, () -> spyStudentDAO.registerStudent(student));
        }
    }

    @Test
    void modifyStudent_Null_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {studentDAO.modifyStudent(null);});
    }

    @Test
    void modifyStudent_Successful_ReturnsTrue() throws SQLException {
        Student student = new Student(50, "Juan", "Pérez", "juan@test.com", "pass", "M", true, "S21011011", true, null, 9.8f);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = studentDAO.modifyStudent(student);
        assertTrue(result);
        verify(preparedStatement).setBoolean(1, true);
        verify(preparedStatement).setFloat(2, 9.8f);
        verify(preparedStatement).setInt(3, 50);
    }

    @Test
    void modifyStudent_NotFound_ReturnsFalse() throws SQLException {
        Student student = new Student(999, "Nombre", "Apellidos", "mail@test.com", "123", "M", true, "S000", false, null, 0.0f);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);
        boolean result = studentDAO.modifyStudent(student);
        assertFalse(result);
        verify(preparedStatement).setInt(3, 999);
    }

    @Test
    void modifyStudent_SQLException_ThrowsDataOperationException() throws SQLException {
        Student student = new Student(1, "Luis", "Paz", "luis@test.com", "123", "M", true, "S111", false, null, 7.0f);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de integridad"));
        assertThrows(DataOperationException.class, () -> {studentDAO.modifyStudent(student);});
    }

    @Test
    void getStudents_Empty_ReturnsEmptyList() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<Student> result = studentDAO.getStudents();
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
    }

    @Test
    void getStudents_Successful_ReturnsList() throws SQLException {
        String enrollment1 = "S21011011";
        String enrollment2 = "S21011022";
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString("matricula")).thenReturn(enrollment1, enrollment2);
        Student student1 = mock(Student.class);
        Student student2 = mock(Student.class);
        StudentDAO spyDAO = spy(studentDAO);
        doReturn(student1).when(spyDAO).getStudentByEnrollment(enrollment1);
        doReturn(student2).when(spyDAO).getStudentByEnrollment(enrollment2);
        List<Student> result = spyDAO.getStudents();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(student1, result.get(0));
        assertEquals(student2, result.get(1));
        verify(spyDAO).getStudentByEnrollment(enrollment1);
        verify(spyDAO).getStudentByEnrollment(enrollment2);
    }

    @Test
    void getStudents_PartialFailure_ContinuesProcessing() throws SQLException {
        String enrollmentFail = "S00000000";
        String enrollmentSucces = "S21011011";
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString("matricula")).thenReturn(enrollmentFail, enrollmentSucces);
        Student student = mock(Student.class);
        StudentDAO spyDAO = spy(studentDAO);
        doThrow(new NoSuchElementException()).when(spyDAO).getStudentByEnrollment(enrollmentFail);
        doReturn(student).when(spyDAO).getStudentByEnrollment(enrollmentSucces);
        List<Student> result = spyDAO.getStudents();
        assertEquals(1, result.size());
        assertEquals(student, result.get(0));
    }

    @Test
    void getStudents_SQLException_ThrowsDataOperationException() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Database Down"));
        assertThrows(DataOperationException.class, () -> {studentDAO.getStudents();});
    }

    @Test
    void getStudentsWithoutProject_Empty_ReturnsEmptyList() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<Student> result = studentDAO.getStudentsWithoutProject();
        assertTrue(result.isEmpty());
        verify(preparedStatement).executeQuery();
    }

    @Test
    void getStudentsWithoutProject_Successful_ReturnsList() throws SQLException {
        String enrollment1 = "S21011001";
        String enrollment2 = "S21011002";
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString("matricula")).thenReturn(enrollment1, enrollment2);
        Student student1 = mock(Student.class);
        Student student2 = mock(Student.class);
        StudentDAO spyDAO = spy(studentDAO);
        doReturn(student1).when(spyDAO).getStudentByEnrollment(enrollment1);
        doReturn(student2).when(spyDAO).getStudentByEnrollment(enrollment2);
        List<Student> result = spyDAO.getStudentsWithoutProject();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(student1, result.get(0));
        verify(spyDAO).getStudentByEnrollment(enrollment1);
    }

    @Test
    void getStudentsWithoutProject_PartialFailure_StaysResilient() throws SQLException {
        String enrollmentFail = "S00000000";
        String enrollmentSucces = "S21011001";
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString("matricula")).thenReturn(enrollmentFail, enrollmentSucces);
        Student student = mock(Student.class);
        StudentDAO spyDAO = spy(studentDAO);
        doThrow(new DataOperationException("Fallo")).when(spyDAO).getStudentByEnrollment(enrollmentFail);
        doReturn(student).when(spyDAO).getStudentByEnrollment(enrollmentSucces);
        List<Student> result = spyDAO.getStudentsWithoutProject();
        assertEquals(1, result.size());
        assertEquals(student, result.get(0));
    }

    @Test
    void getStudentsWithoutProject_SQLException_ThrowsDataOperationException() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de sintaxis SQL"));
        assertThrows(DataOperationException.class, () -> {studentDAO.getStudentsWithoutProject();});
    }

    @Test
    void getActiveStudents_Empty_ReturnsEmptyList() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<Student> result = studentDAO.getActiveStudents();
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
    }

    @Test
    void getActiveStudents_Successful_ReturnsList() throws SQLException {
        String activeEnrollment1 = "S21011001";
        String activeEnrollment2 = "S21011002";
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString("matricula")).thenReturn(activeEnrollment1, activeEnrollment2);
        Student student1 = mock(Student.class);
        Student student2 = mock(Student.class);
        StudentDAO spyDAO = spy(studentDAO);
        doReturn(student1).when(spyDAO).getStudentByEnrollment(activeEnrollment1);
        doReturn(student2).when(spyDAO).getStudentByEnrollment(activeEnrollment2);
        List<Student> result = spyDAO.getActiveStudents();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(student1, result.get(0));
        verify(spyDAO).getStudentByEnrollment(activeEnrollment1);
    }

    @Test
    void getActiveStudents_PartialFailure_Continues() throws SQLException {
        String enrollmentFail = "S99999999";
        String enrollmentSucces = "S21011001";
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString("matricula")).thenReturn(enrollmentFail, enrollmentSucces);
        Student student = mock(Student.class);
        StudentDAO spyDAO = spy(studentDAO);
        doThrow(new NoSuchElementException()).when(spyDAO).getStudentByEnrollment(enrollmentFail);
        doReturn(student).when(spyDAO).getStudentByEnrollment(enrollmentSucces);
        List<Student> result = spyDAO.getActiveStudents();
        assertEquals(1, result.size());
        assertEquals(student, result.get(0));
    }

    @Test
    void getActiveStudents_SQLException_ThrowsDataOperationException() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de enlace"));
        assertThrows(DataOperationException.class, () -> {studentDAO.getActiveStudents();});
    }

    @Test
    void saveSelectedProjects_Successful() throws SQLException {
        Student student = new Student(1, "Juan", "Perez", "juan@test.com", "123", "M", true, "S21011001", false, null, 0.0f);
        List<Project> projects = new ArrayList<>();
        projects.add(new Project(101, "Proyecto A"));
        projects.add(new Project(102, "Proyecto B"));
        projects.add(new Project(103, "Proyecto C"));
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeBatch()).thenReturn(new int[]{1, 1, 1});
        studentDAO.saveSelectedProjects(projects, student);
        verify(preparedStatement, times(3)).addBatch();
        verify(preparedStatement).executeBatch();
        verify(preparedStatement, atLeastOnce()).setString(1, "S21011001");
    }

    @Test
    void saveSelectedProjects_EmptyList_DoesNothing() throws SQLException {
        Student student = new Student(1, "Juan", "Perez", "mail", "123", "M", true, "S210", false, null, 0.0f);
        List<Project> projects = new ArrayList<>();
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        studentDAO.saveSelectedProjects(projects, student);
        verify(preparedStatement, never()).addBatch();
        verify(preparedStatement).executeBatch();
    }

    @Test
    void saveSelectedProjects_SQLException_ThrowsDataOperationException() throws SQLException {
        Student student = new Student(1, "Juan", "Perez", "mail", "123", "M", true, "S210", false, null, 0.0f);
        List<Project> projects = Collections.singletonList(new Project(1, "Test"));
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeBatch()).thenThrow(new SQLException("Duplicate entry"));
        assertThrows(DataOperationException.class, () -> {studentDAO.saveSelectedProjects(projects, student);});
    }

    @Test
    void getSelectedProjects_Successful_ReturnsList() throws SQLException {
        Student student = new Student(1, "Ana", "Díaz", "ana@test.com", "123", "F", true, "S21011001", false, null, 0.0f);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getInt("proyecto_seleccionado")).thenReturn(101, 102);
        Project project1 = mock(Project.class);
        Project project2 = mock(Project.class);
        try (MockedConstruction<ProjectDAO> mockedProjectDAO = mockConstruction(ProjectDAO.class,
                (mock, context) -> {
                    when(mock.getProjectById(101)).thenReturn(project1);
                    when(mock.getProjectById(102)).thenReturn(project2);
                })) {
            List<Project> result = studentDAO.getSelectedProjects(student);
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(project1, result.get(0));
            assertEquals(project2, result.get(1));
            verify(preparedStatement).setString(1, "S21011001");
        }
    }

    @Test
    void getSelectedProjects_Empty_ReturnsEmptyList() throws SQLException {
        Student student = new Student(1, "Ana", "Díaz", "ana@test.com", "123", "F", true, "S21011001", false, null, 0.0f);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<Project> result = studentDAO.getSelectedProjects(student);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
    }

    @Test
    void getSelectedProjects_SQLException_ThrowsDataOperationException() throws SQLException {
        Student student = new Student(1, "Ana", "Díaz", "ana@test.com", "123", "F", true, "S21011001", false, null, 0.0f);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de lectura"));
        assertThrows(DataOperationException.class, () -> {studentDAO.getSelectedProjects(student);});
    }

    @Test
    void assignProject_Successful_UpdatesStudentAndProject() throws SQLException {
        Student student = new Student(1, "Luis", "Paz", "mail", "123", "M", true, "S21011001", false, null, 0.0f);
        Project project = new Project(5, "Sistema de Gestión", 10);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        try (MockedConstruction<ProjectDAO> mockedProjectDAO = mockConstruction(ProjectDAO.class, (mock, context) -> {
                    when(mock.modifyProject(any(Project.class))).thenReturn(true);
                })) {
            boolean result = studentDAO.assignProject(student, project);
            assertTrue(result);
            assertEquals(9, project.getAvailablePlaces());
            verify(preparedStatement).setInt(1, 5);
            verify(preparedStatement).setString(2, "S21011001");
        }
    }

    @Test
    void assignProject_UpdateFails_ReturnsFalse() throws SQLException {
        Student student = new Student(1, "Luis", "Paz", "mail", "123", "M", true, "S000", false, null, 0.0f);
        Project project = new Project(5, "Test", 5);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);
        boolean result = studentDAO.assignProject(student, project);
        assertFalse(result);
        assertEquals(5, project.getAvailablePlaces());
    }

    @Test
    void assignProject_SQLException_ThrowsDataOperationException() throws SQLException {
        Student student = new Student(1, "Luis", "Paz", "mail", "123", "M", true, "S111", false, null, 0.0f);
        Project project = new Project(1, "Test", 1);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Deadlock detectado"));
        assertThrows(DataOperationException.class, () -> {studentDAO.assignProject(student, project);});
    }

    @Test
    void assignEducationalExperience_Successful_ReturnsTrue() throws SQLException {
        Student student = new Student(45, "Pedro", "López", "pedro@test.com", "123", "M", true, "S21011001", false, null, 0.0f);
        EducationalExperience educationalExperience = new EducationalExperience("88421", "Prácticas Profesionales", "FEB-JUN 2026");
        Practice practice = new Practice(student, educationalExperience);
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = studentDAO.assignEducationalExperience(practice);
        assertTrue(result);
        verify(preparedStatement).setInt(1, 45);
        verify(preparedStatement).setString(2, "FEB-JUN 2026");
        verify(preparedStatement).setString(3, "88421");
    }

    @Test
    void assignEducationalExperience_Fails_ReturnsFalse() throws SQLException {
        Student student = new Student(1, "A", "B", "mail", "1", "M", true, "S1", false, null, 0.0f);
        EducationalExperience educationalExperience = new EducationalExperience("000", "EE", "PER");
        Practice practice = new Practice(student, educationalExperience);
        when(connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);
        boolean result = studentDAO.assignEducationalExperience(practice);
        assertFalse(result);
    }

    @Test
    void assignEducationalExperience_SQLException_ThrowsDataOperationException() throws SQLException {
        Student student = new Student(1, "A", "B", "mail", "1", "M", true, "S1", false, null, 0.0f);
        EducationalExperience educationalExperience = new EducationalExperience("123", "EE", "PER");
        Practice practice = new Practice(student, educationalExperience);
        when(connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Foreign key constraint fails"));
        assertThrows(DataOperationException.class, () -> {studentDAO.assignEducationalExperience(practice);});
    }
}
