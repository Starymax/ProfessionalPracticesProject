package mx.fei.gui.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.fei.gui.views.GUIActivityPlan;
import mx.fei.gui.views.GUIRegisterProject;
import mx.fei.gui.views.GUIRegisterProjectManager;
import mx.fei.logic.dao.ActivityDAO;
import mx.fei.logic.dao.ProjectDAO;
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
        private Project savedProject = null;

        public ControllerRegisterProject(GUIRegisterProject guiRegisterProject) {
            this.guiRegisterProject = guiRegisterProject;
        }

        public void handleAddProjectManagerContinueActivityPlanButtonsAndEnterpriseComboBox(ActionEvent event) {
            if (!(event.getSource() instanceof Button)) {
                updateProjectManagers();
                return;
            }
            Button source = (Button) event.getSource();
            switch (source.getText()) {
                case "Añadir Responsable" -> {
                    addProjectManager();
                }
                case "Guardar" -> {
                    saveProject();
                }
                case "Plan de Actividades" -> {
                    openActivityPlan();
                }
                case "Cancelar" -> {
                    cancel();
                }
            }
        }

        private void saveProject() {
            if (guiRegisterProject.validateFields()) {
                Project project = buildProject();
                try {
                    ProjectDAO projectDAO = new ProjectDAO();
                    int generatedId = projectDAO.registerProject(project);
                    project.setProjectId(generatedId);
                    savedProject = project;
                    guiRegisterProject.showSuccess("Proyecto guardado correctamente.");
                    guiRegisterProject.enableActivityPlanButton();
                } catch (DataOperationException e) {
                    guiRegisterProject.showError(e.getMessage());
                }
            }
        }

        private void openActivityPlan() {
            try {
                ActivityDAO activityDAO = new ActivityDAO();
                boolean hasActivities = !activityDAO.getActivitiesByProjectId(savedProject.getProjectId()).isEmpty();
                if (hasActivities) {
                    guiRegisterProject.showError("Este proyecto ya tiene un plan de actividades asignado.");
                    guiRegisterProject.disableActivityPlanButton();
                } else {
                    GUIActivityPlan guiActivityPlan = new GUIActivityPlan();
                    guiActivityPlan.setProject(savedProject);
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    guiActivityPlan.start(stage);
                }
            } catch (DataOperationException e) {
                guiRegisterProject.showError(e.getMessage());
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
                stage.setOnHidden(event -> refreshProjectManagers(projectManagerDAO, enterprise));
            } else {
                guiRegisterProject.showError("Seleccione una Organización para agregar un responsable");
            }
        }

        private void refreshProjectManagers(ProjectManagerDAO projectManagerDAO, Enterprise enterprise) {
            try {
                guiRegisterProject.loadProjectManagers(projectManagerDAO.getProjectManagersByEnterprise(enterprise));
            } catch (DataOperationException e) {
                guiRegisterProject.showError(e.getMessage());
            }
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

        private Project buildProject() {
            String name = guiRegisterProject.getTextFieldName().getText();
            String description = guiRegisterProject.getTextAreaDescription().getText();
            String generalObjective = guiRegisterProject.getTextFieldGeneralObjective().getText();
            String mediatesObjectives = guiRegisterProject.getTextAreaMediateObjectives().getText();
            String immediateObjectives = guiRegisterProject.getTextAreaImmediateObjective().getText();
            String methodology = guiRegisterProject.getTextFieldMethodology().getText();
            String responsibilities = guiRegisterProject.getTextAreaResponsibilities().getText();
            String resources = guiRegisterProject.getTextAreaResources().getText();
            Date initialDate = Date.valueOf(guiRegisterProject.getDatePickerStartDate().getValue());
            Date finalDate = Date.valueOf(guiRegisterProject.getDatePickerFinalDate().getValue());
            int availablePlaces = Integer.parseInt(guiRegisterProject.getTextFieldAvailablePlaces().getText());
            Enterprise enterprise = guiRegisterProject.getComboBoxEnterprise().getValue();
            ProjectManager projectManager = guiRegisterProject.getComboBoxProjectManager().getValue();
            return new Project(0, name, description, generalObjective, mediatesObjectives, immediateObjectives,
                    methodology, responsibilities, resources, initialDate, finalDate, true,
                    availablePlaces, enterprise, projectManager);
        }
    }