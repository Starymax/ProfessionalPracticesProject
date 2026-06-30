package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIChooseEnterprise;
import mx.fei.gui.views.GUIModifyEnterprise;
import mx.fei.logic.dao.EnterpriseDAO;
import mx.fei.logic.dto.Enterprise;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerModifyEnterprise {
    private GUIModifyEnterprise guiModifyEnterprise;
    private EnterpriseDAO enterpriseDAO;
    private static final Logger LOGGER = Logger.getLogger(ControllerModifyEnterprise.class.getName());

    public ControllerModifyEnterprise(GUIModifyEnterprise guiModifyEnterprise) {
        this.guiModifyEnterprise = guiModifyEnterprise;
        this.enterpriseDAO = new EnterpriseDAO();
    }

    public void handleUpdate() {
        if (guiModifyEnterprise.validateFields()) {
            try {
                Enterprise enterpriseUpdated = buildEnterprise();
                boolean updated = enterpriseDAO.modifyEnterprise(enterpriseUpdated);
                if (updated) {
                    guiModifyEnterprise.showSuccess("Organización actualizada exitosamente.");
                }
            } catch (NumberFormatException e) {
                guiModifyEnterprise.showError("Los campos de usuarios directos e indirectos deben ser números enteros.");
            } catch (IllegalArgumentException e) {
                guiModifyEnterprise.showError(e.getMessage());
            } catch (DataOperationException e) {
                LOGGER.log(Level.SEVERE, "Error al modificar la organización", e);
                guiModifyEnterprise.showError(e.getMessage());
            }
        }
    }

    private Enterprise buildEnterprise() {
        int enterpriseId = guiModifyEnterprise.getEnterprise().getEnterpriseId();
        String name = guiModifyEnterprise.getTextFieldName().getText().trim();
        String sector = guiModifyEnterprise.getComboBoxSector().getValue();
        String phone = guiModifyEnterprise.getTextFieldPhone().getText().trim();
        String mail = guiModifyEnterprise.getTextFieldMail().getText().trim();
        String city = guiModifyEnterprise.getComboBoxCity().getValue();
        String country = guiModifyEnterprise.getComboBoxCountry().getValue();
        long directUsers = Long.parseLong(guiModifyEnterprise.getTextFieldDirectUsers().getText().trim());
        long indirectUsers = Long.parseLong(guiModifyEnterprise.getTextFieldIndirectUsers().getText().trim());
        boolean active = guiModifyEnterprise.getToggleState().isSelected();
        return new Enterprise(
                enterpriseId,
                name,
                sector,
                phone,
                mail,
                city,
                directUsers,
                indirectUsers,
                active,
                country
        );
    }

    private void openSelectEnterprise() {
        GUIChooseEnterprise guiChooseEnterprise = new GUIChooseEnterprise();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        guiChooseEnterprise.start(stage);
    }

    public void handleCancelButtonAction() {
        openSelectEnterprise();
        guiModifyEnterprise.closeWindow();
    }

}