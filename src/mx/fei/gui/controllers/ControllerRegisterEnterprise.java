package mx.fei.gui.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import mx.fei.gui.views.GUIManageEnterprise;
import mx.fei.gui.views.GUIRegisterEnterprise;
import mx.fei.logic.dao.EnterpriseDAO;
import mx.fei.logic.dto.Enterprise;
import mx.fei.logic.exceptions.DataOperationException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerRegisterEnterprise {
    private GUIRegisterEnterprise guiRegisterEnterprise;
    private EnterpriseDAO enterpriseDAO;
    private static final Logger logger = Logger.getLogger(ControllerRegisterEnterprise.class.getName());

    public ControllerRegisterEnterprise(GUIRegisterEnterprise guiRegisterEnterprise) {
        this.guiRegisterEnterprise = guiRegisterEnterprise;
        this.enterpriseDAO = new EnterpriseDAO();
    }

    public void handleButtonsRegisterCancel(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Registrar" -> handleRegister();
            case "Cancelar" -> {
                openManageEnterprise();
                guiRegisterEnterprise.closeWindow();
            }
        }
    }

    private void handleRegister() {
        if (guiRegisterEnterprise.validatedFields()) {
            try {
                int directUsers = Integer.parseInt(guiRegisterEnterprise.getTextFieldDirectUsers().getText().trim());
                int indirectUsers = Integer.parseInt(guiRegisterEnterprise.getTextFieldIndirectUsers().getText().trim());
                String city = guiRegisterEnterprise.getComboBoxCity().getValue();
                String country = guiRegisterEnterprise.getComboBoxCountry().getValue();
                Enterprise enterprise = new Enterprise(
                        0,
                        guiRegisterEnterprise.getTextFieldName().getText().trim(),
                        guiRegisterEnterprise.getComboBoxSector().getValue(),
                        guiRegisterEnterprise.getTextFieldPhone().getText().trim(),
                        guiRegisterEnterprise.getTextFieldMail().getText().trim(),
                        city,
                        directUsers,
                        indirectUsers,
                        true,
                        country
                );
                int idGenerated = enterpriseDAO.registerEnterprise(enterprise);
                boolean registered = false;
                if (idGenerated > 0) {
                    registered = true;
                }
                if (registered) {
                    guiRegisterEnterprise.showSuccess("Organización registrada exitosamente.");
                    openManageEnterprise();
                    guiRegisterEnterprise.closeWindow();
                }
            } catch (NumberFormatException e) {
                guiRegisterEnterprise.showError("Los campos de usuarios directos e indirectos deben ser números enteros.");
            } catch (IllegalArgumentException e) {
                guiRegisterEnterprise.showError(e.getMessage());
            } catch (DataOperationException e) {
                logger.log(Level.SEVERE, "Error al registrar la organización", e);
                guiRegisterEnterprise.showError("Error al registrar la organización. Intente más tarde.");
            }
        }
    }
    private void openManageEnterprise() {
        GUIManageEnterprise guiManageEnterprise = new GUIManageEnterprise();
        Stage stage = new Stage();
        guiManageEnterprise.start(stage);
    }
}