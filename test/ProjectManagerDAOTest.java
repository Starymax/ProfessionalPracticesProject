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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class ProjectManagerDAOTest {
    private ProjectManagerDAO projectManagerDAO;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private MockedStatic<DatabaseConnectionManager> databaseConnectionManager;

    @BeforeEach
    void setUp() throws SQLException {
        projectManagerDAO = new ProjectManagerDAO();
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);
        databaseConnectionManager = mockStatic(DatabaseConnectionManager.class);
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

    @Test
    void registerProjectManager_ProjectManagerIsNull_ReturnsFalse() throws DataOperationException {
        boolean result = projectManagerDAO.registerProjectManager(null);
        assertFalse(result);
    }

    @Test
    void registerProjectManager_ProjectManagerAlreadyExists_ReturnsFalse() throws DataOperationException {
        ProjectManager projectManager = new ProjectManager();
        projectManager.setProjectManagerId(10);
        ProjectManagerDAO secondProjectManagerDAO = spy(projectManagerDAO);
        doReturn(new ProjectManager()).when(secondProjectManagerDAO).getProjectManagerById(10);
        boolean result = secondProjectManagerDAO.registerProjectManager(projectManager);
        assertFalse(result);
    }

    @Test
    void registerProjectManager_NewProjectManagerInserted_ReturnsTrue() throws SQLException {
        ProjectManager projectManager = new ProjectManager();
        projectManager.setProjectManagerId(20);
        projectManager.setName("Ana García");
        projectManager.setEmailProjectManager("ana@empresa.com");
        projectManager.setPhoneNumberProjectManager("2281234567");
        projectManager.setRol("Líder de Proyecto");
        projectManager.setEnterpriseId(1);
        ProjectManagerDAO secondProjectManagerDAO = spy(projectManagerDAO);
        doReturn(null).when(secondProjectManagerDAO).getProjectManagerById(20);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = secondProjectManagerDAO.registerProjectManager(projectManager);
        assertTrue(result);
    }

    @Test
    void registerProjectManager_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        ProjectManager projectManager = new ProjectManager();
        projectManager.setProjectManagerId(30);
        ProjectManagerDAO secondProjectManagerDAO = spy(projectManagerDAO);
        doReturn(null).when(secondProjectManagerDAO).getProjectManagerById(30);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de inserción"));
        assertThrows(DataOperationException.class, () -> secondProjectManagerDAO.registerProjectManager(projectManager));
    }

    @Test
    void getProjectManagerById_ProjectManagerExists_ReturnsProjectManagerWithExpectedName() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("nombre_responsable")).thenReturn("Carlos Ruiz");
        when(resultSet.getString("correo_responsable")).thenReturn("cruiz@empresa.com");
        when(resultSet.getString("telefono_responsable")).thenReturn("2288112233");
        when(resultSet.getString("cargo")).thenReturn("Gerente de TI");
        when(resultSet.getInt("id_empresa")).thenReturn(1);
        ProjectManager projectManager = projectManagerDAO.getProjectManagerById(5);
        assertEquals("Carlos Ruiz", projectManager.getName());
    }

    @Test
    void getProjectManagerById_ProjectManagerDoesNotExist_ReturnsNull() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        ProjectManager projectManager = projectManagerDAO.getProjectManagerById(99);
        assertNull(projectManager);
    }

    @Test
    void getProjectManagerById_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Timeout"));
        assertThrows(DataOperationException.class, () -> projectManagerDAO.getProjectManagerById(1));
    }

    @Test
    void getProjectManagersByEnterprise_EnterpriseHasNoProjectManagers_ReturnsEmptyList() throws SQLException {
        Enterprise enterprise = new Enterprise();
        enterprise.setEnterpriseId(1);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<ProjectManager> result = projectManagerDAO.getProjectManagersByEnterprise(enterprise);
        assertTrue(result.isEmpty());
    }

    @Test
    void getProjectManagersByEnterprise_EnterpriseHasTwoProjectManagers_ReturnsListWithTwoProjectManagers() throws SQLException {
        Enterprise enterprise = new Enterprise();
        enterprise.setEnterpriseId(5);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getInt("id_responsable")).thenReturn(101, 102);
        ProjectManager projectManager1 = mock(ProjectManager.class);
        ProjectManager projectManager2 = mock(ProjectManager.class);
        ProjectManagerDAO secondProjectManagerDAO = spy(projectManagerDAO);
        doReturn(projectManager1).when(secondProjectManagerDAO).getProjectManagerById(101);
        doReturn(projectManager2).when(secondProjectManagerDAO).getProjectManagerById(102);
        List<ProjectManager> result = secondProjectManagerDAO.getProjectManagersByEnterprise(enterprise);
        assertEquals(2, result.size());
    }

    @Test
    void getProjectManagersByEnterprise_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        Enterprise enterprise = new Enterprise();
        enterprise.setEnterpriseId(1);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error DB"));
        assertThrows(DataOperationException.class, () -> projectManagerDAO.getProjectManagersByEnterprise(enterprise));
    }
}
