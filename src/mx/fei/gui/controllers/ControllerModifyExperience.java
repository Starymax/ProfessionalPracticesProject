package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIChooseExperience;
import mx.fei.gui.views.GUIModifyExperience;
import mx.fei.logic.dao.EducationalExperienceDAO;
import mx.fei.logic.dao.ProfessorDAO;
import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.dto.Professor;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.List;

public class ControllerModifyExperience {
    private GUIModifyExperience guiModifyExperience;
    private EducationalExperienceDAO educationalExperienceDAO;
    private ProfessorDAO professorDAO;

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

    public void handleUpdateReturnButtons(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Actualizar" -> {
                handleUpdate();
            }
            case "Regresar" -> {
                guiModifyExperience.closeWindow();
                openChooseExperience();
            }
        }
    }

    private void handleUpdate() {
        if (guiModifyExperience.validateFields()) {
            Professor selectedProfessor = guiModifyExperience.getSelectedProfessor();
            Professor professorToAssign = selectedProfessor != null ? selectedProfessor : guiModifyExperience.getExperience().getProfessor();
            if (professorToAssign == null) {
                guiModifyExperience.showError("Debe seleccionar un profesor, la experiencia no tiene ninguno asignado.");
            } else  {
                EducationalExperience educationalExperienceUpdated = buildExperience(professorToAssign);
                try {
                    boolean updated = educationalExperienceDAO.modifyEducationalExperience(educationalExperienceUpdated);
                    if (updated) {
                        guiModifyExperience.showSuccess("Experiencia actualizada exitosamente.");
                        guiModifyExperience.closeWindow();
                        openChooseExperience();
                    }
                } catch (IllegalArgumentException e) {
                    guiModifyExperience.showError(e.getMessage());
                } catch (DataOperationException e) {
                    guiModifyExperience.showError("Error al actualizar. Intente más tarde.");
                }
            }
        }
    }

    private EducationalExperience buildExperience(Professor professorToAssign) {
        String nrc = guiModifyExperience.getExperience().getNrc();
        String name = guiModifyExperience.getTextFieldName().getText().trim();
        String career = guiModifyExperience.getTextFieldCareer().getText().trim();
        String period = guiModifyExperience.getSelectedPeriod();
        boolean activeStatus = guiModifyExperience.getToggleActiveState().isSelected();
        return new EducationalExperience(nrc, name, career, professorToAssign, period, activeStatus);
    }

    private void openChooseExperience() {
        GUIChooseExperience guiChooseExperience = new GUIChooseExperience();
        Stage stage = new Stage();
        guiChooseExperience.setToModify(true);
        guiChooseExperience.start(stage);
    }
}