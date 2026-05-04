package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIAssignProject;
import mx.fei.logic.dao.ProjectDAO;
import mx.fei.logic.dao.StudentDAO;
import mx.fei.logic.dto.Project;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataOperationException;

import java.util.Optional;

public class ControllerAssignProject {

    private final GUIAssignProject guiAssignProject;

    public ControllerAssignProject(GUIAssignProject guiAssignProject) {
        this.guiAssignProject = guiAssignProject;
    }

    public void handleButtonAction(ActionEvent event) {
        if (event.getSource() == guiAssignProject.getButtonAssign()) {
            assignProject();
        } else if (event.getSource() == guiAssignProject.getButtonCancel()) {
            cancel();
        }
    }

    private void assignProject() {
        Project selected = guiAssignProject.getSelectedProject();
        Student student = guiAssignProject.getStudent();
        if (selected == null) {
            guiAssignProject.showError("Seleccione un proyecto de la lista.");
        } else {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar asignación");
            confirm.setHeaderText(null);
            confirm.setContentText("¿Seguro que desea asignar este proyecto al estudiante?");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    updateAvailablePlaces(selected);
                    StudentDAO studentDAO = new StudentDAO();
                    studentDAO.assignProject(student, selected);
                    guiAssignProject.showSuccess("Asignación realizada exitosamente.");
                    guiAssignProject.getStage().close();
                } catch (DataOperationException e) {
                    guiAssignProject.showError(e.getMessage());
                }
            }
        }
    }

    private void cancel() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancelar");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Seguro que desea cancelar?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            guiAssignProject.getStage().close();
        }
    }

    private void updateAvailablePlaces(Project project) {
        try {
            ProjectDAO projectDAO = new ProjectDAO();
            int availablePlaces = project.getAvailablePlaces() - 1;
            project.setAvailablePlaces(availablePlaces);
            projectDAO.modifyProject(project);
        } catch (DataOperationException e) {
            guiAssignProject.showError(e.getMessage());
        }
    }
}