import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dao.EducationalExperienceDAO;
import mx.fei.logic.dao.PracticeDAO;
import mx.fei.logic.dao.StudentDAO;
import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.dto.Practice;
import mx.fei.logic.dto.Student;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mockConstruction;

public class PracticeDAOTest {
    private PracticeDAO practiceDAO;

    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private MockedStatic<DatabaseConnectionManager> databaseConnectionManager;
    private DatabaseConnectionManager mockManager;

    @BeforeEach
    void setUp() throws SQLException {
        practiceDAO = new PracticeDAO();
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
    void getPracticeById_PracticeExists_ReturnsPracticeWithMappedStudent() throws SQLException {
        int idStudent = 45;
        String nrc = "88421";
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("id_alumno")).thenReturn(idStudent);
        when(resultSet.getString("nrc")).thenReturn(nrc);
        when(resultSet.getString("periodo")).thenReturn("FEB-JUN 2026");
        when(resultSet.getFloat("calificacion")).thenReturn(9.0f);
        Student student = mock(Student.class);
        EducationalExperience educationalExperience = mock(EducationalExperience.class);
        Practice expectedPractice = new Practice(student, educationalExperience, "FEB-JUN 2026", 9.0f);
        try (MockedConstruction<StudentDAO> mockedStudentDAO = mockConstruction(StudentDAO.class, (mock, context) -> when(mock.getStudentById(idStudent)).thenReturn(student));
             MockedConstruction<EducationalExperienceDAO> mockedEducationalExperienceDAO = mockConstruction(EducationalExperienceDAO.class,
                     (mock, context) -> when(mock.getEducationalExperienceByNrc(nrc)).thenReturn(educationalExperience))) {
            Practice result = practiceDAO.getPracticeById(1);
            assertEquals(expectedPractice, result);
        }
    }

