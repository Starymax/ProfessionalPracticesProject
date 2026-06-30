package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIProfessorMenu;
import mx.fei.gui.views.GUILogin;
import mx.fei.gui.views.GUICoordinatorMenu;
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
import javafx.stage.Stage;

import java.util.NoSuchElementException;

public class ControllerLogin {
    private final GUILogin guiLogin;
    private final UserDAO userDAO;

    public ControllerLogin(GUILogin guiLogin) {
        this.guiLogin = guiLogin;
        this.userDAO = new UserDAO();
        defaultSession();
    }

    public void handleLogin() {
        if (validateFields()) {
            String mail = guiLogin.getTextFieldMail().getText().trim();
            String rawPassword = guiLogin.getTextFieldPassword().getText();
            defaultSession();
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
                guiLogin.showError(e.getMessage());
            }
        }
    }

    private boolean validateFields() {
        boolean fieldsValidated = true;
        if (guiLogin.getTextFieldMail().getText().trim().isEmpty()) {
            guiLogin.showError("El campo correo es obligatorio.");
            fieldsValidated = false;
        } else if (guiLogin.getTextFieldPassword().getText().isEmpty()) {
            guiLogin.showError("El campo contraseña es obligatorio.");
            fieldsValidated = false;
        }
        return fieldsValidated;
    }

    private void defaultSession() {
        try {
            userDAO.logInByRole(UserRole.DEFAULT);
        }  catch (DataOperationException e) {
            guiLogin.showError(e.getMessage());
        }
    }

    private void studentLogin(User user) {
        StudentDAO studentDAO = new StudentDAO();
        try {
            userDAO.logInByRole(UserRole.STUDENT);
            Student student = studentDAO.getStudentById(user.getUserId());
            GUIStudentMenu guiStudentMenu = new GUIStudentMenu();
            Stage studentMenuStage = new Stage();
            guiStudentMenu.start(studentMenuStage);
            guiStudentMenu.setStudentInfo(student);
            guiLogin.closeWindow();
        } catch (DataOperationException e) {
            guiLogin.showError(e.getMessage());
        }
    }

    private void adminLogin(Professor professor) {
        try {
            userDAO.logInByRole(UserRole.ADMINISTRATOR);
            GUIAdministratorMenu guiAdministratorMenu = new GUIAdministratorMenu();
            Stage administratorMenuStage = new Stage();
            guiAdministratorMenu.start(administratorMenuStage);
            guiAdministratorMenu.setAdministratorInfo(professor);
            guiLogin.closeWindow();
        } catch (DataOperationException e) {
            guiLogin.showError(e.getMessage());
        }
    }

    private void coordinatorLogin(Professor coordinator) {
        try {
            userDAO.logInByRole(UserRole.COORDINATOR);
            GUICoordinatorMenu guiCoordinatorMenu = new GUICoordinatorMenu();
            Stage stage = new Stage();
            guiCoordinatorMenu.start(stage);
            guiCoordinatorMenu.setCoordinatorInfo(coordinator);
            guiLogin.closeWindow();
        } catch (DataOperationException e) {
            guiLogin.showError(e.getMessage());
        }
    }

    private void professorLogin(Professor professor) {
        try {
            userDAO.logInByRole(UserRole.PROFESSOR);
            GUIProfessorMenu guiProfessorMenu = new GUIProfessorMenu();
            Stage stage = new Stage();
            guiProfessorMenu.start(stage);
            guiProfessorMenu.setProfessorInfo(professor);
            guiLogin.closeWindow();
        } catch (DataOperationException e) {
            guiLogin.showError(e.getMessage());
        }
    }
}