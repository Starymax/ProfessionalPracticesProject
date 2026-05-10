package mx.fei.gui.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.fei.gui.views.GUIRegisterProject;
import mx.fei.gui.views.GUIRegisterProjectManager;
import mx.fei.logic.dao.ProjectManagerDAO;
import mx.fei.logic.dto.Enterprise;
import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.ProjectManager;
import mx.fei.logic.exceptions.DataOperationException;

import java.sql.Date;
import java.util.Optional;

public class ControllerRegisterProject {

    private final GUIRegisterProject guiRegisterProject;

    public ControllerRegisterProject(GUIRegisterProject guiRegisterProject) {
        this.guiRegisterProject = guiRegisterProject;
    }

    public void handleButtonAction(ActionEvent event) {
        if (event.getSource() == guiRegisterProject.getAddProjectManagerButton()) {
            addProjectManager();
        } else if (event.getSource() == guiRegisterProject.getContinueButton()) {
            continueButton();
        } else if (event.getSource() == guiRegisterProject.getCancelButton()) {
            cancel();
        }
    }

    private void addProjectManager() {
        GUIRegisterProjectManager guiRegisterProjectManager = new GUIRegisterProjectManager();
        ProjectManagerDAO projectManagerDAO = new ProjectManagerDAO();
        Stage stage = new Stage();
        stage.setTitle("Add Project Manager");
        stage.initModality(Modality.APPLICATION_MODAL);
        guiRegisterProjectManager.start(stage);
        try {
            guiRegisterProject.loadProjectManagers(projectManagerDAO.getProjectManagers());
        } catch (DataOperationException e) {
            guiRegisterProjectManager.showError(e.getMessage());
        }
    }

    private void continueButton() {
        if (guiRegisterProject.validateFields()) {
            Project project = buildProject();
           // TODO: armar objeto Project y pasar a siguiente GUI (horario/calendarización)
        }
    }

    private Project buildProject() {
        String name = guiRegisterProject.getNameTextField().getText();
        String description = guiRegisterProject.getDescriptionTextArea().getText();
        String generalObjective = guiRegisterProject.getGeneralObjectiveTextField().getText();
        String mediatesObjectives = guiRegisterProject.getMediateObjectivesTextField().getText();
        String immediateObjectives = guiRegisterProject.getImmediateObjectivesTextField().getText();
        String methodology = guiRegisterProject.getMethodologyTextField().getText();
        String resources = guiRegisterProject.getResourcesTextField().getText();
        Date initialDate = Date.valueOf(guiRegisterProject.getInitialDatePicker().getValue());
        Date finalDate = Date.valueOf(guiRegisterProject.getFinalDatePicker().getValue());
        int availablePlaces = Integer.parseInt(guiRegisterProject.getAvailablePlacesTextField().getText());
        Enterprise enterprise = guiRegisterProject.getEnterpriseComboBox().getValue();
        ProjectManager projectManager = guiRegisterProject.getProjectManagerComboBox().getValue();
        return new Project(0, name, description, generalObjective, mediatesObjectives, immediateObjectives, methodology, resources, initialDate, finalDate, true, availablePlaces, enterprise, projectManager);
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