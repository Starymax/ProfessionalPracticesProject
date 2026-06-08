import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dao.ProfessorDAO;
import mx.fei.logic.dao.UserDAO;
import mx.fei.logic.dto.Professor;
import mx.fei.logic.dto.RegistrationStatus;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mockConstruction;

public class ProfessorDAOTest {

    private ProfessorDAO professorDAO;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private MockedStatic<DatabaseConnectionManager> databaseConnectionManager;
    private DatabaseConnectionManager mockManager;

    @BeforeEach
    void setUp() throws SQLException {
        professorDAO = new ProfessorDAO();
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
    void getProfessorByPersonalNumber_ProfessorExists_ReturnsExpectedProfessor() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("id_usuario")).thenReturn(1);
        when(resultSet.getInt("numero_de_personal")).thenReturn(12345);
        when(resultSet.getString("nombre")).thenReturn("Ana");
        when(resultSet.getString("apellidos")).thenReturn("Lopez");
        when(resultSet.getString("correo")).thenReturn("ana@uv.mx");
        when(resultSet.getString("contrasena")).thenReturn("pass123");
        when(resultSet.getBoolean("estado_activo")).thenReturn(true);
        when(resultSet.getString("genero")).thenReturn("F");
        when(resultSet.getBoolean("es_coordinador")).thenReturn(false);
        when(resultSet.getBoolean("es_administrador")).thenReturn(false);
        when(resultSet.getString("turno")).thenReturn("Matutino");
        Professor expectedProfessor = new Professor(1, "Ana", "Lopez", "ana@uv.mx", "pass123", "F", true, 12345, false, false, "Matutino");
        Professor result = professorDAO.getProfessorByPersonalNumber(12345);
        assertEquals(expectedProfessor, result);
    }

    @Test
    void getProfessorByPersonalNumber_ProfessorDoesNotExist_ReturnsNull() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        Professor result = professorDAO.getProfessorByPersonalNumber(99999);
        assertNull(result);
    }

    @Test
    void getProfessorByPersonalNumber_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de conexion"));
        assertThrows(DataOperationException.class, () -> professorDAO.getProfessorByPersonalNumber(12345));
    }

    @Test
    void getProfessorById_ProfessorIsCoordinator_ReturnsProfessorMarkedAsCoordinator() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("numero_de_personal")).thenReturn(12345);
        when(resultSet.getString("nombre")).thenReturn("Carlos");
        when(resultSet.getString("apellidos")).thenReturn("Ruiz");
        when(resultSet.getString("correo")).thenReturn("carlos@uv.mx");
        when(resultSet.getString("contrasena")).thenReturn("secret");
        when(resultSet.getBoolean("estado_activo")).thenReturn(true);
        when(resultSet.getString("genero")).thenReturn("M");
        when(resultSet.getBoolean("es_coordinador")).thenReturn(true);
        when(resultSet.getBoolean("es_administrador")).thenReturn(false);
        when(resultSet.getString("turno")).thenReturn("Vespertino");
        Professor result = professorDAO.getProfessorById(1);
        assertTrue(result.isCoordinator());
    }

    @Test
    void getProfessorById_ProfessorDoesNotExist_ReturnsNull() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        Professor result = professorDAO.getProfessorById(999);
        assertNull(result);
    }

    @Test
    void getProfessorById_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Timeout"));
        assertThrows(DataOperationException.class, () -> professorDAO.getProfessorById(1));
    }

    @Test
    void registerProfessor_NewProfessorInserted_ReturnsTrue() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        PreparedStatement insertStatement = mock(PreparedStatement.class);
        when(insertStatement.executeUpdate()).thenReturn(1);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement).thenReturn(insertStatement);
        Professor professor = mock(Professor.class);
        when(professor.getPersonalNumber()).thenReturn(11111);
        when(professor.getShift()).thenReturn("Matutino");
        when(professor.isCoordinator()).thenReturn(false);
        when(professor.isAdmin()).thenReturn(false);
        try (MockedConstruction<UserDAO> mockedUserDAO = mockConstruction(UserDAO.class, (mock, context) -> when(mock.registerUser(professor)).thenReturn(5))) {
            boolean result = professorDAO.registerProfessor(professor);
            assertTrue(result);
        }
    }

    @Test
    void registerProfessor_ProfessorIsNull_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> professorDAO.registerProfessor(null));
    }

    @Test
    void registerProfessor_PersonalNumberAlreadyExists_ThrowsIllegalStateException() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("nombre")).thenReturn("Existente");
        Professor professor = mock(Professor.class);
        when(professor.getPersonalNumber()).thenReturn(12345);
        assertThrows(IllegalStateException.class, () -> professorDAO.registerProfessor(professor));
    }

    @Test
    void registerProfessor_UserRegistrationFails_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        Professor professor = mock(Professor.class);
        when(professor.getPersonalNumber()).thenReturn(22222);
        try (MockedConstruction<UserDAO> mockedUserDAO = mockConstruction(UserDAO.class,
                (mock, context) -> when(mock.registerUser(professor)).thenReturn(RegistrationStatus.FAILURE.getValue()))) {
            assertThrows(DataOperationException.class, () -> professorDAO.registerProfessor(professor));
        }
    }

    @Test
    void registerProfessor_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        Professor professor = mock(Professor.class);
        when(professor.getPersonalNumber()).thenReturn(33333);
        try (MockedConstruction<UserDAO> mockedUserDAO = mockConstruction(UserDAO.class,
                (mock, context) -> when(mock.registerUser(professor)).thenThrow(new DataOperationException("Error SQL")))) {
            assertThrows(DataOperationException.class, () -> professorDAO.registerProfessor(professor));
        }
    }

    @Test
    void getProfessors_OneProfessorRegistered_ReturnsListWithOneProfessor() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("id_usuario")).thenReturn(1);
        when(resultSet.getInt("numero_de_personal")).thenReturn(12345);
        when(resultSet.getString("nombre")).thenReturn("Luis");
        when(resultSet.getString("apellidos")).thenReturn("Garcia");
        when(resultSet.getString("correo")).thenReturn("luis@uv.mx");
        when(resultSet.getString("contrasena")).thenReturn("pass");
        when(resultSet.getBoolean("estado_activo")).thenReturn(true);
        when(resultSet.getString("genero")).thenReturn("M");
        when(resultSet.getBoolean("es_coordinador")).thenReturn(false);
        when(resultSet.getBoolean("es_administrador")).thenReturn(false);
        when(resultSet.getString("turno")).thenReturn("Matutino");
        Professor expectedProfessor = new Professor(1, "Luis", "Garcia", "luis@uv.mx", "pass", "M", true, 12345, false, false, "Matutino");
        List<Professor> result = professorDAO.getProfessors();
        assertEquals(List.of(expectedProfessor), result);
    }

    @Test
    void getProfessors_NoProfessorsRegistered_ReturnsEmptyList() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<Professor> result = professorDAO.getProfessors();
        assertTrue(result.isEmpty());
    }

    @Test
    void getProfessors_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de red"));
        assertThrows(DataOperationException.class, () -> professorDAO.getProfessors());
    }

    @Test
    void modifyProfessor_UpdateAffectsOneRow_ReturnsTrue() throws SQLException {
        Professor professor = mock(Professor.class);
        when(professor.getPersonalNumber()).thenReturn(12345);
        when(professor.isCoordinator()).thenReturn(true);
        when(professor.isAdmin()).thenReturn(false);
        when(professor.getShift()).thenReturn("Matutino");
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = professorDAO.modifyProfessor(professor);
        assertTrue(result);
    }

    @Test
    void modifyProfessor_ProfessorIsNull_ReturnsFalse() throws SQLException {
        boolean result = professorDAO.modifyProfessor(null);
        assertFalse(result);
    }

    @Test
    void modifyProfessor_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        Professor professor = mock(Professor.class);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de escritura"));
        assertThrows(DataOperationException.class, () -> professorDAO.modifyProfessor(professor));
    }

    @Test
    void existsCoordinator_ActiveCoordinatorExists_ReturnsTrue() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        boolean result = professorDAO.existsCoordinator();
        assertTrue(result);
    }

    @Test
    void existsCoordinator_NoActiveCoordinatorExists_ReturnsFalse() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        boolean result = professorDAO.existsCoordinator();
        assertFalse(result);
    }

    @Test
    void existsCoordinator_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de conexion"));
        assertThrows(DataOperationException.class, () -> professorDAO.existsCoordinator());
    }
}
