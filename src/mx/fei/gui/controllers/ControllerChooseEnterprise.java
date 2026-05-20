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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerChooseEnterprise {
    private GUIChooseEnterprise guiChooseEnterprise;
    private EnterpriseDAO enterpriseDAO;
    private static final Logger logger = Logger.getLogger(ControllerChooseEnterprise.class.getName());

    public ControllerChooseEnterprise(GUIChooseEnterprise guiChooseEnterprise) {
        this.guiChooseEnterprise = guiChooseEnterprise;
        this.enterpriseDAO = new EnterpriseDAO();
        loadEnterprises();
    }

    private void loadEnterprises() {
        try {
            List<Enterprise> enterprises = enterpriseDAO.getEnterprises();
            if (enterprises.isEmpty()) {
                guiChooseEnterprise.showError("No existen organizaciones registradas.");
            } else {
                guiChooseEnterprise.setEnterprises(enterprises);
            }
        } catch (DataOperationException e) {
            logger.log(Level.SEVERE, "Error al cargar organizaciones", e);
            guiChooseEnterprise.showError("Error al cargar la lista de organizaciones.");
        }
    }

    public void handleSelectReturn(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Seleccionar" -> handleSelect();
            case "Regresar" -> guiChooseEnterprise.closeWindow();
        }
    }

    private void handleSelect() {
        Enterprise selected = guiChooseEnterprise.getSelectedEnterprise();
        if (selected != null) {
            openModifyEnterprise();
            guiChooseEnterprise.closeWindow();
        } else {
            guiChooseEnterprise.showError("Selecciona una organización de la lista.");
        }
    }

    private void openModifyEnterprise() {
        GUIModifyEnterprise  guiModifyEnterprise = new GUIModifyEnterprise(guiChooseEnterprise.getSelectedEnterprise());
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        guiModifyEnterprise.start(stage);
    }
}