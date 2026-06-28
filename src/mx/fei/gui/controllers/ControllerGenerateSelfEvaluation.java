package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIGenerateSelfEvaluation;
import mx.fei.gui.utils.SelfEvaluationGenerator;
import mx.fei.logic.dao.PracticeDAO;
import mx.fei.logic.dto.Student;
import mx.fei.logic.dto.Practice;
import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.Enterprise;
import mx.fei.logic.dto.ProjectManager;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerGenerateSelfEvaluation {

    private static final Logger LOGGER = Logger.getLogger(ControllerGenerateSelfEvaluation.class.getName());
    private final GUIGenerateSelfEvaluation guiGenerateSelfEvaluation;
    private final Stage stage;
    private final Student student;
    private Practice practice;
    private Project project;
    private Enterprise enterprise;
    private ProjectManager manager;

    public ControllerGenerateSelfEvaluation(GUIGenerateSelfEvaluation guiGenerateSelfEvaluation, Stage stage, Student student) {
        this.guiGenerateSelfEvaluation = guiGenerateSelfEvaluation;
        this.stage = stage;
        this.student = student;
    }

    public void loadData() {
        if (student == null) {
            guiGenerateSelfEvaluation.showError("No hay estudiante seleccionado.");
        } else {
            try {
                PracticeDAO practiceDAO = new PracticeDAO();
                practice = practiceDAO.getPracticeByEnrollment(student.getEnrollment());
                if (practice == null || practice.getEducationalExperience() == null) {
                    guiGenerateSelfEvaluation.showError("El estudiante no tiene una práctica o experiencia asignada.");
                } else {
                    project = student.getAssignedProject();
                    if (project == null) {
                        guiGenerateSelfEvaluation.showError("El estudiante no tiene un proyecto asignado.");
                    } else {
                        enterprise = project.getEnterprise();
                        manager = project.getProjectManager();
                        guiGenerateSelfEvaluation.getLabelStudentName().setText(student.getName() + " " + student.getLastName());
                        guiGenerateSelfEvaluation.getLabelEnrollment().setText(student.getEnrollment());
                        guiGenerateSelfEvaluation.getLabelOrganization().setText(enterprise != null ? enterprise.getName() : "No especificada");
                        guiGenerateSelfEvaluation.getLabelResponsible().setText(manager != null ? manager.getName() : "No asignado");
                        guiGenerateSelfEvaluation.getLabelProject().setText(project.getNameProject() != null ? project.getNameProject() : "Sin nombre");
                    }
                }
            } catch (DataOperationException e) {
                LOGGER.log(Level.SEVERE, "Error cargando datos para autoevaluación", e);
                guiGenerateSelfEvaluation.showError("Error al cargar los datos: " + e.getMessage());
            }
        }
    }

    public void handleSelfEvaluationButtons(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Imprimir" -> {
                if (!validateAnswers()) {
                    guiGenerateSelfEvaluation.showError("Las respuestas incluidas no son validas.");
                } else {
                    printPDF();
                }
            }
            case "Regresar" -> guiGenerateSelfEvaluation.closeWindow();
        }
    }

    private void printPDF() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Seleccionar carpeta para guardar la autoevaluación");
        File directory = chooser.showDialog(stage);
        if (directory != null) {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = String.format("Autoevaluacion_%s_%s.pdf", student.getEnrollment(), timestamp);
            String outputPath = new File(directory, fileName).getAbsolutePath();
            Map<String, Object> params = buildParameters();
            SelfEvaluationGenerator generator = new SelfEvaluationGenerator();
            boolean success = generator.generate(params, outputPath);
            if (success) {
                guiGenerateSelfEvaluation.showSuccess("Autoevaluación generada exitosamente en:\n" + outputPath);
                guiGenerateSelfEvaluation.closeWindow();
            } else {
                guiGenerateSelfEvaluation.showError("Error al generar el PDF.");
            }
        }
    }

    private Map<String, Object> buildParameters() {
        Map<String, Object> params = new HashMap<>();
        URL logoResource = getClass().getResource("/images/selfEvaluation.png");
        params.put("studentName", student.getName() + " " + student.getLastName());
        params.put("studentEnrollment", student.getEnrollment());
        params.put("enterprise", enterprise != null ? enterprise.getName() : "");
        params.put("projectManager", manager != null ? manager.getName() : "");
        params.put("projectName", project.getNameProject() != null ? project.getNameProject() : "");
        params.put("logo", logoResource);
        for (int i = 0; i < guiGenerateSelfEvaluation.getAnswerCombos().size(); i++) {
            int answer = guiGenerateSelfEvaluation.getAnswerCombos().get(i).getValue();
            for (int col = 1; col <= guiGenerateSelfEvaluation.getCOLUMNS(); col++) {
                String paramName = "quest" + (i+1) + "response" + col;
                String value = (answer == col) ? "X" : "";
                params.put(paramName, value);
            }
        }
        int total = 0;
        for (int i = 0; i < guiGenerateSelfEvaluation.getROWS(); i++) {
            total += guiGenerateSelfEvaluation.getAnswerCombos().get(i).getValue();
        }
        String totalStr = String.valueOf(total);
        params.put("finalScore", totalStr);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDateTime date = LocalDateTime.now();
        params.put("placeAndDate", "Xalapa.Ver "+date.format(formatter));
        return params;
    }

    private boolean validateAnswers() {
        boolean answersValid = true;
        for (int i = 0; i < guiGenerateSelfEvaluation.getAnswerCombos().size(); i++) {
            Integer value = guiGenerateSelfEvaluation.getAnswerCombos().get(i).getValue();
            if (value == null) {
                guiGenerateSelfEvaluation.showError("Debe seleccionar una respuesta para la afirmación " + (i+1));
                answersValid = false;
            }
        }
        return answersValid;
    }
}