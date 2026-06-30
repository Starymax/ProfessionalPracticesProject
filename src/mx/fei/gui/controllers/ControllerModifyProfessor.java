package mx.fei.gui.controllers;

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
    private static final Logger LOGGER = Logger.getLogger(ControllerModifyProfessor.class.getName());

    public ControllerModifyProfessor(GUIModifyProfessor guiModifyProfessor) {
        this.guiModifyProfessor = guiModifyProfessor;
        this.professorDAO = new ProfessorDAO();
        this.userDAO = new UserDAO();
    }

    public void handleUpdate() {
        if (guiModifyProfessor.validateFields()) {
            Professor professorOriginal = guiModifyProfessor.getProfessor();
            Professor professorUpdated = builProfessor(professorOriginal);
            try {
                boolean existCoordinator = professorUpdated.isCoordinator() && !professorOriginal.isCoordinator() && professorDAO.existsCoordinator();
                if (!existCoordinator) {
                    userDAO.updateUser(professorUpdated);
                    boolean updated = professorDAO.modifyProfessor(professorUpdated);
                    if (updated) {
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

    private Professor builProfessor(Professor original) {
        int originalId = original.getUserId();
        String name = guiModifyProfessor.getTextFieldName().getText().trim();
        String lastName = guiModifyProfessor.getTextFieldLastName().getText().trim();
        String email = guiModifyProfessor.getTextFieldEmail().getText().trim();
        String originalPassword = original.getPassword();
        String gender = guiModifyProfessor.getComboBoxGender().getValue();
        String shift = guiModifyProfessor.getComboBoxShift().getValue();
        boolean isCoordinator = guiModifyProfessor.getCheckBoxIsCoordinator().isSelected();
        boolean isAdministrator = guiModifyProfessor.getCheckBoxIsAdministrator().isSelected();
        boolean activeStatus = guiModifyProfessor.getToggleState().isSelected();
        int originalPersonalNumber = original.getPersonalNumber();
        Professor professorUpdated = new Professor(
                originalId,
                name,
                lastName,
                email,
                originalPassword,
                gender,
                activeStatus,
                originalPersonalNumber,
                isCoordinator,
                isAdministrator,
                shift
        );
        return professorUpdated;
    }
}