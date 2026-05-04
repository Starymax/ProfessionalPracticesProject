package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIRegisterEnterprise;
import mx.fei.logic.dao.EnterpriseDAO;
import mx.fei.logic.dto.Enterprise;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class ControllerRegisterEnterprise {

    private final GUIRegisterEnterprise guiRegisterEnterprise;

    public ControllerRegisterEnterprise(GUIRegisterEnterprise guiRegisterEnterprise) {
        this.guiRegisterEnterprise = guiRegisterEnterprise;
    }

    public void handleButtonAction(ActionEvent event) {
        if (event.getSource() == guiRegisterEnterprise.getRegisterButton()) {
            if (guiRegisterEnterprise.validateFields() && guiRegisterEnterprise.validateFieldsInt()) {
                register();
            }
        } else if (event.getSource() == guiRegisterEnterprise.getCancelButton()) {
            cancel();
        }
    }

    private void register() {
        EnterpriseDAO enterpriseDAO = new EnterpriseDAO();
        try {
            Enterprise enterprise = getEnterprise();
            if (enterpriseDAO.registerEnterprise(enterprise) > 0) {
                guiRegisterEnterprise.showSuccess("Empresa registrada correctamente.");
            }
        } catch (DataOperationException e) {
            guiRegisterEnterprise.showError("Error al insertar la empresa.");
        }
    }

    private Enterprise getEnterprise() {
        String name = guiRegisterEnterprise.getNameTextField().getText();
        String address = guiRegisterEnterprise.getAddressTextField().getText();
        String phoneNumber = guiRegisterEnterprise.getPhoneNumberTextField().getText().trim();
        String email = guiRegisterEnterprise.getEmailTextField().getText().trim();
        String sector = guiRegisterEnterprise.getSectorTextField().getText();
        int directUsers = Integer.parseInt(guiRegisterEnterprise.getDirectUsersTextField().getText().trim());
        int indirectUsers = Integer.parseInt(guiRegisterEnterprise.getIndirectUsersTextField().getText().trim());
        return new Enterprise(0, name, sector, phoneNumber, email, address, directUsers, indirectUsers, true);
    }

    private void cancel() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancelar registro");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Seguro que desea cancelar? Se perderá la información ingresada.");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            guiRegisterEnterprise.getStage().close();
        }
    }
}