package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIGenerateAcceptanceLetter;
import mx.fei.gui.views.GUIGenerateDocuments;
import mx.fei.gui.views.GUIGenerateSelfEvaluation;
import mx.fei.logic.dao.DocumentDAO;
import mx.fei.logic.dao.StudentAdvanceDAO;
import mx.fei.logic.dto.DocumentType;
import mx.fei.logic.dto.Practice;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.stage.Modality;
import javafx.stage.Stage;

public class ControllerGenerateDocuments {
    private GUIGenerateDocuments guiGenerateDocuments;
    private final int HOURS_OBJECTIVE = 420;

    public ControllerGenerateDocuments(GUIGenerateDocuments guiGenerateDocuments) {
        this.guiGenerateDocuments = guiGenerateDocuments;
    }

    public void handleAcceptanceLetter() {
        try {
            Practice practice = guiGenerateDocuments.getPractice();
            Student student = practice.getStudent();
            DocumentDAO documentDAO = new DocumentDAO();
            if (student == null) {
                guiGenerateDocuments.showError("El estudiante no puede ser nulo");
            } else if (documentDAO.isDocumentValidated(practice, DocumentType.ACCEPTANCE_LETTER)) {
                guiGenerateDocuments.showError("El oficio de aceptación ya fue validado y no se puede volver a generar.");
            } else {
                GUIGenerateAcceptanceLetter guiGenerateAcceptanceLetter = new GUIGenerateAcceptanceLetter(student);
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                guiGenerateAcceptanceLetter.start(stage);
            }
        } catch (DataOperationException e) {
            guiGenerateDocuments.showError(e.getMessage());
        }
    }

    public void handleSelfEvaluation() {
        try {
            Practice practice = guiGenerateDocuments.getPractice();
            float totalHours = new StudentAdvanceDAO().getTotalHoursByIdStudent(practice.getStudent().getUserId());
            DocumentDAO documentDAO = new DocumentDAO();
            if (documentDAO.isDocumentValidated(practice, DocumentType.SELF_EVALUATION)) {
                guiGenerateDocuments.showError("La autoevaluación ya fue validada y no se puede volver a generar.");
            } else if (totalHours < HOURS_OBJECTIVE) {
                guiGenerateDocuments.showError("No puedes generar la autoevaluación porque aún no has completado las " + HOURS_OBJECTIVE + " horas requeridas. Llevas " + totalHours + " horas.");
            } else {
                Stage stage = new Stage();
                GUIGenerateSelfEvaluation guiGenerateSelfEvaluation = new GUIGenerateSelfEvaluation(practice.getStudent());
                guiGenerateSelfEvaluation.start(stage);
            }
        } catch (DataOperationException e) {
            guiGenerateDocuments.showError(e.getMessage());
        }
    }
}