package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.Notification;
import mx.fei.logic.exceptions.DataOperationException;
import mx.fei.logic.idao.IDAONotification;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotificationDAO implements IDAONotification {

    private static final Logger LOGGER = Logger.getLogger(NotificationDAO.class.getName());

    @Override
    public boolean sendNotification(Notification notification) throws DataOperationException {
        String query = "INSERT INTO notificacion (titulo, mensaje, id_alumno) VALUES (?,?,?)";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, notification.getTitle());
            preparedStatement.setString(2, notification.getMessage());
            preparedStatement.setInt(3, notification.getStudent().getUserId());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al enviar notificación", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al enviar la notificación.");
        }
    }

    @Override
    public List<Notification> getNotificationsByStudentId(int studentId) throws DataOperationException {
        String query = "SELECT id_notificacion, titulo, mensaje, fecha_emision, leida FROM notificacion WHERE id_alumno = ? ORDER BY fecha_emision DESC";
        List<Notification> notifications = new ArrayList<>();
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, studentId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    notifications.add(new Notification(
                            resultSet.getInt("id_notificacion"),
                            resultSet.getString("titulo"),
                            resultSet.getString("mensaje"),
                            resultSet.getTimestamp("fecha_emision"),
                            resultSet.getBoolean("leida"),
                            null
                    ));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener notificaciones del alumno", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener las notificaciones.");
        }
        return notifications;
    }

    @Override
    public boolean markAsRead(int notificationId) throws DataOperationException {
        String query = "UPDATE notificacion SET leida = TRUE WHERE id_notificacion = ?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, notificationId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al marcar notificación como leída", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al marcar la notificación como leída.");
        }
    }

    @Override
    public int countUnreadNotifications(int studentId) throws DataOperationException {
        int count = 0;
        String query = "SELECT COUNT(*) FROM notificacion WHERE id_alumno = ? AND leida = FALSE";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, studentId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    count = resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al contar notificaciones no leídas", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al contar las notificaciones no leídas.");
        }
        return count;
    }
}