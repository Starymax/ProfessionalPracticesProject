package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIManageEnterprise;
import mx.fei.gui.views.GUIRegisterEnterprise;
import mx.fei.logic.dao.EnterpriseDAO;
import mx.fei.logic.dto.Enterprise;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerRegisterEnterprise {
    private GUIRegisterEnterprise guiRegisterEnterprise;
    private EnterpriseDAO enterpriseDAO;
    private static final Logger LOGGER = Logger.getLogger(ControllerRegisterEnterprise.class.getName());

    public ControllerRegisterEnterprise(GUIRegisterEnterprise guiRegisterEnterprise) {
        this.guiRegisterEnterprise = guiRegisterEnterprise;
        this.enterpriseDAO = new EnterpriseDAO();
    }

    public void handleRegisterCancelButtons(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Registrar" -> {
                handleRegister();
            }
            case "Cancelar" -> {
                openManageEnterprise();
                guiRegisterEnterprise.closeWindow();
            }
        }
    }

    private void handleRegister() {
        if (guiRegisterEnterprise.validatedFields()) {
            try {
                Enterprise enterprise = getEnterprise();
                int idGenerated = enterpriseDAO.registerEnterprise(enterprise);
                boolean enterpriseRegistered = false;
                if (idGenerated > 0) {
                    enterpriseRegistered = true;
                }
                if (enterpriseRegistered) {
                    guiRegisterEnterprise.showSuccess("Organización registrada exitosamente.");
                    openManageEnterprise();
                    guiRegisterEnterprise.closeWindow();
                }
            } catch (IllegalArgumentException e) {
                guiRegisterEnterprise.showError(e.getMessage());
            } catch (DataOperationException e) {
                LOGGER.log(Level.SEVERE, "Error al registrar la organización", e);
                guiRegisterEnterprise.showError(e.getMessage());
            }
        }
    }

    private Enterprise getEnterprise() {
        int idDefault = 0;
        String name = guiRegisterEnterprise.getTextFieldName().getText().trim();
        String sector = guiRegisterEnterprise.getComboBoxSector().getValue();
        String phone = guiRegisterEnterprise.getTextFieldPhone().getText().trim();
        String mail = guiRegisterEnterprise.getTextFieldMail().getText().trim();
        long directUsers = Long.parseLong(guiRegisterEnterprise.getTextFieldDirectUsers().getText().trim());
        long indirectUsers = Long.parseLong(guiRegisterEnterprise.getTextFieldIndirectUsers().getText().trim());
        String city = guiRegisterEnterprise.getComboBoxCity().getValue();
        String country = guiRegisterEnterprise.getComboBoxCountry().getValue();
        boolean activeStatusDefault = true;
        Enterprise enterprise = new Enterprise(
                idDefault,
                name,
                sector,
                phone,
                mail,
                city,
                directUsers,
                indirectUsers,
                activeStatusDefault,
                country
        );
        return enterprise;
    }

    private void openManageEnterprise() {
        GUIManageEnterprise guiManageEnterprise = new GUIManageEnterprise();
        Stage stage = new Stage();
        guiManageEnterprise.start(stage);
    }
}