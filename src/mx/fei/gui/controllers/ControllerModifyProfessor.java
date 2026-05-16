package mx.fei.gui.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import mx.fei.gui.views.GUIModifyProfessor;
import mx.fei.logic.dao.ProfessorDAO;
import mx.fei.logic.dao.UserDAO;
import mx.fei.logic.dto.Professor;
import mx.fei.logic.exceptions.DataOperationException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerModifyProfessor {
    private GUIModifyProfessor guiModifyProfessor;
    private ProfessorDAO professorDAO;
    private UserDAO userDAO;
    private static final Logger logger = Logger.getLogger(ControllerModifyProfessor.class.getName());

    public ControllerModifyProfessor(GUIModifyProfessor guiModifyProfessor) {
        this.guiModifyProfessor = guiModifyProfessor;
        this.professorDAO = new ProfessorDAO();
        this.userDAO = new UserDAO();
    }

    public void handleUpdateCancelButtons(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Actualizar" -> handleUpdate();
            case "Cancelar" -> guiModifyProfessor.closeWindow();
        }
    }

    private void handleUpdate() {
        if (guiModifyProfessor.validateFields()) {
            Professor original = guiModifyProfessor.getProfessor();
            Professor updated = getProfessor(original);
            try {
                userDAO.updateUser(updated);
                boolean result = professorDAO.modifyProfessor(updated);
                if (result) {
                    guiModifyProfessor.showSuccess("Profesor actualizado exitosamente.");
                    guiModifyProfessor.closeWindow();
                }
            } catch (IllegalArgumentException e) {
                guiModifyProfessor.showError(e.getMessage());
            } catch (DataOperationException e) {
                logger.log(Level.SEVERE, "Error al modificar profesor", e);
                guiModifyProfessor.showError("Error al actualizar. Intente más tarde.");
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