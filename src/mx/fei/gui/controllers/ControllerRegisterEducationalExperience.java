package mx.fei.gui.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import mx.fei.gui.views.GUIManageExperience;
import mx.fei.gui.views.GUIRegisterEducationalExperience;
import mx.fei.logic.dao.EducationalExperienceDAO;
import mx.fei.logic.dao.PeriodDAO;
import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.dto.Period;
import mx.fei.logic.exceptions.DataOperationException;

import java.util.NoSuchElementException;

public class ControllerRegisterEducationalExperience {
    GUIRegisterEducationalExperience guiRegisterEducationalExperience;
    EducationalExperienceDAO educationalExperienceDAO;
    Alert alertInformation = new Alert(AlertType.INFORMATION);
    Alert alertWarning = new Alert(AlertType.WARNING);
    Alert alertError = new Alert(AlertType.ERROR);
    public ControllerRegisterEducationalExperience(GUIRegisterEducationalExperience guiRegisterEducationalExperience) {
        this.guiRegisterEducationalExperience = guiRegisterEducationalExperience;
        educationalExperienceDAO = new EducationalExperienceDAO();
    }

    public void handleRegisterCancelButtons(ActionEvent event) {
        Button source = (Button) event.getSource();
        if (source.getText().equals("Registrar")) {
            if (guiRegisterEducationalExperience.validateFields()) {
                if (nrcExists()) {
                    alertWarning.setTitle("NRC no disponible");
                    alertWarning.setHeaderText(null);
                    alertWarning.setContentText("El NRC ingresado ya fue registrado previamente.");
                    alertWarning.showAndWait();
                } else {
                    String nrc = guiRegisterEducationalExperience.getTextFieldNrc().getText().trim();
                    String name = guiRegisterEducationalExperience.getTextFieldName().getText().trim();
                    String career = guiRegisterEducationalExperience.getTextFieldCareer().getText().trim();
                    int year = guiRegisterEducationalExperience.getComboBoxYear().getValue();
                    int semesterNumber = guiRegisterEducationalExperience.getSelectedSemesterNumber();
                    PeriodDAO  periodDAO = new PeriodDAO();
                    Period period;
                    try {
                        try {
                            period = periodDAO.getPeriodByYearAndNumber(year, semesterNumber);
                        } catch (NoSuchElementException e) {
                            periodDAO.activatePeriod(year,semesterNumber);
                            period = periodDAO.getPeriodByYearAndNumber(year,semesterNumber);
                        }
                        EducationalExperience educationalExperience = new EducationalExperience(nrc, name, career, null, period);
                        boolean registered = educationalExperienceDAO.registerEducationalExperience(educationalExperience);
                        if (registered) {
                            alertInformation.setTitle("Experiencia educativa registrada");
                            alertInformation.setHeaderText(null);
                            alertInformation.setContentText("Experiencia educativa registrada exitosamente.");
                            alertInformation.showAndWait();
                            guiRegisterEducationalExperience.closeWindow();
                            openManageExperience();
                        }
                    } catch (IllegalArgumentException e) {
                        alertWarning.setTitle("Error");
                        alertWarning.setHeaderText(null);
                        alertWarning.setContentText("Datos invalidos, intente nuevamente.");
                        alertWarning.showAndWait();
                    } catch (DataOperationException e) {
                        alertError.setTitle("Error");
                        alertError.setHeaderText(null);
                        alertError.setContentText("Error al registrar la experiencia. Intente mas tarde");
                        alertError.showAndWait();
                    }
                }
            }
        } else if (source.getText().equals("Cancelar")) {
            guiRegisterEducationalExperience.closeWindow();
            GUIManageExperience guiManageExperience = new GUIManageExperience();
            Stage stage = new Stage();
            stage.setTitle("Gestionar experiencia");
            guiManageExperience.start(stage);
        }
    }

    public boolean nrcExists() {
        boolean nrcExists = false;
        String nrc = guiRegisterEducationalExperience.getTextFieldNrc().getText().trim();
        try {
            educationalExperienceDAO.getEducationalExperienceByNrc(nrc);
            nrcExists = true;
        } catch (NoSuchElementException e) {
            return nrcExists;
        } catch (DataOperationException e) {
            alertError.setTitle("Error");
            alertError.setHeaderText(null);
            alertError.setContentText("Error al verificar el NRC. Intente mas tarde.");
            alertError.showAndWait();
            return nrcExists = true;
        }
        return nrcExists;
    }
    public void openManageExperience() {
        GUIManageExperience guiManageExperience = new GUIManageExperience();
        Stage stage = new Stage();
        guiManageExperience.start(stage);
    }
}