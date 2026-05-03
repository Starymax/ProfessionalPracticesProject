package mx.fei.gui.controllers;

import javafx.stage.Stage;
import mx.fei.gui.views.GUILogin;
import mx.fei.gui.views.GUISelectProjects;
import mx.fei.gui.views.GUIStudentMenu;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import mx.fei.logic.dao.ProjectDAO;
import mx.fei.logic.dto.Project;
import mx.fei.logic.exceptions.DataOperationException;

import java.util.List;
import java.util.Optional;

public class ControllerStudentMenu implements EventHandler<ActionEvent> {

    private final GUIStudentMenu guiStudentMenu;
    private final ProjectDAO projectDAO;

    public ControllerStudentMenu(GUIStudentMenu guiStudentMenu) {
        this.guiStudentMenu = guiStudentMenu;
        projectDAO = new ProjectDAO();
    }

    @Override
    public void handle(ActionEvent event) {
        if (event.getSource() == guiStudentMenu.getButtonSelectProjects()) {
            openSelectProjects();
        } else if (event.getSource() == guiStudentMenu.getButtonReports()) {
            openReports();
        } else if (event.getSource() == guiStudentMenu.getButtonDocuments()) {
            openDocuments();
        } else if (event.getSource() == guiStudentMenu.getButtonLogout()) {
            logout();
        }
    }

    private void openSelectProjects() {
        try {
            List<Project> projectList = projectDAO.getAvailableProjects();
            GUISelectProjects guiSelectProjects = new GUISelectProjects();
            Stage stage = new Stage();
            guiSelectProjects.start(stage);
            guiSelectProjects.setStudent(guiStudentMenu.getStudent());
            guiSelectProjects.loadProjects(projectList);
            guiStudentMenu.getStage().close();
        } catch (DataOperationException e) {
            guiStudentMenu.showError(e.getMessage());
        }
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
            GUILogin guiLogin = new GUILogin();
            Stage loginStage = new Stage();
            guiLogin.start(loginStage);
            guiStudentMenu.getStage().close();
        }
    }
}