package mx.fei.gui.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import mx.fei.gui.views.GUICoordinator;
import mx.fei.gui.views.GUILogin;
import mx.fei.gui.views.GUIProfessor;
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
                    GUICoordinator guiCoordinator = new GUICoordinator(professor);
                    Stage stage = new Stage();
                    guiCoordinator.start(stage);
                    guiLogin.closeWindow();
                } else if (professor.isAdmin()) {
                    guiLogin.showSuccess("Bienvenido administrador: " + user.getName());
                    // TODO: abrir menu de administrador
                } else {
                    GUIProfessor guiProfessor = new GUIProfessor(professor);
                    Stage stage = new Stage();
                    guiProfessor.start(stage);
                    guiLogin.closeWindow();
                }
            }
            guiLogin.closeWindow();
        } catch (NoSuchElementException e) {
            guiLogin.showError("Correo o contraseña incorrectos.");
        } catch (DataOperationException e) {
            logger.log(Level.SEVERE, "Error en login", e);
            guiLogin.showError("Error interno. Intente mas tarde.");
        }
    }

    private boolean validateFields() {
        boolean validated = true;
        if (guiLogin.getTextFieldMail().getText().trim().isEmpty()) {
            guiLogin.showError("El campo correo es obligatorio.");
            validated = false;
        } else if (guiLogin.getTextFieldPassword().getText().isEmpty()) {
            guiLogin.showError("El campo contraseña es obligatorio.");
            validated = false;
        }
        return validated;
    }
}
