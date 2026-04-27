package mx.fei.gui.controllers;

import javafx.stage.Stage;
import mx.fei.gui.views.GUILogin;
import mx.fei.gui.views.GUIStudentMenu;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class ControllerStudentMenu implements EventHandler<ActionEvent> {

    private final GUIStudentMenu gui;

    public ControllerStudentMenu(GUIStudentMenu gui) {
        this.gui = gui;
    }

    @Override
    public void handle(ActionEvent event) {
        if (event.getSource() == gui.getButtonSelectProjects()) {
            openSelectProjects();
        } else if (event.getSource() == gui.getButtonReports()) {
            openReports();
        } else if (event.getSource() == gui.getButtonDocuments()) {
            openDocuments();
        } else if (event.getSource() == gui.getButtonLogout()) {
            logout();
        }
    }

    private void openSelectProjects() {
        // TODO: abrir GUISelectProjects
    }

    private void openReports() {
        // TODO: abrir GUIReports
    }

    private void openDocuments() {
        // TODO: abrir GUIDocuments
    }

    private void logout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cerrar Sesión");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Seguro que desea cerrar sesión?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            gui.getStage().close();
            GUILogin guiLogin = new GUILogin();
            Stage loginStage = new Stage();
            guiLogin.start(loginStage);
            gui.getStage().close();
        }
    }
}