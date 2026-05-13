package mx.fei.gui.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import mx.fei.gui.views.GUISelectProjects;
import mx.fei.logic.dao.StudentDAO;
import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataOperationException;

import java.util.List;
import java.util.Optional;

public class ControllerSelectProjects {

    private final GUISelectProjects guiSelectProjects;
    private final StudentDAO studentDAO;

    public ControllerSelectProjects(GUISelectProjects guiSelectProjects) {
        this.guiSelectProjects = guiSelectProjects;
        studentDAO = new StudentDAO();
    }

    public void handleButtonAction(ActionEvent event) {
        if (event.getSource() == guiSelectProjects.getSelectButton()) {
            selectProjects();
        } else if (event.getSource() == guiSelectProjects.getCancelButton()) {
            cancel();
        }
    }

    private void selectProjects() {
        Student student;
        if (guiSelectProjects.getSelectedCount() < 3) {
            guiSelectProjects.showError("Debes seleccionar exactamente 3 proyectos.");
        } else {
            student = guiSelectProjects.getStudent();
            if (student == null) {
                guiSelectProjects.showError("No se encontró el estudiante para la selección de proyectos.");
            } else {
                List<Project> selectedProjects = guiSelectProjects.getSelectedProjects();
                try {
                    studentDAO.saveSelectedProjects(selectedProjects, student);
                    guiSelectProjects.showSuccess("Proyectos seleccionados correctamente.");
                    guiSelectProjects.getStage().close();

                } catch (DataOperationException e) {
                    guiSelectProjects.showError(e.getMessage());
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
            guiSelectProjects.getStage().close();
        }
    }
}