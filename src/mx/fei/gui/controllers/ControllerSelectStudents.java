package mx.fei.gui.controllers;

import mx.fei.gui.views.GUISelectStudents;
import mx.fei.logic.dao.StudentDAO;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.stage.Stage;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerSelectStudents {
    private GUISelectStudents guiSelectStudents;
    private StudentDAO studentDAO;
    private Stage stage;
    private static final Logger LOGGER = Logger.getLogger(ControllerSelectStudents.class.getName());

    public ControllerSelectStudents(GUISelectStudents guiSelectStudents, Stage stage) {
        this.guiSelectStudents = guiSelectStudents;
        this.studentDAO = new StudentDAO();
        this.stage = stage;
        loadStudents();
    }

    private void loadStudents() {
        try {
            List<Student> students = studentDAO.getStudentsWithoutEducationalExperience();
            List<Student> studentsAlreadySelected = guiSelectStudents.getGuiAddStudents().getStudentsToAdd();
            guiSelectStudents.setStudents(students, studentsAlreadySelected);
        } catch (DataOperationException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar estudiantes", e);
            guiSelectStudents.showError(e.getMessage());
        }
    }

    public void handleSelect() {
        List<Student> checkedStudents = guiSelectStudents.getCheckedStudents();
        if (checkedStudents.isEmpty()) {
            guiSelectStudents.showError("Selecciona al menos un alumno.");
        } else {
            guiSelectStudents.getGuiAddStudents().setStudentsToAdd(checkedStudents);
            guiSelectStudents.closeWindow();
        }
    }
}