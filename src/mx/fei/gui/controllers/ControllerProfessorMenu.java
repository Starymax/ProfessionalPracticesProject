package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIChooseExperience;
import mx.fei.gui.views.GUIEvaluateStudentSelection;
import mx.fei.gui.views.GUILogin;
import mx.fei.gui.views.GUIProfessorMenu;
import mx.fei.logic.dao.EducationalExperienceDAO;
import mx.fei.logic.dao.UserDAO;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

public class ControllerProfessorMenu {
    private GUIProfessorMenu guiProfessorMenu;
    private EducationalExperienceDAO educationalExperienceDAO;
    public ControllerProfessorMenu(GUIProfessorMenu guiProfessorMenu) {
        educationalExperienceDAO = new EducationalExperienceDAO();
        this.guiProfessorMenu = guiProfessorMenu;
    }

    public void handleEvaluateConsultStudentsGoBack(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Evaluar" -> {
                if (!professorHadAssignedExperiences()) {
                    guiProfessorMenu.showError("No tiene ninguna experiencia asignada.");
                } else {
                    openEvaluateStudent();
                }
            }
            case  "Consultar practica" -> {
                if (!professorHadAssignedExperiences()) {
                    guiProfessorMenu.showError("No tiene ninguna experiencia asignada.");
                } else {
                    openConsultStudent();
                }
            }
            case "Regresar" -> {
                goBack();
            }
            case "Cerrar Sesión" -> {
                logout();
            }
        }
    }

    private void openEvaluateStudent() {
        GUIEvaluateStudentSelection guiEvaluateStudentSelection = new GUIEvaluateStudentSelection(guiProfessorMenu.getProfessor());
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        guiEvaluateStudentSelection.start(stage);
    }

    private void openConsultStudent() {
        GUIChooseExperience guiChooseExperience = new GUIChooseExperience();
        guiChooseExperience.setToConsultStudent(true);
        guiChooseExperience.setProfessor(guiProfessorMenu.getProfessor());
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        guiChooseExperience.start(stage);
    }

    private void goBack() {
        guiProfessorMenu.getStage().close();
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
            GUILogin guiLogin = new GUILogin();
            Stage loginStage = new Stage();
            guiLogin.start(loginStage);
            List<Window> windows = new ArrayList<>(Window.getWindows());
            for (Window window : windows) {
                if (window instanceof Stage stage && stage != loginStage && stage.isShowing()) {
                    stage.close();
                }
            }
        }
    }

    private boolean professorHadAssignedExperiences() {
        boolean hadAssignedExperiences = true;
        try {
            if (educationalExperienceDAO.getEducationalExperiencesByProfessor(guiProfessorMenu.getProfessor().getUserId()).isEmpty()) {
                hadAssignedExperiences = false;
            }
        } catch (DataOperationException e) {
            guiProfessorMenu.showError("Error al obtener las experiencias educativas.");
        }
        return hadAssignedExperiences;
    }
}