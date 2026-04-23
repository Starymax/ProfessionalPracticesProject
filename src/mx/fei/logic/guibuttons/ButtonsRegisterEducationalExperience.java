package mx.fei.logic.guibuttons;

import mx.fei.guis.GUIRegisterEducationalExperience;
import mx.fei.logic.dao.EducationalExperienceDAO;
import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.exceptions.DataOperationException;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.NoSuchElementException;

public class ButtonsRegisterEducationalExperience implements ActionListener {
    GUIRegisterEducationalExperience guiRegisterEducationalExperience;
    EducationalExperienceDAO educationalExperienceDAO;
    public ButtonsRegisterEducationalExperience(GUIRegisterEducationalExperience guiRegisterEducationalExperience) {
        this.guiRegisterEducationalExperience = guiRegisterEducationalExperience;
        educationalExperienceDAO = new EducationalExperienceDAO();
    }
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getActionCommand().equals("Registrar")) {
            if (!guiRegisterEducationalExperience.validateFields()) {
            } else if (nrcExists()) {
                JOptionPane.showMessageDialog(guiRegisterEducationalExperience,"El NRC ingresado ya fue registrado previamente");
            } else {
                String nrc = guiRegisterEducationalExperience.getTextFieldNrc().getText().trim();
                String name = guiRegisterEducationalExperience.getTextFieldName().getText().trim();
                String carrer = guiRegisterEducationalExperience.getTextFieldCareer().getText().trim();
                String period = guiRegisterEducationalExperience.getTextFieldPeriod().getText().trim();
                EducationalExperience educationalExperience = new EducationalExperience(nrc, name, carrer, period, null);
                try {
                    boolean registered = educationalExperienceDAO.registerEducationalExperience(educationalExperience);
                    if (registered) {
                        JOptionPane.showMessageDialog(guiRegisterEducationalExperience, "Experiencia educativa registrada exitosamente.", "Exito", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(guiRegisterEducationalExperience, e.getMessage(), "Datos invalidos", JOptionPane.WARNING_MESSAGE);
                } catch (DataOperationException e) {
                    JOptionPane.showMessageDialog(guiRegisterEducationalExperience, "Error al registrar la experiencia. Intente mas tarde", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (event.getActionCommand().equals("Cancelar")) {
            guiRegisterEducationalExperience.dispose();
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
            JOptionPane.showMessageDialog(guiRegisterEducationalExperience,
                    "Error al verificar el NRC. Intente mas tarde.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return nrcExists = true;
        }
        return nrcExists;
    }
}