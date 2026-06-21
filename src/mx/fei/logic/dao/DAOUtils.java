package mx.fei.logic.dao;

import java.sql.SQLException;

public class DAOUtils {
    private DAOUtils() {}

    public static boolean isConnectionError(SQLException e) {
        String state = e.getSQLState();
        String className = e.getClass().getName();
        return state == null
                || state.startsWith("08")
                || className.contains("Communications")
                || className.contains("CJCommunications");
    }
}
