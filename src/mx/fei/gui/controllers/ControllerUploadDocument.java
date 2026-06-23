package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIUploadDocuments;
import mx.fei.logic.dao.DocumentDAO;
import mx.fei.logic.dao.PracticeDAO;
import mx.fei.logic.dto.Document;
import mx.fei.logic.dto.DocumentType;
import mx.fei.logic.dto.Practice;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerUploadDocument {
    private GUIUploadDocuments guiUploadDocument;
    private Practice practice;
    private DocumentDAO documentDAO;
    private Stage stage;
    private static final Logger LOGGER = Logger.getLogger(ControllerUploadDocument.class.getName());

    public ControllerUploadDocument(GUIUploadDocuments guiUploadDocument, Stage stage) {
        this.guiUploadDocument = guiUploadDocument;
        this.practice = guiUploadDocument.getPractice();
        if (practice == null) {
            guiUploadDocument.showError("No se encontró su práctica");
            guiUploadDocument.closeWindow();
        }
        this.documentDAO = new DocumentDAO();
        this.stage = stage;
        loadUploadedDocument();
    }

    public void handleSelectUploadCancelButtons(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Seleccionar" -> {
                handleSelect();
            }
            case "Subir" -> {
                handleUpload();
            }
            case "Cancelar" -> {
                guiUploadDocument.closeWindow();
            }
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
            LOGGER.log(Level.SEVERE, "Error al cargar los documentos subidos.", e);
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
        boolean proceed = true;
        if (selectedDocuments.isEmpty()) {
            guiUploadDocument.showError("Selecciona al menos un documento antes de subir.");
            proceed = false;
        }
        if (proceed && !guiUploadDocument.showConfirmation("¿Seguro de subir estos archivos?")) {
            proceed = false;
        }
        if (proceed) {
            Practice currentPractice = getCurrentPractice();
            if (currentPractice != null) {
                processDocuments(selectedDocuments, currentPractice);
            }
        }
    }

    private Practice getCurrentPractice() {
        Practice currentPractice = null;
        try {
            PracticeDAO practiceDAO = new PracticeDAO();
            currentPractice = practiceDAO.getPracticeByEnrollment(guiUploadDocument.getStudentEnrollment());
            if (currentPractice == null || currentPractice.getId() == 0) {
                guiUploadDocument.showError("No se encontró una la practica del estudiante.");
                currentPractice = null;
            }
        } catch (DataOperationException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener la práctica del estudiante.", e);
            guiUploadDocument.showError("Hubp un error al recuperar los datos de la práctica del estudiante.");
        }
        return  currentPractice;
    }

    private void processDocuments(Map<DocumentType, Document> selectedDocuments, Practice currentPractice) {
        boolean allUploaded = true;
        StringBuilder errors = new StringBuilder();
        boolean prerequisitesChecked = false;
        boolean prerequisitesMet = false;
        for (Document document : new ArrayList<>(selectedDocuments.values())) {
            boolean canUpload = true;
            if (document.getDocumentType().isReport()) {
                if (!prerequisitesChecked) {
                    prerequisitesMet = checkReportPrerequisites(currentPractice);
                    prerequisitesChecked = true;
                }
                canUpload = prerequisitesMet;
                if (!canUpload) {
                    errors.append("- ").append(document.getName()).append(" Requiere la carta de aceptación y el horario validados por el coordinador\n");
                }
            }
            if (canUpload) {
                boolean success = uploadSingleDocument(document, currentPractice, errors);
                if (!success) {
                    allUploaded = false;
                }
            } else {
                allUploaded = false;
            }
        }
        if (allUploaded) {
            guiUploadDocument.showSuccess("Todos los documentos se subieron exitosamente.");
            guiUploadDocument.closeWindow();
        } else {
            guiUploadDocument.showError("Los siguiente documentos no pudieron procesarse:\n" + errors);
        }
    }

    private boolean checkReportPrerequisites(Practice currentPractice) {
        boolean areDocumentsUploaded = false;
        try {
            areDocumentsUploaded = documentDAO.areInitialDocumentsUploaded(currentPractice);
        } catch (DataOperationException e) {
            LOGGER.log(Level.SEVERE, "Error al verificar los prerrequisitos de los reportes.", e);
        }
        return areDocumentsUploaded;
    }

    private boolean uploadSingleDocument(Document document, Practice currentPractice, StringBuilder errors) {
        boolean succes = false;
        try {
            String targetPath = documentDAO.uploadDocument(guiUploadDocument.getStudentEnrollment(), document);
            document.setDirectory(targetPath);
            int newId = documentDAO.loadDocument(currentPractice, document);
            if (newId <=0) {
                errors.append("- ").append(document.getName()).append(" (Error en BD\n)");
            } else {
                guiUploadDocument.markAsUploaded(document.getDocumentType());
                succes = true;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al subir el documento: " + document.getName(), e);
            errors.append("- ").append(document.getName()).append(" (Fallo IO\n)");
        } catch (DataOperationException e) {
            LOGGER.log(Level.SEVERE, "Error de base de datos al guardar: " + document.getName(), e);
            errors.append("- ").append(document.getName()).append(" (Fallo BD\n)");
        }
        return succes;
    }
}