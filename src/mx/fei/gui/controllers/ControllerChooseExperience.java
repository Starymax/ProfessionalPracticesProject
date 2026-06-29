package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIAddStudents;
import mx.fei.gui.views.GUIChooseExperience;
import mx.fei.gui.views.GUIChooseStudent;
import mx.fei.gui.views.GUIManageExperience;
import mx.fei.gui.views.GUIModifyExperience;
import mx.fei.logic.dao.EducationalExperienceDAO;
import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class ControllerChooseExperience {
    private GUIChooseExperience guiChooseExperience;
    private EducationalExperienceDAO educationalExperienceDAO;

    public ControllerChooseExperience(GUIChooseExperience guiChooseExperience) {
        this.guiChooseExperience = guiChooseExperience;
        this.educationalExperienceDAO = new EducationalExperienceDAO();
        loadExperiences();
    }

    public void handleSelectReturnButtons(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Seleccionar" -> {
                handleSelectExperience();
            }
            case "Regresar" -> {
                guiChooseExperience.closeWindow();
            }
        }
    }

    private void loadExperiences() {
        try {
            List<EducationalExperience> experiences;
            if (guiChooseExperience.isToModify()) {
                experiences = educationalExperienceDAO.getEducationalExperiences();
            } else if (guiChooseExperience.isToConsultStudent()) {
                experiences = educationalExperienceDAO.getEducationalExperiencesByProfessor(guiChooseExperience.getProfessor().getUserId());
            } else {
                experiences = educationalExperienceDAO.getActiveEducationalExperiences();
            }
            guiChooseExperience.setExperiences(experiences);
        } catch (DataOperationException e) {
            guiChooseExperience.showError(e.getMessage());
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
            } else if (guiChooseExperience.isToConsultStudent()) {
                GUIChooseStudent guiChooseStudent = new GUIChooseStudent();
                guiChooseStudent.setConsultByExperience(true);
                guiChooseStudent.setEducationalExperience(selectedExperience);
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                guiChooseStudent.start(stage);
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
}