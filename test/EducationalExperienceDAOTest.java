import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dao.EducationalExperienceDAO;
import mx.fei.logic.dao.ProfessorDAO;
import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.dto.Professor;
import mx.fei.logic.exceptions.DataOperationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;

public class EducationalExperienceDAOTest {
    private EducationalExperienceDAO educationalExperienceDAO;
    private EducationalExperienceDAO spyEducationalExperienceDAO;
    private ResultSet resultSet;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private MockedStatic<DatabaseConnectionManager> databaseConnectionManager;
    private DatabaseConnectionManager mockManager;

    @BeforeEach
    void setUp() throws SQLException {
        educationalExperienceDAO = new EducationalExperienceDAO();
        spyEducationalExperienceDAO = spy(educationalExperienceDAO);
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        mockManager = mock(DatabaseConnectionManager.class);
        databaseConnectionManager = mockStatic(DatabaseConnectionManager.class);
        databaseConnectionManager.when(DatabaseConnectionManager::getInstance).thenReturn(mockManager);
        when(mockManager.getConnection()).thenReturn(connection);
        resultSet = mock(ResultSet.class);
    }

    @AfterEach
    void tearDown() {
        if (databaseConnectionManager != null) {
            databaseConnectionManager.close();
        }
    }

    @Test
    void registerEducationalExperience_NrcAlreadyExists_ThrowsIllegalStateException() throws DataOperationException {
        EducationalExperience educationalExperience = new EducationalExperience();
        educationalExperience.setNrc("12345");
        doReturn(new EducationalExperience()).when(spyEducationalExperienceDAO).getEducationalExperienceByNrc("12345");
        assertThrows(IllegalStateException.class, () -> spyEducationalExperienceDAO.registerEducationalExperience(educationalExperience));
    }

    @Test
    void registerEducationalExperience_PeriodIsEmpty_ThrowsIllegalArgumentException() throws DataOperationException {
        EducationalExperience educationalExperience = new EducationalExperience();
        educationalExperience.setNrc("12345");
        educationalExperience.setPeriod("");
        doThrow(new NoSuchElementException()).when(spyEducationalExperienceDAO).getEducationalExperienceByNrc("12345");
        assertThrows(IllegalArgumentException.class, () -> spyEducationalExperienceDAO.registerEducationalExperience(educationalExperience));
    }

    @Test
    void registerEducationalExperience_InsertWithProfessor_ReturnsTrue() throws SQLException {
        EducationalExperience educationalExperience = new EducationalExperience();
        educationalExperience.setNrc("12345");
        educationalExperience.setName("Construcción de Software");
        educationalExperience.setEducationalProgram("Ingeniería de Software");
        educationalExperience.setPeriod("FEB-JUN 2026");
        Professor professor = mock(Professor.class);
        when(professor.getUserId()).thenReturn(99);
        educationalExperience.setProfessor(professor);
        doThrow(new NoSuchElementException()).when(spyEducationalExperienceDAO).getEducationalExperienceByNrc("12345");
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = spyEducationalExperienceDAO.registerEducationalExperience(educationalExperience);
        assertTrue(result);
    }

    @Test
    void registerEducationalExperience_InsertWithoutProfessor_ReturnsTrue() throws SQLException {
        EducationalExperience educationalExperience = new EducationalExperience();
        educationalExperience.setNrc("54321");
        educationalExperience.setPeriod("AGO-ENE 2026");
        educationalExperience.setProfessor(null);
        doThrow(new NoSuchElementException()).when(spyEducationalExperienceDAO).getEducationalExperienceByNrc("54321");
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = spyEducationalExperienceDAO.registerEducationalExperience(educationalExperience);
        assertTrue(result);
    }

