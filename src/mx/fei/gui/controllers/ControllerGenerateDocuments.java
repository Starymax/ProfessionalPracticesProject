package mx.fei.gui.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.fei.gui.views.GUIGenerateAcceptanceLetter;
import mx.fei.gui.views.GUIGenerateDocuments;
import mx.fei.gui.views.GUIGenerateSelfEvaluation;
import mx.fei.logic.dao.StudentAdvanceDAO;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataOperationException;

public class ControllerGenerateDocuments {
    private GUIGenerateDocuments guiGenerateDocuments;

    public ControllerGenerateDocuments(GUIGenerateDocuments guiGenerateDocuments) {
        this.guiGenerateDocuments = guiGenerateDocuments;
    }

    public void handleButtonsGenerateDocuments(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Generar oficio de aceptación" -> handleAcceptanceLetter();
            case "Generar autoevaluación" -> handleSelfEvaluation();
            case "Regresar" -> guiGenerateDocuments.closeWindow();
        }
    }

    private void handleAcceptanceLetter() {
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

    private void handleSelfEvaluation() {
        try {
            StudentAdvanceDAO studentAdvanceDAO = new StudentAdvanceDAO();
            float totalHours = studentAdvanceDAO.getTotalHoursByIdStudent(guiGenerateDocuments.getPractice().getStudent().getUserId());
            if (totalHours < 420) {
                guiGenerateDocuments.showError("No puedes generar la autoevaluación porque aún no has completado las 420 horas requeridas. Llevas " + totalHours + " horas.");
            } else {
                Stage stage = new Stage();
                GUIGenerateSelfEvaluation guiGenerateSelfEvaluation = new GUIGenerateSelfEvaluation(guiGenerateDocuments.getPractice().getStudent());
                guiGenerateSelfEvaluation.start(stage);
            }
        } catch (DataOperationException e) {
            guiGenerateDocuments.showError("Error al verificar las horas");
        }
    }
}