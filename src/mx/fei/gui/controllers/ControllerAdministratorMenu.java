package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIAdministratorMenu;
import mx.fei.gui.views.GUILogin;
import mx.fei.gui.views.GUIRegisterProfessor;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;

public class ControllerAdministratorMenu implements EventHandler<ActionEvent> {

    private final GUIAdministratorMenu guiAdministratorMenu;

    public ControllerAdministratorMenu(GUIAdministratorMenu guiAdministratorMenu) {
        this.guiAdministratorMenu = guiAdministratorMenu;
    }

    @Override
    public void handle(ActionEvent event) {
        if (event.getSource() == guiAdministratorMenu.getButtonRegisterProfessor()) {
            openRegisterProfessor();
        } else if (event.getSource() == guiAdministratorMenu.getButtonModifyProfessor()) {
            openModifyProfessor();
        } else if (event.getSource() == guiAdministratorMenu.getButtonProfessorView()) {
            openProfessorView();
        } else if (event.getSource() == guiAdministratorMenu.getButtonLogout()) {
            logout();
        }
    }

    private void openRegisterProfessor() {
        GUIRegisterProfessor guiRegisterProfessor = new GUIRegisterProfessor();
        Stage stage = new Stage();
        guiRegisterProfessor.start(stage);
    }

    private void openModifyProfessor() {
        // TODO: abrir GUIModifyProfessor
    }

    private void openProfessorView() {
        // TODO: abrir GUIProfessorView
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
            guiAdministratorMenu.getStage().close();
        }
    }
}