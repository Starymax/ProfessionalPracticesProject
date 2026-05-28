package mx.fei.gui.controllers;

import javafx.event.ActionEvent;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.gui.views.GUISendNotificationOfAssign;
import mx.fei.logic.dao.NotificationDAO;
import mx.fei.logic.dto.Notification;
import mx.fei.logic.exceptions.DataOperationException;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerSendNotificationOfAssign {

    private static final Logger LOGGER = Logger.getLogger(ControllerSendNotificationOfAssign.class.getName());
    private final GUISendNotificationOfAssign guiSendNotificationOfAssign;
    private final NotificationDAO notificationDAO;

    public ControllerSendNotificationOfAssign(GUISendNotificationOfAssign guiSendNotificationOfAssign) {
        this.guiSendNotificationOfAssign = guiSendNotificationOfAssign;
        this.notificationDAO = new NotificationDAO();
    }

    public void handleSend(ActionEvent actionEvent) {
        String title = guiSendNotificationOfAssign.getTitleText() == null ? "" : guiSendNotificationOfAssign.getTitleText().trim();
        String message = guiSendNotificationOfAssign.getMessageText() == null ? "" : guiSendNotificationOfAssign.getMessageText().trim();
        ArrayList<String> errors = new ArrayList<>();
        GUIUtils.validateShortText(title, "Título", errors);
        GUIUtils.validateLongText(message, "Mensaje", errors);
        if (!errors.isEmpty()) {
            GUIUtils.showErrors(errors);
        } else {
            try {
                Notification notification = new Notification(0, title, message, new Date(), false, guiSendNotificationOfAssign.getStudent());
                boolean sent = notificationDAO.sendNotification(notification);
                if (!sent) {
                    GUIUtils.showError("No se pudo enviar la notificación.");
                } else {
                    GUIUtils.showSuccess("Notificación enviada correctamente.");
                    guiSendNotificationOfAssign.setWasSent(true);
                    guiSendNotificationOfAssign.close();
                }
            } catch (DataOperationException e) {
                LOGGER.log(Level.SEVERE, "Error al enviar notificación", e);
                GUIUtils.showError(e.getMessage());
            }
        }
    }

    public void handleCancel(ActionEvent actionEvent) {
        guiSendNotificationOfAssign.close();
    }
}