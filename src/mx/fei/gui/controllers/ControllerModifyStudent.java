package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIChooseStudent;
import mx.fei.gui.views.GUIModifyStudent;
import mx.fei.logic.dao.StudentDAO;
import mx.fei.logic.dao.UserDAO;
import mx.fei.logic.dto.Project;
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
    private static final Logger LOGGER = Logger.getLogger(ControllerModifyStudent.class.getName());

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
                Student studentUpdated = buildStudent(guiModifyStudent.getStudent());
                userDAO.updateUser(studentUpdated);
                boolean updated = studentDAO.modifyStudent(studentUpdated);
                if (updated) {
                    guiModifyStudent.showSuccess("Alumno actualizado exitosamente.");
                    guiModifyStudent.closeWindow();
                }
            } catch (IllegalArgumentException e) {
                guiModifyStudent.showError(e.getMessage());
            } catch (DataOperationException e) {
                LOGGER.log(Level.SEVERE, "Error al modificar estudiante", e);
                guiModifyStudent.showError(e.getMessage());
            }
        }
    }

    private Student buildStudent(Student originalStudent) {
        int originalStudenId = originalStudent.getUserId();
        String originalStudentPassword = originalStudent.getPassword();
        String names = guiModifyStudent.getTextFieldNames().getText().trim();
        String lastName =  guiModifyStudent.getTextFieldLastName().getText().trim();
        String originalStudentEnrollment = originalStudent.getEnrollment();
        String mail = guiModifyStudent.getTextFieldMail().getText().trim();
        Project originalStudentProject = originalStudent.getAssignedProject();
        String gender = guiModifyStudent.getRadioButtonMan().isSelected() ? "Hombre" : "Mujer";
        boolean indigenousLanguage = guiModifyStudent.getRadioButtonSpeakIndigenousLanguage().isSelected();
        boolean activeStatus = guiModifyStudent.getToggleActiveState().isSelected();
        float grade = Float.parseFloat(guiModifyStudent.getTextFieldGrade().getText().trim());
        return new Student(
                originalStudenId,
                names,
                lastName,
                mail,
                originalStudentPassword,
                gender,
                activeStatus,
                originalStudentEnrollment,
                indigenousLanguage,
                originalStudentProject,
                grade
        );
    }
}
