package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIChooseExperience;
import mx.fei.gui.views.GUIManageExperience;
import mx.fei.gui.views.GUIRegisterEducationalExperience;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ControllerManageExperience {
    private GUIManageExperience guiManageExperience;

    public ControllerManageExperience(GUIManageExperience guiManageExperience) {
        this.guiManageExperience = guiManageExperience;
    }

    public void handleRegisterModifyButtons(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Registrar nueva experiencia" -> {
                openRegisterExperience();
            }
            case "Modificar experiencia" -> {
                openModifyExperience();
            }
            case "Dar de alta experiencia" -> {
                fillExperience();
            }
            case "Regresar" -> {
                guiManageExperience.closeWindow();
            }
        }
    }
    private void openRegisterExperience() {
        GUIRegisterEducationalExperience guiRegisterEducationalExperience = new GUIRegisterEducationalExperience();
        Stage stage = new Stage();
        stage.setTitle("Registrar experiencia");
        guiRegisterEducationalExperience.start(stage);
        guiManageExperience.closeWindow();
    }

    private void openModifyExperience() {
        GUIChooseExperience guiChooseExperience = new GUIChooseExperience();
        Stage stage = new Stage();
        stage.setTitle("Modificar experiencia");
        guiChooseExperience.setToModify(true);
        guiChooseExperience.start(stage);
        guiManageExperience.closeWindow();
    }
    private void fillExperience () {
        GUIChooseExperience guiChooseExperience = new GUIChooseExperience();
        Stage stage = new Stage();
        stage.setTitle("Dar de alta experiencia");
        guiChooseExperience.start(stage);
        guiManageExperience.closeWindow();
    }
}