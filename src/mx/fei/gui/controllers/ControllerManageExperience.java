package mx.fei.gui.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import mx.fei.gui.views.GUIChooseExperience;
import mx.fei.gui.views.GUIManageExperience;
import mx.fei.gui.views.GUIRegisterEducationalExperience;

public class ControllerManageExperience {
    private GUIManageExperience guiManageExperience;

    public ControllerManageExperience(GUIManageExperience guiManageExperience) {
        this.guiManageExperience = guiManageExperience;
    }

    public void handleRegisterModifyButtons(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Registrar nueva experiencia" -> {
                GUIRegisterEducationalExperience guiRegisterEducationalExperience = new GUIRegisterEducationalExperience();
                Stage stage = new Stage();
                stage.setTitle("Registrar experiencia");
                guiRegisterEducationalExperience.start(stage);
                guiManageExperience.closeWindow();
            }
            case "Modificar experiencia" -> {
                GUIChooseExperience  guiChooseExperience = new GUIChooseExperience();
                Stage stage = new Stage();
                stage.setTitle("Modificar experiencia");
                guiChooseExperience.setToModify(true);
                guiChooseExperience.start(stage);
                guiManageExperience.closeWindow();
            }
            case "Dar de alta experiencia" -> {
                GUIChooseExperience  guiChooseExperience = new GUIChooseExperience();
                Stage stage = new Stage();
                stage.setTitle("Dar de alta experiencia");
                guiChooseExperience.start(stage);
                guiManageExperience.closeWindow();
            }
            case "Regresar" -> guiManageExperience.closeWindow();
        }
    }
}