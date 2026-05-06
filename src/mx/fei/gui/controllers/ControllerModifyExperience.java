package mx.fei.gui.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import mx.fei.gui.views.GUIChooseExperience;
import mx.fei.gui.views.GUIModifyExperience;
import mx.fei.logic.dao.EducationalExperienceDAO;
import mx.fei.logic.dao.ProfessorDAO;
import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.dto.Professor;
import mx.fei.logic.exceptions.DataOperationException;
import java.util.List;

public class ControllerModifyExperience {
    private GUIModifyExperience guiModifyExperience;
    private EducationalExperienceDAO educationalExperienceDAO;
    private ProfessorDAO professorDAO;
    private GUIChooseExperience guiChooseExperience = new GUIChooseExperience();

    public ControllerModifyExperience(GUIModifyExperience guiModifyExperience) {
        this.guiModifyExperience = guiModifyExperience;
        this.educationalExperienceDAO = new EducationalExperienceDAO();
        this.professorDAO = new ProfessorDAO();
        loadProfessors();
    }

    private void loadProfessors() {
        try {
            List<Professor> professors = professorDAO.getProfessors();
            guiModifyExperience.setProfessors(professors);
        } catch (DataOperationException e) {
            guiModifyExperience.showError("Error al cargar la lista de profesores.");
        }
    }

    public void handleButtons(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Actualizar" -> handleUpdate();
            case "Regresar" -> {
                guiModifyExperience.closeWindow();
                Stage stage = new Stage();
                stage.setTitle("Seleccionar experiencia");
                guiChooseExperience.start(stage);
            }
        }
    }

    private void handleUpdate() {
        if (guiModifyExperience.validateFields()) {
            Professor selectedProfessor = guiModifyExperience.getSelectedProfessor();
            if (selectedProfessor == null) {
                selectedProfessor = guiModifyExperience.getExperience().getProfessor();
            }
            if (selectedProfessor != null) {
                EducationalExperience updated = new EducationalExperience(
                        guiModifyExperience.getExperience().getNrc(),
                        guiModifyExperience.getTextFieldName().getText().trim(),
                        guiModifyExperience.getTextFieldCareer().getText().trim(),
                        guiModifyExperience.getTextFieldPeriod().getText().trim(),
                        selectedProfessor
                );
                try {
                    boolean result = educationalExperienceDAO.modifyEducationalExperience(updated, selectedProfessor);
                    if (result) {
                        guiModifyExperience.showSuccess("Experiencia actualizada exitosamente.");
                        guiModifyExperience.closeWindow();
                        Stage stage = new Stage();
                        stage.setTitle("Seleccionar experiencia");
                        guiChooseExperience.start(stage);
                    }
                } catch (IllegalArgumentException e) {
                    guiModifyExperience.showError(e.getMessage());
                } catch (DataOperationException e) {
                    guiModifyExperience.showError("Error al actualizar. Intente más tarde.");
                }
            } else {
                guiModifyExperience.closeWindow();
            }
        }
    }
}