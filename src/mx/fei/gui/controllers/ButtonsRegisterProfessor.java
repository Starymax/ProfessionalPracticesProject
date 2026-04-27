package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIRegisterProfessor;
import mx.fei.logic.dao.ProfessorDAO;
import mx.fei.logic.dto.Professor;
import mx.fei.logic.exceptions.DataOperationException;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class ButtonsRegisterProfessor implements ActionListener {

    private final GUIRegisterProfessor guiRegisterProfessor;

    public ButtonsRegisterProfessor(GUIRegisterProfessor guiRegisterProfessor) {
        this.guiRegisterProfessor = guiRegisterProfessor;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == guiRegisterProfessor.getButtonRegister()) {
            if (guiRegisterProfessor.validateFields() && guiRegisterProfessor.validateFieldPassword()) {
                register();
            }
        } else if (actionEvent.getSource() == guiRegisterProfessor.getButtonCancel()) {
            cancel();
        }
    }

    private void register() {
        ProfessorDAO professorDAO = new ProfessorDAO();
        try {
            String name = guiRegisterProfessor.getTextFieldName().getText();
            String lastName = guiRegisterProfessor.getTextFieldLastName().getText();
            int personalNumber = Integer.parseInt(guiRegisterProfessor.getTextFieldPersonalNumber().getText().trim());
            String gender = Arrays.toString(guiRegisterProfessor.getComboBoxGender().getSelectedObjects());
            String email = guiRegisterProfessor.getTextFieldEmail().getText();
            String password = new String(guiRegisterProfessor.getTextFieldPassword().getPassword());
            String hashPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            String shift = Arrays.toString(guiRegisterProfessor.getComboBoxShift().getSelectedObjects());
            boolean isCoordinator = guiRegisterProfessor.getCheckBoxIsCoordinator().isSelected();
            boolean isAdministrator = guiRegisterProfessor.getCheckBoxIsAdministrator().isSelected();
            Professor professor = new Professor(0, name, lastName, email, hashPassword, gender, true, personalNumber, isCoordinator, isAdministrator, shift);
            if (professorDAO.registerProfessor(professor)) {
                if (isCoordinator) {
                    JOptionPane.showMessageDialog(guiRegisterProfessor, "Coordinador registrado exitosamente", "Continuar", JOptionPane.INFORMATION_MESSAGE);
                } else if (isAdministrator) {
                    JOptionPane.showMessageDialog(guiRegisterProfessor, "Administrador registrado exitosamente", "Continuar", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(guiRegisterProfessor, "Profesor registrado exitosamente", "Continuar", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(guiRegisterProfessor, e.getMessage(), "El campo solo debe contener números", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(guiRegisterProfessor, e.getMessage(), "Datos inválidos", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(guiRegisterProfessor, e.getMessage(), "Numero de personal duplicado", JOptionPane.WARNING_MESSAGE);
        } catch (DataOperationException e) {
            JOptionPane.showMessageDialog(guiRegisterProfessor, e.getMessage(), "Error al insertar el Profesor", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cancel() {
        int confirmDialog = JOptionPane.showConfirmDialog(guiRegisterProfessor, "¿Seguro que desea cancelar? Se perderá la información ingresada.", "Cancelar registro", JOptionPane.YES_NO_OPTION);
        if (confirmDialog == JOptionPane.YES_OPTION) {
            guiRegisterProfessor.dispose();
        }
    }
}