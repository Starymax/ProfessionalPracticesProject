package mx.fei.gui.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.fei.gui.views.GUIDocumentPreview;
import mx.fei.gui.views.GUIValidateStudentDocuments;
import mx.fei.logic.dao.DocumentDAO;
import mx.fei.logic.dao.PracticeDAO;
import mx.fei.logic.dto.Document;
import mx.fei.logic.dto.DocumentReviewItem;
import mx.fei.logic.dto.DocumentType;
import mx.fei.logic.dto.Practice;
import mx.fei.logic.dto.Student;
import mx.fei.logic.dto.StudentValidationSummary;
import mx.fei.logic.exceptions.DataOperationException;

import java.util.ArrayList;
import java.util.List;

public class ControllerValidateStudentDocuments {

    private final GUIValidateStudentDocuments guiValidateStudentDocuments;
    private Student currentStudent;

    public ControllerValidateStudentDocuments(GUIValidateStudentDocuments guiValidateStudentDocuments) {
        this.guiValidateStudentDocuments = guiValidateStudentDocuments;
    }

    public void loadStudents() {
        try {
            DocumentDAO documentDAO = new DocumentDAO();
            List<StudentValidationSummary> summaries = documentDAO.getStudentsWithUploadedDocuments();
            guiValidateStudentDocuments.loadStudents(summaries);
        } catch (DataOperationException e) {
            guiValidateStudentDocuments.showError(e.getMessage());
        }
    }

    public void onStudentSelected() {
        StudentValidationSummary summary = guiValidateStudentDocuments.getSelectedStudentSummary();
        if (summary != null) {
            currentStudent = summary.getStudent();
            refreshStudentDetail();
        }
    }

    private void refreshStudentDetail() {
        if (currentStudent != null) {
            try {
                PracticeDAO practiceDAO = new PracticeDAO();
                Practice practice = practiceDAO.getPracticeByEnrollment(currentStudent.getEnrollment());
                List<DocumentReviewItem> items = buildReviewItems(practice);
                guiValidateStudentDocuments.showStudentDetail(currentStudent, currentStudent.getAssignedProject(), items);
            } catch (DataOperationException e) {
                guiValidateStudentDocuments.showError(e.getMessage());
            }
        }
    }

    private List<DocumentReviewItem> buildReviewItems(Practice practice) throws DataOperationException {
        List<Document> uploadedDocuments = new ArrayList<>();
        if (practice != null) {
            DocumentDAO documentDAO = new DocumentDAO();
            uploadedDocuments = documentDAO.getDocumentsForValidation(practice);
        }
        List<DocumentReviewItem> items = new ArrayList<>();
        for (DocumentType documentType : DocumentType.values()) {
            if (!documentType.isReport()) {
                Document matchedDocument = findDocument(uploadedDocuments, documentType);
                items.add(new DocumentReviewItem(documentType, matchedDocument));
            }
        }
        return items;
    }

    private Document findDocument(List<Document> documents, DocumentType documentType) {
        Document found = null;
        for (Document document : documents) {
            if (document.getDocumentType() == documentType) {
                found = document;
                break;
            }
        }
        return found;
    }

    public void handleReviewCloseButtons(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Ver / Revisar documento" -> {
                openDocumentPreview();
            }
            case "Cerrar" -> {
                guiValidateStudentDocuments.closeWindow();
            }
        }
    }

    private void openDocumentPreview() {
        DocumentReviewItem item = guiValidateStudentDocuments.getSelectedDocumentItem();
        if (item == null) {
            guiValidateStudentDocuments.showError("Seleccione un documento de la lista.");
        } else {
            if (!item.isUploaded()) {
                guiValidateStudentDocuments.showError("Este documento no ha sido subido por el alumno.");
            } else {
                GUIDocumentPreview guiDocumentPreview = new GUIDocumentPreview(item.getDocument());
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setOnHidden(hiddenEvent -> {
                    loadStudents();
                    refreshStudentDetail();
                });
                guiDocumentPreview.start(stage);
            }
        }
    }
}
