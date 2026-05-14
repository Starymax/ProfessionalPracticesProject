package mx.fei.gui.controllers;

import javafx.stage.Modality;
import mx.fei.gui.views.GUIManageProjects;
import mx.fei.gui.views.GUIRegisterProject;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import mx.fei.gui.views.GUISelectProjects;
import mx.fei.logic.dao.EnterpriseDAO;
import mx.fei.logic.dao.ProjectDAO;
import mx.fei.logic.exceptions.DataOperationException;

public class ControllerManageProjects implements EventHandler<ActionEvent> {

    private final GUIManageProjects guiManageProjects;

    public ControllerManageProjects(GUIManageProjects guiManageProjects) {
        this.guiManageProjects = guiManageProjects;
    }

    @Override
    public void handle(ActionEvent event) {
        if (event.getSource() == guiManageProjects.getButtonRegisterProject()) {
            openRegisterProject();
        } else if (event.getSource() == guiManageProjects.getButtonManageProject()) {
            openModifyProject();
        } else if (event.getSource() == guiManageProjects.getButtonGoBack()) {
            goBack();
        }
    }

    private void openRegisterProject() {
        try {
            GUIRegisterProject guiRegisterProject = new GUIRegisterProject();
            Stage newStage = new Stage();
            newStage.initModality(Modality.APPLICATION_MODAL);
            guiRegisterProject.start(newStage);
            EnterpriseDAO enterpriseDAO = new EnterpriseDAO();
            guiRegisterProject.loadEnterprises(enterpriseDAO.getActiveEnterprises());
            guiManageProjects.getStage().close();
        } catch (DataOperationException e) {
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
            guiManageProjects.showError(e.getMessage());
        }
    }

    private void goBack() {
        guiManageProjects.getStage().close();
    }
}