import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dao.ProjectManagerDAO;
import mx.fei.logic.dto.Enterprise;
import mx.fei.logic.dto.ProjectManager;
import mx.fei.logic.exceptions.DataOperationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProjectManagerDAOTest {
    private ProjectManagerDAO projectManagerDAO;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private MockedStatic<DatabaseConnectionManager> databaseConnectionManager;

    @BeforeEach
    void  setUp() throws SQLException {
        projectManagerDAO = new ProjectManagerDAO();
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);
        databaseConnectionManager = mockStatic(DatabaseConnectionManager.class);
        databaseConnectionManager.when(DatabaseConnectionManager::getConnection).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(connection.prepareStatement(anyString(),anyInt())).thenReturn(preparedStatement);
    }

    @AfterEach
    void tearDown() {
        if(databaseConnectionManager != null) {
            databaseConnectionManager.close();
        }
    }

    @Test
    void registerProjectManager_Null_ReturnsFalse() throws SQLException {
        boolean result = projectManagerDAO.registerProjectManager(null);
        assertFalse(result);
    }

    @Test
    void registerProjectManager_AlreadyExists_ReturnsFalse() throws SQLException {
        ProjectManager projectManager = new ProjectManager();
        projectManager.setProjectManagerId(10);
        ProjectManagerDAO spyDAO = spy(projectManagerDAO);
        doReturn(new ProjectManager()).when(spyDAO).getProjectManagerById(10);
        boolean result = spyDAO.registerProjectManager(projectManager);
        assertFalse(result);
        verify(connection, never()).prepareStatement(anyString());
    }

    @Test
    void registerProjectManager_Successful_ReturnsTrue() throws SQLException {
        ProjectManager projectManager = new ProjectManager();
        projectManager.setProjectManagerId(20);
        projectManager.setName("Ana García");
        projectManager.setEmailProjectManager("ana@empresa.com");
        projectManager.setPhoneNumberProjectManager("2281234567");
        projectManager.setRol("Líder de Proyecto");
        projectManager.setEnterpriseId(1);
        ProjectManagerDAO spyDAO = spy(projectManagerDAO);
        doReturn(null).when(spyDAO).getProjectManagerById(20);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = spyDAO.registerProjectManager(projectManager);
        assertTrue(result);
        verify(preparedStatement).setString(1, "Ana García");
        verify(preparedStatement).setInt(5, 1);
    }

    @Test
    void registerProjectManager_SQLException_ThrowsDataOperationException() throws SQLException {
        ProjectManager projectManager = new ProjectManager();
        projectManager.setProjectManagerId(30);
        ProjectManagerDAO spyDAO = spy(projectManagerDAO);
        doReturn(null).when(spyDAO).getProjectManagerById(30);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de inserción"));
        assertThrows(DataOperationException.class, () -> {spyDAO.registerProjectManager(projectManager);});
    }

    @Test
    void getProjectManagerById_Successful() throws SQLException {
        int idTest = 5;
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("nombre_responsable")).thenReturn("Carlos Ruiz");
        when(resultSet.getString("correo_responsable")).thenReturn("cruiz@empresa.com");
        when(resultSet.getString("telefono_responsable")).thenReturn("2288112233");
        when(resultSet.getString("cargo")).thenReturn("Gerente de TI");
        when(resultSet.getInt("id_empresa")).thenReturn(1);
        ProjectManager projectManager = projectManagerDAO.getProjectManagerById(idTest);
        assertNotNull(projectManager);
        assertEquals(idTest, projectManager.getProjectManagerId());
        assertEquals("Carlos Ruiz", projectManager.getName());
        assertEquals("Gerente de TI", projectManager.getRol());
        verify(preparedStatement).setInt(1, idTest);
    }

    @Test
    void getProjectManagerById_NotFound_ReturnsNull() throws SQLException {
        int idTest = 99;
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        ProjectManager projectManager = projectManagerDAO.getProjectManagerById(idTest);
        assertNull(projectManager);
    }

    @Test
    void getProjectManagerById_SQLException_ThrowsDataOperationException() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Timeout"));
        assertThrows(DataOperationException.class, () -> {projectManagerDAO.getProjectManagerById(1);});
    }

    @Test
    void getProjectManagersByEnterprise_Empty_ReturnsEmptyList() throws SQLException {
        Enterprise enterprise = new Enterprise();
        enterprise.setEnterpriseId(1);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<ProjectManager> projectManagers = projectManagerDAO.getProjectManagersByEnterprise(enterprise);
        assertTrue(projectManagers.isEmpty());
        assertEquals(0, projectManagers.size());
    }

    @Test
    void getProjectManagersByEnterprise_Successful_ReturnsList() throws SQLException {
        Enterprise enterprise = new Enterprise();
        enterprise.setEnterpriseId(5);
        int idProjetManager1 = 101;
        int idProjectManager2 = 102;
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getInt("id_responsable")).thenReturn(idProjetManager1, idProjectManager2);
        ProjectManager projectManager1 = mock(ProjectManager.class);
        ProjectManager projectManager2 = mock(ProjectManager.class);
        ProjectManagerDAO spyDAO = spy(projectManagerDAO);
        doReturn(projectManager1).when(spyDAO).getProjectManagerById(idProjetManager1);
        doReturn(projectManager2).when(spyDAO).getProjectManagerById(idProjectManager2);
        List<ProjectManager> projectManagers = spyDAO.getProjectManagersByEnterprise(enterprise);
        assertNotNull(projectManagers);
        assertEquals(2, projectManagers.size());
        assertEquals(projectManager1, projectManagers.get(0));
        assertEquals(projectManager2, projectManagers.get(1));
        verify(preparedStatement).setInt(1, 5);
        verify(spyDAO).getProjectManagerById(idProjetManager1);
        verify(spyDAO).getProjectManagerById(idProjectManager2);
    }

    @Test
    void getProjectManagersByEnterprise_SQLException_ThrowsDataOperationException() throws SQLException {
        Enterprise enterprise = new Enterprise();
        enterprise.setEnterpriseId(1);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error DB"));
        assertThrows(DataOperationException.class, () -> {projectManagerDAO.getProjectManagersByEnterprise(enterprise);});
    }
}
