package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIAdministratorMenu;
import mx.fei.gui.views.GUIChooseProfessor;
import mx.fei.gui.views.GUILogin;
import mx.fei.gui.views.GUIProfessorMenu;
import mx.fei.logic.dao.UserDAO;
import mx.fei.gui.views.GUIRegisterProfessor;

import javafx.stage.Modality;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;

public class ControllerAdministratorMenu {

    private final GUIAdministratorMenu guiAdministratorMenu;

    public ControllerAdministratorMenu(GUIAdministratorMenu guiAdministratorMenu) {
        this.guiAdministratorMenu = guiAdministratorMenu;
    }

    public void openRegisterProfessor() {
        GUIRegisterProfessor guiRegisterProfessor = new GUIRegisterProfessor();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        guiRegisterProfessor.start(stage);
    }

    public void openModifyProfessor() {
        GUIChooseProfessor guiChooseProfessor = new GUIChooseProfessor();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        guiChooseProfessor.start(stage);
    }

    public void openProfessorView() {
        GUIProfessorMenu guiProfessorMenu = new GUIProfessorMenu();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        guiProfessorMenu.start(stage);
        guiProfessorMenu.setProfessorInfo(guiAdministratorMenu.getProfessor());
    }

    public void logout() {
        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle("Cerrar Sesión");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Seguro que desea cerrar sesión?");
        Optional<ButtonType> resultConfirmation = confirm.showAndWait();
        if (resultConfirmation.isPresent() && resultConfirmation.get() == ButtonType.OK) {
            UserDAO userDAO = new UserDAO();
            userDAO.logout();
            GUILogin guiLogin = new GUILogin();
            Stage loginStage = new Stage();
            guiLogin.start(loginStage);
            guiAdministratorMenu.getStage().close();
        }
    }
}