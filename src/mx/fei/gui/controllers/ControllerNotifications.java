package mx.fei.gui.controllers;

import mx.fei.gui.views.GUINotifications;
import mx.fei.logic.dao.NotificationDAO;
import mx.fei.logic.dto.Notification;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.event.ActionEvent;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerNotifications {

    private static final Logger LOGGER = Logger.getLogger(ControllerNotifications.class.getName());
    private final GUINotifications guiNotifications;
    private final NotificationDAO notificationDAO;

    public ControllerNotifications(GUINotifications guiNotifications) {
        this.guiNotifications = guiNotifications;
        this.notificationDAO  = new NotificationDAO();
    }

    public void loadNotifications() {
        try {
            List<Notification> notifications = notificationDAO.getNotificationsByStudentId(guiNotifications.getStudent().getUserId());
            guiNotifications.loadNotifications(notifications);
        } catch (DataOperationException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar notificaciones", e);
            guiNotifications.showError(e.getMessage());
        }
    }

    public void handleClose(ActionEvent event) {
        guiNotifications.getStage().close();
    }

    public void markNotificationAsRead(Notification notification) {
        try {
            boolean updated = notificationDAO.markAsRead(notification.getId());
            if (updated) {
                notification.setRead(true);
                guiNotifications.refreshList();
            }
        } catch (DataOperationException e) {
            LOGGER.log(Level.SEVERE, "Error al marcar notificación como leída", e);
        }
    }
}