    @Test
    void getPracticeById_PracticeDoesNotExist_ThrowsNoSuchElementException() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        assertThrows(NoSuchElementException.class, () -> practiceDAO.getPracticeById(999));
    }

    @Test
    void getPracticeById_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de conexión"));
        assertThrows(DataOperationException.class, () -> practiceDAO.getPracticeById(1));
    }

    @Test
    void createPractice_PracticeIsNull_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> practiceDAO.createPractice(null));
    }

    @Test
    void createPractice_StudentIsNull_ThrowsIllegalArgumentException() {
        Practice practice = new Practice(null, new EducationalExperience("123", "EE", "PER"), "2026", 0.0f);
        assertThrows(IllegalArgumentException.class, () -> practiceDAO.createPractice(practice));
    }

    @Test
    void createPractice_PeriodIsBlank_ThrowsIllegalArgumentException() {
        Student student = new Student(1, "A", "B", "m", "p", "M", true, "S1", false, null, 0.0f);
        Practice practice = new Practice(student, new EducationalExperience("123", "EE", "PER"), "", 0.0f);
        assertThrows(IllegalArgumentException.class, () -> practiceDAO.createPractice(practice));
    }

    @Test
    void createPractice_InsertAffectsOneRow_ReturnsTrue() throws SQLException {
        Student student = new Student(10, "Juan", "Perez", "juan@test.com", "123", "M", true, "S21011001", false, null, 0.0f);
        EducationalExperience educationalExperience = new EducationalExperience("FEB-JUN 2026", "Prácticas", "88421");
        Practice practice = new Practice(student, educationalExperience, "FEB-JUN 2026", 9.5f);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = practiceDAO.createPractice(practice);
        assertTrue(result);
    }

    @Test
    void createPractice_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        Student student = new Student(1, "A", "B", "m", "p", "M", true, "S1", false, null, 0.0f);
        Practice practice = new Practice(student, new EducationalExperience("1", "E", "P"), "2026", 0.0f);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Duplicate entry"));
        assertThrows(DataOperationException.class, () -> practiceDAO.createPractice(practice));
    }

    @Test
    void getPracticeByEnrollment_EnrollmentIsBlank_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> practiceDAO.getPracticeByEnrollment(""));
    }

    @Test
    void getPracticeByEnrollment_PracticeDoesNotExist_ReturnsNull() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        Practice practice = practiceDAO.getPracticeByEnrollment("S21011001");
        assertNull(practice);
    }

    @Test
    void getPracticeByEnrollment_PracticeExists_ReturnsExpectedPractice() throws SQLException {
        String enrollment = "S21011001";
        String nrc = "88421";
        int practiceId = 10;
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("id_practica")).thenReturn(practiceId);
        when(resultSet.getString("periodo")).thenReturn("FEB-JUN 2026");
        when(resultSet.getString("nrc")).thenReturn(nrc);
        when(resultSet.getFloat("calificacion")).thenReturn(9.0f);
        Student student = mock(Student.class);
        EducationalExperience educationalExperience = mock(EducationalExperience.class);
        Practice expectedPractice = new Practice(10, student, educationalExperience, "FEB-JUN 2026", 9.0f);
        try (MockedConstruction<EducationalExperienceDAO> mockedEducationalExperienceDAO = mockConstruction(EducationalExperienceDAO.class, (mock, context) -> when(mock.getEducationalExperienceByNrc(nrc)).thenReturn(educationalExperience));
             MockedConstruction<StudentDAO> mockedStudentDAO = mockConstruction(StudentDAO.class,
                     (mock, context) -> when(mock.getStudentByEnrollment(enrollment)).thenReturn(student))) {
            Practice result = practiceDAO.getPracticeByEnrollment(enrollment);
            assertEquals(expectedPractice, result);
        }
    }

    @Test
    void getPracticeByEnrollment_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de conexión"));
        assertThrows(DataOperationException.class, () -> practiceDAO.getPracticeByEnrollment("S12345"));
    }

    @Test
    void getCurrentPeriod_Always_ReturnsCurrentYearMonth() {
        String expectedPeriod = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String result = practiceDAO.getCurrentPeriod();
        assertEquals(expectedPeriod, result);
    }

    @Test
    void getStudentsWithPractice_MultipleStudentsExist_ReturnsListWithExpectedStudents() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getInt("id_usuario")).thenReturn(101, 102);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        Student expectedStudent1 = new Student(101,"Diego", "León", "S220011");
        Student expectedStudent2 = new Student(102, "Ian", "Uziel", "S220012");
        List<Student> expectedList = List.of(expectedStudent1, expectedStudent2);
        try (MockedConstruction<StudentDAO> mockedStudentDAO = mockConstruction(StudentDAO.class, (mock, context) -> {
            when(mock.getStudentById(101)).thenReturn(expectedStudent1);
            when(mock.getStudentById(102)).thenReturn(expectedStudent2);
        })) {
            List<Student> studentList = practiceDAO.getStudentsWithPractice();
            assertEquals(expectedList, studentList, "La lista de estudiantes recuperada no coincide con la esperada");
        }
    }

    @Test
    void getStudentsWithPractice_OneStudentFailsToLoad_ReturnsListWithOnlySuccessfulStudents() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getInt("id_usuario")).thenReturn(101, 102);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        Student expectedStudent1 = new Student(101,"Diego", "León", "S220011");
        List<Student> expectedList = List.of(expectedStudent1);
        try (MockedConstruction<StudentDAO> mockedStudentDAO = mockConstruction(StudentDAO.class, (mock, context) -> {
            when(mock.getStudentById(101)).thenReturn(expectedStudent1);
            when(mock.getStudentById(102)).thenThrow(new DataOperationException("Error de carga simulado"));
        })) {
            List<Student> studentList = practiceDAO.getStudentsWithPractice();
            assertEquals(expectedList, studentList, "La lista debería contener únicamente al estudiante cargado con éxito");
        }
    }
}