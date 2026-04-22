package mx.fei.logic.guibuttons;

import mx.fei.gui.GUIRegisterProfessor;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonsRegisterProfessor implements ActionListener {

    private GUIRegisterProfessor guiRegisterProfessor;

    public ButtonsRegisterProfessor(GUIRegisterProfessor guiRegisterProfessor) {
        this.guiRegisterProfessor = guiRegisterProfessor;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == guiRegisterProfessor.getButtonRegister()) {
            register();
        } else if (actionEvent.getSource() == guiRegisterProfessor.getButtonCancel()) {
            cancel();
        }
    }

    private void register() {
        if (guiRegisterProfessor.getTextFieldName().getText().trim().isEmpty() ||
                guiRegisterProfessor.getTextFieldPersonalNumber().getText().trim().isEmpty() ||
                guiRegisterProfessor.getTextFieldEmail().getText().trim().isEmpty() ||
                new String(guiRegisterProfessor.getTextFieldPassword().getPassword()).trim().isEmpty() ||
                guiRegisterProfessor.getTextFieldNRC().getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(guiRegisterProfessor,
                    "Complete todos los campos antes de registrar.",
                    "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(guiRegisterProfessor,
                "Profesor registrado exitosamente." +
                        (guiRegisterProfessor.getCheckBoxCoordinator().isSelected() ? "\nCoordinador registrado exitosamente." : ""),
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cancel() {
        int confirmDialog = JOptionPane.showConfirmDialog(guiRegisterProfessor,
                "¿Seguro que desea cancelar? Se perderá la información ingresada.",
                "Cancelar registro", JOptionPane.YES_NO_OPTION);
        if (confirmDialog == JOptionPane.YES_OPTION) guiRegisterProfessor.dispose();
    }
}