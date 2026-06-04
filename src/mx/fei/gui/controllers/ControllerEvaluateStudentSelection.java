package mx.fei.gui.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.fei.gui.views.GUIEvaluateStudent;
import mx.fei.gui.views.GUIEvaluateStudentSelection;
import mx.fei.logic.dao.EducationalExperienceDAO;
import mx.fei.logic.dao.StudentDAO;
import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.dto.Professor;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataOperationException;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerEvaluateStudentSelection {

    private final GUIEvaluateStudentSelection guiEvaluateStudentSelection;
    private static final Logger logger = Logger.getLogger(ControllerEvaluateStudentSelection.class.getName());

    public ControllerEvaluateStudentSelection(GUIEvaluateStudentSelection guiEvaluateStudentSelection) {
        this.guiEvaluateStudentSelection = guiEvaluateStudentSelection;
        loadExperiences();
    }

    private void loadExperiences() {
        Professor professor = guiEvaluateStudentSelection.getProfessor();
        if (professor == null) {
            guiEvaluateStudentSelection.showError("No se pudo identificar al profesor en sesión.");
        } else {
            try {
                EducationalExperienceDAO educationalExperienceDAO = new EducationalExperienceDAO();
                List<EducationalExperience> experiences = educationalExperienceDAO.getEducationalExperiencesByProfessor(professor.getUserId());
                guiEvaluateStudentSelection.loadExperiences(experiences);
            } catch (DataOperationException e) {
                logger.log(Level.SEVERE, "Error al cargar las experiencias educativas del profesor", e);
                guiEvaluateStudentSelection.showError("Error al cargar las experiencias educativas.");
            }
        }
    }

    public void handleExperienceSelection(ActionEvent event) {
        EducationalExperience experience = guiEvaluateStudentSelection.getSelectedExperience();
        if (experience != null) {
            try {
                StudentDAO studentDAO = new StudentDAO();
                List<Student> students = studentDAO.getStudentsByEducationalExperience(experience.getNrc());
                guiEvaluateStudentSelection.loadStudents(students);
            } catch (DataOperationException e) {
                logger.log(Level.SEVERE, "Error al cargar los estudiantes de la experiencia educativa", e);
                guiEvaluateStudentSelection.showError("Error al cargar los estudiantes de la experiencia educativa.");
            }
        }
    }

    public void handleEvaluateCancelButtons(ActionEvent event) {
        Button source = (Button) event.getSource();
        if (source == guiEvaluateStudentSelection.getButtonEvaluate()) {
            evaluateStudent();
        } else if (source == guiEvaluateStudentSelection.getButtonCancel()) {
            guiEvaluateStudentSelection.getStage().close();
        }
    }

    private void evaluateStudent() {
        Student selectedStudent = guiEvaluateStudentSelection.getSelectedStudent();
        if (selectedStudent == null) {
            guiEvaluateStudentSelection.showError("Seleccione un alumno de la lista.");
        } else {
            GUIEvaluateStudent guiEvaluateStudent = new GUIEvaluateStudent(selectedStudent);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            guiEvaluateStudent.start(stage);
            guiEvaluateStudent.loadData();
            guiEvaluateStudentSelection.getStage().close();
        }
    }
}
