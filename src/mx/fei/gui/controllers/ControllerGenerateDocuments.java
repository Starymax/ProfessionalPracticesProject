package mx.fei.gui.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.fei.gui.views.GUIGenerateAcceptanceLetter;
import mx.fei.gui.views.GUIGenerateDocuments;
import mx.fei.logic.dto.Student;

public class ControllerGenerateDocuments {
    private GUIGenerateDocuments guiGenerateDocuments;

    public ControllerGenerateDocuments(GUIGenerateDocuments guiGenerateDocuments) {
        this.guiGenerateDocuments = guiGenerateDocuments;
    }

    public void handleButtonsGenerateDocuments(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Generar solicitud de prácticas" -> handlePracticeRequest();
            case "Generar oficio de aceptación" -> handleAcceptanceLetter();
            case "Generar autoevaluación" -> handleSelfEvaluation();
            case "Regresar" -> guiGenerateDocuments.closeWindow();
        }
    }

    private void handlePracticeRequest() {

    }

    private void handleAcceptanceLetter() {
        Student student = guiGenerateDocuments.getPractice().getStudent();
        if(student == null) {
            guiGenerateDocuments.showError("El estudiante no puede ser nulo");
        } else {
            GUIGenerateAcceptanceLetter  guiGenerateAcceptanceLetter = new GUIGenerateAcceptanceLetter(student);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            guiGenerateAcceptanceLetter.start(stage);
        }
    }

    private void handleSelfEvaluation() {
        // TODO: abrir ventana o generar autoevaluación
    }
}