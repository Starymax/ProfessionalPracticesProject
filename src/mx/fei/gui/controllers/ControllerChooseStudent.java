package mx.fei.gui.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Modality;
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
            logger.log(Level.SEVERE,"Error al cargar a los estudiantes", e);
            guiChooseStudent.showError("Error al cargar la lista de estudiantes.");
        }
    }

    public void handleSelectAndReturnButtons(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Seleccionar" -> handleSelectStudent();
            case "Regresar" -> {
                guiChooseStudent.closeWindow();
            }
        }
    }

    private void handleSelectStudent() {
        Student studentSelected;
        try {
            studentSelected = guiChooseStudent.getSelectedStudent();
            GUIModifyStudent guiModifyStudent = new GUIModifyStudent();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            guiModifyStudent.start(stage);
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            guiChooseStudent.showError("Seleccione un estudiante.");
        }
    }
}
