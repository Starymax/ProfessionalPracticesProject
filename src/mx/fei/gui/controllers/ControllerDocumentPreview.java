package mx.fei.gui.controllers;

import javafx.event.ActionEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.fei.gui.views.GUICustomNotification;
import mx.fei.gui.views.GUIDocumentPreview;
import mx.fei.logic.dao.DocumentDAO;
import mx.fei.logic.dao.NotificationDAO;
import mx.fei.logic.dto.Document;
import mx.fei.logic.dto.Notification;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataOperationException;

public class ControllerDocumentPreview {

    private final GUIDocumentPreview guiDocumentPreview;

    public ControllerDocumentPreview(GUIDocumentPreview guiDocumentPreview) {
        this.guiDocumentPreview = guiDocumentPreview;
    }

    public void handleValidateRejectCloseButtons(ActionEvent event) {
        if (event.getSource() == guiDocumentPreview.getButtonValidate()) {
            validateDocument();
        } else if (event.getSource() == guiDocumentPreview.getButtonReject()) {
            rejectDocument();
        } else if (event.getSource() == guiDocumentPreview.getButtonClose()) {
            guiDocumentPreview.closeWindow();
        }
    }

    private void validateDocument() {
        Document document = guiDocumentPreview.getDocument();
        try {
            DocumentDAO documentDAO = new DocumentDAO();
            boolean validated = documentDAO.validateDocument(document.getId());
            if (validated) {
                notifyStudent(document, "Documento validado", "Tu documento '" + document.getDocumentType().getDocumentType() + "' ha sido validado por el coordinador.");
                guiDocumentPreview.showSuccess("Documento validado correctamente. Se notificó al alumno.");
                guiDocumentPreview.closeWindow();
            } else {
                guiDocumentPreview.showError("No se pudo validar el documento.");
            }
        } catch (DataOperationException e) {
            guiDocumentPreview.showError(e.getMessage());
        }
    }

    private void rejectDocument() {
        Document document = guiDocumentPreview.getDocument();
        GUICustomNotification guiCustomNotification = new GUICustomNotification(document, guiDocumentPreview);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        guiCustomNotification.start(stage);
    }

    private void notifyStudent(Document document, String title, String message) throws DataOperationException {
        Student student = document.getPractice() != null ? document.getPractice().getStudent() : null;
        if (student != null) {
            NotificationDAO notificationDAO = new NotificationDAO();
            Notification notification = new Notification(0, title, message, null, false, student);
            notificationDAO.sendNotification(notification);
        }
    }
}
