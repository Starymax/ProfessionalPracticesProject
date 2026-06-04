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
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mockConstruction;

public class PracticeDAOTest {
    PracticeDAO practiceDAO;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private MockedStatic<DatabaseConnectionManager> databaseConnectionManager;

    @BeforeEach
    void setUp() throws SQLException {
        practiceDAO =  new PracticeDAO();
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
    void getPracticeById_Successful() throws SQLException {
        int idTest = 1;
        int idStudent = 45;
        String nrc = "88421";
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("id_alumno")).thenReturn(idStudent);
        when(resultSet.getString("nrc")).thenReturn(nrc);
        when(resultSet.getString("periodo")).thenReturn("FEB-JUN 2026");
        when(resultSet.getFloat("calificacion")).thenReturn(9.0f);
        Student student = mock(Student.class);
        EducationalExperience educationalExperience = mock(EducationalExperience.class);
        try (MockedConstruction<StudentDAO> mockStudentDAO = mockConstruction(StudentDAO.class, (mock, context) -> when(mock.getStudentById(idStudent)).thenReturn(student));
             MockedConstruction<EducationalExperienceDAO> mockEEDAO = mockConstruction(EducationalExperienceDAO.class, (mock, context) -> when(mock.getEducationalExperienceByNrc(nrc)).thenReturn(educationalExperience))) {
            Practice practice = practiceDAO.getPracticeById(idTest);
            assertNotNull(practice);
            assertEquals(student, practice.getStudent());
            assertEquals(educationalExperience, practice.getEducationalExperience());
            assertEquals("FEB-JUN 2026", practice.getPeriod());
            verify(preparedStatement).setInt(1, idTest);
        }
    }

    @Test
    void getPracticeById_NotFound_ThrowsNoSuchElementException() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        assertThrows(NoSuchElementException.class, () -> {practiceDAO.getPracticeById(999);});
    }

    @Test
    void getPracticeById_SQLException_ThrowsDataOperationException() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de conexión"));
        assertThrows(DataOperationException.class, () -> {practiceDAO.getPracticeById(1);});
    }

    @Test
    void createPractice_NullPractice_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> practiceDAO.createPractice(null));
    }

    @Test
    void createPractice_NullStudent_ThrowsIllegalArgumentException() {
        Practice practice = new Practice(null, new EducationalExperience("123", "EE", "PER"), "2026", 0.0f);
        assertThrows(IllegalArgumentException.class, () -> practiceDAO.createPractice(practice));
    }

    @Test
    void createPractice_InvalidPeriod_ThrowsIllegalArgumentException() {
        Student student = new Student(1, "A", "B", "m", "p", "M", true, "S1", false, null, 0.0f);
        Practice practice = new Practice(student, new EducationalExperience("123", "EE", "PER"), "", 0.0f);
        assertThrows(IllegalArgumentException.class, () -> practiceDAO.createPractice(practice));
    }

    @Test
    void createPractice_Successful_ReturnsTrue() throws SQLException {
        Student student = new Student(10, "Juan", "Perez", "juan@test.com", "123", "M", true, "S21011001", false, null, 0.0f);
        EducationalExperience educationalExperience = new EducationalExperience("FEB-JUN 2026", "Prácticas", "88421");
        Practice practice = new Practice(student, educationalExperience, "FEB-JUN 2026", 9.5f);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = practiceDAO.createPractice(practice);
        assertTrue(result);
        verify(preparedStatement).setInt(1, 10);
        verify(preparedStatement).setString(2, "88421"); // Ahora sí debería recibir el NRC correcto
        verify(preparedStatement).setString(3, "FEB-JUN 2026");
        verify(preparedStatement).setFloat(4, 9.5f);
    }

    @Test
    void createPractice_SQLException_ThrowsDataOperationException() throws SQLException {
        Student student = new Student(1, "A", "B", "m", "p", "M", true, "S1", false, null, 0.0f);
        Practice practice = new Practice(student, new EducationalExperience("1", "E", "P"), "2026", 0.0f);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Duplicate entry"));
        assertThrows(DataOperationException.class, () -> practiceDAO.createPractice(practice));
    }

    @Test
    void getPracticeByEnrollment_InvalidEnrollment_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> practiceDAO.getPracticeByEnrollment(""));
    }

    @Test
    void getPracticeByEnrollment_NotFound_ReturnsNull() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        Practice practice = practiceDAO.getPracticeByEnrollment("S21011001");
        assertNull(practice);
    }

    @Test
    void getPracticeByEnrollment_Successful() throws SQLException {
        String enrollment = "S21011001";
        String nrc = "88421";
        int practiceId = 10;
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("id_practica")).thenReturn(practiceId);
        when(resultSet.getString("periodo")).thenReturn("FEB-JUN 2026");
        when(resultSet.getString("nrc")).thenReturn(nrc);
        when(resultSet.getFloat("calificacion")).thenReturn(9.0f);
        Student student = mock(Student.class);
        EducationalExperience educationalExperience = mock(EducationalExperience.class);
        try (MockedConstruction<EducationalExperienceDAO> mockEEDAO = mockConstruction(EducationalExperienceDAO.class, (mock, context) -> when(mock.getEducationalExperienceByNrc(nrc)).thenReturn(educationalExperience));
             MockedConstruction<StudentDAO> mockStudentDAO = mockConstruction(StudentDAO.class, (mock, context) -> when(mock.getStudentByEnrollment(enrollment)).thenReturn(student))) {
            Practice practice = practiceDAO.getPracticeByEnrollment(enrollment);
            assertNotNull(practice);
            assertEquals(practiceId, practice.getId());
            assertEquals(student, practice.getStudent());
            assertEquals(educationalExperience, practice.getEducationalExperience());
            verify(preparedStatement).setString(1, enrollment);
        }
    }

    @Test
    void getPracticeByEnrollment_SQLException_ThrowsDataOperationException() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de conexión"));
        assertThrows(DataOperationException.class, () -> practiceDAO.getPracticeByEnrollment("S12345"));
    }

    @Test
    void getCurrentPeriod_ReturnsCorrectFormat() {
        String expectedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String result = practiceDAO.getCurrentPeriod();
        assertNotNull(result);
        assertEquals(expectedDate, result);
        assertTrue(result.matches("\\d{4}-\\d{2}"));
    }
}
