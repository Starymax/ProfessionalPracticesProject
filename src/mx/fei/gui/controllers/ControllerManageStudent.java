package mx.fei.gui.controllers;

import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import mx.fei.gui.views.GUIChooseStudent;
import mx.fei.gui.views.GUIManageStudent;
import mx.fei.gui.views.GUIRegisterStudent;
import mx.fei.gui.views.GUISelectStudentForAssignProject;
import mx.fei.logic.dao.StudentDAO;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataOperationException;

import java.util.List;

public class ControllerManageStudent {
    private GUIManageStudent guiManageStudent;

    public ControllerManageStudent(GUIManageStudent guiManageStudent) {
        this.guiManageStudent = guiManageStudent;
    }

    public void handleButtonAction(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Registrar estudiante" -> {
                registerStudent();
            }
            case "Modificar estudiante" -> {
                GUIChooseStudent guiChooseStudent = new GUIChooseStudent();
                Stage stage = new Stage();
                stage.setTitle("Modificar estudiante");
                guiChooseStudent.start(stage);
                guiManageStudent.closeWindow();
            }
            case "Asignar proyecto"     -> {
                assignProject();
            }
            case "Regresar" -> {
                guiManageStudent.closeWindow();
            }
        }
    }

    private void registerStudent() {
        GUIRegisterStudent guiRegisterStudent = new GUIRegisterStudent();
        Stage stage = new Stage();
        guiRegisterStudent.start(stage);
        stage.setTitle("Registrar estudiante");
        guiManageStudent.closeWindow();
    }

    private void assignProject() {
        try {
            StudentDAO studentDAO = new StudentDAO();
            List<Student> studentList = studentDAO.getStudentsWithoutProject();
            GUISelectStudentForAssignProject guiSelectStudentForAssignProject = new GUISelectStudentForAssignProject();
            Stage stage = new Stage();
            guiSelectStudentForAssignProject.start(stage);
            guiSelectStudentForAssignProject.loadStudents(studentList);
            stage.setTitle("Seleccionar estudiante");
            guiManageStudent.closeWindow();
        } catch (DataOperationException e) {
            guiManageStudent.showError(e.getMessage());
        }
    }
}