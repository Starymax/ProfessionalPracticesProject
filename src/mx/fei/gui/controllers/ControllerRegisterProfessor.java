package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIRegisterProfessor;
import mx.fei.logic.dao.ProfessorDAO;
import mx.fei.logic.dto.Professor;
import mx.fei.logic.exceptions.DataOperationException;
import org.mindrot.jbcrypt.BCrypt;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class ControllerRegisterProfessor {

    private final GUIRegisterProfessor guiRegisterProfessor;

    public ControllerRegisterProfessor(GUIRegisterProfessor guiRegisterProfessor) {
        this.guiRegisterProfessor = guiRegisterProfessor;
    }

    public void handleRegisterCancelButtons(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Registrar" -> {
                if (guiRegisterProfessor.validateFields()) {
                    register();
                }
            }
            case "Cancelar" -> cancel();
        }
    }

    private void register() {
        ProfessorDAO professorDAO = new ProfessorDAO();
        try {
            Professor professor = getProfessor();
            if (professor.isCoordinator()) {
                if (professorDAO.existsCoordinator()) {
                    guiRegisterProfessor.showError("Ya existe un Coordinador registrado");
                } else if (professorDAO.registerProfessor(professor)) {
                    guiRegisterProfessor.showSuccess("Coordinador registrado exitosamente.");
                    guiRegisterProfessor.getStage().close();
                }
            } else if (professorDAO.registerProfessor(professor)) {
                if (professor.isAdmin()) {
                    guiRegisterProfessor.showSuccess("Administrador registrado exitosamente.");
                    guiRegisterProfessor.getStage().close();
                } else {
                    guiRegisterProfessor.showSuccess("Profesor registrado exitosamente.");
                    guiRegisterProfessor.getStage().close();
                }
            }
        } catch (IllegalArgumentException | IllegalStateException | DataOperationException e) {
            guiRegisterProfessor.showError("Error al registrar el profesor");
        }
    }

    private Professor getProfessor() {
        String name = guiRegisterProfessor.getTextFieldName().getText();
        String lastName = guiRegisterProfessor.getTextFieldLastName().getText();
        int personalNumber = Integer.parseInt(guiRegisterProfessor.getTextFieldPersonalNumber().getText().trim());
        String gender = guiRegisterProfessor.getComboBoxGender().getValue();
        String email = guiRegisterProfessor.getTextFieldEmail().getText();
        String password = guiRegisterProfessor.getTextFieldPassword().getText();
        String hashPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String shift = guiRegisterProfessor.getComboBoxShift().getValue();
        boolean isCoordinator = guiRegisterProfessor.getCheckBoxIsCoordinator().isSelected();
        boolean isAdministrator = guiRegisterProfessor.getCheckBoxIsAdministrator().isSelected();
        return new Professor(0, name, lastName, email, hashPassword, gender, true, personalNumber, isCoordinator, isAdministrator, shift);
    }

    private void cancel() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancelar registro");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Seguro que desea cancelar? Se perderá la información ingresada.");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            guiRegisterProfessor.getStage().close();
        }
    }
}