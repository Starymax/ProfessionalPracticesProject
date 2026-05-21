package mx.fei.gui.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import mx.fei.gui.views.GUIRegisterEducationalExperience;
import mx.fei.logic.dao.EducationalExperienceDAO;
import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.exceptions.DataOperationException;

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
        if (source.getText().equals("Registrar")) {
            handleRegisterButton();
        } else if (source.getText().equals("Cancelar")) {
            guiRegisterEducationalExperience.closeWindow();
        }
    }

    private void handleRegisterButton() {
        if (guiRegisterEducationalExperience.validateFields()) {
            if (nrcExists()) {
                guiRegisterEducationalExperience.showError("NRC ya registrado");
            } else {
                String nrc = guiRegisterEducationalExperience.getTextFieldNrc().getText().trim();
                String name = guiRegisterEducationalExperience.getTextFieldName().getText().trim();
                String career = guiRegisterEducationalExperience.getTextFieldCareer().getText().trim();
                String period = guiRegisterEducationalExperience.getSelectedPeriod();
                EducationalExperience educationalExperience = new EducationalExperience(nrc, name, career, null, period);
                try {
                    boolean registered = educationalExperienceDAO.registerEducationalExperience(educationalExperience);
                    if (registered) {
                        guiRegisterEducationalExperience.showSuccess("Experiencia educativa registrada exitosamente.");
                        guiRegisterEducationalExperience.closeWindow();
                    }
                } catch (IllegalArgumentException e) {
                    guiRegisterEducationalExperience.showError("Datos invalidos");
                } catch (DataOperationException e) {
                    guiRegisterEducationalExperience.showError("Error al registrar el experiencia");
                }
            }
        }
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
            guiRegisterEducationalExperience.showError("Error al verificar el NRC. Intente más tarde.");
        }
        return nrcExists;
    }
}