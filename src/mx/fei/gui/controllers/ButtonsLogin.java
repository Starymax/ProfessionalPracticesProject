package mx.fei.gui.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import mx.fei.gui.views.GUILogin;
import mx.fei.logic.dao.UserDAO;
import mx.fei.logic.dto.Professor;
import mx.fei.logic.dto.Student;
import mx.fei.logic.dto.User;
import mx.fei.logic.exceptions.DataOperationException;
import org.mindrot.jbcrypt.BCrypt;

import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ButtonsLogin implements EventHandler<ActionEvent> {
    private GUILogin guiLogin;
    private UserDAO userDAO;
    private static final Logger logger = Logger.getLogger(ButtonsLogin.class.getName());

    public ButtonsLogin(GUILogin guiLogin) {
        this.guiLogin = guiLogin;
        this.userDAO = new UserDAO();
    }

    @Override
    public void handle(ActionEvent event) {
        Button source = (Button) event.getSource();
        if (source.getText().equals("Ingresar")) {
            handleLogin();
        } else if (source.getText().equals("Cancelar")) {
            guiLogin.closeWindow();
        }
    }

    private void handleLogin() {
        if (!validateFields()) {
            return;
        }
        String mail = guiLogin.getTextFieldMail().getText().trim();
        String rawPassword = guiLogin.getTextFieldPassword().getText();

        try {
            User user = userDAO.getUserByEmail(mail);

            if (!user.isActive()) {
                guiLogin.showError("El usuario esta inactivo. Contacte al administrador.");
                return;
            }

            if (!BCrypt.checkpw(rawPassword, user.getPassword())) {
                guiLogin.showError("Correo o contraseña incorrectos.");
                return;
            }
            if (user instanceof Student) {
                guiLogin.showSuccess("Bienvenido estudiante: " + user.getName());
                // TODO: abrir menu de estudiante
            } else if (user instanceof Professor professor) {
                if (professor.isCoordinator()) {
                    guiLogin.showSuccess("Bienvenido coordinador: " + user.getName());
                    // TODO: abrir menu de coordinador
                } else if (professor.isAdmin()) {
                    guiLogin.showSuccess("Bienvenido administrador: " + user.getName());
                    // TODO: abrir menu de administrador
                } else {
                    guiLogin.showSuccess("Bienvenido profesor: " + user.getName());
                    // TODO: abrir menu de profesor
                }
            }
            guiLogin.closeWindow();
        } catch (NoSuchElementException e) {
            guiLogin.showError("Correo o contraseña incorrectos.");
        } catch (DataOperationException e) {
            logger.log(Level.SEVERE, "Error en login", e);
            guiLogin.showError("Error interno. Intente más tarde.");
        }
    }

    private boolean validateFields() {
        if (guiLogin.getTextFieldMail().getText().trim().isEmpty()) {
            guiLogin.showError("El campo correo es obligatorio.");
            return false;
        }
        if (guiLogin.getTextFieldPassword().getText().isEmpty()) {
            guiLogin.showError("El campo contraseña es obligatorio.");
            return false;
        }
        return true;
    }
}
