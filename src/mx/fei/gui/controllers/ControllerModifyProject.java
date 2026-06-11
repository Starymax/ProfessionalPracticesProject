package mx.fei.gui.controllers;

import javafx.scene.control.Button;
import mx.fei.gui.views.GUIModifyProject;
import mx.fei.gui.views.GUIRegisterProjectManager;
import mx.fei.logic.dao.ActivityDAO;
import mx.fei.logic.dao.ProjectDAO;
import mx.fei.logic.dao.ProjectManagerDAO;
import mx.fei.logic.dto.Enterprise;
import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.ProjectManager;
import mx.fei.logic.exceptions.DataOperationException;
import mx.fei.gui.views.GUIActivityPlan;

import javafx.collections.FXCollections;
import javafx.stage.Modality;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

public class ControllerModifyProject {

    private final GUIModifyProject guiModifyProject;

    public ControllerModifyProject(GUIModifyProject guiModifyProject) {
        this.guiModifyProject = guiModifyProject;
    }

    public void handleAddProjectManagerContinueCancelButtonsAndEnterpriseComboBox(ActionEvent event) {
        if (!(event.getSource() instanceof Button)) {
            updateProjectManagers();
        } else {
            Button source = (Button) event.getSource();
            switch (source.getText()) {
                case "Añadir Responsable" -> {
                    addProjectManager();
                }
                case "Plan de Actividades" -> {
                    openActivityPlan();
                }
                case "Continuar" -> {
                    saveChanges();
                }
                case "Cancelar" -> {
                    cancel();
                }
            }
        }
    }

    private void updateProjectManagers() {
        Enterprise enterprise = guiModifyProject.getComboBoxEnterprise().getValue();
        ProjectManagerDAO projectManagerDAO = new ProjectManagerDAO();
        List<ProjectManager> projectManagers = new ArrayList<>();
        try {
            projectManagers = projectManagerDAO.getProjectManagersByEnterprise(enterprise);
        } catch (DataOperationException e) {
            guiModifyProject.showError(e.getMessage());
        }
        guiModifyProject.getComboBoxProjectManager().setItems(FXCollections.observableArrayList(projectManagers));
    }

    private void addProjectManager() {
        if (guiModifyProject.getComboBoxEnterprise().getValue() != null) {
            GUIRegisterProjectManager guiRegisterProjectManager = new GUIRegisterProjectManager();
            ProjectManagerDAO projectManagerDAO = new ProjectManagerDAO();
            Stage stage = new Stage();
            stage.setTitle("Añadir Responsable");
            stage.initModality(Modality.APPLICATION_MODAL);
            Enterprise enterprise = guiModifyProject.getComboBoxEnterprise().getValue();
            guiRegisterProjectManager.start(stage);
            guiRegisterProjectManager.loadEnterprise(enterprise);
            stage.setOnHidden(event -> refreshProjectManagers(projectManagerDAO, enterprise));
        } else {
            guiModifyProject.showError("Debe seleccionar una Organización para añadir un responsable");
        }
    }

    private void openActivityPlan() {
        try {
            ActivityDAO activityDAO = new ActivityDAO();
            boolean hasActivities = !activityDAO.getActivitiesByProjectId(guiModifyProject.getProject().getProjectId()).isEmpty();
            if (hasActivities) {
                guiModifyProject.showError("Este proyecto ya tiene un plan de actividades asignado.");
                guiModifyProject.disableActivityPlanButton();
            } else {
                GUIActivityPlan guiActivityPlan = new GUIActivityPlan();
                guiActivityPlan.setProject(guiModifyProject.getProject());
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                guiActivityPlan.start(stage);
            }
        } catch (DataOperationException e) {
            guiModifyProject.showError(e.getMessage());
        }
    }

    private void refreshProjectManagers(ProjectManagerDAO projectManagerDAO, Enterprise enterprise) {
        try {
            guiModifyProject.loadProjectManagers(projectManagerDAO.getProjectManagersByEnterprise(enterprise));
        } catch (DataOperationException e) {
            guiModifyProject.showError(e.getMessage());
        }
    }

    private void saveChanges() {
        if (guiModifyProject.validateFields() && confirmChanges()) {
            try {
                ProjectDAO projectDAO = new ProjectDAO();
                projectDAO.modifyProject(buildProject());
                guiModifyProject.showSuccess("Proyecto actualizado exitosamente.");
                guiModifyProject.getStage().close();
            } catch (DataOperationException e) {
                guiModifyProject.showError("Error al guardar los cambios: " + e.getMessage());
            }
        }
    }

    private boolean confirmChanges() {
        Alert confirmation = new Alert(AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmar cambios");
        confirmation.setHeaderText(null);
        confirmation.setContentText("¿Seguro que desea guardar los cambios del proyecto?");
        Optional<ButtonType> result = confirmation.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private Project buildProject() {
        int projectId = guiModifyProject.getProject().getProjectId();
        String name = guiModifyProject.getTextFieldName().getText();
        String description = guiModifyProject.getTextAreaDescription().getText();
        String generalObjective = guiModifyProject.getTextFieldGeneralObjective().getText();
        String immediateObjectives = guiModifyProject.getTextAreaImmediateObjectives().getText();
        String mediateObjectives = guiModifyProject.getTextAreaMediateObjectives().getText();
        String methodology = guiModifyProject.getTextFieldMethodology().getText();
        String resources = guiModifyProject.getTextAreaResources().getText();
        Date startDate = Date.valueOf(guiModifyProject.getDatePickerStartDate().getValue());
        Date finalDate = Date.valueOf(guiModifyProject.getDatePickerFinalDate().getValue());
        String responsibilities = guiModifyProject.getTextAreaResponsibilities().getText();
        int availableSpots = Integer.parseInt(guiModifyProject.getTextFieldAvailablePlaces().getText().trim());
        boolean isActive = guiModifyProject.isActiveSelected();
        Enterprise enterprise = guiModifyProject.getComboBoxEnterprise().getValue();
        ProjectManager projectManager = guiModifyProject.getComboBoxProjectManager().getValue();
        return new Project(projectId, name, description, generalObjective, mediateObjectives, immediateObjectives, methodology, responsibilities, resources, startDate, finalDate, isActive, availableSpots, enterprise, projectManager);
    }

    private void cancel() {
        Alert confirmation = new Alert(AlertType.CONFIRMATION);
        confirmation.setTitle("Cancelar");
        confirmation.setHeaderText(null);
        confirmation.setContentText("¿Seguro que desea cancelar? Se perderán los cambios realizados.");
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            guiModifyProject.getStage().close();
        }
    }
}