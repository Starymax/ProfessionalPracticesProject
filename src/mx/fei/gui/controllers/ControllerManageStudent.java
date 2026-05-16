package mx.fei.gui.controllers;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import mx.fei.gui.views.*;
import mx.fei.logic.dao.StudentDAO;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataOperationException;

import java.util.List;

public class ControllerManageStudent {
    private GUIManageStudent guiManageStudent;

    public ControllerManageStudent(GUIManageStudent guiManageStudent) {
        this.guiManageStudent = guiManageStudent;
    }

    public void handleRegisterModifyAssignButtons(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Registrar estudiante" -> {
                registerStudent();
            }
            case "Modificar estudiante" -> {
                modifyStudent();
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
        stage.initModality(Modality.APPLICATION_MODAL);
        guiRegisterStudent.start(stage);
        stage.setTitle("Registrar estudiante");
    }

    private void modifyStudent() {
        GUIChooseStudent guiChooseStudent = new GUIChooseStudent();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Modificar estudiante");
        guiChooseStudent.start(stage);
    }

    private void assignProject() {
        try {
            StudentDAO studentDAO = new StudentDAO();
            List<Student> studentList = studentDAO.getStudentsWithoutProject();
            GUISelectStudentForAssignProject guiSelectStudentForAssignProject = new GUISelectStudentForAssignProject();
            Stage stage = new Stage();
            guiSelectStudentForAssignProject.start(stage);
            stage.initModality(Modality.APPLICATION_MODAL);
            guiSelectStudentForAssignProject.loadStudents(studentList);
            stage.setTitle("Seleccionar estudiante");
        } catch (DataOperationException e) {
            guiManageStudent.showError(e.getMessage());
        }
    }
}