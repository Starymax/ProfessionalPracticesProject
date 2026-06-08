import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dao.ActivityDAO;
import mx.fei.logic.dao.ProjectDAO;
import mx.fei.logic.dto.Activity;
import mx.fei.logic.dto.Enterprise;
import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.ProjectManager;
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
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class ProjectDAOTest {

    private ProjectDAO projectDAO;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private MockedStatic<DatabaseConnectionManager> databaseConnectionManager;
    private DatabaseConnectionManager mockManager;

    @BeforeEach
    void setUp() throws SQLException {
        projectDAO = new ProjectDAO();
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);
        mockManager = mock(DatabaseConnectionManager.class);
        databaseConnectionManager = Mockito.mockStatic(DatabaseConnectionManager.class);
        databaseConnectionManager.when(DatabaseConnectionManager::getInstance).thenReturn(mockManager);
        when(mockManager.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);
    }

    @AfterEach
    void tearDown() {
        if (databaseConnectionManager != null) {
            databaseConnectionManager.close();
        }
    }

    private void stubProjectResultSet(ResultSet projectResultSet) throws SQLException {
        Date date = new Date(1000000000L);
        when(projectResultSet.getInt("id_proyecto")).thenReturn(1);
        when(projectResultSet.getString("nombre_proyecto")).thenReturn("Proyecto A");
        when(projectResultSet.getString("descripcion_proyecto")).thenReturn("Descripcion");
        when(projectResultSet.getString("objetivo_general")).thenReturn("Objetivo");
        when(projectResultSet.getString("objetivos_inmediatos")).thenReturn("Inm");
        when(projectResultSet.getString("objetivos_mediatos")).thenReturn("Med");
        when(projectResultSet.getString("metodologia")).thenReturn("Metodologia");
        when(projectResultSet.getString("responsabilidades")).thenReturn("Resp");
        when(projectResultSet.getString("recursos")).thenReturn("Rec");
        when(projectResultSet.getDate("fecha_inicio")).thenReturn(date);
        when(projectResultSet.getDate("fecha_final")).thenReturn(date);
        when(projectResultSet.getBoolean("estado_activo")).thenReturn(true);
        when(projectResultSet.getInt("lugares_disponibles")).thenReturn(5);
        when(projectResultSet.getObject("id_empresa")).thenReturn(1);
        when(projectResultSet.getInt("id_empresa")).thenReturn(1);
        when(projectResultSet.getString("nombre_empresa")).thenReturn("Empresa A");
        when(projectResultSet.getString("sector")).thenReturn("Tech");
        when(projectResultSet.getString("tel_empresa")).thenReturn("111");
        when(projectResultSet.getString("correo_empresa")).thenReturn("a@test.com");
        when(projectResultSet.getString("ciudad")).thenReturn("Xalapa");
        when(projectResultSet.getLong("usuarios_directos")).thenReturn(1L);
        when(projectResultSet.getLong("usuarios_indirectos")).thenReturn(2L);
        when(projectResultSet.getBoolean("activo_empresa")).thenReturn(true);
        when(projectResultSet.getString("pais")).thenReturn("México");
        when(projectResultSet.getObject("id_responsable")).thenReturn(2);
        when(projectResultSet.getInt("id_responsable")).thenReturn(2);
        when(projectResultSet.getString("nombre_responsable")).thenReturn("Manager A");
        when(projectResultSet.getString("correo_responsable")).thenReturn("manager@test.com");
        when(projectResultSet.getString("telefono_responsable")).thenReturn("222");
        when(projectResultSet.getString("cargo")).thenReturn("Director");
    }

    private Project buildMockProject() {
        Project project = mock(Project.class);
        Enterprise enterprise = mock(Enterprise.class);
        ProjectManager projectManager = mock(ProjectManager.class);
        when(enterprise.getEnterpriseId()).thenReturn(1);
        when(projectManager.getProjectManagerId()).thenReturn(2);
        when(project.getEnterprise()).thenReturn(enterprise);
        when(project.getProjectManager()).thenReturn(projectManager);
        when(project.getNameProject()).thenReturn("Proyecto A");
        when(project.getDescriptionProject()).thenReturn("Desc");
        when(project.getGeneralObjective()).thenReturn("Obj");
        when(project.getImmediateObjectives()).thenReturn("Inm");
        when(project.getMediatesObjectives()).thenReturn("Med");
        when(project.getMethodology()).thenReturn("Met");
        when(project.getResponsibilities()).thenReturn("Resp");
        when(project.getResources()).thenReturn("Rec");
        when(project.getStartDate()).thenReturn(new Date(System.currentTimeMillis()));
        when(project.getFinalDate()).thenReturn(new Date(System.currentTimeMillis()));
        when(project.getActiveStatus()).thenReturn(true);
        when(project.getAvailablePlaces()).thenReturn(5);
        when(project.getProjectId()).thenReturn(1);
        return project;
    }

    @Test
    void getProjectById_ProjectExists_ReturnsProjectWithExpectedName() throws SQLException {
        Date date = new Date(1000000000L);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        stubProjectResultSet(resultSet);
        Enterprise expectedEnterprise = new Enterprise(1, "Empresa A", "Tech", "111", "a@test.com", "Xalapa", 1L, 2L, true, "México");
        ProjectManager expectedManager = new ProjectManager(2, "Manager A", "manager@test.com", "222", "Director", 1);
        Project expectedProject = new Project(1, "Proyecto A", "Descripcion", "Objetivo", "Inm", "Med", "Metodologia", "Resp", "Rec",
                date, date, true, 5, expectedEnterprise, expectedManager);
        Project result = projectDAO.getProjectById(1);
        assertEquals(expectedProject, result);
    }

    @Test
    void getProjectById_ProjectDoesNotExist_ThrowsNoSuchElementException() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        assertThrows(NoSuchElementException.class, () -> projectDAO.getProjectById(99));
    }

    @Test
    void getProjectById_EnterpriseLookupFails_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        stubProjectResultSet(resultSet);
        when(resultSet.getObject("id_empresa")).thenReturn(null);
        Project result = projectDAO.getProjectById(1);
        assertNull(result.getEnterprise());
    }

    @Test
    void getProjectById_ProjectManagerColumnsAreNull_ProjectHasNullProjectManager() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        stubProjectResultSet(resultSet);
        when(resultSet.getObject("id_responsable")).thenReturn(null);
        Project result = projectDAO.getProjectById(1);
        assertNull(result.getProjectManager());
    }

    @Test
    void getProjectById_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de conexion"));
        assertThrows(DataOperationException.class, () -> projectDAO.getProjectById(1));
    }

    @Test
    void getProjectById_ResultSetReadThrowsSQLException_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenThrow(new SQLException("Error leyendo cursor"));
        assertThrows(DataOperationException.class, () -> projectDAO.getProjectById(1));
    }

    @Test
    void registerProject_InsertGeneratesKey_ReturnsGeneratedId() throws SQLException {
        Project project = buildMockProject();
        ResultSet generatedKeys = mock(ResultSet.class);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(10);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        int result = projectDAO.registerProject(project);
        assertEquals(10, result);
    }

    @Test
    void registerProject_InsertGeneratesNoKey_ReturnsMinusOne() throws SQLException {
        Project project = buildMockProject();
        ResultSet generatedKeys = mock(ResultSet.class);
        when(generatedKeys.next()).thenReturn(false);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        int result = projectDAO.registerProject(project);
        assertEquals(-1, result);
    }

    @Test
    void registerProject_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        Project project = buildMockProject();
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de insercion"));
        assertThrows(DataOperationException.class, () -> projectDAO.registerProject(project));
    }

    @Test
    void getActiveProjects_OneActiveProject_ReturnsListWithOneProject() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        stubProjectResultSet(resultSet);
        List<Project> result = projectDAO.getActiveProjects();
        assertEquals(1, result.size());
    }

    @Test
    void getActiveProjects_NoActiveProjects_ReturnsEmptyList() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<Project> result = projectDAO.getActiveProjects();
        assertTrue(result.isEmpty());
    }

    @Test
    void getActiveProjects_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de red"));
        assertThrows(DataOperationException.class, () -> projectDAO.getActiveProjects());
    }

    @Test
    void getAllProjects_OneProjectRegistered_ReturnsListWithOneProject() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        stubProjectResultSet(resultSet);
        List<Project> result = projectDAO.getAllProjects();
        assertEquals(1, result.size());
    }

    @Test
    void getAllProjects_NoProjectsRegistered_ReturnsEmptyList() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<Project> result = projectDAO.getAllProjects();
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllProjects_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de red"));
        assertThrows(DataOperationException.class, () -> projectDAO.getAllProjects());
    }

    @Test
    void getAvailableProjects_ProjectHasPlacesAndActivities_ReturnsListWithOneProject() throws DataOperationException {
        Project projectWithPlaces = new Project();
        projectWithPlaces.setProjectId(1);
        projectWithPlaces.setAvailablePlaces(3);
        ProjectDAO secondProjectDAO = spy(projectDAO);
        doReturn(List.of(projectWithPlaces)).when(secondProjectDAO).getActiveProjects();
        try (MockedConstruction<ActivityDAO> mockedActivityDAO = mockConstruction(ActivityDAO.class,
                (mock, context) -> {
                    List<Activity> activities = new ArrayList<>();
                    activities.add(mock(Activity.class));
                    when(mock.getActivitiesByProjectId(1)).thenReturn(activities);
                })) {
            List<Project> result = secondProjectDAO.getAvailableProjects();
            assertEquals(1, result.size());
        }
    }

    @Test
    void getAvailableProjects_ProjectHasNoPlaces_ReturnsEmptyList() throws DataOperationException {
        Project project = mock(Project.class);
        when(project.getAvailablePlaces()).thenReturn(0);
        ProjectDAO secondProjectDAO = spy(projectDAO);
        doReturn(List.of(project)).when(secondProjectDAO).getActiveProjects();
        List<Project> result = secondProjectDAO.getAvailableProjects();
        assertTrue(result.isEmpty());
    }

    @Test
    void getAvailableProjects_GetActiveProjectsFails_ThrowsDataOperationException() throws DataOperationException {
        ProjectDAO secondProjectDAO = spy(projectDAO);
        doThrow(new DataOperationException("Error al obtener proyectos activos")).when(secondProjectDAO).getActiveProjects();
        assertThrows(DataOperationException.class, () -> secondProjectDAO.getAvailableProjects());
    }

    @Test
    void modifyProject_UpdateAffectsOneRow_ReturnsTrue() throws SQLException {
        Project project = buildMockProject();
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = projectDAO.modifyProject(project);
        assertTrue(result);
    }

    @Test
    void modifyProject_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        Project project = buildMockProject();
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de escritura"));
        assertThrows(DataOperationException.class, () -> projectDAO.modifyProject(project));
    }
}
