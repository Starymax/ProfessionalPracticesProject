package mx.fei.gui.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import mx.fei.gui.views.GUIAddStudents;
import mx.fei.gui.views.GUIChooseExperience;
import mx.fei.gui.views.GUIManageExperience;
import mx.fei.gui.views.GUIModifyExperience;
import mx.fei.logic.dao.EducationalExperienceDAO;
import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.exceptions.DataOperationException;
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
            guiChooseExperience.showError("Error al cargar las experiencias educativas.");
        }
    }

    public void handleButtonsSelectReturn(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Seleccionar" -> handleSelect();
            case "Regresar" -> {
                GUIManageExperience guiManageExperience = new GUIManageExperience();
                Stage stage = new Stage();
                stage.setTitle("Seleccionar experiencia");
                guiManageExperience.start(stage);
                guiChooseExperience.closeWindow();
            }
        }
    }

    private void handleSelect() {
        EducationalExperience selected = guiChooseExperience.getSelectedExperience();
        if (selected == null) {
            guiChooseExperience.showError("Selecciona una experiencia de la lista.");
        }
        if (guiChooseExperience.isToModify()) {
            GUIModifyExperience guiModifyExperience = new GUIModifyExperience(selected);
            Stage stage = new Stage();
            guiModifyExperience.start(stage);
            guiChooseExperience.closeWindow();
        } else {
            GUIAddStudents  guiAddStudents = new GUIAddStudents(selected);
            Stage stage = new Stage();
            guiAddStudents.start(stage);
            guiChooseExperience.closeWindow();
        }

    }
}