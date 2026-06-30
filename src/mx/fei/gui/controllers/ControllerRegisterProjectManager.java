package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIRegisterProjectManager;
import mx.fei.logic.dao.ProjectManagerDAO;
import mx.fei.logic.dto.ProjectManager;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class ControllerRegisterProjectManager{

    private final GUIRegisterProjectManager guiRegisterProjectManager;

    public ControllerRegisterProjectManager(GUIRegisterProjectManager guiRegisterProjectManager) {
        this.guiRegisterProjectManager = guiRegisterProjectManager;
    }

    public void register() {
        if (guiRegisterProjectManager.validateFields()) {
            Alert confirmation = new Alert(AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmar registro");
            confirmation.setHeaderText(null);
            confirmation.setContentText("¿Seguro que desea guardar estos datos?");
            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    int projectMangerIdDefault = 0;
                    String name = guiRegisterProjectManager.getTextFieldName().getText();
                    String position = guiRegisterProjectManager.getTextFieldPosition().getText();
                    String phoneNumber = guiRegisterProjectManager.getTextFieldPhoneNumber().getText();
                    String email = guiRegisterProjectManager.getTextFieldEmail().getText();
                    int enterpriseId = guiRegisterProjectManager.getEnterprise().getEnterpriseId();
                    ProjectManager projectManager = new ProjectManager(projectMangerIdDefault, name, email, phoneNumber, position, enterpriseId);
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

    public void cancel() {
        Alert confirmation = new Alert(AlertType.CONFIRMATION);
        confirmation.setTitle("Cancelar registro");
        confirmation.setHeaderText(null);
        confirmation.setContentText("¿Seguro que desea cancelar? Se perderá la información ingresada.");
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            guiRegisterProjectManager.getStage().close();
        }
    }
}