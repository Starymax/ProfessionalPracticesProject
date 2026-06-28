package mx.fei.gui.controllers;

import mx.fei.logic.dao.NotificationDAO;
import mx.fei.logic.dao.PracticeDAO;
import mx.fei.logic.dao.ProjectDAO;
import mx.fei.logic.dao.StudentDAO;
import mx.fei.logic.dao.UserDAO;
import mx.fei.logic.dto.Notification;
import mx.fei.logic.dto.Practice;
import mx.fei.logic.dto.Project;
import mx.fei.logic.exceptions.DataOperationException;
import mx.fei.gui.views.GUIStudentMenu;
import mx.fei.gui.views.GUISelectProjects;
import mx.fei.gui.views.GUIGenerateReport;
import mx.fei.gui.views.GUIRegisterAdvance;
import mx.fei.gui.views.GUIUploadDocuments;
import mx.fei.gui.views.GUILogin;
import mx.fei.gui.views.GUIGenerateDocuments;
import mx.fei.gui.views.GUINotifications;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerStudentMenu {

    private final GUIStudentMenu guiStudentMenu;
    private final ProjectDAO projectDAO;
    private final PracticeDAO practiceDAO;
    private final NotificationDAO notificationDAO;
    private final int MINIMUM_PROJECTS = 3;
    private static final Logger LOGGER = Logger.getLogger(ControllerStudentMenu.class.getName());

    public ControllerStudentMenu(GUIStudentMenu guiStudentMenu) {
        this.guiStudentMenu = guiStudentMenu;
        projectDAO = new ProjectDAO();
        practiceDAO = new PracticeDAO();
        notificationDAO = new NotificationDAO();
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
            LOGGER.log(Level.WARNING, "Error al cargar notificaciones no leídas:", e.getMessage());
            guiStudentMenu.updateUnreadCount(0);
        }
    }

    public void handleButtonsMenu(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Seleccionar Proyectos" -> {
                openSelectProjects();
            }
            case "Generar Documentos" -> {
                if (!studentHadAssignedProject()) {
                    guiStudentMenu.showError("No tiene proyecto asignado.");
                } else {
                    openGenerateDocuments();
                }
            }
            case "Generar Reportes" -> {
                if (!studentHadAssignedProject()) {
                    guiStudentMenu.showError("No tiene proyecto asignado.");
                } else {
                    openReports();
                }
            }
            case "Registro de Avances" -> {
                if (!studentHadAssignedProject()) {
                    guiStudentMenu.showError("No tiene proyecto asignado.");
                } else {
                    openRegisterAdvance();
                }
            }
            case "Subir Documentos" -> {
                openDocuments(guiStudentMenu.getStudent().getEnrollment());
            }
            case "🔔 Notificaciones" -> {
                openNotifications();
            }
            case "Cerrar Sesión" -> {
                logout();
            }
        }
    }

    private void openSelectProjects() {
        StudentDAO studentDAO = new StudentDAO();
        try {
            if (studentDAO.getSelectedProjects(guiStudentMenu.getStudent()).isEmpty()) {
                List<Project> projectList = projectDAO.getAvailableProjects();
                if (projectList.size() < MINIMUM_PROJECTS) {
                    guiStudentMenu.showError("No hay suficientes proyectos disponibles por el momento. Intente más tarde.");
                } else {
                    GUISelectProjects guiSelectProjects = new GUISelectProjects();
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    guiSelectProjects.start(stage);
                    guiSelectProjects.setStudent(guiStudentMenu.getStudent());
                    guiSelectProjects.loadProjects(projectList);
                }
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
            if (practice == null) {
                guiStudentMenu.showError("No tiene ninguna practica asignada, intentelo mas tarde.");
            } else {
                GUIGenerateReport guiGenerateReport = new GUIGenerateReport(practice);
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                guiGenerateReport.start(stage);
            }
        } catch (DataOperationException e) {
            LOGGER.log(Level.SEVERE,"Error al obtener el proyecto del estudiante", e.getMessage());
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
        NotificationDAO notificationDAO = new NotificationDAO();
        try {
            List<Notification> notifications = notificationDAO.getNotificationsByStudentId(guiStudentMenu.getStudent().getUserId());
            if (notifications.isEmpty()) {
                guiStudentMenu.showError("No tiene notificaciones por el momento.");
            } else {
                GUINotifications guiNotifications = new GUINotifications();
                ControllerNotifications controllerNotifications = new ControllerNotifications(guiNotifications);
                Stage newStage = new Stage();
                guiNotifications.start(newStage);
                guiNotifications.setStudent(guiStudentMenu.getStudent());
                controllerNotifications.loadNotifications();
                newStage.setOnHidden(e -> loadUnreadCount());
                }
            } catch (DataOperationException e) {
            LOGGER.log(Level.SEVERE, "Error al abrir notificaciones: " + e.getMessage());
            guiStudentMenu.showError("Error al cargar notificaciones. Intente más tarde.");
        }
    }

    private void openDocuments(String enrollment) {
        try {
            Practice practice = practiceDAO.getPracticeByEnrollment(enrollment);
            if (practice == null) {
                guiStudentMenu.showError("No se encuentra registrado en una practica por el momento. Debe estar en una experiencia educativa.");
            } else {
                GUIUploadDocuments guiUploadDocuments = new GUIUploadDocuments(practice);
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                guiUploadDocuments.start(stage);
            }
        } catch (DataOperationException e) {
            guiStudentMenu.showError(e.getMessage());
            LOGGER.log(Level.SEVERE, "Error al obtener práctica por matrícula", e.getMessage());
        }
    }

    private void logout() {
        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle("Cerrar Sesión");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Seguro que desea cerrar sesión?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            UserDAO userDAO = new UserDAO();
            userDAO.logout();
            GUILogin guiLogin = new GUILogin();
            Stage loginStage = new Stage();
            guiLogin.start(loginStage);
            guiStudentMenu.getStage().close();
        }
    }

    private void openGenerateDocuments() {
        try {
            Practice practice = practiceDAO.getPracticeByEnrollment(guiStudentMenu.getStudent().getEnrollment());
            GUIGenerateDocuments guiGenerateDocuments = new GUIGenerateDocuments(practice);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            guiGenerateDocuments.start(stage);
        } catch (DataOperationException e) {
            guiStudentMenu.showError(e.getMessage());
        }
    }

    private boolean studentHadAssignedProject() {
        boolean hadAssignedProject = true;
        if (guiStudentMenu.getStudent().getAssignedProject() == null) {
            hadAssignedProject = false;
        }
        return hadAssignedProject;
    }
}