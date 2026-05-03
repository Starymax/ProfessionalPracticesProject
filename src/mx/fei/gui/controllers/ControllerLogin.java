package mx.fei.gui.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import mx.fei.gui.views.GUIProfessor;
import mx.fei.gui.views.GUILogin;
import mx.fei.gui.views.GUICoordinator;
import mx.fei.gui.views.GUIStudentMenu;
import mx.fei.gui.views.GUIAdministratorMenu;
import mx.fei.logic.dao.StudentDAO;
import mx.fei.logic.dao.UserDAO;
import mx.fei.logic.dto.Professor;
import mx.fei.logic.dto.Student;
import mx.fei.logic.dto.User;
import mx.fei.logic.dto.UserRole;
import mx.fei.logic.exceptions.DataOperationException;
import org.mindrot.jbcrypt.BCrypt;

import java.util.NoSuchElementException;
import java.util.logging.Logger;

public class ControllerLogin {
    private final GUILogin guiLogin;
    private final UserDAO userDAO;
    private static final Logger logger = Logger.getLogger(ControllerLogin.class.getName());

    public ControllerLogin(GUILogin guiLogin) {
        this.guiLogin = guiLogin;
        this.userDAO = new UserDAO();
        defaultSession();
    }

    public void handleButtons(ActionEvent event) {
        Button source = (Button) event.getSource();
        if (source.getText().equals("Ingresar")) {
            handleLogin();
        } else if (source.getText().equals("Cancelar")) {
            guiLogin.closeWindow();
        }
    }

    private void handleLogin() {
        if (validateFields()) {
            String mail = guiLogin.getTextFieldMail().getText().trim();
            String rawPassword = guiLogin.getTextFieldPassword().getText();
            try {
                User user = userDAO.getUserByEmail(mail);
                if (user.isActive() && BCrypt.checkpw(rawPassword, user.getPassword())) {
                    if (user instanceof Student) {
                        studentLogin(user);
                    } else if (user instanceof Professor professor) {
                        if (professor.isCoordinator()) {
                            coordinatorLogin(professor);
                        } else if (professor.isAdmin()) {
                            adminLogin(professor);
                        } else {
                            professorLogin(professor);
                        }
                    }
                } else {
                    guiLogin.showError("Correo o contraseña incorrectos.");
                }
            } catch (NoSuchElementException e) {
                guiLogin.showError("Correo o contraseña incorrectos.");
            } catch (DataOperationException e) {
                guiLogin.showError("Error interno. Intente más tarde.");
            }
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

    private void defaultSession() {
        try {
            userDAO.logInByRole(UserRole.DEFAULT);
        }  catch (DataOperationException e) {
            guiLogin.showError("Error interno. Intente más tarde.");
        }
    }

    private void studentLogin(User user) {
        StudentDAO studentDAO = new StudentDAO();
        try {
            Student student = studentDAO.getStudentById(user.getUserId());
            GUIStudentMenu guiStudentMenu = new GUIStudentMenu();
            Stage studentMenuStage = new Stage();
            guiStudentMenu.start(studentMenuStage);
            guiStudentMenu.setStudentInfo(student);
            userDAO.logInByRole(UserRole.STUDENT);
            guiLogin.closeWindow();
        } catch (DataOperationException | NoSuchElementException e) {
            guiLogin.showError("Error interno. Intente más tarde.");
            guiLogin.closeWindow();
        }
    }

    private void adminLogin(Professor professor) {
        GUIAdministratorMenu guiAdministratorMenu = new GUIAdministratorMenu();
        Stage administratorMenuStage = new Stage();
        guiAdministratorMenu.start(administratorMenuStage);
        guiAdministratorMenu.setAdministratorName(professor.getName());
        try {
            userDAO.logInByRole(UserRole.ADMINISTRATOR);
        } catch (DataOperationException e) {
            guiLogin.showError("Error interno. Intente más tarde.");
        }
        guiLogin.closeWindow();
    }

    private void coordinatorLogin(Professor Coordinator) {
        GUICoordinator guiCoordinator = new GUICoordinator();
        Stage stage = new Stage();
        guiCoordinator.start(stage);
        try {
            userDAO.logInByRole(UserRole.COORDINATOR);
        } catch (DataOperationException e) {
            guiLogin.showError("Error interno. Intente más tarde.");
        }
        guiLogin.closeWindow();
    }

    private void professorLogin(Professor professor) {
        GUIProfessor guiProfessor = new GUIProfessor();
        Stage stage = new Stage();
        guiProfessor.start(stage);
        guiLogin.closeWindow();
        try {
            userDAO.logInByRole(UserRole.PROFESSOR);
        } catch (DataOperationException e) {
            guiLogin.showError("Error interno. Intente más tarde.");
        }
    }
}