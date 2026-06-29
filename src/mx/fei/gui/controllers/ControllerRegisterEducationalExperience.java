package mx.fei.gui.controllers;

import mx.fei.logic.dao.EducationalExperienceDAO;
import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.exceptions.DataOperationException;
import mx.fei.gui.views.GUIRegisterEducationalExperience;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;

import java.util.NoSuchElementException;

public class ControllerRegisterEducationalExperience {
    GUIRegisterEducationalExperience guiRegisterEducationalExperience;
    EducationalExperienceDAO educationalExperienceDAO;
    Alert alertError = new Alert(AlertType.ERROR);
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

    private void handleRegisterButton() {
        if (guiRegisterEducationalExperience.validateFields()) {
            if (nrcExists()) {
                guiRegisterEducationalExperience.showError("NRC ya registrado");
            } else {
                try {
                    boolean registered = educationalExperienceDAO.registerEducationalExperience(buildEducationalExperience());
                    if (registered) {
                        guiRegisterEducationalExperience.showSuccess("Experiencia educativa registrada exitosamente.");
                        guiRegisterEducationalExperience.closeWindow();
                    }
                } catch (IllegalArgumentException e) {
                    guiRegisterEducationalExperience.showError("Datos invalidos");
                } catch (DataOperationException e) {
                    guiRegisterEducationalExperience.showError(e.getMessage());
                }
            }
        }
    }

    private EducationalExperience buildEducationalExperience() {
        String nrc = guiRegisterEducationalExperience.getTextFieldNrc().getText().trim();
        String name = guiRegisterEducationalExperience.getTextFieldName().getText().trim();
        String career = guiRegisterEducationalExperience.getTextFieldCareer().getText().trim();
        String period = guiRegisterEducationalExperience.getSelectedPeriod();
        return new EducationalExperience(nrc, name, career, null, period, true);
    }

    private boolean nrcExists() {
        boolean nrcExists = false;
        String nrc = guiRegisterEducationalExperience.getTextFieldNrc().getText().trim();
        try {
            educationalExperienceDAO.getEducationalExperienceByNrc(nrc);
            nrcExists = true;
        } catch (NoSuchElementException e) {
            nrcExists = false;
        } catch (DataOperationException e) {
            guiRegisterEducationalExperience.showError(e.getMessage());
        }
        return nrcExists;
    }
}