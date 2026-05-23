package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIUploadDocuments;
import mx.fei.logic.dao.DocumentDAO;
import mx.fei.logic.dao.PracticeDAO;
import mx.fei.logic.dao.StudentDAO;
import mx.fei.logic.dto.Document;
import mx.fei.logic.dto.DocumentType;
import mx.fei.logic.dto.Practice;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataOperationException;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerUploadDocument {
    private GUIUploadDocuments guiUploadDocument;
    private DocumentDAO documentDAO;
    private Stage stage;
    private static final Logger logger = Logger.getLogger(ControllerUploadDocument.class.getName());

    public ControllerUploadDocument(GUIUploadDocuments guiUploadDocument, Stage stage) {
        this.guiUploadDocument = guiUploadDocument;
        this.documentDAO = new DocumentDAO();
        this.stage = stage;
        loadUploadedDocument();
    }

    public void handleSelectUploadCancelButtons(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Seleccionar" -> handleSelect();
            case "Subir" -> handleUpload();
            case "Cancelar" -> guiUploadDocument.closeWindow();
        }
    }

    private void loadUploadedDocument() {
        try {
            PracticeDAO practiceDAO = new PracticeDAO();
            Practice practice = practiceDAO.getPracticeByEnrollment(guiUploadDocument.getStudentEnrollment());
            List<Document> uploaded = documentDAO.getDocumentsByPractice(practice);
            guiUploadDocument.setUploadedDocuments(uploaded);
        } catch (NoSuchElementException e) {

        } catch (DataOperationException e) {
            logger.log(Level.SEVERE, "Error al cargar los documentos subidos.", e);
        }
    }

    private void handleSelect() {
        String selectedTypeStr = guiUploadDocument.getSelectedType();
        if (selectedTypeStr == null || selectedTypeStr.isEmpty()) {
            guiUploadDocument.showError("Por favor, selecciona una categoría y un tipo antes de buscar el archivo.");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar documento para: " + selectedTypeStr);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            guiUploadDocument.processSingleFile(selectedFile, selectedTypeStr);
        }
    }

    private void handleUpload() {
        Map<DocumentType, Document> selectedDocuments = guiUploadDocument.getSelectedDocuments();
        if (selectedDocuments.isEmpty()) {
            guiUploadDocument.showError("Selecciona al menos un documento antes de subir.");
        } else if (guiUploadDocument.showConfirmation("¿Seguro de subir estos archivos?")) {
            boolean canProceed = false;
            Practice currentPractice = null;
            try {
                StudentDAO studentDAO = new StudentDAO();
                Student student = studentDAO.getStudentByEnrollment(guiUploadDocument.getStudentEnrollment());
                PracticeDAO practiceDAO = new PracticeDAO();
                currentPractice = practiceDAO.getPracticeByEnrollment(guiUploadDocument.getStudentEnrollment());
                if (currentPractice != null && currentPractice.getId() != 0) {
                    canProceed = true;
                } else {
                    guiUploadDocument.showError("No se encontró una práctica activa para este estudiante.");
                }
            } catch (DataOperationException e) {
                logger.log(Level.SEVERE, "Error al obtener la práctica del estudiante", e);
                guiUploadDocument.showError("Hubo un error al recuperar los datos de la práctica del estudiante.");
            }
            if (canProceed) {
                boolean allUploaded = true;
                StringBuilder errors = new StringBuilder();
                for (Map.Entry<DocumentType, Document> entry : selectedDocuments.entrySet()) {
                    Document document = entry.getValue();
                    try {
                        boolean physicalUpload = documentDAO.uploadDocument(guiUploadDocument.getStudentEnrollment(), document);
                        if (physicalUpload) {
                            int newId = documentDAO.loadDocument(currentPractice, document);
                            if (newId <= 0) {
                                allUploaded = false;
                                errors.append("- ").append(document.getName()).append(" (Error en BD)\n");
                            } else {
                                guiUploadDocument.markAsUploaded(document.getDocumentType());
                            }
                        } else {
                            allUploaded = false;
                            errors.append("- ").append(document.getName()).append(" (Error al mover archivo)\n");
                        }
                    } catch (IOException e) {
                        logger.log(Level.SEVERE, "Error al subir físicamente el documento: " + document.getName(), e);
                        allUploaded = false;
                        errors.append("- ").append(document.getName()).append(" (Fallo IO)\n");
                    } catch (DataOperationException e) {
                        logger.log(Level.SEVERE, "Error de base de datos al guardar: " + document.getName(), e);
                        allUploaded = false;
                        errors.append("- ").append(document.getName()).append(" (Fallo BD)\n");
                    }
                }
                if (allUploaded) {
                    guiUploadDocument.showSuccess("Todos los documentos se subieron y registraron exitosamente.");
                    guiUploadDocument.closeWindow();
                } else {
                    guiUploadDocument.showError("Los siguientes documentos no pudieron procesarse:\n" + errors);
                }
            }
        }
    }
}