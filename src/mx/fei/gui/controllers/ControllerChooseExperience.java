package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIChooseExperience;
import mx.fei.gui.views.GUIManageExperience;
import mx.fei.gui.views.GUIModifyExperience;
import mx.fei.logic.dao.EducationalExperienceDAO;
import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import mx.fei.gui.views.GUIAddStudents;

import java.util.List;

public class ControllerChooseExperience {
    private GUIChooseExperience guiChooseExperience;
    private EducationalExperienceDAO educationalExperienceDAO;

    public ControllerChooseExperience(GUIChooseExperience guiChooseExperience) {
        this.guiChooseExperience = guiChooseExperience;
        this.educationalExperienceDAO = new EducationalExperienceDAO();
        loadExperiences();
    }

    private void loadExperiences() {
        try {
            List<EducationalExperience> experiences = educationalExperienceDAO.getEducationalExperiences();
            guiChooseExperience.setExperiences(experiences);
        } catch (DataOperationException e) {
            guiChooseExperience.showError(e.getMessage());
        }
    }

    public void handleSelectReturnButtons(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Seleccionar" -> {
                handleSelectExperience();
            }
            case "Regresar" -> {
                openMenuExperience();
            }
        }
    }

    private void handleSelectExperience() {
        EducationalExperience selectedExperience;
        try {
            selectedExperience = guiChooseExperience.getSelectedExperience();
            if (guiChooseExperience.isToModify()) {
                GUIModifyExperience guiModifyExperience = new GUIModifyExperience(selectedExperience);
                Stage stage = new Stage();
                guiModifyExperience.start(stage);
                guiChooseExperience.closeWindow();
            } else {
                GUIAddStudents guiAddStudents = new GUIAddStudents(selectedExperience);
                Stage stage = new Stage();
                guiAddStudents.start(stage);
                guiChooseExperience.closeWindow();
            }
        } catch (IllegalStateException e) {
            guiChooseExperience.showError("Seleccione un experiencia.");
        }
    }

    private void openMenuExperience() {
        GUIManageExperience guiManageExperience = new GUIManageExperience();
        Stage stage = new Stage();
        stage.setTitle("Seleccionar experiencia");
        guiManageExperience.start(stage);
        guiChooseExperience.closeWindow();
    }
}