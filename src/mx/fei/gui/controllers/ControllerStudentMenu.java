package mx.fei.gui.controllers;

import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.fei.gui.views.*;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import mx.fei.logic.dao.PracticeDAO;
import mx.fei.logic.dao.ProjectDAO;
import mx.fei.logic.dao.StudentDAO;
import mx.fei.logic.dto.Practice;
import mx.fei.logic.dto.Project;
import mx.fei.logic.exceptions.DataOperationException;

import java.util.List;
import java.util.Optional;

public class ControllerStudentMenu {

    private final GUIStudentMenu guiStudentMenu;
    private final ProjectDAO projectDAO;

    public ControllerStudentMenu(GUIStudentMenu guiStudentMenu) {
        this.guiStudentMenu = guiStudentMenu;
        projectDAO = new ProjectDAO();
    }

    public void handleButtonsMenu(ActionEvent event) {
        if (event.getSource() == guiStudentMenu.getButtonSelectProjects()) {
            openSelectProjects();
        } else if (event.getSource() == guiStudentMenu.getButtonReports()) {
            openReports();
        } else if (event.getSource() == guiStudentMenu.getButtonRegisterAdvance()) {
            openRegisterAdvance();
        } else if (event.getSource() == guiStudentMenu.getButtonDocuments()) {
            openDocuments(guiStudentMenu.getStudent().getEnrollment());
        } else if (event.getSource() == guiStudentMenu.getButtonLogout()) {
            logout();
        }
    }

    private void openSelectProjects() {
        StudentDAO studentDAO = new StudentDAO();
        try {
            if (studentDAO.getSelectedProjects(guiStudentMenu.getStudent()).isEmpty()) {
                List<Project> projectList = projectDAO.getAvailableProjects();
                GUISelectProjects guiSelectProjects = new GUISelectProjects();
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                guiSelectProjects.start(stage);
                guiSelectProjects.setStudent(guiStudentMenu.getStudent());
                guiSelectProjects.loadProjects(projectList);
            } else {
                guiStudentMenu.showError("Proyectos ya seleccionados.");
            }
        } catch (DataOperationException e) {
            guiStudentMenu.showError(e.getMessage());
        }
    }

    private void openReports() {
        if (guiStudentMenu.getStudent() == null) {
            guiStudentMenu.showError("No hay estudiante seleccionado.");
        } else {
            try {
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                PracticeDAO practiceDAO = new PracticeDAO();
                Practice practice = practiceDAO.getPracticeByEnrollment(guiStudentMenu.getStudent().getEnrollment());
                GUIGenerateReport guiGenerateReport = new GUIGenerateReport(practice);
                guiGenerateReport.start(stage);
            } catch (Exception e) {
                guiStudentMenu.showError("No se pudo abrir la generación de reportes: " + e);
            }
        }
    }

    private void openRegisterAdvance() {
        if (guiStudentMenu.getStudent() == null) {
            guiStudentMenu.showError("No hay estudiante asignado.");
        } else {
            try {
                GUIRegisterAdvance guiRegisterAdvance = new GUIRegisterAdvance(guiStudentMenu.getStudent());
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                guiRegisterAdvance.start(stage);
            } catch (Exception e) {
                guiStudentMenu.showError("No se pudo abrir el registro de avances: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void openDocuments(String enrollment) {
        GUIUploadDocuments  guiUploadDocuments = new GUIUploadDocuments(enrollment);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        guiUploadDocuments.start(stage);
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
            guiStudentMenu.getStage().close();
        }
    }
}