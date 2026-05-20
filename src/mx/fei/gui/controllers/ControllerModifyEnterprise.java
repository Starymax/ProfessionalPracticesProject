package mx.fei.gui.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.fei.gui.views.GUIChooseEnterprise;
import mx.fei.gui.views.GUIModifyEnterprise;
import mx.fei.logic.dao.EnterpriseDAO;
import mx.fei.logic.dto.Enterprise;
import mx.fei.logic.exceptions.DataOperationException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerModifyEnterprise {
    private GUIModifyEnterprise guiModifyEnterprise;
    private EnterpriseDAO enterpriseDAO;
    private static final Logger logger = Logger.getLogger(ControllerModifyEnterprise.class.getName());

    public ControllerModifyEnterprise(GUIModifyEnterprise guiModifyEnterprise) {
        this.guiModifyEnterprise = guiModifyEnterprise;
        this.enterpriseDAO = new EnterpriseDAO();
    }

    public void handleAceptCancel(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Aceptar" -> {
                handleUpdate();
            }
            case "Cancelar" -> {
                openSelectEnterprise();
                guiModifyEnterprise.closeWindow();
            }
        }
    }

    private void handleUpdate() {
        if (guiModifyEnterprise.validateFields()) {
            try {
                long directUsers = Long.parseLong(guiModifyEnterprise.getTextFieldDirectUsers().getText().trim());
                long indirectUsers = Long.parseLong(guiModifyEnterprise.getTextFieldIndirectUsers().getText().trim());
                boolean active = guiModifyEnterprise.getToggleState().isSelected();
                Enterprise updated = new Enterprise(
                        guiModifyEnterprise.getEnterprise().getEnterpriseId(),
                        guiModifyEnterprise.getTextFieldName().getText().trim(),
                        guiModifyEnterprise.getComboBoxSector().getValue(),
                        guiModifyEnterprise.getTextFieldPhone().getText().trim(),
                        guiModifyEnterprise.getTextFieldMail().getText().trim(),
                        guiModifyEnterprise.getComboBoxCity().getValue(),
                        directUsers,
                        indirectUsers,
                        active,
                        guiModifyEnterprise.getComboBoxCountry().getValue()
                );
                boolean result = enterpriseDAO.modifyEnterprise(updated);
                if (result) {
                    guiModifyEnterprise.showSuccess("Organización actualizada exitosamente.");
                }
            } catch (NumberFormatException e) {
                guiModifyEnterprise.showError("Los campos de usuarios directos e indirectos deben ser números enteros.");
            } catch (IllegalArgumentException e) {
                guiModifyEnterprise.showError(e.getMessage());
            } catch (DataOperationException e) {
                logger.log(Level.SEVERE, "Error al modificar la organización", e);
                guiModifyEnterprise.showError("Error al actualizar. Intente más tarde.");
            }
        }
    }

    private void openSelectEnterprise() {
        GUIChooseEnterprise guiChooseEnterprise = new GUIChooseEnterprise();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        guiChooseEnterprise.start(stage);
    }
}