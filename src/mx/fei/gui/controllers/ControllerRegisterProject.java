package mx.fei.gui.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.fei.gui.views.GUIActivityPlan;
import mx.fei.gui.views.GUIRegisterProject;
import mx.fei.gui.views.GUIRegisterProjectManager;
import mx.fei.logic.dao.ProjectManagerDAO;
import mx.fei.logic.dto.Enterprise;
import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.ProjectManager;
import mx.fei.logic.exceptions.DataOperationException;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ControllerRegisterProject {

    private final GUIRegisterProject guiRegisterProject;

    public ControllerRegisterProject(GUIRegisterProject guiRegisterProject) {
        this.guiRegisterProject = guiRegisterProject;
    }

    public void handleAddProjectManagerContinueButtonsAndEnterpriseComboBox(ActionEvent event) {
        if (event.getSource() == guiRegisterProject.getComboBoxEnterprise()) {
            updateProjectManagers();
        } else if (event.getSource() == guiRegisterProject.getButtonAddProjectManager()) {
            addProjectManager();
        } else if (event.getSource() == guiRegisterProject.getButtonContinue()) {
            continueButton();
        } else if (event.getSource() == guiRegisterProject.getButtonCancel()) {
            cancel();
        }
    }

    private void updateProjectManagers() {
        Enterprise enterprise = guiRegisterProject.getComboBoxEnterprise().getValue();
        ProjectManagerDAO projectManagerDAO = new ProjectManagerDAO();
        List<ProjectManager> projectManagers = new ArrayList<>();
        try {
            projectManagers = projectManagerDAO.getProjectManagersByEnterprise(enterprise);
        } catch (DataOperationException e) {
            guiRegisterProject.showError(e.getMessage());
        }
        guiRegisterProject.getComboBoxProjectManager().setItems(FXCollections.observableArrayList(projectManagers));
    }

    private void addProjectManager() {
        if (guiRegisterProject.getComboBoxEnterprise().getValue() != null) {
            GUIRegisterProjectManager guiRegisterProjectManager = new GUIRegisterProjectManager();
            ProjectManagerDAO projectManagerDAO = new ProjectManagerDAO();
            Stage stage = new Stage();
            stage.setTitle("Añadir Responsable");
            stage.initModality(Modality.APPLICATION_MODAL);
            Enterprise enterprise = guiRegisterProject.getComboBoxEnterprise().getValue();
            guiRegisterProjectManager.start(stage);
            guiRegisterProjectManager.loadEnterprise(enterprise);
            stage.setOnHidden(event -> {
                try {
                    guiRegisterProject.loadProjectManagers(projectManagerDAO.getProjectManagersByEnterprise(enterprise));
                } catch (DataOperationException e) {
                    guiRegisterProject.showError(e.getMessage());
                }
            });
        } else {
            guiRegisterProject.showError("Seleccione una Organización para agregar un practicante");
        }
    }

    private void continueButton() {
        if (guiRegisterProject.validateFields()) {
            Project project = buildProject();
            GUIActivityPlan guiActivityPlan = new GUIActivityPlan();
            guiActivityPlan.setProject(project);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            guiActivityPlan.start(stage);
        }
    }

    private Project buildProject() {
        String name = guiRegisterProject.getTextFieldName().getText();
        String description = guiRegisterProject.getTextAreaDescription().getText();
        String generalObjective = guiRegisterProject.getTextFieldGeneralObjective().getText();
        String mediatesObjectives = guiRegisterProject.getTextFieldMediateObjectives().getText();
        String immediateObjectives = guiRegisterProject.getTextFieldImmediateObjective().getText();
        String methodology = guiRegisterProject.getTextFieldMethodology().getText();
        String responsabilities = guiRegisterProject.getTextFieldResponsibilities().getText();
        String resources = guiRegisterProject.getTextFieldResources().getText();
        Date initialDate = Date.valueOf(guiRegisterProject.getDatePickerStartDate().getValue());
        Date finalDate = Date.valueOf(guiRegisterProject.getDatePickerFinalDate().getValue());
        int availablePlaces = Integer.parseInt(guiRegisterProject.getTextFieldAvailablePlaces().getText());
        Enterprise enterprise = guiRegisterProject.getComboBoxEnterprise().getValue();
        ProjectManager projectManager = guiRegisterProject.getComboBoxProjectManager().getValue();
        return new Project(0, name, description, generalObjective, mediatesObjectives, immediateObjectives, methodology, responsabilities, resources, initialDate, finalDate, true, availablePlaces, enterprise, projectManager);
    }

    private void cancel() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancelar");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Seguro que desea cancelar? Se perderá la información ingresada.");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            guiRegisterProject.getStage().close();
        }
    }
}