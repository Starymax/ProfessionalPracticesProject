package mx.fei.gui.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import mx.fei.gui.views.GUIManageEnterprise;
import mx.fei.gui.views.GUIRegisterEnterprise;

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
            case "Modificar organización vinculada" -> { /* TODO: abrir GUIModifyEnterprise */ }
            case "Regresar" -> guiManageEnterprise.closeWindow();
        }
    }

    private void openRegisterEnterprise() {
        GUIRegisterEnterprise guiRegisterEnterprise = new GUIRegisterEnterprise();
        Stage stage = new Stage();
        guiRegisterEnterprise.start(stage);
    }
}