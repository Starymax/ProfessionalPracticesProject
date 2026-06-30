package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIGenerateAcceptanceLetter;
import mx.fei.gui.views.GUIGenerateDocuments;
import mx.fei.gui.views.GUIGenerateSelfEvaluation;
import mx.fei.logic.dao.StudentAdvanceDAO;
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
        Student student = guiGenerateDocuments.getPractice().getStudent();
        if(student == null) {
            guiGenerateDocuments.showError("El estudiante no puede ser nulo");
        } else {
            GUIGenerateAcceptanceLetter guiGenerateAcceptanceLetter = new GUIGenerateAcceptanceLetter(student);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            guiGenerateAcceptanceLetter.start(stage);
        }
    }

    public void handleSelfEvaluation() {
        try {
            StudentAdvanceDAO studentAdvanceDAO = new StudentAdvanceDAO();
            float totalHours = studentAdvanceDAO.getTotalHoursByIdStudent(guiGenerateDocuments.getPractice().getStudent().getUserId());
            if (totalHours < HOURS_OBJECTIVE) {
                guiGenerateDocuments.showError("No puedes generar la autoevaluación porque aún no has completado las " + HOURS_OBJECTIVE + " horas requeridas. Llevas " + totalHours + " horas.");
            } else {
                Stage stage = new Stage();
                GUIGenerateSelfEvaluation guiGenerateSelfEvaluation = new GUIGenerateSelfEvaluation(guiGenerateDocuments.getPractice().getStudent());
                guiGenerateSelfEvaluation.start(stage);
            }
        } catch (DataOperationException e) {
            guiGenerateDocuments.showError(e.getMessage());
        }
    }
}