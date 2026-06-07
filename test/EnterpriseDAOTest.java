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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.doReturn;

public class EnterpriseDAOTest {
    private EnterpriseDAO enterpriseDAO;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private MockedStatic<DatabaseConnectionManager> databaseConnectionManager;
    private DatabaseConnectionManager mockManager;

    @BeforeEach
    public void setUp() throws SQLException {
        enterpriseDAO = new EnterpriseDAO();
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
    void getEnterpriseById_EnterpriseExists_ReturnsEnterpriseWithExpectedName() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("id_empresa")).thenReturn(1);
        when(resultSet.getString("nombre_empresa")).thenReturn("Empresa Test");
        when(resultSet.getString("sector")).thenReturn("Tecnología");
        when(resultSet.getString("telefono")).thenReturn("2281234567");
        when(resultSet.getString("correo")).thenReturn("empresa@test.com");
        when(resultSet.getString("ciudad")).thenReturn("Xalapa");
        when(resultSet.getLong("usuarios_directos")).thenReturn(10L);
        when(resultSet.getLong("usuarios_indirectos")).thenReturn(50L);
        when(resultSet.getBoolean("estado_activo")).thenReturn(true);
        when(resultSet.getString("pais")).thenReturn("México");
        Enterprise enterprise = enterpriseDAO.getEnterpriseById(1);
        assertEquals("Empresa Test", enterprise.getName());
    }

    @Test
    void getEnterpriseById_EnterpriseDoesNotExist_ThrowsNoSuchElementException() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        assertThrows(NoSuchElementException.class, () -> enterpriseDAO.getEnterpriseById(999));
    }

    @Test
    void getEnterpriseById_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de conexión"));
        assertThrows(DataOperationException.class, () -> enterpriseDAO.getEnterpriseById(1));
    }

    @Test
    void registerEnterprise_EnterpriseIsNull_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> enterpriseDAO.registerEnterprise(null));
    }

    @Test
    void registerEnterprise_InsertGeneratesKey_ReturnsGeneratedId() throws SQLException {
        Enterprise enterprise = new Enterprise(0, "UV Software", "Educación", "2281000000", "uv@software.com", "Xalapa", 10L, 50L, true, "México");
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(77);
        int result = enterpriseDAO.registerEnterprise(enterprise);
        assertEquals(77, result);
    }

    @Test
    void registerEnterprise_InsertGeneratesNoKey_ReturnsMinusOne() throws SQLException {
        Enterprise enterprise = mock(Enterprise.class);
        when(connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        int result = enterpriseDAO.registerEnterprise(enterprise);
        assertEquals(-1, result);
    }

    @Test
    void registerEnterprise_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        Enterprise enterprise = mock(Enterprise.class);
        when(connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de integridad"));
        assertThrows(DataOperationException.class, () -> enterpriseDAO.registerEnterprise(enterprise));
    }

    @Test
    void getEnterprises_NoEnterprisesRegistered_ReturnsEmptyList() throws DataOperationException, SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<Enterprise> result = enterpriseDAO.getEnterprises();
        assertTrue(result.isEmpty());
    }

    @Test
    void getEnterprises_TwoEnterprisesRegistered_ReturnsListWithTwoEnterprises() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getInt("id_empresa")).thenReturn(1, 2);
        when(resultSet.getString("nombre_empresa")).thenReturn("Empresa A", "Empresa B");
        when(resultSet.getString("sector")).thenReturn("Tech", "Salud");
        when(resultSet.getString("telefono")).thenReturn("111", "222");
        when(resultSet.getString("correo")).thenReturn("a@test.com", "b@test.com");
        when(resultSet.getString("ciudad")).thenReturn("Xalapa", "Veracruz");
        when(resultSet.getLong("usuarios_directos")).thenReturn(1L, 2L);
        when(resultSet.getLong("usuarios_indirectos")).thenReturn(3L, 4L);
        when(resultSet.getBoolean("estado_activo")).thenReturn(true, true);
        when(resultSet.getString("pais")).thenReturn("México", "México");
        List<Enterprise> result = enterpriseDAO.getEnterprises();
        assertEquals(2, result.size());
    }

    @Test
    void getEnterprises_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de red"));
        assertThrows(DataOperationException.class, () -> enterpriseDAO.getEnterprises());
    }

    @Test
    void modifyEnterprise_EnterpriseIsNull_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> enterpriseDAO.modifyEnterprise(null));
    }

    @Test
    void modifyEnterprise_UpdateAffectsOneRow_ReturnsTrue() throws SQLException {
        Enterprise enterprise = new Enterprise(1, "UV Software", "Tecnología", "2281001122", "new@software.com", "Xalapa", 15L, 60L, true, "México");
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = enterpriseDAO.modifyEnterprise(enterprise);
        assertTrue(result);
    }

    @Test
    void modifyEnterprise_UpdateAffectsZeroRows_ReturnsFalse() throws SQLException {
        Enterprise enterprise = new Enterprise(999, "Empresa Fantasma", "Sector", "2281000000", "fantasma@test.com", "Xalapa", 0L, 0L, false, "México");
        when(preparedStatement.executeUpdate()).thenReturn(0);
        boolean result = enterpriseDAO.modifyEnterprise(enterprise);
        assertFalse(result);
    }

    @Test
    void modifyEnterprise_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        Enterprise enterprise = new Enterprise(1, "Empresa", "Sector", "2281000000", "empresa@test.com", "Xalapa", 0L, 0L, true, "México");
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Conexión perdida"));
        assertThrows(DataOperationException.class, () -> enterpriseDAO.modifyEnterprise(enterprise));
    }
}
