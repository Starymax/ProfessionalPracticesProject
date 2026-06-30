package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIModifyProject;
import mx.fei.gui.views.GUISelectProjects;
import mx.fei.logic.dao.EnterpriseDAO;
import mx.fei.logic.dao.ProjectDAO;
import mx.fei.logic.dao.ProjectManagerDAO;
import mx.fei.logic.dao.StudentDAO;
import mx.fei.logic.dto.Enterprise;
import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ControllerSelectProjects {

    private final GUISelectProjects guiSelectProjects;
    private final StudentDAO studentDAO;
    private final int MAX_PROJECTS_TO_SELECT = 3;
    private final int MAX_PROJECTS_TO_MODIFY = 1;
    private final int NO_SELECTIONS = 0;

    public ControllerSelectProjects(GUISelectProjects guiSelectProjects) {
        this.guiSelectProjects = guiSelectProjects;
        studentDAO = new StudentDAO();
    }

    public void chooseProjectToModify() {
        int selectedCount = guiSelectProjects.getSelectedCount();
        if (selectedCount == NO_SELECTIONS) {
            guiSelectProjects.showError("Seleccione un proyecto de la lista.");
        } else if (selectedCount > MAX_PROJECTS_TO_MODIFY) {
            guiSelectProjects.showError("Solo puedes modificar un proyecto a la vez");
        } else {
            openModifyProject(guiSelectProjects.getSelectedProjects().get(0));
        }
    }

    private void openModifyProject(Project project) {
        GUIModifyProject guiModifyProject = new GUIModifyProject();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        guiModifyProject.start(stage);
        guiModifyProject.loadProject(project);
        guiModifyProject.loadEnterprises(getActiveEnterprises());
        guiModifyProject.getComboBoxEnterprise().setValue(project.getEnterprise());
        loadProjectManagers(guiModifyProject, project);
        guiModifyProject.getComboBoxProjectManager().setValue(project.getProjectManager());
        stage.setOnHidden(event -> reloadProjects(guiModifyProject));
    }

    private List<Enterprise> getActiveEnterprises() {
        List<Enterprise> enterprises = new ArrayList<>();
        try {
            EnterpriseDAO enterpriseDAO = new EnterpriseDAO();
            enterprises = enterpriseDAO.getActiveEnterprises();
        } catch (DataOperationException e) {
            guiSelectProjects.showError(e.getMessage());
        }
        return enterprises;
    }

    private void loadProjectManagers(GUIModifyProject guiModifyProject, Project project) {
        try {
            ProjectManagerDAO projectManagerDAO = new ProjectManagerDAO();
            guiModifyProject.loadProjectManagers(projectManagerDAO.getProjectManagersByEnterprise(project.getEnterprise()));
        } catch (DataOperationException e) {
            guiModifyProject.showError(e.getMessage());
        }
    }

    private void selectProjectsToAssign() {
        Student student;
        if (guiSelectProjects.getSelectedCount() < MAX_PROJECTS_TO_SELECT) {
            guiSelectProjects.showError("Debes seleccionar exactamente 3 proyectos.");
        } else {
            student = guiSelectProjects.getStudent();
            if (student == null) {
                guiSelectProjects.showError("No se encontró el estudiante para la selección de proyectos.");
            } else {
                List<Project> selectedProjects = guiSelectProjects.getSelectedProjects();
                try {
                    studentDAO.saveSelectedProjects(selectedProjects, student);
                    guiSelectProjects.showSuccess("Proyectos seleccionados correctamente.");
                    guiSelectProjects.getStage().close();
                } catch (DataOperationException e) {
                    guiSelectProjects.showError(e.getMessage());
                }
            }
        }
    }

    public void cancel() {
        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle("Cancelar");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Seguro que desea cancelar?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            guiSelectProjects.getStage().close();
        }
    }

    private void reloadProjects(GUIModifyProject guiModifyProject) {
        try {
            ProjectDAO projectDAO = new ProjectDAO();
            guiSelectProjects.loadProjects(projectDAO.getAllProjects());
        } catch (DataOperationException e) {
            guiModifyProject.showError(e.getMessage());
        }
    }

    public void handleSelectButtonAction() {
        if (guiSelectProjects.isModify()) {
        chooseProjectToModify();
        } else {
        selectProjectsToAssign();
        }
    }

}