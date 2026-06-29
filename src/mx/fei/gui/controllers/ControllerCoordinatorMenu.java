package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIManageEnterprise;
import mx.fei.gui.views.GUIManageStudent;
import mx.fei.gui.views.GUIManageProjects;
import mx.fei.gui.views.GUIManageExperience;
import mx.fei.gui.views.GUIValidateStudentDocuments;
import mx.fei.gui.views.GUILogin;
import mx.fei.gui.views.GUIProfessorMenu;
import mx.fei.gui.views.GUICoordinatorMenu;
import mx.fei.logic.dao.DocumentDAO;
import mx.fei.logic.dao.UserDAO;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.fei.logic.exceptions.DataOperationException;

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
                manageStudents();
            }
            case "Gestionar proyectos" -> {
                manageProjects();
            }
            case "Gestionar organizaciones" -> {
                manageEnterprises();
            }
            case "Gestionar experiencia educativa" -> {
                manageEducationalExperience();
            }
            case "Validar documentos" -> {
                if (!existStudentsWithUploadedDocuments()) {
                    guiCoordinatorMenu.showError("No hay estudiantes con documentos pendientes por el momento.");
                } else {
                    ValidateDocuments();
                }
            }
            case "Consultar profesor" -> {
                ConsultProfessor();
            }
            case "Cerrar Sesión" -> {
                logout();
            }
        }
    }

    private void ConsultProfessor() {
        GUIProfessorMenu guiProfessorMenu = new GUIProfessorMenu();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        guiProfessorMenu.start(stage);
        guiProfessorMenu.setProfessorInfo(guiCoordinatorMenu.getCoordinator());
    }

    private void ValidateDocuments() {
        GUIValidateStudentDocuments guiValidateStudentDocuments = new GUIValidateStudentDocuments();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Validar documentos");
        guiValidateStudentDocuments.start(stage);
    }

    private void manageEducationalExperience() {
        GUIManageExperience guiManageExperience = new GUIManageExperience();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Gestionar experiencia");
        guiManageExperience.start(stage);
    }

    private void manageEnterprises() {
        GUIManageEnterprise guiManageEnterprise = new GUIManageEnterprise();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Gestionar organizaciones");
        guiManageEnterprise.start(stage);
    }

    private void manageProjects() {
        GUIManageProjects guiManageProjects = new GUIManageProjects();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Gestionar proyectos");
        guiManageProjects.start(stage);
    }

    private void manageStudents() {
        GUIManageStudent guiManageStudent = new GUIManageStudent();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Gestionar alumnos");
        guiManageStudent.start(stage);
    }

    private void logout() {
        Alert confirm = new Alert(AlertType.CONFIRMATION);
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

    private boolean existStudentsWithUploadedDocuments() {
        DocumentDAO documentDAO = new DocumentDAO();
        boolean exists = true;
        try {
            if (documentDAO.getStudentsWithUploadedDocuments().isEmpty()){
                exists = false;
            }
        } catch (DataOperationException e) {
            guiCoordinatorMenu.showError(e.getMessage());
        }
        return exists;
    }
}