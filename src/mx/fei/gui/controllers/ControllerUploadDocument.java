package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIUploadDocuments;
import mx.fei.logic.dao.ExpedientDAO;
import mx.fei.logic.dto.Document;
import mx.fei.logic.dto.DocumentType;
import mx.fei.logic.exceptions.DataOperationException;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerUploadDocument {
    private GUIUploadDocuments guiUploadDocument;
    private ExpedientDAO expedientDAO;
    private Stage stage;
    private static final Logger logger = Logger.getLogger(ControllerUploadDocument.class.getName());

    public ControllerUploadDocument(GUIUploadDocuments guiUploadDocument, Stage stage) {
        this.guiUploadDocument = guiUploadDocument;
        this.expedientDAO = new ExpedientDAO();
        this.stage = stage;
    }

    public void handleSelectUploadCancelButtons(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Seleccionar" -> handleSelect();
            case "Subir" -> handleUpload();
            case "Cancelar" -> guiUploadDocument.closeWindow();
        }
    }

    private void handleSelect() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar documentos");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(stage);
        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            guiUploadDocument.processSelectedFiles(selectedFiles);
        }
    }

    private String getColumnName(DocumentType type) {
        return switch (type) {
            case COMPETENCE_EVALUATION -> "evaluacion_competencias";
            case ACCEPTANCE_LETTER -> "oficio_aceptacion";
            case WORK_PLAN -> "plan_trabajo";
            case STUDENT_SCHEDULE -> "horario";
            case LETTER_OF_RELEASE -> "carta_liberacion";
            default -> throw new IllegalArgumentException("Tipo de documento no reconocido: " + type);
        };
    }

    private void handleUpload() {
        Map<DocumentType, Document> selectedDocuments = guiUploadDocument.getSelectedDocuments();
        if (selectedDocuments.isEmpty()) {
            guiUploadDocument.showError("Selecciona al menos un documento antes de subir.");
        } else if (guiUploadDocument.showConfirmation("¿Seguro de subir estos archivos?")) {
            boolean allUploaded = true;
            StringBuilder errors = new StringBuilder();
            for (Map.Entry<DocumentType, Document> entry : selectedDocuments.entrySet()) {
                Document document = entry.getValue();
                try {
                    boolean uploaded = expedientDAO.uploadDocument(guiUploadDocument.getStudentEnrollment(), document);
                    if (uploaded) {
                        expedientDAO.loadDocument(guiUploadDocument.getStudentEnrollment(), getColumnName(document.getDocumentType()), true);
                    } else {
                        allUploaded = false;
                        errors.append("- ").append(document.getFileName()).append("\n");
                    }
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Error al subir documento: " + document.getFileName(), e);
                    allUploaded = false;
                    errors.append("- ").append(document.getFileName()).append("\n");
                } catch (DataOperationException e) {
                    logger.log(Level.SEVERE, "Error al actualizar estado del documento", e);
                    allUploaded = false;
                    errors.append("- ").append(document.getFileName()).append("\n");
                }
            }
            if (allUploaded) {
                guiUploadDocument.showSuccess("Todos los documentos se subieron exitosamente.");
                guiUploadDocument.closeWindow();
            } else {
                guiUploadDocument.showError("Los siguientes documentos no pudieron subirse:\n" + errors);
            }
        }
    }
}