package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIManageProjects;
import mx.fei.gui.views.GUIRegisterProject;
import mx.fei.gui.views.GUISelectProjects;
import mx.fei.logic.dao.EnterpriseDAO;
import mx.fei.logic.dao.ProjectDAO;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.stage.Modality;

public class ControllerManageProjects {

    private final GUIManageProjects guiManageProjects;

    public ControllerManageProjects(GUIManageProjects guiManageProjects) {
        this.guiManageProjects = guiManageProjects;
    }

    public void handleRegisterModifyReturnButtons(ActionEvent event) {
        Button button = (Button) event.getSource();
        switch (button.getText()) {
            case "Registrar proyecto" -> {
                openRegisterProject();
            }
            case "Gestionar proyecto" -> {
                openModifyProject();
            }
            case "Regresar" -> {
                goBack();
            }
        }
    }

    private void openRegisterProject() {
        GUIRegisterProject guiRegisterProject = new GUIRegisterProject();
        Stage newStage = new Stage();
        newStage.initModality(Modality.APPLICATION_MODAL);
        guiRegisterProject.start(newStage);
        try {
            EnterpriseDAO enterpriseDAO = new EnterpriseDAO();
            guiRegisterProject.loadEnterprises(enterpriseDAO.getActiveEnterprises());
            guiManageProjects.getStage().close();
        } catch (DataOperationException e) {
            newStage.close();
            guiManageProjects.showError(e.getMessage());
        }
    }

    private void openModifyProject() {
        GUISelectProjects guiSelectProjects = new GUISelectProjects();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        guiSelectProjects.start(stage);
        guiSelectProjects.setModify(true);
        ProjectDAO projectDAO = new ProjectDAO();
        try {
            guiSelectProjects.loadProjects(projectDAO.getAllProjects());
        } catch (DataOperationException e) {
            stage.close();
            guiManageProjects.showError(e.getMessage());
        }
    }

    private void goBack() {
        guiManageProjects.getStage().close();
    }
}