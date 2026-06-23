package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIGeneratePartialReport;
import mx.fei.gui.utils.PartialReportGenerator;
import mx.fei.logic.dao.PracticeDAO;
import mx.fei.logic.dao.ReportDAO;
import mx.fei.logic.dao.StudentAdvanceDAO;
import mx.fei.logic.dao.ActivityDAO;
import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.dto.Practice;
import mx.fei.logic.dto.ReportActivityProgress;
import mx.fei.logic.dto.Student;
import mx.fei.logic.dto.PartialActivityRow;
import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.Report;
import mx.fei.logic.dto.Activity;
import mx.fei.logic.dto.WeeklyLog;
import mx.fei.logic.dto.StudentAdvance;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerGeneratePartialReport {

    private static final Logger LOGGER = Logger.getLogger(ControllerGeneratePartialReport.class.getName());
    private final GUIGeneratePartialReport guiGeneratePartialReport;
    private final Stage stage;
    private final Student student;
    private final ReportDAO reportDAO;
    private Practice practice;
    private EducationalExperience educationalExperience;
    private List<Activity> activities;
    private List<WeeklyLog> weeklyLogs;
    private Map<Integer, List<StudentAdvance>> advancesByWeek;

    public ControllerGeneratePartialReport(GUIGeneratePartialReport guiGeneratePartialReport, Stage stage, Student student) {
        this.guiGeneratePartialReport = guiGeneratePartialReport;
        this.stage = stage;
        this.student = student;
        reportDAO = new ReportDAO();
    }

    public void loadData() {
        String errorMessage = resolveStudentData();
        if (errorMessage != null) {
            guiGeneratePartialReport.showError(errorMessage);
        }
    }

    public void handlePartialReportButtons(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch (source.getText()) {
            case "Exportar PDF" -> {
                exportPDF();
            }
            case "Cancelar" -> {
                guiGeneratePartialReport.closeWindow();
            }
        }
    }

    private String resolveStudentData() {
        String errorMessage = null;
        if (student == null) {
            errorMessage = "No hay estudiante seleccionado para generar el reporte.";
        } else {
            try {
                PracticeDAO practiceDAO = new PracticeDAO();
                practice = practiceDAO.getPracticeByEnrollment(student.getEnrollment());
                if (practice == null || practice.getEducationalExperience() == null) {
                    errorMessage = "El estudiante no tiene una práctica o experiencia educativa asignada.";
                } else {
                    populateReportFields();
                    loadActivitiesData();
                    populateActivityRows();
                }
            } catch (DataOperationException e) {
                LOGGER.log(Level.SEVERE, "Error cargando datos para reporte parcial", e);
                errorMessage = "Error al cargar los datos: " + e.getMessage();
            }
        }
        return errorMessage;
    }

    private void populateReportFields() {
        educationalExperience = practice.getEducationalExperience();
        guiGeneratePartialReport.setCareer(educationalExperience.getEducationalProgram());
        guiGeneratePartialReport.setNrc(educationalExperience.getNrc());
        guiGeneratePartialReport.setProfessor(formatProfessorName(educationalExperience));
        guiGeneratePartialReport.setPeriod(educationalExperience.getPeriod());
        Project project = student.getAssignedProject();
        String enterprise = (project != null && project.getEnterprise() != null) ? project.getEnterprise().getName() : "N/A";
        String projectName = (project != null) ? project.getNameProject() : "N/A";
        String objective = (project != null) ? project.getGeneralObjective() : "N/A";
        String methodology = (project != null) ? project.getMethodology() : "N/A";
        guiGeneratePartialReport.setStudentName(student.getName() + " " + student.getLastName());
        guiGeneratePartialReport.setEnrollment(student.getEnrollment());
        guiGeneratePartialReport.setOrganization(enterprise);
        guiGeneratePartialReport.setProjectName(projectName);
        guiGeneratePartialReport.setObjectiveAndMethodology(objective, methodology);
    }

    private String formatProfessorName(EducationalExperience experience) {
        String professor = "N/A";
        if (experience.getProfessor() != null) {
            professor = experience.getProfessor().getName() + " " + experience.getProfessor().getLastName();
        }
        return professor;
    }

    private void loadActivitiesData() throws DataOperationException {
        Project project = student.getAssignedProject();
        ActivityDAO activityDAO = new ActivityDAO();
        activities = activityDAO.getActivitiesByProjectId(project.getProjectId());
        weeklyLogs = new ArrayList<>();
        advancesByWeek = new HashMap<>();
        StudentAdvanceDAO advanceDAO = new StudentAdvanceDAO();
        for (Activity activity : activities) {
            List<WeeklyLog> logs = activityDAO.getWeeklyLogsByActivityId(activity.getActivityId());
            weeklyLogs.addAll(logs);
            loadAdvancesForLogs(logs, advanceDAO);
        }
    }

    private void loadAdvancesForLogs(List<WeeklyLog> logs, StudentAdvanceDAO advanceDAO) throws DataOperationException {
        for (WeeklyLog log : logs) {
            List<StudentAdvance> advances = advanceDAO.getAdvancesByStudentAndWeeklyLog(student.getUserId(), log.getWeeklyLogId());
            advancesByWeek.put(log.getWeeklyLogId(), advances);
        }
    }

    private void populateActivityRows() {
        ObservableList<PartialActivityRow> partialActivityRows = FXCollections.observableArrayList();
        for (Activity activity : activities) {
            partialActivityRows.add(createActivityRow(activity));
        }
        guiGeneratePartialReport.setActivities(partialActivityRows);
    }

    private PartialActivityRow createActivityRow(Activity activity) {
        float totalPlanned = 0;
        Map<Integer, Float> plannedByWeek = new HashMap<>();
        Map<Integer, Float> realByWeek = new HashMap<>();
        for (WeeklyLog log : weeklyLogs) {
            if (log.getActivity().getActivityId() == activity.getActivityId()) {
                totalPlanned += log.getPlannedHours();
                plannedByWeek.put(log.getWeek(), (float) log.getPlannedHours());
                List<StudentAdvance> advances = advancesByWeek.getOrDefault(log.getWeeklyLogId(), Collections.emptyList());
                float real = 0;
                for (StudentAdvance advance : advances) {
                    real += advance.getRealizedHours();
                }
                realByWeek.put(log.getWeek(), real);
            }
        }
        String[] plan = new String[9];
        String[] real = new String[9];
        for (int week = 1; week <= guiGeneratePartialReport.getTOTAL_WEEKS(); week++) {
            plan[week] = String.format("%.1f", plannedByWeek.getOrDefault(week, 0f));
            real[week] = String.format("%.1f", realByWeek.getOrDefault(week, 0f));
        }

        return new PartialActivityRow(
                activity.getName(),
                String.format("%.1f hrs", totalPlanned),
                plan[1], real[1], plan[2], real[2],
                plan[3], real[3], plan[4], real[4],
                plan[5], real[5], plan[6], real[6],
                plan[7], real[7], plan[8], real[8]
        );
    }

    private void exportPDF() {
        String results = guiGeneratePartialReport.getResultsObtained();
        if (practice == null || educationalExperience == null) {
            guiGeneratePartialReport.showError("No hay datos suficientes para generar el PDF.");
        } else if (results == null || results.isBlank()) {
            guiGeneratePartialReport.showError("Debe escribir los resultados obtenidos al momento.");
        } else {
            if (guiGeneratePartialReport.validateFields()) {
                DirectoryChooser chooser = new DirectoryChooser();
                chooser.setTitle("Seleccionar carpeta para guardar el PDF");
                File directory = chooser.showDialog(stage);
                if (directory != null) {
                    exportAndPersist(directory, results);
                }
            }
        }
    }

    private void exportAndPersist(File directory, String results) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = String.format("ReporteParcial_%s_%s.pdf", student.getEnrollment(), timestamp);
        String outputPath = new File(directory, fileName).getAbsolutePath();
        Map<String, Object> parameters = buildParameters(results);
        PartialReportGenerator generator = new PartialReportGenerator();
        boolean isGenerated = generator.generate(parameters, outputPath);
        if (!isGenerated) {
            guiGeneratePartialReport.showError("Error al generar el PDF.");
        } else if (savePartialReport()) {
            guiGeneratePartialReport.showSuccess("Reporte exportado exitosamente en:\n" + outputPath);
            guiGeneratePartialReport.closeWindow();
        }
    }

    private Map<String, Object> buildParameters(String results) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("logo", getClass().getResource("/images/reporte_parcial1.png"));
        parameters.put("dateReport", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        parameters.put("career", cleanNull(educationalExperience.getEducationalProgram()));
        parameters.put("nrc", cleanNull(educationalExperience.getNrc()));
        parameters.put("coveredHours", cleanNull(calculateTotalCoveredHours()));
        parameters.put("professor", buildProfessorParameter());
        parameters.put("student", (cleanNull(student.getName()) + " " + cleanNull(student.getLastName())).trim());
        putProjectParameters(parameters);
        parameters.put("reportNumber", String.valueOf(calculateReportNumber()));
        parameters.put("resultsObtaneid", cleanNull(results));
        String currentPeriod = formatPeriod(practice.getPeriod());
        parameters.put("period", cleanNull(currentPeriod));
        parameters.put("reportPeriod", cleanNull(currentPeriod));
        putActivityParameters(parameters);
        parameters.put("logoPartial2", getClass().getResource("/images/partialReport2.png"));
        parameters.put("observations", guiGeneratePartialReport.getTextAreaObservations().getText());
        return parameters;
    }

    private String buildProfessorParameter() {
        String professorName = "";
        if (educationalExperience.getProfessor() != null) {
            professorName = cleanNull(educationalExperience.getProfessor().getName()) + " " + cleanNull(educationalExperience.getProfessor().getLastName());
        }
        return professorName.trim().isEmpty() ? "N/A" : professorName.trim();
    }

    private void putProjectParameters(Map<String, Object> parameters) {
        Project project = student.getAssignedProject();
        parameters.put("enterprise", (project != null && project.getEnterprise() != null) ? cleanNull(project.getEnterprise().getName()) : "N/A");
        parameters.put("project", project != null ? cleanNull(project.getNameProject()) : "N/A");
        parameters.put("generalObjective", project != null ? cleanNull(project.getGeneralObjective()) : "N/A");
        parameters.put("methodology", project != null ? cleanNull(project.getMethodology()) : "N/A");
    }

    private void putActivityParameters(Map<String, Object> parameters) {
        ObservableList<PartialActivityRow> rows = guiGeneratePartialReport.getTableActivities().getItems();
        int rowCount = Math.min(rows.size(), guiGeneratePartialReport.getAMOUNT_OF_ACTIVITIES());
        for (int i = 0; i < rowCount; i++) {
            putFilledActivity(parameters, rows.get(i), i + 1);
        }
        for (int i = rowCount + 1; i <= guiGeneratePartialReport.getAMOUNT_OF_ACTIVITIES(); i++) {
            putEmptyActivity(parameters, i);
        }
    }

    private void putFilledActivity(Map<String, Object> parameters, PartialActivityRow partialActivityRow, int index) {
        parameters.put("activity" + index, cleanNull(partialActivityRow.getActivityName()));
        for (int week = 1; week <= guiGeneratePartialReport.getTOTAL_WEEKS(); week++) {
            parameters.put("activity" + index + "PlanS" + week, cleanNull(getPlanValue(partialActivityRow, week)));
            parameters.put("activity" + index + "RealS" + week, cleanNull(getRealValue(partialActivityRow, week)));
        }
    }

    private void putEmptyActivity(Map<String, Object> parameters, int index) {
        parameters.put("activity" + index, "");
        for (int week = 1; week <= guiGeneratePartialReport.getTOTAL_WEEKS(); week++) {
            parameters.put("activity" + index + "PlanS" + week, "");
            parameters.put("activity" + index + "RealS" + week, "");
        }
    }

    private String cleanNull(String value) {
        return (value == null || value.trim().equalsIgnoreCase("null")) ? "" : value;
    }

    private String getPlanValue(PartialActivityRow row, int week) {
        switch (week) {
            case 1 -> {
                return row.getWeek1Plan();
            }
            case 2 -> {
                return row.getWeek2Plan();
            }
            case 3 -> {
                return row.getWeek3Plan();
            }
            case 4 -> {
                return row.getWeek4Plan();
            }
            case 5 -> {
                return row.getWeek5Plan();
            }
            case 6 -> {
                return row.getWeek6Plan();
            }
            case 7 -> {
                return row.getWeek7Plan();
            }
            case 8 -> {
                return row.getWeek8Plan();
            }
            default -> {
                return "0";
            }
        }
    }
    private String getRealValue(PartialActivityRow row, int week) {
        switch (week) {
            case 1 -> {
                return row.getWeek1Real();
            }
            case 2 -> {
                return row.getWeek2Real();
            }
            case 3 -> {
                return row.getWeek3Real();
            }
            case 4 -> {
                return row.getWeek4Real();
            }
            case 5 -> {
                return row.getWeek5Real();
            }
            case 6 -> {
                return row.getWeek6Real();
            }
            case 7 -> {
                return row.getWeek7Real();
            }
            case 8 -> {
                return row.getWeek8Real();
            }
            default -> {
                return "0";
            }
        }
    }

    private boolean savePartialReport() {
        String results = guiGeneratePartialReport.getResultsObtained();
        String validationError = validateReportData(results);
        boolean saved = false;
        if (validationError != null) {
            guiGeneratePartialReport.showError(validationError);
        } else {
            saved = persistPartialReport(results);
        }
        return saved;
    }

    private String validateReportData(String results) {
        String errorMessage = null;
        if (student == null || educationalExperience == null) {
            errorMessage = "Faltan datos del estudiante o de la experiencia educativa.";
        } else if (results == null || results.trim().isEmpty()) {
            errorMessage = "Debe escribir los resultados obtenidos antes de guardar.";
        }
        return errorMessage;
    }

    private boolean persistPartialReport(String results) {
        String errorMessage = null;
        boolean saved = false;
        try {
            Report report = new Report(0, "PARCIAL", new Date(), "", results, student, educationalExperience.getNrc());
            report.setActivityProgressList(buildProgressList());
            if (reportDAO.createPartialReport(report)) {
                LOGGER.log(Level.INFO, "Reporte guardado correctamente.");
                saved = true;
            } else {
                errorMessage = "No se pudo guardar el reporte.";
            }
        } catch (DataOperationException e) {
            LOGGER.log(Level.SEVERE, "Error al guardar reporte parcial", e);
            errorMessage = "Error al guardar el reporte: " + e.getMessage();
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Error de formato en las horas de la tabla", e);
            errorMessage = "Error: Asegúrese de que las horas en la tabla sean números válidos.";
        }
        if (errorMessage != null) {
            guiGeneratePartialReport.showError(errorMessage);
        }
        return saved;
    }

    private List<ReportActivityProgress> buildProgressList() {
        ObservableList<PartialActivityRow> partialActivityRows = guiGeneratePartialReport.getTableActivities().getItems();
        List<Activity> activityList = activities;
        if (partialActivityRows.size() != activityList.size()) {
            LOGGER.warning("Número de filas en tabla no coincide con actividades reales.");
        }
        List<ReportActivityProgress> progressList = new ArrayList<>();
        int limit = Math.min(partialActivityRows.size(), activityList.size());
        for (int i = 0; i < limit; i++) {
            progressList.add(calculateActivityProgress(partialActivityRows.get(i), activityList.get(i)));
        }
        return progressList;
    }

    private ReportActivityProgress calculateActivityProgress(PartialActivityRow partialActivityRow, Activity activity) {
        float totalPlanned = 0f;
        float totalReal = 0f;
        List<WeeklyLog> weeklyLogs = new ArrayList<>();
        for (int week = 1; week <= guiGeneratePartialReport.getTOTAL_WEEKS(); week++) {
            float planned = Float.parseFloat(getPlanValue(partialActivityRow, week));
            float real = Float.parseFloat(getRealValue(partialActivityRow, week));
            totalPlanned += planned;
            totalReal += real;
            if (planned > 0 || real > 0) {
                WeeklyLog weeklyLog = new WeeklyLog();
                weeklyLog.setWeek(week);
                weeklyLog.setPlannedHours((int) planned);
                weeklyLog.setWorkedHours((int) real);
                weeklyLogs.add(weeklyLog);
            }
        }
        float progressPercentage = (totalPlanned > 0) ? (totalReal / totalPlanned) * 100f : 0f;
        return new ReportActivityProgress(progressPercentage, "", activity, weeklyLogs);
    }

    private String calculateTotalCoveredHours() {
        float totalCovered = 0f;
        ObservableList<PartialActivityRow> rows = guiGeneratePartialReport.getTableActivities().getItems();
        for (PartialActivityRow partialActivityRow : rows) {
            for (int week = 1; week <= guiGeneratePartialReport.getTOTAL_WEEKS(); week++) {
                try {
                    totalCovered += Float.parseFloat(getRealValue(partialActivityRow, week));
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.WARNING, "Valor no numérico ignorado en la suma de horas: " + getRealValue(partialActivityRow, week));
                }
            }
        }
        return String.format("%.1f", totalCovered);
    }

    private String formatPeriod(String rawPeriod) {
        String formattedPeriod = "N/A";
        if (rawPeriod != null && rawPeriod.length() >= 7) {
            String year = rawPeriod.substring(0, 4);
            String month = rawPeriod.substring(5, 7);
            if ("08".equals(month)) {
                formattedPeriod = "Ago-Ene " + year;
            } else if ("02".equals(month)) {
                formattedPeriod = "Feb-Jul " + year;
            } else {
                formattedPeriod = rawPeriod;
            }
        }
        return formattedPeriod;
    }

    private int calculateReportNumber() {
        int reportNumber = 1;
        try {
            int previousReports = reportDAO.countReportsByTypeAndStudent("PARCIAL", student.getUserId());
            reportNumber = previousReports + 1;
        } catch (DataOperationException e) {
            LOGGER.log(Level.WARNING, "Error al obtener el conteo  de los reportes previos. Se asignará 1", e);
        }
        return reportNumber;
    }
}