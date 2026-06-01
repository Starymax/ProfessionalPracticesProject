package mx.fei.gui.controllers;

import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.fei.gui.views.GUIStudentMenu;
import mx.fei.gui.views.GUISelectProjects;
import mx.fei.gui.views.GUIGenerateReport;
import mx.fei.gui.views.GUIRegisterAdvance;
import mx.fei.gui.views.GUIUploadDocuments;
import mx.fei.gui.views.GUILogin;
import mx.fei.gui.views.GUIGenerateDocuments;
import mx.fei.gui.views.GUINotifications;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import mx.fei.logic.dao.NotificationDAO;
import mx.fei.logic.dao.PracticeDAO;
import mx.fei.logic.dao.ProjectDAO;
import mx.fei.logic.dao.StudentDAO;
import mx.fei.logic.dto.Practice;
import mx.fei.logic.dto.Project;
import mx.fei.logic.exceptions.DataOperationException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerStudentMenu {

    private final GUIStudentMenu guiStudentMenu;
    private final ProjectDAO projectDAO;
    private final PracticeDAO practiceDAO;
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private static final Logger logger = Logger.getLogger(ControllerStudentMenu.class.getName());

    public ControllerStudentMenu(GUIStudentMenu guiStudentMenu) {
        this.guiStudentMenu = guiStudentMenu;
        projectDAO = new ProjectDAO();
        practiceDAO = new PracticeDAO();
    }

    public void loadUnreadCount() {
        try {
            if (guiStudentMenu.getStudent() == null) {
                guiStudentMenu.updateUnreadCount(0);
                return;
            }
            int unread = notificationDAO.countUnreadNotifications(guiStudentMenu.getStudent().getUserId());
            guiStudentMenu.updateUnreadCount(unread);
        } catch (DataOperationException e) {
            logger.log(Level.WARNING, "Error al cargar notificaciones no leídas", e);
            guiStudentMenu.updateUnreadCount(0);
        } catch (RuntimeException e) {
            logger.log(Level.WARNING, "Error inesperado al cargar notificaciones no leídas", e);
            guiStudentMenu.updateUnreadCount(0);
        }
    }

    public void handleButtonsMenu(ActionEvent event) {
        if (event.getSource() == guiStudentMenu.getButtonSelectProjects()) {
            openSelectProjects();
        } else if (event.getSource() == guiStudentMenu.getButtonReports()) {
            openReports();
        } else if (event.getSource() == guiStudentMenu.getButtonRegisterAdvance()) {
            openRegisterAdvance();
        } else if (event.getSource() == guiStudentMenu.getButtonDocuments()) {
            openDocuments(guiStudentMenu.getStudent().getEnrollment());
        } else if (event.getSource() == guiStudentMenu.getButtonLogout()) {
            logout();
        } else if (event.getSource() == guiStudentMenu.getButtonGenerateDocuments()) {
            openGenerateDocuments();
        } else if (event.getSource() == guiStudentMenu.getButtonNotifications()) {
            openNotifications();
        }
    }

    private void openSelectProjects() {
        StudentDAO studentDAO = new StudentDAO();
        try {
            if (studentDAO.getSelectedProjects(guiStudentMenu.getStudent()).isEmpty()) {
                List<Project> projectList = projectDAO.getAvailableProjects();
                GUISelectProjects guiSelectProjects = new GUISelectProjects();
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                guiSelectProjects.start(stage);
                guiSelectProjects.setStudent(guiStudentMenu.getStudent());
                guiSelectProjects.loadProjects(projectList);
            } else {
                guiStudentMenu.showError("Proyectos ya seleccionados.");
            }
        } catch (DataOperationException e) {
            guiStudentMenu.showError(e.getMessage());
        }
    }

    private void openReports() {
        try {
            PracticeDAO practiceDAO = new PracticeDAO();
            Practice practice = practiceDAO.getPracticeByEnrollment(guiStudentMenu.getStudent().getEnrollment());
            GUIGenerateReport guiGenerateReport = new GUIGenerateReport(practice);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            guiGenerateReport.start(stage);
        } catch (DataOperationException e) {
            guiStudentMenu.showError(e.getMessage());
        }
    }

    private void openRegisterAdvance() {
        if (guiStudentMenu.getStudent() == null) {
            guiStudentMenu.showError("No hay estudiante asignado.");
        } else {
            try {
                GUIRegisterAdvance guiRegisterAdvance = new GUIRegisterAdvance(guiStudentMenu.getStudent());
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                guiRegisterAdvance.start(stage);
            } catch (IllegalStateException e) {
                guiStudentMenu.showError("No se pudo abrir el registro de avances: " + e.getMessage());
            }
        }
    }

    private void openNotifications() {
        try {
            GUINotifications guiNotifications = new GUINotifications();
            Stage newStage = new Stage();
            guiNotifications.start(newStage);
            guiNotifications.setStudent(guiStudentMenu.getStudent());
            ControllerNotifications controller = new ControllerNotifications(guiNotifications);
            controller.loadNotifications();
            newStage.setOnHidden(e -> loadUnreadCount());
        } catch (RuntimeException e) {
            logger.log(Level.SEVERE, "Error al abrir notificaciones", e);
        }
    }

    private void openDocuments(String enrollment) {
        GUIUploadDocuments guiUploadDocuments = new GUIUploadDocuments(enrollment);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        guiUploadDocuments.start(stage);
    }

    private void logout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cerrar Sesión");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Seguro que desea cerrar sesión?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            GUILogin guiLogin = new GUILogin();
            Stage loginStage = new Stage();
            guiLogin.start(loginStage);
            guiStudentMenu.getStage().close();
        }
    }

    private void openGenerateDocuments() {
        try {
            Practice practice = practiceDAO.getPracticeByEnrollment(guiStudentMenu.getStudent().getEnrollment());
            if (practice == null) {
                guiStudentMenu.showError("No tiene ninguna practica asignada, intentelo mas tarde.");
            } else {
                GUIGenerateDocuments guiGenerateDocuments = new GUIGenerateDocuments(practice);
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                guiGenerateDocuments.start(stage);
            }
        } catch (DataOperationException e) {
            guiStudentMenu.showError("Error al obtener la practica del estudia.");
        }
    }
}