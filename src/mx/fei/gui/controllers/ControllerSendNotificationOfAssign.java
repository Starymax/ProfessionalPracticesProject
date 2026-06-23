package mx.fei.gui.controllers;

import mx.fei.gui.utils.GUIUtils;
import mx.fei.gui.views.GUISendNotificationOfAssign;
import mx.fei.logic.dto.Notification;
import mx.fei.logic.exceptions.DataOperationException;
import mx.fei.logic.dao.NotificationDAO;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;

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

    public void handleSendCancelButtons(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Enviar" -> {
                send();
            }
            case "Cancelar" -> {
                guiSendNotificationOfAssign.close();
            }
        }
    }

    private void send() {
        String title = guiSendNotificationOfAssign.getTitleText() == null ? "" : guiSendNotificationOfAssign.getTitleText().trim();
        String message = guiSendNotificationOfAssign.getMessageText() == null ? "" : guiSendNotificationOfAssign.getMessageText().trim();
        ArrayList<String> errors = new ArrayList<>();
        GUIUtils.validateShortText(title, "Título", errors);
        GUIUtils.validateLongText(message, "Mensaje", errors);
        if (!errors.isEmpty()) {
            guiSendNotificationOfAssign.showErrors(errors);
        } else {
            try {
                Notification notification = new Notification(0, title, message, new Date(), false, guiSendNotificationOfAssign.getStudent());
                boolean sent = notificationDAO.sendNotification(notification);
                if (!sent) {
                    guiSendNotificationOfAssign.showError("No se pudo enviar la notificación.");
                } else {
                    guiSendNotificationOfAssign.showSuccess("Notificación enviada correctamente.");
                    guiSendNotificationOfAssign.setWasSent(true);
                    guiSendNotificationOfAssign.close();
                }
            } catch (DataOperationException e) {
                LOGGER.log(Level.SEVERE, "Error al enviar notificación", e);
                guiSendNotificationOfAssign.showError(e.getMessage());
            }
        }
    }
}