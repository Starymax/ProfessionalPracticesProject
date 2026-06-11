package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIChooseStudent;
import mx.fei.gui.views.GUIModifyStudent;
import mx.fei.logic.dao.StudentDAO;
import mx.fei.logic.dao.UserDAO;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerModifyStudent {
    private GUIModifyStudent guiModifyStudent;
    private StudentDAO studentDAO;
    private UserDAO userDAO;
    private GUIChooseStudent guiChooseStudent = new GUIChooseStudent();
    private static final Logger logger = Logger.getLogger(ControllerModifyStudent.class.getName());

    public ControllerModifyStudent(GUIModifyStudent guiModifyStudent) {
        this.guiModifyStudent = guiModifyStudent;
        this.studentDAO = new StudentDAO();
        this.userDAO = new UserDAO();
    }

    public void handleUpdateCancelButtons(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Actualizar" -> {
                handleUpdate();
            }
            case "Cancelar" -> {
                guiModifyStudent.closeWindow();
            }
        }
    }

    private void handleUpdate() {
        if (guiModifyStudent.validateFields()) {
            try {
                Student original = guiModifyStudent.getStudent();
                String gender = guiModifyStudent.getRadioButtonMan().isSelected() ? "Hombre" : "Mujer";
                boolean indigenousLanguage = guiModifyStudent.getRadioButtonSpeakIndigenousLanguage().isSelected();
                boolean active = guiModifyStudent.getToggleState().isSelected();
                float grade = Float.parseFloat(guiModifyStudent.getTextFieldGrade().getText().trim());
                Student updated = new Student(
                        original.getUserId(),
                        guiModifyStudent.getTextFieldNames().getText().trim(),
                        guiModifyStudent.getTextFieldLastName().getText().trim(),
                        guiModifyStudent.getTextFieldMail().getText().trim(),
                        original.getPassword(),
                        gender,
                        active,
                        original.getEnrollment(),
                        indigenousLanguage,
                        original.getAssignedProject(),
                        grade
                );
                userDAO.updateUser(updated);
                boolean result = studentDAO.modifyStudent(updated);
                if (result) {
                    guiModifyStudent.showSuccess("Alumno actualizado exitosamente.");
                    guiModifyStudent.closeWindow();
                }
            } catch (IllegalArgumentException e) {
                guiModifyStudent.showError(e.getMessage());
            } catch (DataOperationException e) {
                logger.log(Level.SEVERE, "Error al modificar estudiante", e);
                guiModifyStudent.showError("Error al actualizar. Intente más tarde.");
            }
        }
    }
}
