package mx.fei.logic.guibuttons;

import mx.fei.gui.GUIRegisterStudent;
import mx.fei.logic.dao.StudentDAO;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataOperationException;


import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonsRegisterStudent implements ActionListener {
    GUIRegisterStudent guiRegisterStudent;
    StudentDAO studentDAO;
    public ButtonsRegisterStudent(GUIRegisterStudent guiRegisterStudent) {
        this.guiRegisterStudent = guiRegisterStudent;
        studentDAO = new StudentDAO();
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getActionCommand().equals("Confirmar")) {
            if (!guiRegisterStudent.validateFields()) {
                return;
            }
            String names = guiRegisterStudent.getTextFieldNames().getText().trim();
            String lastNames = guiRegisterStudent.getTextFieldLastName().getText().trim();
            String mail = guiRegisterStudent.getTextFieldMail().getText().trim();
            String password = new String(guiRegisterStudent.getTextFieldPassword().getPassword());
            String enrollment = guiRegisterStudent.getTextFieldEnrollment().getText().trim();
            String period = guiRegisterStudent.getTextFieldPeriod().getText().trim();
            String genero = guiRegisterStudent.getRadioButtonMan().isSelected() ? "Hombre" : "Mujer";
            boolean indigenousLanguage = guiRegisterStudent.getRadioButtonSpeakIndigenousLanguage().isSelected();
            boolean active = guiRegisterStudent.getToggleState().isSelected();

            Student student = new Student(0, names, lastNames, mail, password, genero, active, enrollment, period, indigenousLanguage, 0.0f, null, null);
            try {
                boolean registered = studentDAO.registerStudent(student);
                if (registered) {
                    JOptionPane.showMessageDialog(guiRegisterStudent, "Alumno registrado exitosamente.", "Exito", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(guiRegisterStudent,e.getMessage(), "Datos invalidos", JOptionPane.WARNING_MESSAGE);
            } catch (IllegalStateException e) {
                JOptionPane.showMessageDialog(guiRegisterStudent,e.getMessage(), "Matricula duplicada", JOptionPane.WARNING_MESSAGE);
            } catch (DataOperationException e) {
                JOptionPane.showMessageDialog(guiRegisterStudent, "Error interno al registrar el alumno. Intente más tarde.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if (event.getActionCommand().equals("Cancelar")) {
            guiRegisterStudent.dispose();
        }
    }
}
