package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIChooseExperience;
import mx.fei.gui.views.GUIManageExperience;
import mx.fei.gui.views.GUIRegisterEducationalExperience;
import mx.fei.logic.dao.EducationalExperienceDAO;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.stage.Stage;
import javafx.stage.Modality;

public class ControllerManageExperience {
    private GUIManageExperience guiManageExperience;

    public ControllerManageExperience(GUIManageExperience guiManageExperience) {
        this.guiManageExperience = guiManageExperience;
    }

    public void openRegisterExperience() {
        GUIRegisterEducationalExperience guiRegisterEducationalExperience = new GUIRegisterEducationalExperience();
        Stage stage = new Stage();
        stage.setTitle("Registrar experiencia");
        guiRegisterEducationalExperience.start(stage);
        guiManageExperience.closeWindow();
    }

    private void openModifyExperience() {
        GUIChooseExperience guiChooseExperience = new GUIChooseExperience();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Modificar experiencia");
        guiChooseExperience.setToModify(true);
        guiChooseExperience.start(stage);
    }
    private void fillExperience () {
        GUIChooseExperience guiChooseExperience = new GUIChooseExperience();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Dar de alta experiencia");
        guiChooseExperience.start(stage);
    }

    private boolean existEducationalExperiences() {
        EducationalExperienceDAO educationalExperienceDAO = new EducationalExperienceDAO();
        boolean experiencesExist = true;
        try {
            if (educationalExperienceDAO.getEducationalExperiences().isEmpty()) {
                experiencesExist = false;
            }
        } catch (DataOperationException e) {
            guiManageExperience.showError(e.getMessage());
        }
        return experiencesExist;
    }

    public void handleModifyExperienceButtonAction() {
        if (!existEducationalExperiences()){
        guiManageExperience.showError("No existen experiencias disponibles por el momento.");
        } else {
        openModifyExperience();
        }
    }

    public void handleActivateExperienceButtonAction() {
        if (!existEducationalExperiences()){
        guiManageExperience.showError("No existen experiencias disponibles por el momento.");
        } else {
        fillExperience();
        }
    }

}