package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIChooseEnterprise;
import mx.fei.gui.views.GUIManageEnterprise;
import mx.fei.gui.views.GUIRegisterEnterprise;
import mx.fei.logic.dao.EnterpriseDAO;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ControllerManageEnterprise {
    private GUIManageEnterprise guiManageEnterprise;

    public ControllerManageEnterprise(GUIManageEnterprise guiManageEnterprise) {
        this.guiManageEnterprise = guiManageEnterprise;
    }

    public void handleRegisterModifyReturnButtons(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Registrar organización vinculada" -> {
                guiManageEnterprise.closeWindow();
                openRegisterEnterprise();
            }
            case "Modificar organización vinculada" -> {
                if (!existEnterprises()) {
                    guiManageEnterprise.showError("No existen organizaciones disponibles por el momento.");
                } else {
                    openSelectEnterprise();
                }
            }
            case "Regresar" -> {
                guiManageEnterprise.closeWindow();
            }
        }
    }

    private void openRegisterEnterprise() {
        GUIRegisterEnterprise guiRegisterEnterprise = new GUIRegisterEnterprise();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        guiRegisterEnterprise.start(stage);
    }

    private void openSelectEnterprise() {
        GUIChooseEnterprise guiChooseEnterprise = new GUIChooseEnterprise();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        guiChooseEnterprise.start(stage);
    }

    private boolean existEnterprises() {
        EnterpriseDAO enterpriseDAO = new EnterpriseDAO();
        boolean exist = true;
        try {
            if (enterpriseDAO.getEnterprises().isEmpty()) {
                exist = false;
            }
        } catch (DataOperationException e) {
            guiManageEnterprise.showError("Error al obtener la lista de la lista de las organizaciones");
        }
        return exist;
    }
}