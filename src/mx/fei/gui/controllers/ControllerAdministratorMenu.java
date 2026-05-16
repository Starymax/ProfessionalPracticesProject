package mx.fei.gui.controllers;

import javafx.stage.Modality;
import mx.fei.gui.views.*;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;

public class ControllerAdministratorMenu {

    private final GUIAdministratorMenu guiAdministratorMenu;

    public ControllerAdministratorMenu(GUIAdministratorMenu guiAdministratorMenu) {
        this.guiAdministratorMenu = guiAdministratorMenu;
    }

    public void handleRegisterModifyProfessorViewCancelButtons(ActionEvent event) {
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
        stage.initModality(Modality.APPLICATION_MODAL);
        guiRegisterProfessor.start(stage);
    }

    private void openModifyProfessor() {
        GUIChooseProfessor guiChooseProfessor = new GUIChooseProfessor();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        guiChooseProfessor.start(stage);
    }

    private void openProfessorView() {
        GUIProfessorMenu guiProfessorMenu = new GUIProfessorMenu();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        guiProfessorMenu.start(stage);
        guiProfessorMenu.setProfessorInfo(guiAdministratorMenu.getProfessor());
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