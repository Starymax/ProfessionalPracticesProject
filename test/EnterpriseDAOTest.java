import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dao.EnterpriseDAO;
import mx.fei.logic.dto.Enterprise;
import mx.fei.logic.exceptions.DataOperationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.doReturn;

public class EnterpriseDAOTest {
    private EnterpriseDAO enterpriseDAO = new EnterpriseDAO();
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private MockedStatic<DatabaseConnectionManager> databaseConnectionManager;

    @BeforeEach
    public void setUp() throws SQLException {
        enterpriseDAO = new EnterpriseDAO();
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
    void getEnterpriseById_Successful() throws SQLException {
        int idTest = 1;
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("nombre_empresa")).thenReturn("Empresa Test");
        when(resultSet.getString("sector")).thenReturn("Tecnología");
        when(resultSet.getString("telefono")).thenReturn("2281002030");
        when(resultSet.getString("correo")).thenReturn("contacto@test.com");
        when(resultSet.getString("ciudad")).thenReturn("Xalapa");
        when(resultSet.getLong("usuarios_directos")).thenReturn(100L);
        when(resultSet.getLong("usuarios_indirectos")).thenReturn(500L);
        when(resultSet.getBoolean("estado_activo")).thenReturn(true);
        when(resultSet.getString("pais")).thenReturn("México");
        Enterprise enterprise = enterpriseDAO.getEnterpriseById(idTest);
        assertNotNull(enterprise);
        assertEquals(idTest, enterprise.getEnterpriseId());
        assertEquals("Empresa Test", enterprise.getName());
        assertEquals(100L, enterprise.getDirectUsers());
        assertTrue(enterprise.isActiveStatus());
        verify(preparedStatement).setInt(1, idTest);
    }

    @Test
    void getEnterpriseById_NotFound_ThrowsNoSuchElementException() throws SQLException {
        int idTest = 999;
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        assertThrows(NoSuchElementException.class, () -> {enterpriseDAO.getEnterpriseById(idTest);});
    }

    @Test
    void getEnterpriseById_SQLException_ThrowsDataOperationException() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de conexión"));
        assertThrows(DataOperationException.class, () -> {enterpriseDAO.getEnterpriseById(1);});
    }

    @Test
    void registerEnterprise_Null_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {enterpriseDAO.registerEnterprise(null);});
    }

    @Test
    void registerEnterprise_Successful_ReturnsGeneratedId() throws SQLException {
        Enterprise enterprise = new Enterprise(0, "", "", "", "", "", 0L, 0L, false, "");
        enterprise.setName("UV Software");
        enterprise.setSector("Educación");
        enterprise.setPhoneNumber("2281000000");
        enterprise.setContactEmail("uv@software.com");
        enterprise.setCity("Xalapa");
        enterprise.setDirectUsers(10L);
        enterprise.setIndirectUsers(50L);
        enterprise.setActiveStatus(true);
        enterprise.setCountry("México");
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(77);
        int result = enterpriseDAO.registerEnterprise(enterprise);
        assertEquals(77, result);
        verify(preparedStatement).setLong(6, 10L);
        verify(preparedStatement).setLong(7, 50L);
        verify(preparedStatement).setBoolean(8, true);
    }

    @Test
    void registerEnterprise_NoIdGenerated_ReturnsMinusOne() throws SQLException {
        Enterprise enterprise = mock(Enterprise.class);
        enterprise.setName("Empresa Sin ID");
        when(connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        int result = enterpriseDAO.registerEnterprise(enterprise);
        assertEquals(-1, result);
    }

    @Test
    void registerEnterprise_SQLException_ThrowsDataOperationException() throws SQLException {
        Enterprise enterprise = mock(Enterprise.class);
        enterprise.setName("Error organización");
        when(connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de integridad"));
        assertThrows(DataOperationException.class, () -> {enterpriseDAO.registerEnterprise(enterprise);});
    }

    @Test
    void getEnterprises_Empty_ReturnsEmptyList() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<Enterprise> result = enterpriseDAO.getEnterprises();
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
    }

    @Test
    void getEnterprises_Successful_ReturnsList() throws SQLException {
        int idTest1 = 101;
        int idTest2 = 102;
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getInt("id_empresa")).thenReturn(idTest1, idTest2);
        Enterprise enterprise1 = mock(Enterprise.class);
        Enterprise enterprise2 = mock(Enterprise.class);
        EnterpriseDAO spyDAO = spy(enterpriseDAO);
        doReturn(enterprise1).when(spyDAO).getEnterpriseById(idTest1);
        doReturn(enterprise2).when(spyDAO).getEnterpriseById(idTest2);
        List<Enterprise> enterprises = spyDAO.getEnterprises();
        assertNotNull(enterprises);
        assertEquals(2, enterprises.size());
        assertEquals(enterprise1, enterprises.get(0));
        assertEquals(enterprise2, enterprises.get(1));
        verify(spyDAO).getEnterpriseById(idTest1);
        verify(spyDAO).getEnterpriseById(idTest2);
    }

    @Test
    void getEnterprises_SQLException_ThrowsDataOperationException() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de red"));
        assertThrows(DataOperationException.class, () -> {enterpriseDAO.getEnterprises();});
    }

    @Test
    void modifyEnterprise_Null_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {enterpriseDAO.modifyEnterprise(null);});
    }

    @Test
    void modifyEnterprise_Successful_ReturnsTrue() throws SQLException {
        Enterprise enterprise = new Enterprise(0, "", "", "", "", "", 0L, 0L, false, "");
        enterprise.setEnterpriseId(1);
        enterprise.setName("UV Software Actualizada");
        enterprise.setSector("Tecnología");
        enterprise.setPhoneNumber("2281001122");
        enterprise.setContactEmail("new@software.com");
        enterprise.setCity("Xalapa");
        enterprise.setDirectUsers(15L);
        enterprise.setIndirectUsers(60L);
        enterprise.setActiveStatus(true);
        enterprise.setCountry("México");
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = enterpriseDAO.modifyEnterprise(enterprise);
        assertTrue(result);
        verify(preparedStatement).setInt(10, 1);
        verify(preparedStatement).setString(1, "UV Software Actualizada");
        verify(preparedStatement).setLong(6, 15L);
    }

    @Test
    void modifyEnterprise_NotFound_ReturnsFalse() throws Exception {
        Enterprise enterprise = new Enterprise(0, "", "", "", "", "", 0L, 0L, false, "");
        enterprise.setEnterpriseId(999);
        enterprise.setName("Empresa Fantasma");
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);
        boolean result = enterpriseDAO.modifyEnterprise(enterprise);
        assertFalse(result);
        verify(preparedStatement).setInt(10, 999);
    }

    @Test
    void modifyEnterprise_SQLException_ThrowsDataOperationException() throws Exception {
        Enterprise enterprise = new Enterprise(0, "", "", "", "", "", 0L, 0L, false, "");
        enterprise.setEnterpriseId(1);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Conexión perdida"));
        assertThrows(DataOperationException.class, () -> {enterpriseDAO.modifyEnterprise(enterprise);});
    }
}