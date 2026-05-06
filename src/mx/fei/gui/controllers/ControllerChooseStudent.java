package mx.fei.gui.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import mx.fei.gui.views.GUIChooseStudent;
import mx.fei.gui.views.GUIModifyStudent;
import mx.fei.logic.dao.StudentDAO;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataOperationException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerChooseStudent {
    private GUIChooseStudent guiChooseStudent;
    private StudentDAO studentDAO;
    private static final Logger logger = Logger.getLogger(ControllerChooseStudent.class.getName());

    public ControllerChooseStudent(GUIChooseStudent guiChooseStudent) {
        this.guiChooseStudent = guiChooseStudent;
        this.studentDAO = new StudentDAO();
        loadStudents();
    }

    private void loadStudents() {
        try {
            List<Student> students = studentDAO.getStudents();
            guiChooseStudent.setStudents(students);
        } catch (DataOperationException e) {
            logger.log(Level.SEVERE,"Error al cargar estudiantes", e);
            guiChooseStudent.showError("Error al cargar la lista de estudiantes.");
        }
    }

    public void handleButtons(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Seleccionar" -> handleSelect();
            case "Regresar" -> guiChooseStudent.closeWindow();
        }
    }

    private void handleSelect() {
        Student selected = guiChooseStudent.getSelectedStudent();
        if (selected != null) {
             GUIModifyStudent guiModifyStudent = new GUIModifyStudent(selected);
             Stage stage = new Stage();
             guiModifyStudent.start(stage);
             guiChooseStudent.closeWindow();
        } else {
            guiChooseStudent.showError("Selecciona un alumno de la lista.");
        }
    }
}
