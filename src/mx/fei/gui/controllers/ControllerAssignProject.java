package mx.fei.gui.controllers;

import mx.fei.gui.utils.GUIUtils;
import mx.fei.gui.views.GUIAssignProject;
import mx.fei.gui.views.GUISendNotificationOfAssign;
import mx.fei.logic.dao.ProjectDAO;
import mx.fei.logic.dao.StudentDAO;
import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class ControllerAssignProject {

    private final GUIAssignProject guiAssignProject;
    private final int NO_PLACES = 0;

    public ControllerAssignProject(GUIAssignProject guiAssignProject) {
        this.guiAssignProject = guiAssignProject;
    }

    public void handleAssignCancelButtons(ActionEvent event) {
        Button button = (Button) event.getSource();
        switch (button.getText()) {
            case "Asignar" -> {
                assignProject();
            }
            case "Cancelar" -> {
                cancel();
            }
        }
    }

    private void assignProject() {
        Project selectedProject = guiAssignProject.getSelectedProject();
        Student student = guiAssignProject.getStudent();
        if (selectedProject == null) {
            guiAssignProject.showError("Seleccione un proyecto de la lista.");
        } else if (selectedProject.getAvailablePlaces() > NO_PLACES) {
            confirmAssign(student, selectedProject);
        } else {
            guiAssignProject.showError("Proyecto lleno, seleccione otro.");
        }
    }

    private void confirmAssign(Student student, Project selected) {
        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar asignación");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Seguro que desea asignar este proyecto al estudiante?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            GUISendNotificationOfAssign sendNotification = new GUISendNotificationOfAssign(guiAssignProject.getStage(), student, selected);
            sendNotification.showAndWait();
            if (sendNotification.wasSent() && student != null) {
                persistAssignment(student, selected);
            }
        }
    }

    private void persistAssignment(Student student, Project selected) {
        try {
            StudentDAO studentDAO = new StudentDAO();
            studentDAO.assignProject(student, selected);
            updateAvailablePlaces(selected);
            guiAssignProject.showSuccess("Asignación realizada exitosamente.");
            guiAssignProject.getStage().close();
        } catch (DataOperationException e) {
            guiAssignProject.showError("Error al asignar este proyecto al estudiante: " + e.getMessage());
        }
    }

    private void updateAvailablePlaces(Project project) {
        try {
            ProjectDAO projectDAO = new ProjectDAO();
            int availablePlaces = project.getAvailablePlaces() - 1;
            project.setAvailablePlaces(availablePlaces);
            projectDAO.modifyProject(project);
        } catch (DataOperationException e) {
            GUIUtils.showError(e.getMessage());
        }
    }

    private void cancel() {
        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle("Cancelar");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Seguro que desea cancelar?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            guiAssignProject.getStage().close();
        }
    }
}