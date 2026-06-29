package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIDocumentPreview;
import mx.fei.gui.views.GUIEvaluateStudent;
import mx.fei.gui.views.GUIReportPreview;
import mx.fei.logic.dao.DocumentDAO;
import mx.fei.logic.dao.PracticeDAO;
import mx.fei.logic.dao.StudentAdvanceDAO;
import mx.fei.logic.dto.Document;
import mx.fei.logic.dto.DocumentReviewItem;
import mx.fei.logic.dto.DocumentType;
import mx.fei.logic.dto.Practice;
import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerEvaluateStudent {

    private static final float REQUIRED_PRACTICE_HOURS = 420f;
    private final GUIEvaluateStudent guiEvaluateStudent;
    private final Student student;
    private Practice practice;
    private static final Logger LOGGER = Logger.getLogger(ControllerEvaluateStudent.class.getName());

    public ControllerEvaluateStudent(GUIEvaluateStudent guiEvaluateStudent, Student student) {
        this.guiEvaluateStudent = guiEvaluateStudent;
        this.student = student;
    }

    public void loadData() {
        if (student == null) {
            guiEvaluateStudent.showError("No hay un alumno seleccionado para evaluar.");
        } else {
            Project project = student.getAssignedProject();
            String projectName = (project != null) ? project.getNameProject() : "Sin proyecto asignado";
            guiEvaluateStudent.setStudentInfo(student.getEnrollment(), student.getName() + " " + student.getLastName(), projectName);
            loadHours();
            loadPractice();
            loadReports();
            loadDocuments();
        }
    }

    public void loadReports() {
        if (practice == null) {
            guiEvaluateStudent.setReports(new ArrayList<>());
        } else {
            try {
                DocumentDAO documentDAO = new DocumentDAO();
                List<Document> reports = documentDAO.getUploadedReportsByPractice(practice);
                guiEvaluateStudent.setReports(reports);
            } catch (DataOperationException e) {
                LOGGER.log(Level.SEVERE, "Error al obtener los reportes subidos del alumno", e);
                guiEvaluateStudent.showError(e.getMessage());
            }
        }
    }

    public void loadDocuments() {
        if (practice == null) {
            guiEvaluateStudent.setDocuments(new ArrayList<>());
        } else {
            try {
                List<DocumentReviewItem> items = buildReviewItems();
                guiEvaluateStudent.setDocuments(items);
            } catch (DataOperationException e) {
                LOGGER.log(Level.SEVERE, "Error al obtener los documentos del alumno", e);
                guiEvaluateStudent.showError(e.getMessage());
            }
        }
    }

    public void handlePreviewCloseButtons(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Vista previa" -> {
                previewReport();
            }
            case "Ver documento" -> {
                openDocumentPreview();
            }
            case "Cerrar" -> {
                guiEvaluateStudent.getStage().close();
            }
        }
    }

    private void loadHours() {
        try {
            StudentAdvanceDAO studentAdvanceDAO = new StudentAdvanceDAO();
            float realizedHours = studentAdvanceDAO.getTotalHoursByIdStudent(student.getUserId());
            float remainingHours = Math.max(0f, REQUIRED_PRACTICE_HOURS - realizedHours);
            guiEvaluateStudent.setHours(realizedHours, remainingHours);
        } catch (DataOperationException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener las horas del alumno", e);
            guiEvaluateStudent.showError(e.getMessage());
        }
    }

    private void loadPractice() {
        try {
            PracticeDAO practiceDAO = new PracticeDAO();
            practice = practiceDAO.getPracticeByEnrollment(student.getEnrollment());
        } catch (DataOperationException e) {
            LOGGER.log(Level.SEVERE, "Error al obtener la práctica del alumno", e);
            guiEvaluateStudent.showError(e.getMessage());
        }
    }

    private void previewReport() {
        Document selectedReport = guiEvaluateStudent.getSelectedReport();
        if (selectedReport == null) {
            guiEvaluateStudent.showError("Seleccione un reporte de la lista.");
        } else if (selectedReport.getDirectory() == null || selectedReport.getDirectory().isBlank() || !new File(selectedReport.getDirectory()).exists()) {
            guiEvaluateStudent.showError("No se encontró el archivo del reporte en el equipo.");
        } else {
            GUIReportPreview guiReportPreview = new GUIReportPreview(selectedReport, guiEvaluateStudent);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            guiReportPreview.start(stage);
        }
    }

    private void openDocumentPreview() {
        DocumentReviewItem item = guiEvaluateStudent.getSelectedDocument();
        if (item == null) {
            guiEvaluateStudent.showError("Seleccione un documento de la lista.");
        } else if (!item.isUploaded()) {
            guiEvaluateStudent.showError("Este documento no ha sido subido por el alumno.");
        } else {
            GUIDocumentPreview guiDocumentPreview = new GUIDocumentPreview(item.getDocument());
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setOnHidden(hiddenEvent -> loadDocuments());
            guiDocumentPreview.start(stage);
        }
    }

    private List<DocumentReviewItem> buildReviewItems() throws DataOperationException {
        DocumentDAO documentDAO = new DocumentDAO();
        List<Document> uploadedDocuments = documentDAO.getDocumentsForValidation(practice);
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
}
