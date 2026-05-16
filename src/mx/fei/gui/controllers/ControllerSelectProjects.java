package mx.fei.gui.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ControllerSelectProjects {

    private final GUISelectProjects guiSelectProjects;
    private final StudentDAO studentDAO;

    public ControllerSelectProjects(GUISelectProjects guiSelectProjects) {
        this.guiSelectProjects = guiSelectProjects;
        studentDAO = new StudentDAO();
    }

    public void handleButtonAction(ActionEvent event) {
        if (event.getSource() == guiSelectProjects.getSelectButton()) {
            if (guiSelectProjects.isModify()){
                chooseProjectToModify();
            } else {
                selectProjectsToAssign();
            }
        } else if (event.getSource() == guiSelectProjects.getCancelButton()) {
            cancel();
        }
    }

    public void chooseProjectToModify() {
        int selectedCoutn = guiSelectProjects.getSelectedCount();
        if (selectedCoutn == 0) {
            guiSelectProjects.showError("Seleccione un proyecto de la lista.");
        } else if (selectedCoutn > 1) {
            guiSelectProjects.showError("Solo puedes modificar un proyecto a la vez");
        } else {
            GUIModifyProject guiModifyProject = new GUIModifyProject();
            EnterpriseDAO enterpriseDAO = new EnterpriseDAO();
            List<Enterprise> enterprises = new ArrayList<>();
            try {
                enterprises = enterpriseDAO.getActiveEnterprises();
            } catch (DataOperationException e) {
                guiSelectProjects.showError(e.getMessage());
            }
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            guiModifyProject.start(stage);
            Project project = guiSelectProjects.getSelectedProjects().get(0);
            guiModifyProject.loadProject(project);
            guiModifyProject.loadEnterprises(enterprises);
            guiModifyProject.getComboBoxEnterprise().setValue(project.getEnterprise());
            ProjectManagerDAO projectManagerDAO = new ProjectManagerDAO();
            try {
                guiModifyProject.loadProjectManagers(projectManagerDAO.getProjectManagersByEnterprise(project.getEnterprise()));
            } catch (DataOperationException e) {
                guiModifyProject.showError(e.getMessage());
            }
            guiModifyProject.getComboBoxProjectManager().setValue(project.getProjectManager());
            stage.setOnHidden(event -> {
                try {
                    ProjectDAO projectDAO = new ProjectDAO();
                    guiSelectProjects.loadProjects(projectDAO.getAllProjects());
                } catch (DataOperationException e) {
                    guiModifyProject.showError(e.getMessage());
                }
            });
        }
    }

    private void selectProjectsToAssign() {
        Student student;
        if (guiSelectProjects.getSelectedCount() < 3) {
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

    private void cancel() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancelar");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Seguro que desea cancelar?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            guiSelectProjects.getStage().close();
        }
    }
}