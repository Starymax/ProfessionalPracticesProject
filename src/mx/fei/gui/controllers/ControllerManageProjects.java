package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIManageProjects;
import mx.fei.gui.views.GUIRegisterProject;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import mx.fei.logic.dao.EnterpriseDAO;
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
            openManageProject();
        } else if (event.getSource() == guiManageProjects.getButtonGoBack()) {
            goBack();
        }
    }

    private void openRegisterProject() {
        try {
            GUIRegisterProject guiRegisterProject = new GUIRegisterProject();
            Stage newStage = new Stage();
            guiRegisterProject.start(newStage);
            EnterpriseDAO enterpriseDAO = new EnterpriseDAO();
            guiRegisterProject.loadEnterprises(enterpriseDAO.getActiveEnterprises());
            guiManageProjects.getStage().close();
        } catch (DataOperationException e) {
            guiManageProjects.showError(e.getMessage());
        }
    }

    private void openManageProject() {
        // TODO: abrir GUIManageProject (CU-10)
    }

    private void goBack() {
        // TODO: abrir GUICoordinatorMenu
        guiManageProjects.getStage().close();
    }
}