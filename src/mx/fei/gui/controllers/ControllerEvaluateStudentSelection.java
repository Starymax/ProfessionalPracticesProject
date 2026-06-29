package mx.fei.gui.controllers;

import mx.fei.gui.utils.StudentStatusFilter;
import mx.fei.gui.views.GUIEvaluateStudent;
import mx.fei.gui.views.GUIEvaluateStudentSelection;
import mx.fei.logic.dao.DocumentDAO;
import mx.fei.logic.dao.EducationalExperienceDAO;
import mx.fei.logic.dao.PracticeDAO;
import mx.fei.logic.dao.StudentDAO;
import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.dto.PracticeStatus;
import mx.fei.logic.dto.Professor;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerEvaluateStudentSelection {

    private final GUIEvaluateStudentSelection guiEvaluateStudentSelection;
    private static final Logger LOGGER = Logger.getLogger(ControllerEvaluateStudentSelection.class.getName());
    private List<Student> loadedStudents = new ArrayList<>();
    private Set<Integer> concludedStudentIds = new HashSet<>();
    private Set<Integer> enrolledStudentIds = new HashSet<>();

    public ControllerEvaluateStudentSelection(GUIEvaluateStudentSelection guiEvaluateStudentSelection) {
        this.guiEvaluateStudentSelection = guiEvaluateStudentSelection;
        loadExperiences();
    }

    public void handleExperienceSelection(ActionEvent event) {
        EducationalExperience experience = guiEvaluateStudentSelection.getSelectedExperience();
        if (experience != null) {
            try {
                StudentDAO studentDAO = new StudentDAO();
                loadedStudents = studentDAO.getStudentsByEducationalExperience(experience.getNrc(), experience.getSection());
                concludedStudentIds = new DocumentDAO().getStudentIdsWithConcludedPractice();
                enrolledStudentIds = new PracticeDAO().getEnrolledStudentIds();
                applyStatusFilter();
            } catch (DataOperationException e) {
                LOGGER.log(Level.SEVERE, "Error al cargar los estudiantes de la experiencia educativa", e);
                guiEvaluateStudentSelection.showError(e.getMessage());
            }
        }
    }

    public void handleStatusFilter(ActionEvent event) {
        applyStatusFilter();
    }

    private void applyStatusFilter() {
        PracticeStatus status = PracticeStatus.fromLabel(guiEvaluateStudentSelection.getSelectedStatusLabel());
        List<Student> filteredStudents = StudentStatusFilter.filterByStatus(loadedStudents, status, concludedStudentIds, enrolledStudentIds);
        guiEvaluateStudentSelection.loadStudents(filteredStudents);
    }

    public void handleEvaluateCancelButtons(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Evaluar" -> {
                evaluateStudent();
            }
            case "Cancelar" -> {
                guiEvaluateStudentSelection.getStage().close();
            }
        }
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
                LOGGER.log(Level.SEVERE, "Error al cargar las experiencias educativas del profesor", e);
                guiEvaluateStudentSelection.showError(e.getMessage());
            }
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
