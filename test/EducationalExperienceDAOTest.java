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

import java.sql.*;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class EducationalExperienceDAOTest {
    private EducationalExperienceDAO educationalExperienceDAO;
    private EducationalExperienceDAO spyEE;
    private ResultSet resultSet;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private MockedStatic<DatabaseConnectionManager> databaseConnectionManager;

    @BeforeEach
    void setUp() throws SQLException {
        educationalExperienceDAO = new EducationalExperienceDAO();
        spyEE = spy(educationalExperienceDAO);
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        databaseConnectionManager = mockStatic(DatabaseConnectionManager.class);
        databaseConnectionManager.when(DatabaseConnectionManager::getConnection).thenReturn(connection);
        resultSet = mock(ResultSet.class);
    }

    @AfterEach
    void tearDown() {
        if (databaseConnectionManager != null) {
            databaseConnectionManager.close();
        }
    }

    @Test
    void registerEducationalExperience_NrcAlreadyExists_ThrowsIllegalStateException() throws SQLException {
        EducationalExperience educationalExperience = new EducationalExperience();
        educationalExperience.setNrc("12345");
        doReturn(new EducationalExperience()).when(spyEE).getEducationalExperienceByNrc("12345");
        assertThrows(IllegalStateException.class, () -> {spyEE.registerEducationalExperience(educationalExperience);});
    }

    @Test
    void registerEducationalExperience_PeriodEmpty_ThrowsIllegalArgumentException() throws SQLException {
        EducationalExperience educationalExperience = new EducationalExperience();
        educationalExperience.setNrc("12345");
        educationalExperience.setPeriod("");
        doThrow(new NoSuchElementException()).when(spyEE).getEducationalExperienceByNrc("12345");
        assertThrows(IllegalArgumentException.class, () -> {spyEE.registerEducationalExperience(educationalExperience);});
    }

    @Test
    void registerEducationalExperience_Successful_WithProfessor() throws SQLException {
        EducationalExperience educationalExperience = new EducationalExperience();
        educationalExperience.setNrc("12345");
        educationalExperience.setName("Construcción de Software");
        educationalExperience.setEducationalProgram("Ingeniería de Software");
        educationalExperience.setPeriod("FEB-JUN 2026");
        Professor mockProf = mock(Professor.class);
        when(mockProf.getUserId()).thenReturn(99);
        educationalExperience.setProfessor(mockProf);
        doThrow(new NoSuchElementException()).when(spyEE).getEducationalExperienceByNrc("12345");
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = spyEE.registerEducationalExperience(educationalExperience);
        assertTrue(result);
        verify(preparedStatement).setInt(4, 99);
        verify(preparedStatement).setString(1, "12345");
    }

    @Test
    void registerEducationalExperience_Successful_NoProfessor() throws Exception {
        EducationalExperience educationalExperience = new EducationalExperience();
        educationalExperience.setNrc("54321");
        educationalExperience.setPeriod("AGO-ENE 2026");
        educationalExperience.setProfessor(null);
        doThrow(new NoSuchElementException()).when(spyEE).getEducationalExperienceByNrc("54321");
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = spyEE.registerEducationalExperience(educationalExperience);
        assertTrue(result);
        verify(preparedStatement).setNull(4, Types.NULL);
    }

    @Test
    void modifyEducationalExperience_NullObject_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {educationalExperienceDAO.modifyEducationalExperience(null);});
    }

    @Test
    void modifyEducationalExperience_Successful_WithProfessor() throws Exception {
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
        verify(preparedStatement).setInt(3, 101);
        verify(preparedStatement).setString(4, "12345");
    }

    @Test
    void modifyEducationalExperience_Successful_NoProfessor() throws Exception {
        EducationalExperience educationalExperience = new EducationalExperience();
        educationalExperience.setName("Redes");
        educationalExperience.setEducationalProgram("Tecnologías de Información");
        educationalExperience.setNrc("55555");
        educationalExperience.setProfessor(null);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = educationalExperienceDAO.modifyEducationalExperience(educationalExperience);
        assertTrue(result);
        verify(preparedStatement).setNull(3, Types.NULL);
    }

    @Test
    void modifyEducationalExperience_NotFound_ReturnsFalse() throws Exception {
        EducationalExperience educationalExperience = new EducationalExperience();
        educationalExperience.setNrc("99999");
        educationalExperience.setName("Inexistente");
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);
        boolean result = educationalExperienceDAO.modifyEducationalExperience(educationalExperience);
        assertFalse(result);
    }

    @Test
    void modifyEducationalExperience_SQLException_ThrowsDataOperationException() throws Exception {
        EducationalExperience educationalExperience = new EducationalExperience();
        educationalExperience.setNrc("123");
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de conexión"));
        assertThrows(DataOperationException.class, () -> {educationalExperienceDAO.modifyEducationalExperience(educationalExperience);});
    }

    @Test
    void getEducationalExperienceByNrc_InvalidNrc_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> educationalExperienceDAO.getEducationalExperienceByNrc(""));
    }

    @Test
    void getEducationalExperienceByNrc_NotFound_ThrowsNoSuchElementException() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false); // No hay resultados

        assertThrows(NoSuchElementException.class, () -> educationalExperienceDAO.getEducationalExperienceByNrc("99999"));
    }

    @Test
    void getEducationalExperienceByNrc_Success_NoProfessor() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("NRC")).thenReturn("12345");
        when(resultSet.getString("nombre_experiencia")).thenReturn("Pruebas de Software");
        when(resultSet.getString("programa_educativo")).thenReturn("ISW");
        when(resultSet.getString("periodo")).thenReturn("FEB-JUN 2026");
        when(resultSet.getInt("id_profesor")).thenReturn(0);
        EducationalExperience result = educationalExperienceDAO.getEducationalExperienceByNrc("12345");
        assertNotNull(result);
        assertEquals("12345", result.getNrc());
        assertNull(result.getProfessor());
    }

    @Test
    void getEducationalExperienceByNrc_Success_WithProfessor() throws Exception {
        String nrc = "12345";
        int idProfesor = 50;
        Professor professor = mock(Professor.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("NRC")).thenReturn(nrc);
        when(resultSet.getString("nombre_experiencia")).thenReturn("Base de Datos");
        when(resultSet.getInt("id_profesor")).thenReturn(idProfesor);
        try (MockedConstruction<ProfessorDAO> mocked = mockConstruction(ProfessorDAO.class, (mock, context) -> {
                    when(mock.getProfessorById(idProfesor)).thenReturn(professor);})) {
            EducationalExperience result = educationalExperienceDAO.getEducationalExperienceByNrc(nrc);
            assertNotNull(result);
            assertEquals(professor, result.getProfessor());
        }
    }

    @Test
    void getEducationalExperienceByNrc_SQLException_ThrowsDataOperationException() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error DB"));
        assertThrows(DataOperationException.class, () -> educationalExperienceDAO.getEducationalExperienceByNrc("123"));
    }

    @Test
    void getEducationalExperiences_Empty_ReturnsEmptyList() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<EducationalExperience> result = educationalExperienceDAO.getEducationalExperiences();
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
    }

    @Test
    void getEducationalExperiences_Successful_ReturnsList() throws Exception {
        String nrc1 = "11111";
        String nrc2 = "22222";
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString("NRC")).thenReturn(nrc1, nrc2);
        EducationalExperience educationalExperience1 = mock(EducationalExperience.class);
        EducationalExperience educationalExperience2 = mock(EducationalExperience.class);
        doReturn(educationalExperience1).when(spyEE).getEducationalExperienceByNrc(nrc1);
        doReturn(educationalExperience2).when(spyEE).getEducationalExperienceByNrc(nrc2);
        List<EducationalExperience> result = spyEE.getEducationalExperiences();
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(spyEE, times(1)).getEducationalExperienceByNrc(nrc1);
        verify(spyEE, times(1)).getEducationalExperienceByNrc(nrc2);
    }

    @Test
    void getEducationalExperiences_SQLException_ThrowsDataOperationException() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Conexión perdida"));
        assertThrows(DataOperationException.class, () -> {educationalExperienceDAO.getEducationalExperiences();});
    }
}