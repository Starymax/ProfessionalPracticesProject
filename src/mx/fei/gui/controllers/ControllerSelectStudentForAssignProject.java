package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIAssignProject;
import mx.fei.gui.views.GUISelectStudentForAssignProject;
import mx.fei.logic.dao.ProjectDAO;
import mx.fei.logic.dao.StudentDAO;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

public class ControllerSelectStudentForAssignProject {

    private final GUISelectStudentForAssignProject guiSelectStudentForAssignProject;

    public ControllerSelectStudentForAssignProject(GUISelectStudentForAssignProject guiSelectStudentForAssignProject) {
        this.guiSelectStudentForAssignProject = guiSelectStudentForAssignProject;
    }

    public void handleSelectCancelButtons(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Seleccionar" -> {
                selectStudent();
            }
            case "Cancelar" -> {
                cancel();
            }
        }
    }

    private void selectStudent() {
        Student studentSelected = guiSelectStudentForAssignProject.getSelectedStudent();
        if (studentSelected != null) {
            try {
                StudentDAO studentDAO = new StudentDAO();
                ProjectDAO projectDAO = new ProjectDAO();
                GUIAssignProject guiAssignProject = new GUIAssignProject();
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                guiAssignProject.start(stage);
                guiAssignProject.setStudent(studentSelected);
                guiAssignProject.loadProjects(projectDAO.getActiveProjects(), studentDAO.getSelectedProjects(studentSelected));
                guiSelectStudentForAssignProject.getStage().close();
            } catch (DataOperationException e) {
                guiSelectStudentForAssignProject.showError(e.getMessage());
            }
        } else {
            guiSelectStudentForAssignProject.showError("Seleccione un alumno de la lista.");
        }
    }

    private void cancel() {
        guiSelectStudentForAssignProject.getStage().close();
    }
}