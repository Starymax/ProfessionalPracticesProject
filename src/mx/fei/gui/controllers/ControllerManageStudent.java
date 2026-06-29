package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIChooseStudent;
import mx.fei.gui.views.GUIManageStudent;
import mx.fei.gui.views.GUIRegisterStudent;
import mx.fei.gui.views.GUISelectStudentForAssignProject;
import mx.fei.logic.dao.StudentDAO;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

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
                openRegisterStudent();
            }
            case "Modificar estudiante" -> {
                if (!existStudents()) {
                    guiManageStudent.showError("No existen estudiantes disponibles por el momento.");
                } else {
                    openModifyStudent();
                }
            }
            case "Asignar proyecto" -> {
                if (!existStudents()) {
                    guiManageStudent.showError("No existen estudiantes disponibles por el momento.");
                } else {
                    openAssignProject();
                }
            }
            case "Consultar practicas" -> {
                if (!existStudents()) {
                    guiManageStudent.showError("No existen estudiantes disponibles por el momento.");
                } else {
                    consultPractice();
                }
            }
            case "Regresar" -> {
                guiManageStudent.closeWindow();
            }
        }
    }

    private void openRegisterStudent() {
        GUIRegisterStudent guiRegisterStudent = new GUIRegisterStudent();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        guiRegisterStudent.start(stage);
        stage.setTitle("Registrar estudiante");
    }

    private void openModifyStudent() {
        GUIChooseStudent guiChooseStudent = new GUIChooseStudent();
        guiChooseStudent.setToModifyStudent(true);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Modificar estudiante");
        guiChooseStudent.start(stage);
    }

    private void consultPractice() {
        GUIChooseStudent guiChooseStudent = new GUIChooseStudent();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Consultar prácticas");
        guiChooseStudent.start(stage);
    }

    private void openAssignProject() {
        try {
            StudentDAO studentDAO = new StudentDAO();
            List<Student> studentList = studentDAO.getStudentsWithoutProject();
            GUISelectStudentForAssignProject guiSelectStudentForAssignProject = new GUISelectStudentForAssignProject();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            guiSelectStudentForAssignProject.start(stage);
            guiSelectStudentForAssignProject.loadStudents(studentList);
            stage.setTitle("Seleccionar estudiante");
        } catch (DataOperationException e) {
            guiManageStudent.showError(e.getMessage());
        }
    }

    private boolean existStudents() {
        StudentDAO studentDAO = new StudentDAO();
        boolean studentsExist = true;
        try {
            if (studentDAO.getStudents().isEmpty()) {
                studentsExist = false;
            }
        } catch (DataOperationException e) {
            guiManageStudent.showError("Error al obtener la lista de estudiantes.");
        }
        return studentsExist;
    }
}