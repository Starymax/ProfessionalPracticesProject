package mx.fei.gui.controllers;

import javafx.event.ActionEvent;
import mx.fei.gui.views.GUICustomNotification;
import mx.fei.logic.dao.DocumentDAO;
import mx.fei.logic.dao.NotificationDAO;
import mx.fei.logic.dto.Document;
import mx.fei.logic.dto.Notification;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataOperationException;

public class ControllerCustomNotification {

    private static final int MAX_MESSAGE_LENGTH = 1000;
    private final GUICustomNotification guiCustomNotification;

    public ControllerCustomNotification(GUICustomNotification guiCustomNotification) {
        this.guiCustomNotification = guiCustomNotification;
    }

    public void handleSendCancelButtons(ActionEvent event) {
        if (event.getSource() == guiCustomNotification.getButtonSend()) {
            sendNotificationAndDeleteDocument();
        } else if (event.getSource() == guiCustomNotification.getButtonCancel()) {
            guiCustomNotification.closeWindow();
        }
    }

    private void sendNotificationAndDeleteDocument() {
        String title = guiCustomNotification.getNotificationTitle();
        String message = guiCustomNotification.getNotificationMessage();
        if (title == null || title.trim().isEmpty()) {
            guiCustomNotification.showError("El título de la notificación no puede estar vacío.");
            return;
        }
        if (message == null || message.trim().isEmpty()) {
            guiCustomNotification.showError("El mensaje de la notificación no puede estar vacío.");
            return;
        }
        if (message.length() > MAX_MESSAGE_LENGTH) {
            guiCustomNotification.showError("El mensaje no puede superar los " + MAX_MESSAGE_LENGTH + " caracteres.");
            return;
        }
        Document document = guiCustomNotification.getDocument();
        Student student = document.getPractice() != null ? document.getPractice().getStudent() : null;
        try {
            DocumentDAO documentDAO = new DocumentDAO();
            boolean deleted = documentDAO.deleteDocument(document);
            if (!deleted) {
                guiCustomNotification.showError("No se pudo eliminar el documento.");
                return;
            }
            if (student != null) {
                NotificationDAO notificationDAO = new NotificationDAO();
                Notification notification = new Notification(0, title.trim(), message.trim(), null, false, student);
                notificationDAO.sendNotification(notification);
            }
            guiCustomNotification.showSuccess("Documento rechazado y eliminado. Se notificó al alumno.");
            guiCustomNotification.closeWindow();
            guiCustomNotification.closeParentPreview();
        } catch (DataOperationException e) {
            guiCustomNotification.showError(e.getMessage());
        }
    }
}
