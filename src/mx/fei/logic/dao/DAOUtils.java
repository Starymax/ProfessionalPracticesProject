package mx.fei.logic.dao;

import mx.fei.logic.exceptions.DataOperationException;
import java.sql.SQLException;

/**
 * Utility helpers shared across the DAO layer.
 */
public class DAOUtils {
    private DAOUtils() {

    }

    public static DataOperationException convertSQLExceptiontoDataOperationException(SQLException e, String defaultMessage) {
        String message = isConnectionError(e) ? "Error de conexión. Intente más tarde." : defaultMessage;
        return new DataOperationException(message);
    }

    /**
     * Determines whether the given SQL exception represents a connection-related failure
     * (for example, a communications link failure or an 08 SQL state).
     *
     * @param e the SQL exception to inspect
     * @return true if the exception is considered a connection error
     */
    public static boolean isConnectionError(SQLException e) {
        String state = e.getSQLState();
        String className = e.getClass().getName();
        return state == null || state.startsWith("08") || className.contains("Communications") || className.contains("CJCommunications");
    }
}
