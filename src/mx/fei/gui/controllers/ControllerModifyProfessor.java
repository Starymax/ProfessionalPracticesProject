package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIModifyProfessor;
import mx.fei.logic.dao.ProfessorDAO;
import mx.fei.logic.dao.UserDAO;
import mx.fei.logic.dto.Professor;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerModifyProfessor {
    private GUIModifyProfessor guiModifyProfessor;
    private ProfessorDAO professorDAO;
    private UserDAO userDAO;
    private static final Logger LOGGER = Logger.getLogger(ControllerModifyProfessor.class.getName());

    public ControllerModifyProfessor(GUIModifyProfessor guiModifyProfessor) {
        this.guiModifyProfessor = guiModifyProfessor;
        this.professorDAO = new ProfessorDAO();
        this.userDAO = new UserDAO();
    }

    public void handleUpdateCancelButtons(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Actualizar" -> {
                handleUpdate();
            }
            case "Cancelar" -> {
                guiModifyProfessor.closeWindow();
            }
        }
    }

    private void handleUpdate() {
        if (guiModifyProfessor.validateFields()) {
            Professor original = guiModifyProfessor.getProfessor();
            Professor updated = getProfessor(original);
            try {
                boolean existCoordinator = updated.isCoordinator() && !original.isCoordinator() && professorDAO.existsCoordinator();
                if (!existCoordinator) {
                    userDAO.updateUser(updated);
                    boolean result = professorDAO.modifyProfessor(updated);
                    if (result) {
                        guiModifyProfessor.showSuccess("Profesor actualizado exitosamente.");
                        guiModifyProfessor.closeWindow();
                    }
                } else {
                    guiModifyProfessor.showError("Ya existe un coordinador activo");
                }
            } catch (IllegalArgumentException e) {
                guiModifyProfessor.showError(e.getMessage());
            } catch (DataOperationException e) {
                LOGGER.log(Level.SEVERE, "Error al modificar profesor", e);
                guiModifyProfessor.showError(e.getMessage());
            }
        }
    }

    private Professor getProfessor(Professor original) {
        String name = guiModifyProfessor.getTextFieldName().getText().trim();
        String lastName = guiModifyProfessor.getTextFieldLastName().getText().trim();
        String email = guiModifyProfessor.getTextFieldEmail().getText().trim();
        String gender = guiModifyProfessor.getComboBoxGender().getValue();
        String shift = guiModifyProfessor.getComboBoxShift().getValue();
        boolean isCoordinator = guiModifyProfessor.getCheckBoxIsCoordinator().isSelected();
        boolean isAdministrator = guiModifyProfessor.getCheckBoxIsAdministrator().isSelected();
        boolean active = guiModifyProfessor.getToggleState().isSelected();
        Professor updated = new Professor(
                original.getUserId(),
                name,
                lastName,
                email,
                original.getPassword(),
                gender,
                active,
                original.getPersonalNumber(),
                isCoordinator,
                isAdministrator,
                shift
        );
        return updated;
    }
}