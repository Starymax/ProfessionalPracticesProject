package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIRegisterEnterprise;
import mx.fei.logic.dao.EnterpriseDAO;
import mx.fei.logic.dto.Enterprise;
import mx.fei.logic.exceptions.DataOperationException;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonsRegisterEnterprise implements ActionListener {

    private final GUIRegisterEnterprise guiRegisterEnterprise;

    public ButtonsRegisterEnterprise(GUIRegisterEnterprise guiRegisterEnterprise) {
        this.guiRegisterEnterprise = guiRegisterEnterprise;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == guiRegisterEnterprise.getRegisterButton()) {
            if (guiRegisterEnterprise.validateFields()) {
                register();
            }
        } else if (e.getSource() == guiRegisterEnterprise.getCancelButton()) {
            cancel();
        }
    }

    private void register() {
        EnterpriseDAO enterpriseDAO = new EnterpriseDAO();
        try {
            String name = guiRegisterEnterprise.getNameTextField().getText();
            String address = guiRegisterEnterprise.getAddressTextField().getText();
            long phoneNumberInt = Long.parseLong(guiRegisterEnterprise.getPhoneNumberTextField().getText());
            String phoneNumber = phoneNumberInt + "";
            String email = guiRegisterEnterprise.getEmailTextField().getText();
            String sector = guiRegisterEnterprise.getSectorTextField().getText();
            int directUsers = Integer.parseInt(guiRegisterEnterprise.getDirectUsersTextField().getText());
            int indirectUsers = Integer.parseInt(guiRegisterEnterprise.getIndirectUsersTextField().getText());
            Enterprise enterprise = new Enterprise(0, name, sector, phoneNumber, email, address, directUsers, indirectUsers, true);
            if (enterpriseDAO.registerEnterprise(enterprise) > 0) {
                JOptionPane.showMessageDialog(guiRegisterEnterprise, "Empresa registrada correctamente", "Continuar", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(guiRegisterEnterprise, "Los campos de usuarios y telefono deben contener números", "Aceptar",  JOptionPane.WARNING_MESSAGE);
        } catch (DataOperationException e) {
            JOptionPane.showMessageDialog(guiRegisterEnterprise, "Error al insertar la empresa", "Continuar",  JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cancel() {
        int confirmDialog = JOptionPane.showConfirmDialog(guiRegisterEnterprise, "¿Seguro que desea cancelar? Se perderá la información ingresada.", "Cancelar registro", JOptionPane.YES_NO_OPTION);
        if (confirmDialog == JOptionPane.YES_OPTION) {
            guiRegisterEnterprise.dispose();
        }
    }
}