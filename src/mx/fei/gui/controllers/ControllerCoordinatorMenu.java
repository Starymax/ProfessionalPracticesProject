package mx.fei.gui.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import mx.fei.gui.views.GUICoordinator;
import mx.fei.gui.views.GUILogin;
import mx.fei.gui.views.GUIManageExperience;
import mx.fei.gui.views.GUIManageStudent;

import java.util.Optional;

public class ControllerCoordinatorMenu implements EventHandler<ActionEvent> {
    private GUICoordinator guiCoordinator;

    public ControllerCoordinatorMenu(GUICoordinator guiCoordinator) {
        this.guiCoordinator = guiCoordinator;
    }

    @Override
    public void handle(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Gestionar alumnos" -> {
                GUIManageStudent guiManageStudent = new GUIManageStudent();
                Stage stage = new Stage();
                stage.setTitle("Gestionar alumnos");
                guiManageStudent.start(stage);
            }
            case "Gestionar proyectos" -> { /* TODO: abrir ventana */ }
            case "Gestionar organizaciones" -> { /* TODO: abrir ventana */ }
            case "Gestionar experiencia educativa" -> {
                GUIManageExperience guiManageExperience = new GUIManageExperience();
                Stage stage = new Stage();
                stage.setTitle("Gestionar experiencia");
                guiManageExperience.start(stage);
            }
            case "Consultar profesor" -> { /* TODO: abrir ventana */ }
            case "Regresar" -> {
                logout();
            }
        }
    }

    private void logout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cerrar Sesión");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Seguro que desea cerrar sesión?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            guiCoordinator.closeWindow();
            GUILogin guiLogin = new GUILogin();
            Stage loginStage = new Stage();
            guiLogin.start(loginStage);
        }
    }
}