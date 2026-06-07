package mx.fei.gui.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.fei.gui.views.GUIManageEnterprise;
import mx.fei.gui.views.GUIManageStudent;
import mx.fei.gui.views.GUIManageProjects;
import mx.fei.gui.views.GUIManageExperience;
import mx.fei.gui.views.GUILogin;
import mx.fei.gui.views.GUIProfessorMenu;
import mx.fei.gui.views.GUICoordinatorMenu;
import mx.fei.logic.dao.UserDAO;

import java.util.Optional;

public class ControllerCoordinatorMenu {
    private GUICoordinatorMenu guiCoordinatorMenu;

    public ControllerCoordinatorMenu(GUICoordinatorMenu guiCoordinatorMenu) {
        this.guiCoordinatorMenu = guiCoordinatorMenu;
    }

    public void handleButtonsMenu(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Gestionar alumnos" -> {
                GUIManageStudent guiManageStudent = new GUIManageStudent();
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Gestionar alumnos");
                guiManageStudent.start(stage);
            }
            case "Gestionar proyectos" -> {
                GUIManageProjects guiManageProjects = new GUIManageProjects();
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Gestionar proyectos");
                guiManageProjects.start(stage);
            }
            case "Gestionar organizaciones" -> {
                GUIManageEnterprise guiManageEnterprise = new GUIManageEnterprise();
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Gestionar organizaciones");
                guiManageEnterprise.start(stage);
            }
            case "Gestionar experiencia educativa" -> {
                GUIManageExperience guiManageExperience = new GUIManageExperience();
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Gestionar experiencia");
                guiManageExperience.start(stage);
            }
            case "Consultar profesor" -> {
                GUIProfessorMenu guiProfessorMenu = new GUIProfessorMenu();
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                guiProfessorMenu.start(stage);
                guiProfessorMenu.setProfessorInfo(guiCoordinatorMenu.getCoordinator());
            }
            case "Cerrar Sesión" -> {
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
            UserDAO userDAO = new UserDAO();
            userDAO.logout();
            guiCoordinatorMenu.closeWindow();
            GUILogin guiLogin = new GUILogin();
            Stage loginStage = new Stage();
            guiLogin.start(loginStage);
        }
    }
}