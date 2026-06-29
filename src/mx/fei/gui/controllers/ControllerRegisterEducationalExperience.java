package mx.fei.gui.controllers;

import mx.fei.logic.dao.EducationalExperienceDAO;
import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.exceptions.DataOperationException;
import mx.fei.gui.views.GUIRegisterEducationalExperience;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;

import java.util.ArrayList;
import java.util.List;

public class ControllerRegisterEducationalExperience {
    GUIRegisterEducationalExperience guiRegisterEducationalExperience;
    EducationalExperienceDAO educationalExperienceDAO;
    public ControllerRegisterEducationalExperience(GUIRegisterEducationalExperience guiRegisterEducationalExperience) {
        this.guiRegisterEducationalExperience = guiRegisterEducationalExperience;
        educationalExperienceDAO = new EducationalExperienceDAO();
    }

    public void handleRegisterCancelButtons(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Registrar" -> {
                handleRegisterButton();
            }
            case "Cancelar" -> {
                guiRegisterEducationalExperience.closeWindow();
            }
        }
    }

    public void handleNrcChanged() {
        String nrc = guiRegisterEducationalExperience.getTextFieldNrc().getText().trim();
        try {
            List<Integer> availableSections = computeAvailableSections(nrc);
            guiRegisterEducationalExperience.setAvailableSections(availableSections);
        } catch (DataOperationException e) {
            guiRegisterEducationalExperience.showError(e.getMessage());
        }
    }

    private List<Integer> computeAvailableSections(String nrc) throws DataOperationException {
        List<Integer> usedSections = nrc.isBlank() ? new ArrayList<>() : educationalExperienceDAO.getUsedSectionsByNrc(nrc);
        List<Integer> availableSections = new ArrayList<>();
        for (int section = 1; section <= guiRegisterEducationalExperience.getMaxSections(); section++) {
            if (!usedSections.contains(section)) {
                availableSections.add(section);
            }
        }
        return availableSections;
    }

    private void handleRegisterButton() {
        if (guiRegisterEducationalExperience.validateFields()) {
            try {
                boolean registered = educationalExperienceDAO.registerEducationalExperience(buildEducationalExperience());
                if (registered) {
                    guiRegisterEducationalExperience.showSuccess("Experiencia educativa registrada exitosamente.");
                    guiRegisterEducationalExperience.closeWindow();
                }
            } catch (IllegalStateException e) {
                guiRegisterEducationalExperience.showError(e.getMessage());
            } catch (IllegalArgumentException e) {
                guiRegisterEducationalExperience.showError("Datos invalidos");
            } catch (DataOperationException e) {
                guiRegisterEducationalExperience.showError(e.getMessage());
            }
        }
    }

    private EducationalExperience buildEducationalExperience() {
        String nrc = guiRegisterEducationalExperience.getTextFieldNrc().getText().trim();
        int section = guiRegisterEducationalExperience.getSelectedSection();
        String name = guiRegisterEducationalExperience.getTextFieldName().getText().trim();
        String career = guiRegisterEducationalExperience.getTextFieldCareer().getText().trim();
        String period = guiRegisterEducationalExperience.getSelectedPeriod();
        return new EducationalExperience(nrc, section, name, career, null, period, true);
    }
}
