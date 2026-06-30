package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIManageProjects;
import mx.fei.gui.views.GUIRegisterProject;
import mx.fei.gui.views.GUISelectProjects;
import mx.fei.logic.dao.EnterpriseDAO;
import mx.fei.logic.dao.ProjectDAO;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.stage.Stage;
import javafx.stage.Modality;

public class ControllerManageProjects {

    private final GUIManageProjects guiManageProjects;

    public ControllerManageProjects(GUIManageProjects guiManageProjects) {
        this.guiManageProjects = guiManageProjects;
    }

    public void openRegisterProject() {
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

    private boolean existProjects() {
        ProjectDAO projectDAO = new ProjectDAO();
        boolean projectsExist = true;
        try {
            if (projectDAO.getAllProjects().isEmpty()){
                projectsExist = false;
            }
        } catch (DataOperationException e) {
            guiManageProjects.showError(e.getMessage());
        }
        return projectsExist;
    }

    public void goBack() {
        guiManageProjects.getStage().close();
    }

    public void handleManageProjectButtonAction() {
        if (!existProjects()){
        guiManageProjects.showError("No existen proyectos disponibles por el momento.");
        } else {
        openModifyProject();
        }
    }

}