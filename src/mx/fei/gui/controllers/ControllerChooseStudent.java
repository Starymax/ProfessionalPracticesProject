package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIChooseStudent;
import mx.fei.gui.views.GUIModifyStudent;
import mx.fei.gui.views.GUIPracticeInfo;
import mx.fei.logic.dao.DocumentDAO;
import mx.fei.logic.dao.EducationalExperienceDAO;
import mx.fei.logic.dao.PracticeDAO;
import mx.fei.logic.dao.StudentDAO;
import mx.fei.logic.dto.Practice;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerChooseStudent {
    private GUIChooseStudent guiChooseStudent;
    private StudentDAO studentDAO;
    private PracticeDAO practiceDAO;
    private EducationalExperienceDAO educationalExperienceDAO;
    private static final Logger LOGGER = Logger.getLogger(ControllerChooseStudent.class.getName());
    private final int NO_STUDENTS_SELECTED = 0;

    public ControllerChooseStudent(GUIChooseStudent guiChooseStudent) {
        this.guiChooseStudent = guiChooseStudent;
        this.studentDAO = new StudentDAO();
        this.practiceDAO = new PracticeDAO();
        practiceDAO = new PracticeDAO();
        loadStudents();
    }

    public void handleSelectAndReturnButtons(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Seleccionar" -> {
                handleSelectStudent();
            }
            case "Regresar" -> {
                guiChooseStudent.closeWindow();
            }
        }
    }

    private void loadStudents() {
        try {
            List<Student> students;
            if (guiChooseStudent.isToModifyStudent()) {
                students = studentDAO.getStudents();
            } else if (guiChooseStudent.isConsultByExperience()) {
                students = practiceDAO.getStudentsByNrc(guiChooseStudent.getEducationalExperience().getNrc());
            } else {
                students = practiceDAO.getStudentsWithPractice();
            }
            Set<Integer> concludedStudentIds = new DocumentDAO().getStudentIdsWithConcludedPractice();
            Set<Integer> enrolledStudentIds = practiceDAO.getEnrolledStudentIds();
            guiChooseStudent.setStudents(students, concludedStudentIds, enrolledStudentIds);
        } catch (DataOperationException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar a los estudiantes: " + e.getMessage());
            guiChooseStudent.showError(e.getMessage());
        }
    }

    private void handleSelectStudent() {
        if (guiChooseStudent.getStudents() == null || guiChooseStudent.getStudents().isEmpty()) {
            guiChooseStudent.showError("No hay estudiantes disponibles para modificar.");
        } else {
            int selectedIndex = guiChooseStudent.getListViewStudents().getSelectionModel().getSelectedIndex();
            if (selectedIndex < NO_STUDENTS_SELECTED) {
                guiChooseStudent.showError("Seleccione un estudiante.");
            } else {
                if (guiChooseStudent.isToModifyStudent()) {
                    Student studentSelected = guiChooseStudent.getStudents().get(selectedIndex);
                    GUIModifyStudent guiModifyStudent = new GUIModifyStudent(studentSelected);
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    guiModifyStudent.start(stage);
                }
                else {
                    try {
                        Student studentSelected = guiChooseStudent.getStudents().get(selectedIndex);
                        Practice practice = practiceDAO.getPracticeByEnrollment(studentSelected.getEnrollment());
                        GUIPracticeInfo guiPracticeInfo = new GUIPracticeInfo(practice);
                        Stage stage = new Stage();
                        stage.initModality(Modality.APPLICATION_MODAL);
                        guiPracticeInfo.start(stage);
                        guiChooseStudent.closeWindow();
                    } catch (DataOperationException e) {
                        guiChooseStudent.showError("Error al cargar a los estudiantes");
                    }
                }
            }
        }
    }
}
