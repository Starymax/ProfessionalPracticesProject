import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dao.EnterpriseDAO;
import mx.fei.logic.dao.ProjectDAO;
import mx.fei.logic.dao.ProjectManagerDAO;
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
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @BeforeEach
    void setUp() throws SQLException {
        projectDAO = new ProjectDAO();
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);
        databaseConnectionManager = Mockito.mockStatic(DatabaseConnectionManager.class);
        databaseConnectionManager.when(DatabaseConnectionManager::getConnection).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);
    }

    @AfterEach
    void tearDown() {
        if (databaseConnectionManager != null) {
            databaseConnectionManager.close();
        }
    }

    private void stubProjectResultSet(ResultSet resultSet) throws SQLException {
        when(resultSet.getString("nombre_proyecto")).thenReturn("Proyecto A");
        when(resultSet.getString("descripcion_proyecto")).thenReturn("Descripcion");
        when(resultSet.getString("objetivo_general")).thenReturn("Objetivo");
        when(resultSet.getString("objetivos_inmediatos")).thenReturn("Inm");
        when(resultSet.getString("objetivos_mediatos")).thenReturn("Med");
        when(resultSet.getString("metodologia")).thenReturn("Metodologia");
        when(resultSet.getString("responsabilidades")).thenReturn("Resp");
        when(resultSet.getString("recursos")).thenReturn("Rec");
        when(resultSet.getDate("fecha_inicio")).thenReturn(new Date(System.currentTimeMillis()));
        when(resultSet.getDate("fecha_final")).thenReturn(new Date(System.currentTimeMillis()));
        when(resultSet.getBoolean("estado_activo")).thenReturn(true);
        when(resultSet.getInt("lugares_disponibles")).thenReturn(5);
        when(resultSet.getInt("id_empresa")).thenReturn(1);
        when(resultSet.getInt("id_responsable")).thenReturn(2);
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
    void getProjectById_Found_ReturnsProject() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        stubProjectResultSet(resultSet);
        Enterprise mockEnterprise = mock(Enterprise.class);
        ProjectManager mockProjectManager = mock(ProjectManager.class);
        try (MockedConstruction<EnterpriseDAO> mockedEnterprise = mockConstruction(EnterpriseDAO.class,
                (mock, context) -> when(mock.getEnterpriseById(1)).thenReturn(mockEnterprise));
             MockedConstruction<ProjectManagerDAO> mockedPM = mockConstruction(ProjectManagerDAO.class,
                     (mock, context) -> when(mock.getProjectManagerById(2)).thenReturn(mockProjectManager))) {
            Project result = projectDAO.getProjectById(1);
            assertNotNull(result);
        }
    }

    @Test
    void getProjectById_NotFound_ThrowsNoSuchElementException() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        assertThrows(NoSuchElementException.class, () -> projectDAO.getProjectById(99));
    }

    @Test
    void getProjectById_EnterpriseDAOFails_ThrowsDataOperationException() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        stubProjectResultSet(resultSet);
        try (MockedConstruction<EnterpriseDAO> mockedEnterprise = mockConstruction(EnterpriseDAO.class,
                (mock, context) -> when(mock.getEnterpriseById(anyInt())).thenThrow(new DataOperationException("Error empresa")))) {
            assertThrows(DataOperationException.class, () -> projectDAO.getProjectById(1));
        }
    }

    @Test
    void getProjectById_ProjectManagerDAOFails_ThrowsDataOperationException() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        stubProjectResultSet(resultSet);
        Enterprise mockEnterprise = mock(Enterprise.class);
        try (MockedConstruction<EnterpriseDAO> mockedEnterprise = mockConstruction(EnterpriseDAO.class,
                (mock, context) -> when(mock.getEnterpriseById(anyInt())).thenReturn(mockEnterprise));
             MockedConstruction<ProjectManagerDAO> mockedPM = mockConstruction(ProjectManagerDAO.class,
                     (mock, context) -> when(mock.getProjectManagerById(anyInt())).thenThrow(new DataOperationException("Error responsable")))) {
            assertThrows(DataOperationException.class, () -> projectDAO.getProjectById(1));
        }
    }

    @Test
    void getProjectById_SQLException_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de conexion"));
        assertThrows(DataOperationException.class, () -> projectDAO.getProjectById(1));
    }

    @Test
    void getProjectById_ResultSetNextSQLException_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenThrow(new SQLException("Error leyendo cursor"));
        assertThrows(DataOperationException.class, () -> projectDAO.getProjectById(1));
    }

    @Test
    void registerProject_Successful_ReturnsGeneratedId() throws SQLException, DataOperationException {
        Project project = buildMockProject();
        ResultSet generatedKeys = mock(ResultSet.class);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(10);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        int result = projectDAO.registerProject(project);
        assertEquals(10, result);
    }

    @Test
    void registerProject_NoGeneratedKey_ReturnsMinusOne() throws SQLException, DataOperationException {
        Project project = buildMockProject();
        ResultSet generatedKeys = mock(ResultSet.class);
        when(generatedKeys.next()).thenReturn(false);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        int result = projectDAO.registerProject(project);
        assertEquals(-1, result);
    }

    @Test
    void registerProject_SQLException_ThrowsDataOperationException() throws SQLException {
        Project project = buildMockProject();
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de insercion"));
        assertThrows(DataOperationException.class, () -> projectDAO.registerProject(project));
    }

    @Test
    void getActiveProjects_WithProjects_ReturnsList() throws SQLException, DataOperationException {
        ResultSet listResultSet = mock(ResultSet.class);
        when(listResultSet.next()).thenReturn(true, false);
        when(listResultSet.getInt("id_proyecto")).thenReturn(1);
        Connection secondConnection = mock(Connection.class);
        PreparedStatement detailStatement = mock(PreparedStatement.class);
        ResultSet detailResultSet = mock(ResultSet.class);
        when(detailResultSet.next()).thenReturn(true);
        stubProjectResultSet(detailResultSet);
        when(detailStatement.executeQuery()).thenReturn(detailResultSet);
        when(secondConnection.prepareStatement(anyString())).thenReturn(detailStatement);
        databaseConnectionManager.when(DatabaseConnectionManager::getConnection).thenReturn(connection).thenReturn(secondConnection);
        when(preparedStatement.executeQuery()).thenReturn(listResultSet);
        Enterprise mockEnterprise = mock(Enterprise.class);
        ProjectManager mockProjectManager = mock(ProjectManager.class);
        try (MockedConstruction<EnterpriseDAO> mockedEnterprise = mockConstruction(EnterpriseDAO.class,
                (mock, context) -> when(mock.getEnterpriseById(anyInt())).thenReturn(mockEnterprise));
             MockedConstruction<ProjectManagerDAO> mockedPM = mockConstruction(ProjectManagerDAO.class,
                     (mock, context) -> when(mock.getProjectManagerById(anyInt())).thenReturn(mockProjectManager))) {
            List<Project> result = projectDAO.getActiveProjects();
            assertEquals(1, result.size());
        }
    }

    @Test
    void getActiveProjects_EmptyTable_ReturnsEmptyList() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<Project> result = projectDAO.getActiveProjects();
        assertTrue(result.isEmpty());
    }

    @Test
    void getActiveProjects_SQLException_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de red"));
        assertThrows(DataOperationException.class, () -> projectDAO.getActiveProjects());
    }

    @Test
    void getAllProjects_WithProjects_ReturnsList() throws SQLException, DataOperationException {
        ResultSet listResultSet = mock(ResultSet.class);
        when(listResultSet.next()).thenReturn(true, false);
        when(listResultSet.getInt("id_proyecto")).thenReturn(2);
        Connection secondConnection = mock(Connection.class);
        PreparedStatement detailStatement = mock(PreparedStatement.class);
        ResultSet detailResultSet = mock(ResultSet.class);
        when(detailResultSet.next()).thenReturn(true);
        stubProjectResultSet(detailResultSet);
        when(detailStatement.executeQuery()).thenReturn(detailResultSet);
        when(secondConnection.prepareStatement(anyString())).thenReturn(detailStatement);
        databaseConnectionManager.when(DatabaseConnectionManager::getConnection).thenReturn(connection).thenReturn(secondConnection);
        when(preparedStatement.executeQuery()).thenReturn(listResultSet);
        Enterprise mockEnterprise = mock(Enterprise.class);
        ProjectManager mockProjectManager = mock(ProjectManager.class);
        try (MockedConstruction<EnterpriseDAO> mockedEnterprise = mockConstruction(EnterpriseDAO.class,
                (mock, context) -> when(mock.getEnterpriseById(anyInt())).thenReturn(mockEnterprise));
             MockedConstruction<ProjectManagerDAO> mockedPM = mockConstruction(ProjectManagerDAO.class,
                     (mock, context) -> when(mock.getProjectManagerById(anyInt())).thenReturn(mockProjectManager))) {
            List<Project> result = projectDAO.getAllProjects();
            assertEquals(1, result.size());
        }
    }

    @Test
    void getAllProjects_EmptyTable_ReturnsEmptyList() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<Project> result = projectDAO.getAllProjects();
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllProjects_SQLException_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de red"));
        assertThrows(DataOperationException.class, () -> projectDAO.getAllProjects());
    }

    @Test
    void getAvailableProjects_WithAvailablePlaces_ReturnsList() throws DataOperationException {
        Project projectWithPlaces = mock(Project.class);
        when(projectWithPlaces.getAvailablePlaces()).thenReturn(3);
        ProjectDAO spyProjectDAO = spy(projectDAO);
        doReturn(List.of(projectWithPlaces)).when(spyProjectDAO).getActiveProjects();
        List<Project> result = spyProjectDAO.getAvailableProjects();
        assertEquals(1, result.size());
    }

    @Test
    void getAvailableProjects_NoAvailablePlaces_ReturnsEmptyList() throws DataOperationException {
        Project projectFull = mock(Project.class);
        when(projectFull.getAvailablePlaces()).thenReturn(0);
        ProjectDAO spyProjectDAO = spy(projectDAO);
        doReturn(List.of(projectFull)).when(spyProjectDAO).getActiveProjects();
        List<Project> result = spyProjectDAO.getAvailableProjects();
        assertTrue(result.isEmpty());
    }

    @Test
    void getAvailableProjects_GetActiveProjectsFails_ThrowsDataOperationException() throws DataOperationException {
        ProjectDAO spyProjectDAO = spy(projectDAO);
        doThrow(new DataOperationException("Error al obtener proyectos activos")).when(spyProjectDAO).getActiveProjects();
        assertThrows(DataOperationException.class, () -> spyProjectDAO.getAvailableProjects());
    }

    @Test
    void modifyProject_Successful_ReturnsTrue() throws SQLException, DataOperationException {
        Project project = buildMockProject();
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = projectDAO.modifyProject(project);
        assertTrue(result);
    }

    @Test
    void modifyProject_SQLException_ThrowsDataOperationException() throws SQLException {
        Project project = buildMockProject();
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de escritura"));
        assertThrows(DataOperationException.class, () -> projectDAO.modifyProject(project));
    }
}