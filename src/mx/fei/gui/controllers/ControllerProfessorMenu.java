package mx.fei.gui.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.Window;
import mx.fei.gui.views.GUILogin;
import mx.fei.gui.views.GUIProfessorMenu;
import java.util.Optional;
import java.util.List;

public class ControllerProfessorMenu {
    private GUIProfessorMenu guiProfessorMenu;
    public ControllerProfessorMenu(GUIProfessorMenu guiProfessorMenu) {
        this.guiProfessorMenu = guiProfessorMenu;
    }

    public void handleButtonsMenu(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Gestionar actividades" -> { /* TODO: abrir ventana */ }
            case "Gestionar reportes" -> { /* TODO: abrir ventana */ }
            case "Cerrar Sesión" -> logout();
        }
    }

    private void logout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cerrar Sesión");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Seguro que desea cerrar sesión?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            GUILogin guiLogin = new GUILogin();
            Stage loginStage = new Stage();
            guiLogin.start(loginStage);
            
            List<Window> windows = new java.util.ArrayList<>(Window.getWindows());
            for (Window window : windows) {
                if (window instanceof Stage stage && stage != loginStage && stage.isShowing()) {
                    stage.close();
                }
            }
        }
    }
}
