package mx.fei.gui.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import mx.fei.gui.views.GUIManageExperience;
import mx.fei.gui.views.GUIRegisterEducationalExperience;

public class ControllerManageExperience {
    private GUIManageExperience guiManageExperience;

    public ControllerManageExperience(GUIManageExperience guiManageExperience) {
        this.guiManageExperience = guiManageExperience;
    }

    public void handleButtonAction(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Registrar nueva experiencia" -> {
                GUIRegisterEducationalExperience guiRegisterEducationalExperience = new GUIRegisterEducationalExperience();
                Stage stage = new Stage();
                stage.setTitle("Registrar experiencia");
                guiRegisterEducationalExperience.start(stage);
                guiManageExperience.closeWindow();
            }
            case "Modificar experiencia" -> { /* TODO: abrir ventana modificar */ }
            case "Dar de alta experiencia" -> { /* TODO: abrir ventana dar de alta */ }
            case "Regresar" -> guiManageExperience.closeWindow();
        }
    }
}