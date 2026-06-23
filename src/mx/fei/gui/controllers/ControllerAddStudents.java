package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIAddStudents;
import mx.fei.gui.views.GUIChooseExperience;
import mx.fei.gui.views.GUISelectStudents;
import mx.fei.logic.dao.PracticeDAO;
import mx.fei.logic.dao.StudentDAO;
import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.dto.Practice;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerAddStudents {
    private GUIAddStudents guiAddStudents;
    private StudentDAO studentDAO;
    private Stage stage;
    private final float DEFAULT_GRADE = 0.0f;
    private static final Logger LOGGER = Logger.getLogger(ControllerAddStudents.class.getName());

    public ControllerAddStudents(GUIAddStudents guiAddStudents, Stage stage) {
        this.guiAddStudents = guiAddStudents;
        this.studentDAO = new StudentDAO();
        this.stage = stage;
    }

    public void handleAddConfirmReturnButtons(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Agregar" -> {
                handleAdd();
            }
            case "Confirmar" -> {
                handleConfirm();
            }
            case "Regresar" -> {
                handleBack();
            }
        }
    }

    private void handleAdd() {
        try {
            List<Student> activeStudents = studentDAO.getActiveStudents();
            if (activeStudents.isEmpty()) {
                guiAddStudents.showError("No existen alumnos disponibles por el momento.");
            } else {
                GUISelectStudents guiSelectStudents = new GUISelectStudents(guiAddStudents);
                Stage selectStage = new Stage();
                selectStage.initModality(Modality.APPLICATION_MODAL);
                guiSelectStudents.start(selectStage);
            }
        } catch (DataOperationException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar estudiantes", e);
            guiAddStudents.showError(e.getMessage());
        }
    }

    private void handleConfirm() {
        List<Student> studentsToAdd = guiAddStudents.getStudentsToAdd();
        EducationalExperience experience = guiAddStudents.getExperience();
        String currentPeriod = experience.getPeriod();
        if (studentsToAdd.isEmpty()) {
            guiAddStudents.showError("Debe agregar al menos un estudiante antes de confirmar.");
        } else if (currentPeriod == null || currentPeriod.isBlank()) {
            guiAddStudents.showError("La experiencia educativa no tiene definido un período. Por favor, verifique los datos de la experiencia.");
        } else if (guiAddStudents.showConfirmation("¿Seguro que quiere dar de alta la experiencia educativa?")) {
            assignExperience(studentsToAdd, experience, currentPeriod);
        }
    }

    private void assignExperience(List<Student> studentsToAdd, EducationalExperience experience, String period) {
        try {
            boolean allAssigned = true;
            PracticeDAO practiceDAO = new PracticeDAO();
            for (Student student : studentsToAdd) {
                Practice practice = new Practice(student, experience, period, DEFAULT_GRADE);
                boolean assigned = practiceDAO.createPractice(practice);
                if (!assigned) {
                    allAssigned = false;
                    LOGGER.log(Level.WARNING, "No se pudo asignar la experiencia al estudiante: " + student.getEnrollment());
                }
            }
            if (allAssigned) {
                guiAddStudents.showSuccess("Experiencia educativa asignada exitosamente.");
                guiAddStudents.closeWindow();
            } else {
                guiAddStudents.showError("Algunos estudiantes no pudieron ser asignados.");
            }
        } catch (DataOperationException e) {
            LOGGER.log(Level.SEVERE, "Error al asignar experiencia educativa", e);
            guiAddStudents.showError(e.getMessage());
        }
    }

    private void handleBack() {
        if (guiAddStudents.showConfirmation("¿Seguro desea cancelar? Se perderán los cambios realizados.")) {
            GUIChooseExperience guiChooseExperience = new GUIChooseExperience();
            Stage stage = new Stage();
            guiChooseExperience.start(stage);
            guiAddStudents.closeWindow();
        }
    }
}