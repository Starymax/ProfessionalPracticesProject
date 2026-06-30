package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIGenerateSelfEvaluation;
import mx.fei.gui.utils.SelfEvaluationGenerator;
import mx.fei.logic.dao.DocumentDAO;
import mx.fei.logic.dao.PracticeDAO;
import mx.fei.logic.dto.*;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.control.ComboBox;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
                        checkExistingAnswers();
                    }
                }
            } catch (DataOperationException e) {
                LOGGER.log(Level.SEVERE, "Error cargando datos para autoevaluación: " + e.getMessage());
                guiGenerateSelfEvaluation.showError("Error al cargar los datos: " + e.getMessage());
            }
        }
    }

    private void checkExistingAnswers() {
        try {
            DocumentDAO documentDAO = new DocumentDAO();
            List<Integer> previousAnswers = documentDAO.getSelfEvaluationAnswers(practice);
            if (!previousAnswers.isEmpty()) {
                loadPreviousAnswers(previousAnswers);
                guiGenerateSelfEvaluation.lockAnswers();
            }
        } catch (DataOperationException e) {
            LOGGER.log(Level.WARNING, "No se pudieron verificar respuestas previas: " + e.getMessage());
        }
    }

    private void loadPreviousAnswers(List<Integer> answers) {
        List<ComboBox<Integer>> answerCombos = guiGenerateSelfEvaluation.getAnswerCombos();
        for (int i = 0; i < answerCombos.size() && i < answers.size(); i++) {
            answerCombos.get(i).setValue(answers.get(i));
        }
    }

    

    private void printPDF() {
        DirectoryChooser filesChooser = new DirectoryChooser();
        filesChooser.setTitle("Seleccionar carpeta para guardar la autoevaluación");
        File directory = filesChooser.showDialog(stage);
        if (directory != null) {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = String.format("Autoevaluacion_%s_%s.pdf", student.getEnrollment(), timestamp);
            String outputPath = new File(directory, fileName).getAbsolutePath();
            try {
                if (!guiGenerateSelfEvaluation.isLocked()) {
                    int documentId = registerSelfEvaluationInDatabase(outputPath);
                    saveAnswers(documentId);
                }
                Map<String, Object> parameters = buildParameters();
                SelfEvaluationGenerator generator = new SelfEvaluationGenerator();
                boolean success = generator.generate(parameters, outputPath);
                if (success) {
                    guiGenerateSelfEvaluation.showSuccess("Autoevaluación generada exitosamente en:\n" + outputPath);
                    guiGenerateSelfEvaluation.closeWindow();
                } else {
                    guiGenerateSelfEvaluation.showError("Error al generar el PDF.");
                }
            } catch (DataOperationException e) {
                LOGGER.log(Level.SEVERE, "Error al guardar la autoevaluación en BD: " + e.getMessage());
                guiGenerateSelfEvaluation.showError(e.getMessage());
            }
        }
    }

    private int registerSelfEvaluationInDatabase(String outputPath) throws DataOperationException {
        if (practice == null) {
            throw new DataOperationException("No hay practica disponible para guardar la autoevaluación.");
        }
        DocumentDAO documentDAO = new DocumentDAO();
        Document document = new Document("Autoevaluacion_" + student.getEnrollment() + ".pdf", outputPath, DocumentType.SELF_EVALUATION);
        int documentId = documentDAO.loadDocument(practice, document);
        if (documentId <= 0) {
            throw new DataOperationException("No se pudo registrar el documento de autoevaluación.");
        }
        return documentId;
    }

    private void saveAnswers(int documentId) throws DataOperationException {
        List<Integer> answers = new ArrayList<>();
        for (int i = 0; i < guiGenerateSelfEvaluation.getAnswerCombos().size(); i++) {
            answers.add(guiGenerateSelfEvaluation.getAnswerCombos().get(i).getValue());
        }
        DocumentDAO documentDAO = new DocumentDAO();
        documentDAO.saveAnswersOfSelfEvaluation(documentId, answers);
    }

    private Map<String, Object> buildParameters() {
        Map<String, Object> parameters = new HashMap<>();
        URL logoResource = getClass().getResource("/images/selfEvaluation.png");
        parameters.put("studentName", student.getName() + " " + student.getLastName());
        parameters.put("studentEnrollment", student.getEnrollment());
        parameters.put("enterprise", enterprise != null ? enterprise.getName() : "");
        parameters.put("projectManager", manager != null ? manager.getName() : "");
        parameters.put("projectName", project.getNameProject() != null ? project.getNameProject() : "");
        parameters.put("logo", logoResource);
        for (int i = 0; i < guiGenerateSelfEvaluation.getAnswerCombos().size(); i++) {
            int answer = guiGenerateSelfEvaluation.getAnswerCombos().get(i).getValue();
            for (int column = 1; column <= guiGenerateSelfEvaluation.getColumns(); column++) {
                String parameterName = "quest" + (i+1) + "response" + column;
                String value = (answer == column) ? "X" : "";
                parameters.put(parameterName, value);
            }
        }
        int total = 0;
        for (int i = 0; i < guiGenerateSelfEvaluation.getRows(); i++) {
            total += guiGenerateSelfEvaluation.getAnswerCombos().get(i).getValue();
        }
        String totalString = String.valueOf(total);
        parameters.put("finalScore", totalString);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDateTime date = LocalDateTime.now();
        parameters.put("placeAndDate", "Xalapa.Ver "+date.format(formatter));
        return parameters;
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

    public void handlePrintButtonAction() {
        if (!validateAnswers()) {
        guiGenerateSelfEvaluation.showError("Las respuestas incluidas no son validas.");
        } else {
        printPDF();
        }
    }

}