    @Test
    void modifyEducationalExperience_ExperienceIsNull_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> educationalExperienceDAO.modifyEducationalExperience(null));
    }

    @Test
    void modifyEducationalExperience_UpdateWithProfessor_ReturnsTrue() throws SQLException {
        EducationalExperience educationalExperience = new EducationalExperience();
        educationalExperience.setName("Sistemas Operativos");
        educationalExperience.setEducationalProgram("Ingeniería de Software");
        educationalExperience.setNrc("12345");
        Professor professor = mock(Professor.class);
        when(professor.getUserId()).thenReturn(101);
        educationalExperience.setProfessor(professor);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = educationalExperienceDAO.modifyEducationalExperience(educationalExperience);
        assertTrue(result);
    }

    @Test
    void modifyEducationalExperience_UpdateWithoutProfessor_ReturnsTrue() throws SQLException {
        EducationalExperience educationalExperience = new EducationalExperience();
        educationalExperience.setName("Redes");
        educationalExperience.setEducationalProgram("Tecnologías de Información");
        educationalExperience.setNrc("55555");
        educationalExperience.setProfessor(null);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = educationalExperienceDAO.modifyEducationalExperience(educationalExperience);
        assertTrue(result);
    }

    @Test
    void modifyEducationalExperience_UpdateAffectsZeroRows_ReturnsFalse() throws SQLException {
        EducationalExperience educationalExperience = new EducationalExperience();
        educationalExperience.setNrc("99999");
        educationalExperience.setName("Inexistente");
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);
        boolean result = educationalExperienceDAO.modifyEducationalExperience(educationalExperience);
        assertFalse(result);
    }

    @Test
    void modifyEducationalExperience_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        EducationalExperience educationalExperience = new EducationalExperience();
        educationalExperience.setNrc("123");
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de conexión"));
        assertThrows(DataOperationException.class, () -> educationalExperienceDAO.modifyEducationalExperience(educationalExperience));
    }

    @Test
    void getEducationalExperienceByNrc_NrcIsEmpty_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> educationalExperienceDAO.getEducationalExperienceByNrc(""));
    }

    @Test
    void getEducationalExperienceByNrc_ExperienceDoesNotExist_ThrowsNoSuchElementException() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        assertThrows(NoSuchElementException.class, () -> educationalExperienceDAO.getEducationalExperienceByNrc("99999"));
    }

    @Test
    void getEducationalExperienceByNrc_ExperienceExistsWithoutProfessor_ReturnsExperienceWithExpectedNrc() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("NRC")).thenReturn("12345");
        when(resultSet.getString("nombre_experiencia")).thenReturn("Pruebas de Software");
        when(resultSet.getString("programa_educativo")).thenReturn("ISW");
        when(resultSet.getString("periodo")).thenReturn("FEB-JUN 2026");
        when(resultSet.getInt("id_profesor")).thenReturn(0);
        EducationalExperience result = educationalExperienceDAO.getEducationalExperienceByNrc("12345");
        assertEquals("12345", result.getNrc());
    }

    @Test
    void getEducationalExperienceByNrc_ExperienceExistsWithProfessor_ReturnsExperienceWithAssignedProfessor() throws SQLException {
        String nrc = "12345";
        int idProfessor = 50;
        Professor professor = mock(Professor.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("NRC")).thenReturn(nrc);
        when(resultSet.getString("nombre_experiencia")).thenReturn("Base de Datos");
        when(resultSet.getInt("id_profesor")).thenReturn(idProfessor);
        try (MockedConstruction<ProfessorDAO> mockedProfessorDAO = mockConstruction(ProfessorDAO.class, (mock, context) -> when(mock.getProfessorById(idProfessor)).thenReturn(professor))) {
            EducationalExperience result = educationalExperienceDAO.getEducationalExperienceByNrc(nrc);
            assertEquals(professor, result.getProfessor());
        }
    }

    @Test
    void getEducationalExperienceByNrc_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error DB"));
        assertThrows(DataOperationException.class, () -> educationalExperienceDAO.getEducationalExperienceByNrc("123"));
    }

    @Test
    void getEducationalExperiences_NoExperiencesRegistered_ReturnsEmptyList() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<EducationalExperience> result = educationalExperienceDAO.getEducationalExperiences();
        assertTrue(result.isEmpty());
    }

    @Test
    void getEducationalExperiences_TwoExperiencesRegistered_ReturnsListWithTwoExperiences() throws SQLException {
        String nrc1 = "11111";
        String nrc2 = "22222";
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString("NRC")).thenReturn(nrc1, nrc2);
        EducationalExperience experience1 = mock(EducationalExperience.class);
        EducationalExperience experience2 = mock(EducationalExperience.class);
        doReturn(experience1).when(spyEducationalExperienceDAO).getEducationalExperienceByNrc(nrc1);
        doReturn(experience2).when(spyEducationalExperienceDAO).getEducationalExperienceByNrc(nrc2);
        List<EducationalExperience> result = spyEducationalExperienceDAO.getEducationalExperiences();
        assertEquals(2, result.size());
    }

    @Test
    void getEducationalExperiences_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Conexión perdida"));
        assertThrows(DataOperationException.class, () -> educationalExperienceDAO.getEducationalExperiences());
    }

    @Test
    void getEducationalExperiencesByProfessor_ProfessorHasNoExperiences_ReturnsEmptyList() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<EducationalExperience> result = educationalExperienceDAO.getEducationalExperiencesByProfessor(50);
        assertTrue(result.isEmpty());
    }

    @Test
    void getEducationalExperiencesByProfessor_ProfessorHasTwoExperiences_ReturnsListWithTwoExperiences() throws SQLException {
        String nrc1 = "11111";
        String nrc2 = "22222";
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString("NRC")).thenReturn(nrc1, nrc2);
        EducationalExperience experience1 = mock(EducationalExperience.class);
        EducationalExperience experience2 = mock(EducationalExperience.class);
        doReturn(experience1).when(spyEducationalExperienceDAO).getEducationalExperienceByNrc(nrc1);
        doReturn(experience2).when(spyEducationalExperienceDAO).getEducationalExperienceByNrc(nrc2);
        List<EducationalExperience> result = spyEducationalExperienceDAO.getEducationalExperiencesByProfessor(50);
        assertEquals(2, result.size());
    }

    @Test
    void getEducationalExperiencesByProfessor_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Conexión perdida"));
        assertThrows(DataOperationException.class, () -> educationalExperienceDAO.getEducationalExperiencesByProfessor(50));
    }
}
