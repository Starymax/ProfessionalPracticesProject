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
import java.util.List;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.eq;

public class StudentDAOTest {
    private StudentDAO studentDAO;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private MockedStatic<DatabaseConnectionManager> databaseConnectionManager;
    private DatabaseConnectionManager mockManager;

    @BeforeEach
    public void setUp() throws SQLException {
        studentDAO = new StudentDAO();
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

    private void stubStudentRow(ResultSet rs, int idUser, String enrollment, String nombre, int projectId, boolean activo) throws SQLException {
        when(rs.getInt("id_usuario")).thenReturn(idUser);
        when(rs.getString("matricula")).thenReturn(enrollment);
        when(rs.getString("nombre")).thenReturn(nombre);
        when(rs.getString("apellidos")).thenReturn("Apellido");
        when(rs.getString("correo")).thenReturn(enrollment + "@test.com");
        when(rs.getString("contrasena")).thenReturn("pass");
        when(rs.getBoolean("activo")).thenReturn(activo);
        when(rs.getString("genero")).thenReturn("M");
        when(rs.getBoolean("lengua_indigena")).thenReturn(false);
        when(rs.getInt("proyecto")).thenReturn(projectId);
        when(rs.getFloat("calificacion")).thenReturn(8.0f);
    }

    @Test
    void getStudentByEnrollment_EnrollmentIsEmpty_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> studentDAO.getStudentByEnrollment(""));
    }

    @Test
    void getStudentByEnrollment_StudentDoesNotExist_ThrowsNoSuchElementException() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        assertThrows(NoSuchElementException.class, () -> studentDAO.getStudentByEnrollment("S21012345"));
    }

    @Test
    void getStudentByEnrollment_StudentExistsWithoutProject_ReturnsStudentWithExpectedEnrollment() throws SQLException, DataOperationException {
        String enrollment = "S21011011";
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        stubStudentRow(resultSet, 50, enrollment, "Juan", 0, true);
        Student student = studentDAO.getStudentByEnrollment(enrollment);
        assertEquals(enrollment, student.getEnrollment());
    }

    @Test
    void getStudentByEnrollment_StudentExistsWithProject_ReturnsStudentWithAssignedProject() throws SQLException, DataOperationException {
        String enrollment = "S21011011";
        int projectId = 7;
        Project project = mock(Project.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        stubStudentRow(resultSet, 50, enrollment, "Juan", projectId, true);
        try (MockedConstruction<ProjectDAO> mockedProjectDAO = mockConstruction(ProjectDAO.class,
                (mock, context) -> when(mock.getProjectById(projectId)).thenReturn(project))) {
            Student student = studentDAO.getStudentByEnrollment(enrollment);
            assertEquals(project, student.getAssignedProject());
        }
    }

    @Test
    void getStudentByEnrollment_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error en vista"));
        assertThrows(DataOperationException.class, () -> studentDAO.getStudentByEnrollment("S123"));
    }

    @Test
    void getStudentById_IdIsZero_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> studentDAO.getStudentById(0));
    }

    @Test
    void getStudentById_StudentDoesNotExist_ThrowsNoSuchElementException() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        assertThrows(NoSuchElementException.class, () -> studentDAO.getStudentById(500));
    }

    @Test
    void getStudentById_StudentExistsWithoutProject_ReturnsStudentWithExpectedId() throws SQLException, DataOperationException {
        int idTest = 10;
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        stubStudentRow(resultSet, idTest, "S21011000", "Maria", 0, true);
        Student result = studentDAO.getStudentById(idTest);
        assertEquals(idTest, result.getUserId());
    }

    @Test
    void getStudentById_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de enlace"));
        assertThrows(DataOperationException.class, () -> studentDAO.getStudentById(1));
    }

    @Test
    void registerStudent_StudentIsNull_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> studentDAO.registerStudent(null));
    }

    @Test
    void registerStudent_EnrollmentAlreadyExists_ThrowsIllegalStateException() throws DataOperationException {
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
        try (MockedConstruction<UserDAO> mockedUserDAO = mockConstruction(UserDAO.class,
                (mock, context) -> when(mock.registerUser(any())).thenReturn(-1))) {
            assertThrows(DataOperationException.class, () -> spyStudentDAO.registerStudent(student));
        }
    }

    @Test
    void registerStudent_NewStudentInserted_ReturnsTrue() throws SQLException, DataOperationException {
        String enrollment = "S23013013";
        Student student = new Student(0, "Ana", "García", "ana@test.com", "123", "F", true, enrollment, true, null, 9.5f);
        StudentDAO spyStudentDAO = spy(studentDAO);
        doThrow(new NoSuchElementException()).when(spyStudentDAO).getStudentByEnrollment(enrollment);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        try (MockedConstruction<UserDAO> mockedUserDAO = mockConstruction(UserDAO.class,
                (mock, context) -> when(mock.registerUser(any(Student.class))).thenReturn(100))) {
            boolean result = spyStudentDAO.registerStudent(student);
            assertTrue(result);
        }
    }

    @Test
    void registerStudent_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException, DataOperationException {
        Student student = new Student(0, "Luis", "Paz", "luis@test.com", "123", "M", true, "S24014014", false, null, 7.0f);
        StudentDAO spyStudentDAO = spy(studentDAO);
        doThrow(new NoSuchElementException()).when(spyStudentDAO).getStudentByEnrollment(anyString());
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error DB"));
        try (MockedConstruction<UserDAO> mockedUserDAO = mockConstruction(UserDAO.class,
                (mock, context) -> when(mock.registerUser(any())).thenReturn(200))) {
            assertThrows(DataOperationException.class, () -> spyStudentDAO.registerStudent(student));
        }
    }

    @Test
    void modifyStudent_StudentIsNull_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> studentDAO.modifyStudent(null));
    }

    @Test
    void modifyStudent_UpdateAffectsOneRow_ReturnsTrue() throws SQLException, DataOperationException {
        Student student = new Student(50, "Juan", "Pérez", "juan@test.com", "pass", "M", true, "S21011011", true, null, 9.8f);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = studentDAO.modifyStudent(student);
        assertTrue(result);
    }

    @Test
    void modifyStudent_UpdateAffectsZeroRows_ReturnsFalse() throws SQLException, DataOperationException {
        Student student = new Student(999, "Nombre", "Apellidos", "mail@test.com", "123", "M", true, "S000", false, null, 0.0f);
        when(preparedStatement.executeUpdate()).thenReturn(0);
        boolean result = studentDAO.modifyStudent(student);
        assertFalse(result);
    }

    @Test
    void modifyStudent_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        Student student = new Student(1, "Luis", "Paz", "luis@test.com", "123", "M", true, "S111", false, null, 7.0f);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de integridad"));
        assertThrows(DataOperationException.class, () -> studentDAO.modifyStudent(student));
    }

    @Test
    void getStudents_NoStudentsRegistered_ReturnsEmptyList() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<Student> result = studentDAO.getStudents();
        assertTrue(result.isEmpty());
    }

    @Test
    void getStudents_TwoStudentsRegistered_ReturnsListWithTwoStudents() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getInt("id_usuario")).thenReturn(1, 2);
        when(resultSet.getString("matricula")).thenReturn("S21011001", "S21011002");
        when(resultSet.getString("nombre")).thenReturn("Juan", "Maria");
        when(resultSet.getString("apellidos")).thenReturn("Pérez", "García");
        when(resultSet.getString("correo")).thenReturn("juan@test.com", "maria@test.com");
        when(resultSet.getString("contrasena")).thenReturn("pass", "pass");
        when(resultSet.getBoolean("activo")).thenReturn(true, true);
        when(resultSet.getString("genero")).thenReturn("M", "F");
        when(resultSet.getBoolean("lengua_indigena")).thenReturn(false, false);
        when(resultSet.getInt("proyecto")).thenReturn(0, 0);
        when(resultSet.getFloat("calificacion")).thenReturn(8.0f, 9.0f);
        List<Student> result = studentDAO.getStudents();
        assertEquals(2, result.size());
    }

    @Test
    void getStudents_OneStudentProjectCannotBeLoaded_ReturnsOnlySuccessfullyLoadedStudents() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getInt("id_usuario")).thenReturn(1, 2);
        when(resultSet.getString("matricula")).thenReturn("S21011001", "S21011002");
        when(resultSet.getString("nombre")).thenReturn("Juan", "Maria");
        when(resultSet.getString("apellidos")).thenReturn("Pérez", "García");
        when(resultSet.getString("correo")).thenReturn("juan@test.com", "maria@test.com");
        when(resultSet.getString("contrasena")).thenReturn("pass", "pass");
        when(resultSet.getBoolean("activo")).thenReturn(true, true);
        when(resultSet.getString("genero")).thenReturn("M", "F");
        when(resultSet.getBoolean("lengua_indigena")).thenReturn(false, false);
        when(resultSet.getInt("proyecto")).thenReturn(5, 0);
        when(resultSet.getFloat("calificacion")).thenReturn(8.0f, 9.0f);
        try (MockedConstruction<ProjectDAO> mockedProjectDAO = mockConstruction(ProjectDAO.class,
                (mock, context) -> when(mock.getProjectById(5)).thenThrow(new DataOperationException("Error proyecto")))) {
            List<Student> result = studentDAO.getStudents();
            assertEquals(1, result.size());
        }
    }

    @Test
    void getStudents_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Database Down"));
        assertThrows(DataOperationException.class, () -> studentDAO.getStudents());
    }

    @Test
    void getStudentsWithoutProject_NoStudentsWithoutProject_ReturnsEmptyList() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<Student> result = studentDAO.getStudentsWithoutProject();
        assertTrue(result.isEmpty());
    }

    @Test
    void getStudentsWithoutProject_TwoStudentsWithoutProject_ReturnsListWithTwoStudents() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getInt("id_usuario")).thenReturn(3, 4);
        when(resultSet.getString("matricula")).thenReturn("S21011003", "S21011004");
        when(resultSet.getString("nombre")).thenReturn("Carlos", "Laura");
        when(resultSet.getString("apellidos")).thenReturn("Lopez", "Torres");
        when(resultSet.getString("correo")).thenReturn("c@test.com", "l@test.com");
        when(resultSet.getString("contrasena")).thenReturn("pass", "pass");
        when(resultSet.getBoolean("activo")).thenReturn(true, true);
        when(resultSet.getString("genero")).thenReturn("M", "F");
        when(resultSet.getBoolean("lengua_indigena")).thenReturn(false, false);
        when(resultSet.getInt("proyecto")).thenReturn(0, 0);
        when(resultSet.getFloat("calificacion")).thenReturn(7.0f, 8.5f);
        List<Student> result = studentDAO.getStudentsWithoutProject();
        assertEquals(2, result.size());
    }

    @Test
    void getStudentsWithoutProject_OneStudentProjectCannotBeLoaded_ReturnsOnlySuccessfullyLoadedStudents() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getInt("id_usuario")).thenReturn(3, 4);
        when(resultSet.getString("matricula")).thenReturn("S21011003", "S21011004");
        when(resultSet.getString("nombre")).thenReturn("Carlos", "Laura");
        when(resultSet.getString("apellidos")).thenReturn("Lopez", "Torres");
        when(resultSet.getString("correo")).thenReturn("c@test.com", "l@test.com");
        when(resultSet.getString("contrasena")).thenReturn("pass", "pass");
        when(resultSet.getBoolean("activo")).thenReturn(true, true);
        when(resultSet.getString("genero")).thenReturn("M", "F");
        when(resultSet.getBoolean("lengua_indigena")).thenReturn(false, false);
        when(resultSet.getInt("proyecto")).thenReturn(3, 0);
        when(resultSet.getFloat("calificacion")).thenReturn(7.0f, 8.5f);
        try (MockedConstruction<ProjectDAO> mockedProjectDAO = mockConstruction(ProjectDAO.class,
                (mock, context) -> when(mock.getProjectById(3)).thenThrow(new DataOperationException("Error proyecto")))) {
            List<Student> result = studentDAO.getStudentsWithoutProject();
            assertEquals(1, result.size());
        }
    }

    @Test
    void getStudentsWithoutProject_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de sintaxis SQL"));
        assertThrows(DataOperationException.class, () -> studentDAO.getStudentsWithoutProject());
    }

    @Test
    void getActiveStudents_NoActiveStudents_ReturnsEmptyList() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<Student> result = studentDAO.getActiveStudents();
        assertTrue(result.isEmpty());
    }

    @Test
    void getActiveStudents_TwoActiveStudents_ReturnsListWithTwoStudents() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getInt("id_usuario")).thenReturn(5, 6);
        when(resultSet.getString("matricula")).thenReturn("S21011005", "S21011006");
        when(resultSet.getString("nombre")).thenReturn("Pedro", "Sofia");
        when(resultSet.getString("apellidos")).thenReturn("Rios", "Vega");
        when(resultSet.getString("correo")).thenReturn("p@test.com", "s@test.com");
        when(resultSet.getString("contrasena")).thenReturn("pass", "pass");
        when(resultSet.getBoolean("activo")).thenReturn(true, true);
        when(resultSet.getString("genero")).thenReturn("M", "F");
        when(resultSet.getBoolean("lengua_indigena")).thenReturn(false, false);
        when(resultSet.getInt("proyecto")).thenReturn(0, 0);
        when(resultSet.getFloat("calificacion")).thenReturn(9.0f, 7.5f);
        List<Student> result = studentDAO.getActiveStudents();
        assertEquals(2, result.size());
    }

    @Test
    void getActiveStudents_OneStudentProjectCannotBeLoaded_ReturnsOnlySuccessfullyLoadedStudents() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getInt("id_usuario")).thenReturn(5, 6);
        when(resultSet.getString("matricula")).thenReturn("S21011005", "S21011006");
        when(resultSet.getString("nombre")).thenReturn("Pedro", "Sofia");
        when(resultSet.getString("apellidos")).thenReturn("Rios", "Vega");
        when(resultSet.getString("correo")).thenReturn("p@test.com", "s@test.com");
        when(resultSet.getString("contrasena")).thenReturn("pass", "pass");
        when(resultSet.getBoolean("activo")).thenReturn(true, true);
        when(resultSet.getString("genero")).thenReturn("M", "F");
        when(resultSet.getBoolean("lengua_indigena")).thenReturn(false, false);
        when(resultSet.getInt("proyecto")).thenReturn(7, 0);
        when(resultSet.getFloat("calificacion")).thenReturn(9.0f, 7.5f);
        try (MockedConstruction<ProjectDAO> mockedProjectDAO = mockConstruction(ProjectDAO.class,
                (mock, context) -> when(mock.getProjectById(7)).thenThrow(new DataOperationException("Error proyecto")))) {
            List<Student> result = studentDAO.getActiveStudents();
            assertEquals(1, result.size());
        }
    }

    @Test
    void getActiveStudents_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de enlace"));
        assertThrows(DataOperationException.class, () -> studentDAO.getActiveStudents());
    }

    @Test
    void saveSelectedProjects_ValidProjects_DoesNotThrow() throws SQLException {
        Student student = new Student(1, "Juan", "Perez", "juan@test.com", "123", "M", true, "S21011001", false, null, 0.0f);
        List<Project> projects = List.of(new Project(101, "Proyecto A"), new Project(102, "Proyecto B"), new Project(103, "Proyecto C"));
        when(preparedStatement.executeBatch()).thenReturn(new int[]{1, 1, 1});
        assertDoesNotThrow(() -> studentDAO.saveSelectedProjects(projects, student));
    }

    @Test
    void saveSelectedProjects_EmptyList_DoesNotThrow() throws SQLException {
        Student student = new Student(1, "Juan", "Perez", "mail", "123", "M", true, "S210", false, null, 0.0f);
        assertDoesNotThrow(() -> studentDAO.saveSelectedProjects(List.of(), student));
    }

    @Test
    void saveSelectedProjects_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        Student student = new Student(1, "Juan", "Perez", "mail", "123", "M", true, "S210", false, null, 0.0f);
        List<Project> projects = List.of(new Project(1, "Test"));
        when(preparedStatement.executeBatch()).thenThrow(new SQLException("Duplicate entry"));
        assertThrows(DataOperationException.class, () -> studentDAO.saveSelectedProjects(projects, student));
    }

    @Test
    void getSelectedProjects_StudentHasTwoSelections_ReturnsListWithTwoProjects() throws SQLException, DataOperationException {
        Student student = new Student(1, "Ana", "Díaz", "ana@test.com", "123", "F", true, "S21011001", false, null, 0.0f);
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
            assertEquals(2, result.size());
        }
    }

    @Test
    void getSelectedProjects_StudentHasNoSelections_ReturnsEmptyList() throws SQLException, DataOperationException {
        Student student = new Student(1, "Ana", "Díaz", "ana@test.com", "123", "F", true, "S21011001", false, null, 0.0f);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<Project> result = studentDAO.getSelectedProjects(student);
        assertTrue(result.isEmpty());
    }

    @Test
    void getSelectedProjects_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        Student student = new Student(1, "Ana", "Díaz", "ana@test.com", "123", "F", true, "S21011001", false, null, 0.0f);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de lectura"));
        assertThrows(DataOperationException.class, () -> studentDAO.getSelectedProjects(student));
    }

    @Test
    void assignProject_UpdateAffectsOneRow_ReturnsTrue() throws SQLException, DataOperationException {
        Student student = new Student(1, "Luis", "Paz", "mail", "123", "M", true, "S21011001", false, null, 0.0f);
        Project project = new Project(5, "Sistema de Gestión", 10);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        try (MockedConstruction<ProjectDAO> mockedProjectDAO = mockConstruction(ProjectDAO.class,
                (mock, context) -> when(mock.modifyProject(any(Project.class))).thenReturn(true))) {
            boolean result = studentDAO.assignProject(student, project);
            assertTrue(result);
        }
    }

    @Test
    void assignProject_UpdateAffectsZeroRows_ReturnsFalse() throws SQLException, DataOperationException {
        Student student = new Student(1, "Luis", "Paz", "mail", "123", "M", true, "S000", false, null, 0.0f);
        Project project = new Project(5, "Test", 5);
        when(preparedStatement.executeUpdate()).thenReturn(0);
        boolean result = studentDAO.assignProject(student, project);
        assertFalse(result);
    }

    @Test
    void assignProject_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        Student student = new Student(1, "Luis", "Paz", "mail", "123", "M", true, "S111", false, null, 0.0f);
        Project project = new Project(1, "Test", 1);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Deadlock detectado"));
        assertThrows(DataOperationException.class, () -> studentDAO.assignProject(student, project));
    }

    @Test
    void assignEducationalExperience_InsertAffectsOneRow_ReturnsTrue() throws SQLException, DataOperationException {
        Student student = new Student(45, "Pedro", "López", "pedro@test.com", "123", "M", true, "S21011001", false, null, 0.0f);
        EducationalExperience educationalExperience = new EducationalExperience("88421", "Prácticas Profesionales", "FEB-JUN 2026");
        Practice practice = new Practice(student, educationalExperience);
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = studentDAO.assignEducationalExperience(practice);
        assertTrue(result);
    }

    @Test
    void assignEducationalExperience_InsertAffectsZeroRows_ReturnsFalse() throws SQLException, DataOperationException {
        Student student = new Student(1, "A", "B", "mail", "1", "M", true, "S1", false, null, 0.0f);
        EducationalExperience educationalExperience = new EducationalExperience("000", "EE", "PER");
        Practice practice = new Practice(student, educationalExperience);
        when(connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);
        boolean result = studentDAO.assignEducationalExperience(practice);
        assertFalse(result);
    }

    @Test
    void assignEducationalExperience_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        Student student = new Student(1, "A", "B", "mail", "1", "M", true, "S1", false, null, 0.0f);
        EducationalExperience educationalExperience = new EducationalExperience("123", "EE", "PER");
        Practice practice = new Practice(student, educationalExperience);
        when(connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Foreign key constraint fails"));
        assertThrows(DataOperationException.class, () -> studentDAO.assignEducationalExperience(practice));
    }

    @Test
    void getStudentsByEducationalExperience_NrcIsNull_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> studentDAO.getStudentsByEducationalExperience(null));
    }

    @Test
    void getStudentsByEducationalExperience_NrcIsBlank_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> studentDAO.getStudentsByEducationalExperience("   "));
    }

    @Test
    void getStudentsByEducationalExperience_NoStudentsEnrolled_ReturnsEmptyList() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<Student> result = studentDAO.getStudentsByEducationalExperience("88421");
        assertTrue(result.isEmpty());
    }

    @Test
    void getStudentsByEducationalExperience_TwoStudentsEnrolled_ReturnsListWithTwoStudents() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getInt("id_usuario")).thenReturn(10, 11);
        when(resultSet.getString("matricula")).thenReturn("S21011010", "S21011011");
        when(resultSet.getString("nombre")).thenReturn("Rosa", "Felix");
        when(resultSet.getString("apellidos")).thenReturn("Mora", "Diaz");
        when(resultSet.getString("correo")).thenReturn("r@test.com", "f@test.com");
        when(resultSet.getString("contrasena")).thenReturn("pass", "pass");
        when(resultSet.getBoolean("activo")).thenReturn(true, true);
        when(resultSet.getString("genero")).thenReturn("F", "M");
        when(resultSet.getBoolean("lengua_indigena")).thenReturn(false, false);
        when(resultSet.getInt("proyecto")).thenReturn(0, 0);
        when(resultSet.getFloat("calificacion")).thenReturn(9.5f, 8.0f);
        List<Student> result = studentDAO.getStudentsByEducationalExperience("88421");
        assertEquals(2, result.size());
    }

    @Test
    void getStudentsByEducationalExperience_OneStudentProjectCannotBeLoaded_ReturnsOnlySuccessfullyLoadedStudents() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getInt("id_usuario")).thenReturn(10, 11);
        when(resultSet.getString("matricula")).thenReturn("S21011010", "S21011011");
        when(resultSet.getString("nombre")).thenReturn("Rosa", "Felix");
        when(resultSet.getString("apellidos")).thenReturn("Mora", "Diaz");
        when(resultSet.getString("correo")).thenReturn("r@test.com", "f@test.com");
        when(resultSet.getString("contrasena")).thenReturn("pass", "pass");
        when(resultSet.getBoolean("activo")).thenReturn(true, true);
        when(resultSet.getString("genero")).thenReturn("F", "M");
        when(resultSet.getBoolean("lengua_indigena")).thenReturn(false, false);
        when(resultSet.getInt("proyecto")).thenReturn(2, 0);
        when(resultSet.getFloat("calificacion")).thenReturn(9.5f, 8.0f);
        try (MockedConstruction<ProjectDAO> mockedProjectDAO = mockConstruction(ProjectDAO.class,
                (mock, context) -> when(mock.getProjectById(2)).thenThrow(new DataOperationException("Error proyecto")))) {
            List<Student> result = studentDAO.getStudentsByEducationalExperience("88421");
            assertEquals(1, result.size());
        }
    }

    @Test
    void getStudentsByEducationalExperience_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de enlace"));
        assertThrows(DataOperationException.class, () -> studentDAO.getStudentsByEducationalExperience("88421"));
    }
}
