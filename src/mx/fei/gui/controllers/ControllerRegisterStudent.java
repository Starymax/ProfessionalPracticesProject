package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIRegisterStudent;
import mx.fei.logic.dao.StudentDAO;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataOperationException;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import org.mindrot.jbcrypt.BCrypt;

public class ControllerRegisterStudent {
    private GUIRegisterStudent guiRegisterStudent;
    private StudentDAO studentDAO;

    public ControllerRegisterStudent(GUIRegisterStudent guiRegisterStudent) {
        this.guiRegisterStudent = guiRegisterStudent;
        studentDAO = new StudentDAO();
    }

    public void handleConfirmCancelButtons(ActionEvent event) {
        Button source = (Button) event.getSource();
        if (source.getText().equals("Confirmar")) {
            if (guiRegisterStudent.validateFields()) {
                registerStudent();
            }
        }  else if (source.getText().equals("Cancelar")) {
            cancel();
        }
    }

    private void registerStudent() {
        String names = guiRegisterStudent.getTextFieldNames().getText().trim();
        String lastNames = guiRegisterStudent.getTextFieldLastName().getText().trim();
        String mail = guiRegisterStudent.getTextFieldMail().getText().trim();
        String rawPassword = guiRegisterStudent.getTextFieldPassword().getText();
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        String enrollment = guiRegisterStudent.getTextFieldEnrollment().getText().trim();
        String gender = guiRegisterStudent.getRadioButtonMan().isSelected() ? "Hombre" : "Mujer";
        boolean indigenousLanguage = guiRegisterStudent.getRadioButtonSpeakIndigenousLanguage().isSelected();
        boolean active = guiRegisterStudent.getToggleButtonState().isSelected();
        Student student = new Student(0, names, lastNames, mail, hashedPassword, gender, active, enrollment, indigenousLanguage, null);
        try {
            boolean registered = studentDAO.registerStudent(student);
            if (registered) {
                guiRegisterStudent.showSuccess("Alumno registrado exitosamente.");
                guiRegisterStudent.closeWindow();
            }
        } catch (IllegalArgumentException e) {
            guiRegisterStudent.showError(e.getMessage());
        } catch (IllegalStateException e) {
            guiRegisterStudent.showError(e.getMessage());
        } catch (DataOperationException e) {
            guiRegisterStudent.showError("Error interno al registrar el alumno. Intente mas tarde.");
        }
    }

    private void cancel() {
        guiRegisterStudent.closeWindow();
    }
}