package mx.fei.logic.idao;

import mx.fei.logic.dto.Notification;
import mx.fei.logic.exceptions.DataOperationException;

import java.util.List;

public interface IDAONotification {
    boolean sendNotification(Notification notification) throws DataOperationException;

    List<Notification> getNotificationsByStudentId(int studentId) throws DataOperationException;

    boolean markAsRead(int notificationId) throws DataOperationException;

    int countUnreadNotifications(int studentId) throws DataOperationException;
}
