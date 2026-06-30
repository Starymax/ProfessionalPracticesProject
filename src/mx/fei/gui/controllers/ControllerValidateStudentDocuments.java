package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIDocumentPreview;
import mx.fei.gui.views.GUIValidateStudentDocuments;
import mx.fei.logic.dao.DocumentDAO;
import mx.fei.logic.dao.PracticeDAO;
import mx.fei.logic.dto.Document;
import mx.fei.logic.dto.DocumentReviewItem;
import mx.fei.logic.dto.DocumentType;
import mx.fei.gui.utils.StudentStatusFilter;
import mx.fei.logic.dto.Practice;
import mx.fei.logic.dto.PracticeStatus;
import mx.fei.logic.dto.Student;
import mx.fei.logic.dto.StudentValidationSummary;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ControllerValidateStudentDocuments {

    private final GUIValidateStudentDocuments guiValidateStudentDocuments;
    private Student currentStudent;
    private List<StudentValidationSummary> loadedSummaries = new ArrayList<>();
    private Set<Integer> concludedStudentIds = new HashSet<>();
    private Set<Integer> enrolledStudentIds = new HashSet<>();

    public ControllerValidateStudentDocuments(GUIValidateStudentDocuments guiValidateStudentDocuments) {
        this.guiValidateStudentDocuments = guiValidateStudentDocuments;
    }

    public void loadStudents() {
        try {
            DocumentDAO documentDAO = new DocumentDAO();
            loadedSummaries = documentDAO.getStudentsWithUploadedDocuments();
            concludedStudentIds = documentDAO.getStudentIdsWithConcludedPractice();
            enrolledStudentIds = new PracticeDAO().getEnrolledStudentIds();
            applyStatusFilter();
        } catch (DataOperationException e) {
            guiValidateStudentDocuments.showError(e.getMessage());
        }
    }

    public void handleStatusFilter() {
        applyStatusFilter();
    }

    private void applyStatusFilter() {
        PracticeStatus status = PracticeStatus.fromLabel(guiValidateStudentDocuments.getSelectedStatusLabel());
        List<StudentValidationSummary> filteredSummaries = new ArrayList<>();
        for (StudentValidationSummary summary : loadedSummaries) {
            if (status == null || StudentStatusFilter.resolveStatus(summary.getStudent(), concludedStudentIds, enrolledStudentIds) == status) {
                filteredSummaries.add(summary);
            }
        }
        guiValidateStudentDocuments.loadStudents(filteredSummaries);
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
                List<DocumentReviewItem> documentReviewItems = buildReviewItems(practice);
                guiValidateStudentDocuments.showStudentDetail(currentStudent, currentStudent.getAssignedProject(), documentReviewItems);
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
        List<DocumentReviewItem> documentReviewItems = new ArrayList<>();
        for (DocumentType documentType : DocumentType.values()) {
            if (!documentType.isReport()) {
                Document matchedDocument = findDocument(uploadedDocuments, documentType);
                documentReviewItems.add(new DocumentReviewItem(documentType, matchedDocument));
            }
        }
        return documentReviewItems;
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

    public void openDocumentPreview() {
        DocumentReviewItem documentReviewItem = guiValidateStudentDocuments.getSelectedDocumentItem();
        if (documentReviewItem == null) {
            guiValidateStudentDocuments.showError("Seleccione un documento de la lista.");
        } else {
            if (!documentReviewItem.isUploaded()) {
                guiValidateStudentDocuments.showError("Este documento no ha sido subido por el alumno.");
            } else {
                GUIDocumentPreview guiDocumentPreview = new GUIDocumentPreview(documentReviewItem.getDocument());
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
