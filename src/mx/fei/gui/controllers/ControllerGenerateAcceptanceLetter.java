package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIGenerateAcceptanceLetter;
import mx.fei.gui.utils.AcceptanceLetterGenerator;
import mx.fei.logic.dao.PracticeDAO;
import mx.fei.logic.dto.Student;
import mx.fei.logic.dto.Practice;
import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.ProjectManager;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerGenerateAcceptanceLetter {
    private static final Logger LOGGER = Logger.getLogger(ControllerGenerateAcceptanceLetter.class.getName());
    private final GUIGenerateAcceptanceLetter guiGenerateAcceptanceLetter;
    private final Stage stage;
    private final Student student;
    private Practice practice;
    private Project project;

    public ControllerGenerateAcceptanceLetter(GUIGenerateAcceptanceLetter guiGenerateAcceptanceLetter, Stage stage, Student student) {
        this.guiGenerateAcceptanceLetter = guiGenerateAcceptanceLetter;
        this.stage = stage;
        this.student = student;
    }

    public void loadData() {
        String errorMessage = resolveLetterData();
        if (errorMessage != null) {
            guiGenerateAcceptanceLetter.showError(errorMessage);
        }
    }

    private String resolveLetterData() {
        String errorMessage = null;
        if (student == null) {
            errorMessage = "No hay estudiante seleccionado.";
        } else {
            populateStudentHeader();
            try {
                PracticeDAO practiceDAO = new PracticeDAO();
                practice = practiceDAO.getPracticeByEnrollment(student.getEnrollment());
                if (practice == null) {
                    errorMessage = "El estudiante no tiene una práctica asignada.";
                } else {
                    project = student.getAssignedProject();
                    if (project == null) {
                        errorMessage = "El estudiante no tiene un proyecto asignado.";
                    } else {
                        populateProjectData();
                    }
                }
            } catch (DataOperationException e) {
                LOGGER.log(Level.SEVERE, "Error cargando datos para carta de aceptación", e);
                errorMessage = "Error al cargar datos: " + e.getMessage();
            }
        }
        return errorMessage;
    }

    private void populateStudentHeader() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy");
        guiGenerateAcceptanceLetter.setDate(simpleDateFormat.format(new Date()));
        guiGenerateAcceptanceLetter.setStudentName(student.getName() + " " + student.getLastName());
        guiGenerateAcceptanceLetter.setEnrollment(student.getEnrollment());
    }

    private void populateProjectData() {
        String enterpriseName = (project.getEnterprise() != null) ? project.getEnterprise().getName() : "No especificada";
        guiGenerateAcceptanceLetter.setEnterprise(enterpriseName);
        guiGenerateAcceptanceLetter.setStartDate(project.getStartDate() != null ? project.getStartDate().toString() : "No definida");
        guiGenerateAcceptanceLetter.setEndDate(project.getFinalDate() != null ? project.getFinalDate().toString() : "No definida");
        populateResponsibleContact(project.getProjectManager());
    }

    private void populateResponsibleContact(ProjectManager manager) {
        if (manager == null) {
            guiGenerateAcceptanceLetter.setResponsibleEmail("No especificado");
            guiGenerateAcceptanceLetter.setResponsiblePhone("No especificado");
        } else {
            guiGenerateAcceptanceLetter.setResponsibleEmail(manager.getEmailProjectManager() != null ? manager.getEmailProjectManager() : "");
            guiGenerateAcceptanceLetter.setResponsiblePhone(manager.getPhoneNumberProjectManager() != null ? manager.getPhoneNumberProjectManager() : "");
        }
    }

    public void printPDF() {
        if (practice == null || project == null) {
            guiGenerateAcceptanceLetter.showError("No hay datos suficientes para generar la carta.");
        }
        else if (guiGenerateAcceptanceLetter.validateSchedule()) {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Seleccionar carpeta para guardar la carta");
            File directory = chooser.showDialog(stage);
            if (directory != null) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String timestamp = simpleDateFormat.format(new Date());
                String fileName = String.format("Carta_Aceptacion_%s_%s.pdf", student.getEnrollment(), timestamp);
                String outputPath = new File(directory, fileName).getAbsolutePath();
                Map<String, Object> parameters = buildParameters();
                AcceptanceLetterGenerator generator = new AcceptanceLetterGenerator();
                boolean isGenerated = generator.generate(parameters, outputPath);
                if (isGenerated) {
                    guiGenerateAcceptanceLetter.showSuccess("Carta generada exitosamente en:\n" + outputPath);
                    guiGenerateAcceptanceLetter.closeWindow();
                } else {
                    guiGenerateAcceptanceLetter.showError("Error al generar la carta.");
                }
            }
        }
    }

    private Map<String, Object> buildParameters() {
        Map<String, Object> parameters = new HashMap<>();
        URL logoResource = getClass().getResource("/images/acceptance_letter.png");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy");
        String currentDate = simpleDateFormat.format(new Date());
        parameters.put("logo", logoResource);
        parameters.put("date", currentDate);
        parameters.put("name", student.getName() + " " + student.getLastName());
        parameters.put("enrollment", student.getEnrollment());
        String enterprise = (project.getEnterprise() != null) ? project.getEnterprise().getName() : "N/A";
        parameters.put("enterprise", "Organización: " + enterprise);
        parameters.put("organization", enterprise);
        parameters.put("startDate", project.getStartDate() != null ? project.getStartDate().toString() : "");
        parameters.put("endDate", project.getFinalDate() != null ? project.getFinalDate().toString() : "");
        if (project.getProjectManager() != null) {
            parameters.put("managerName","Responsable: " + project.getProjectManager().getName());
            parameters.put("ManagerCharge","Cargo: " + project.getProjectManager().getRol());
            parameters.put("managerMail", project.getProjectManager().getEmailProjectManager());
            parameters.put("managerPhone", project.getProjectManager().getPhoneNumberProjectManager());
        } else {
            parameters.put("managerMail", "");
            parameters.put("managerPhone", "");
        }
        String[] horarios = guiGenerateAcceptanceLetter.getScheduleStrings();
        parameters.put("mondaySchedule", horarios[0]);
        parameters.put("tuesdaySchedule", horarios[1]);
        parameters.put("wednesdaySchedule", horarios[2]);
        parameters.put("thursdaySchedule", horarios[3]);
        parameters.put("fridaySchedule", horarios[4]);
        return parameters;
    }
}