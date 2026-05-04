package mx.fei.gui.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import mx.fei.gui.views.GUILogin;
import mx.fei.gui.views.GUIProfessor;

public class ControllerProfessorMenu {
    private GUIProfessor guiProfessor;
    public ControllerProfessorMenu(GUIProfessor guiProfessor) {
        this.guiProfessor = guiProfessor;
    }

    public void handleButtonAction(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Gestionar actividades" -> { /* TODO: abrir ventana */ }
            case "Gestionar reportes" -> { /* TODO: abrir ventana */ }
            case "Regresar" -> {
                GUILogin guiLogin = new GUILogin();
                Stage stage = new Stage();
                stage.setTitle("Iniciar Sesion");
                guiProfessor.closeWindow();
            }
        }
    }
}
