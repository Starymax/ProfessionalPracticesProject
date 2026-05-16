package mx.fei.gui.controllers;

import javafx.stage.Stage;
import mx.fei.gui.views.GUIAssignProject;
import mx.fei.gui.views.GUIManageStudent;
import mx.fei.gui.views.GUISelectStudentForAssignProject;
import mx.fei.logic.dao.StudentDAO;
import mx.fei.logic.dto.Student;
import javafx.event.ActionEvent;
import mx.fei.logic.exceptions.DataOperationException;

public class ControllerSelectStudentForAssignProject {

    private final GUISelectStudentForAssignProject guiSelectStudentForAssignProject;

    public ControllerSelectStudentForAssignProject(GUISelectStudentForAssignProject guiSelectStudentForAssignProject) {
        this.guiSelectStudentForAssignProject = guiSelectStudentForAssignProject;
    }

    public void handleSelectCancelButtons(ActionEvent event) {
        if (event.getSource() == guiSelectStudentForAssignProject.getButtonSelect()) {
            selectStudent();
        } else if (event.getSource() == guiSelectStudentForAssignProject.getButtonCancelar()) {
            cancel();
        }
    }

    private void selectStudent() {
        Student studentSelected = guiSelectStudentForAssignProject.getSelectedStudent();
        if (studentSelected != null) {
            GUIAssignProject guiAssignProject = new GUIAssignProject();
            Stage stage = new Stage();
            guiAssignProject.start(stage);
            guiAssignProject.setStudent(studentSelected);
            guiSelectStudentForAssignProject.getStage().close();
            try {
                StudentDAO studentDAO = new StudentDAO();
                guiAssignProject.loadProjects(studentDAO.getSelectedProjects(studentSelected));
            } catch (DataOperationException e) {
                guiAssignProject.showError(e.getMessage());
            }
        } else {
            guiSelectStudentForAssignProject.showError("Seleccione un alumno de la lista.");
        }
    }

    private void cancel() {
        GUIManageStudent guiManageStudent = new GUIManageStudent();
        Stage stage = new Stage();
        guiManageStudent.start(stage);
        stage.setTitle("Gestion estudiante");
        guiSelectStudentForAssignProject.getStage().close();
    }
}