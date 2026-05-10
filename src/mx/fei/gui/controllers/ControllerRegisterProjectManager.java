package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIRegisterProjectManager;
import mx.fei.logic.dao.ProjectManagerDAO;
import mx.fei.logic.dto.ProjectManager;
import mx.fei.logic.exceptions.DataOperationException;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class ControllerRegisterProjectManager{

    private final GUIRegisterProjectManager guiRegisterProjectManager;

    public ControllerRegisterProjectManager(GUIRegisterProjectManager guiRegisterProjectManager) {
        this.guiRegisterProjectManager = guiRegisterProjectManager;
    }

    public void handleButtonAction(ActionEvent event) {
        if (event.getSource() == guiRegisterProjectManager.getButtonRegister()) {
            register();
        } else if (event.getSource() == guiRegisterProjectManager.getButtonCancel()) {
            cancel();
        }
    }

    private void register() {
        if (guiRegisterProjectManager.validateFields()) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmar registro");
            confirmation.setHeaderText(null);
            confirmation.setContentText("¿Seguro que desea guardar estos datos?");
            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    String name = guiRegisterProjectManager.getTextFieldName().getText();
                    String position = guiRegisterProjectManager.getTextFieldPosition().getText();
                    String phoneNumber = guiRegisterProjectManager.getTextFieldPhoneNumber().getText();
                    String email = guiRegisterProjectManager.getTextFieldEmail().getText();
                    ProjectManager projectManager = new ProjectManager(0, name, email, phoneNumber, position);
                    ProjectManagerDAO projectManagerDAO = new ProjectManagerDAO();
                    if (projectManagerDAO.registerProjectManager(projectManager)) {
                        guiRegisterProjectManager.showSuccess("Responsable registrado exitosamente.");
                        guiRegisterProjectManager.getStage().close();
                    }
                } catch (DataOperationException exception) {
                    guiRegisterProjectManager.showError(exception.getMessage());
                }
            }
        }
    }

    private void cancel() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Cancelar registro");
        confirmation.setHeaderText(null);
        confirmation.setContentText("¿Seguro que desea cancelar? Se perderá la información ingresada.");
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            guiRegisterProjectManager.getStage().close();
        }
    }
}