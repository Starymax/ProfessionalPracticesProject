package mx.fei.gui.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import mx.fei.gui.utils.FinalReportGenerator;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.gui.views.GUIGenerateFinalReport;
import mx.fei.logic.dao.ActivityDAO;
import mx.fei.logic.dao.ReportDAO;
import mx.fei.logic.dao.StudentAdvanceDAO;
import mx.fei.logic.dto.Activity;
import mx.fei.logic.dto.FinalReport;
import mx.fei.logic.dto.FinalReportRow;
import mx.fei.logic.dto.Practice;
import mx.fei.logic.dto.Report;
import mx.fei.logic.dto.WeeklyLog;
import mx.fei.logic.dto.ReportType;
import mx.fei.logic.dto.Student;
import mx.fei.logic.dto.StudentAdvance;
import mx.fei.logic.exceptions.DataOperationException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerGenerateFinalReport {

    private static final Logger logger = Logger.getLogger(ControllerGenerateFinalReport.class.getName());
    private final GUIGenerateFinalReport finalReportView;
    private final Stage stage;
    private final ReportDAO reportDAO;
    private final StudentAdvanceDAO studentAdvanceDAO;
    private final ActivityDAO activityDAO;
    private final Student student;
    private final Practice practice;
    private Report currentReport;
    private FinalReport finalReportData;

    public ControllerGenerateFinalReport(GUIGenerateFinalReport finalReportView, Stage stage, Student student, Practice practice) {
        this.finalReportView = finalReportView;
        this.stage = stage;
        this.reportDAO = new ReportDAO();
        this.studentAdvanceDAO = new StudentAdvanceDAO();
        this.activityDAO = new ActivityDAO();
        this.student = student;
        this.practice = practice;
        initialize();
    }

    private void initialize() {
        if (student == null) {
            finalReportView.showError("No hay estudiante seleccionado para generar el reporte final.");
        } else {
            finalReportView.setStudentName(student.getName() + " " + student.getLastName());
            finalReportView.setStudentEnrollment(student.getEnrollment());
            finalReportView.setStudentEmail(student.getEmail());
            finalReportView.setStudentNrc(getPracticeNrc());
            finalReportView.setPeriod(getPracticePeriod());
            if (student.getAssignedProject() != null) {
                String projectName = student.getAssignedProject().getNameProject();
                String enterpriseName = student.getAssignedProject().getEnterprise() != null ? student.getAssignedProject().getEnterprise().getName() : "-";
                String professorName = getProfessorName();
                finalReportView.setProjectInfo(projectName, enterpriseName, professorName);
                finalReportView.setEducationalProgram(getEducationalProgram());
            } else {
                finalReportView.setProjectInfo("No asignado", "-", "-");
                finalReportView.showError("El estudiante no tiene proyecto asignado.");
            }
        }
        loadFinalReportData();
    }

    public void handleFinalReportButtons(ActionEvent event) {
        Button sourceButton = (Button) event.getSource();
        String buttonId = sourceButton.getId();
        switch (buttonId) {
            case "buttonExportPdf" -> handleExportPDF();
            case "buttonCancel" -> handleCancel();
        }
    }

    private void loadFinalReportData() {
        finalReportData = buildFinalReportData();
        finalReportView.setGeneralObjectives(finalReportData.getGeneralObjectives());
        finalReportView.setMethodology(finalReportData.getMethodology());
        finalReportView.setObservations(finalReportData.getObservations());
        finalReportView.setRows(FXCollections.observableArrayList(finalReportData.getFinalReportRows()));
        currentReport = buildReport();
    }

    private FinalReport buildFinalReportData() {
        FinalReport finalReport = new FinalReport();
        finalReport.setCareer(getEducationalProgram());
        finalReport.setNrc(getPracticeNrc());
        finalReport.setProfessor(getProfessorName());
        finalReport.setPeriod(getPracticePeriod());
        finalReport.setStudent(getStudentName());
        finalReport.setEnterprise(getEnterpriseName());
        finalReport.setProject(getProjectName());
        finalReport.setHoursRealized(String.valueOf(getRealizedHours()));
        finalReport.setReportDate(getCurrentReportDate());
        finalReport.setGeneralObjectives(getProjectGeneralObjectives());
        finalReport.setMethodology(getProjectMethodology());
        finalReport.setObservations("");
        finalReport.setFinalReportRows(buildFinalReportRows());
        return finalReport;
    }

    private List<FinalReportRow> buildFinalReportRows() {
        List<FinalReportRow> rows = new ArrayList<>();
        boolean hasProject = student != null && student.getAssignedProject() != null;
        if (hasProject) {
            try {
                List<Activity> activities = activityDAO.getActivitiesByProjectId(student.getAssignedProject().getProjectId());
                List<StudentAdvance> advances = studentAdvanceDAO.getAdvancesByStudentId(student.getUserId());
                Map<Integer, Float> workedHoursByLog = getHoursWorkedByLog(advances);

                for (Activity activity : activities) {
                    List<WeeklyLog> weeklyLogs = activityDAO.getWeeklyLogsByActivityId(activity.getActivityId());
                    float totalPlanned = 0f;
                    float totalWorked = 0f;
                    for (WeeklyLog weeklyLog : weeklyLogs) {
                        totalPlanned += weeklyLog.getPlannedHours();
                        totalWorked += workedHoursByLog.getOrDefault(weeklyLog.getWeeklyLogId(), 0f);
                    }
                    String advanceText = totalPlanned > 0 ? String.format("%.1f%%", (totalWorked / totalPlanned) * 100f) : "";
                    String observationText = ""; // El alumno debe introducir sus propias observaciones de actividad
                    rows.add(new FinalReportRow(activity.getName(), advanceText, observationText, "", "", ""));
                }
            } catch (DataOperationException e) {
                logger.log(Level.WARNING, "No se pudo cargar la información de actividades para el reporte final", e);
            }
        }

        fillBlankFinalReportRows(rows);
        return rows;
    }

    private Map<Integer, Float> getHoursWorkedByLog(List<StudentAdvance> advances) {
        Map<Integer, Float> hoursByLog = new HashMap<>();
        for (StudentAdvance advance : advances) {
            if (advance.getWeeklyLog() != null) {
                int weeklyLogId = advance.getWeeklyLog().getWeeklyLogId();
                float accumulatedHours = hoursByLog.getOrDefault(weeklyLogId, 0f);
                hoursByLog.put(weeklyLogId, accumulatedHours + advance.getRealizedHours());
            }
        }
        return hoursByLog;
    }

    private void fillBlankFinalReportRows(List<FinalReportRow> rows) {
        while (rows.size() < 9) {
            rows.add(new FinalReportRow("", "", "", "", "", ""));
        }
    }

    private Report buildReport() {
        Report report = new Report(0, ReportType.FINAL_REPORT.getReportType(), new Date(), "", "", student, getPracticeNrc());
        report.setReportNumber(1);
        report.setWorkedHours(getRealizedHours());
        report.setAccumulatedHours(report.getWorkedHours());
        report.setObservations("");
        report.setActivityProgressList(new ArrayList<>());
        return report;
    }

    private void syncFinalReportDataFromGui() {
        finalReportData.setGeneralObjectives(finalReportView.getGeneralObjectives());
        finalReportData.setMethodology(finalReportView.getMethodology());
        finalReportData.setObservations(finalReportView.getObservations());
        finalReportData.setFinalReportRows(new ArrayList<>(finalReportView.getFinalReportRows()));
        if (currentReport != null) {
            currentReport.setObservations(finalReportData.getObservations());
        }
        try {
            currentReport.setWorkedHours(getRealizedHours());
            currentReport.setAccumulatedHours(currentReport.getWorkedHours());
        } catch (RuntimeException e) {
            currentReport.setWorkedHours(0f);
            currentReport.setAccumulatedHours(0f);
        }
    }

    private void handleExportPDF() {
        try {
            finalReportView.commitTableEdits();
            syncFinalReportDataFromGui();
        } catch (RuntimeException e) {
            logger.log(Level.WARNING, "Error al sincronizar los datos del reporte final", e);
            finalReportView.showError("Error en la tabla de actividades. Verifica los datos y vuelve a intentarlo.");
            return;
        }
        if (validateFinalReport()) {
            try {
                boolean canExport = true;
                if (currentReport.getReportId() == 0) {
                    boolean created = reportDAO.createFinalReport(currentReport);
                    if (!created) {
                        finalReportView.showError("No se pudo guardar el reporte final.");
                        canExport = false;
                    }
                }
                if (canExport) {
                    DirectoryChooser directoryChooser = new DirectoryChooser();
                    directoryChooser.setTitle("Selecciona dónde guardar el reporte final");
                    File selectedDirectory = directoryChooser.showDialog(stage);
                    if (selectedDirectory != null) {
                        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(System.currentTimeMillis());
                        String fileName = String.format("ReporteFinal_%s_%s.pdf", student.getEnrollment(), timestamp);
                        String outputPath = new File(selectedDirectory, fileName).getAbsolutePath();
                        FinalReportGenerator generator = new FinalReportGenerator();
                        boolean generated = generator.generate(buildParameters(currentReport), outputPath);
                        if (generated) {
                            finalReportView.showSuccess("Reporte final exportado a PDF exitosamente en:\n" + outputPath);
                        } else {
                            finalReportView.showError("Error al generar el PDF del reporte final.");
                        }
                    }
                }
            } catch (DataOperationException e) {
                logger.log(Level.SEVERE, "Error al exportar el reporte final", e);
                finalReportView.showError("Error al exportar el reporte final");
            }
        }
    }

    private Map<String, Object> buildParameters(Report report) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("Career", finalReportData.getCareer());
        parameters.put("NRC", finalReportData.getNrc() != null ? finalReportData.getNrc() : "");
        parameters.put("Professor", finalReportData.getProfessor());
        parameters.put("Period", finalReportData.getPeriod());
        parameters.put("Student", finalReportData.getStudent());
        parameters.put("Enterprise", finalReportData.getEnterprise());
        parameters.put("Project", finalReportData.getProject());
        parameters.put("HoursRealized", finalReportData.getHoursRealized());
        parameters.put("ReportDate", finalReportData.getReportDate());
        parameters.put("GeneralObjectives", finalReportData.getGeneralObjectives());
        parameters.put("Methodology", finalReportData.getMethodology());
        parameters.put("Observations", finalReportData.getObservations());
        parameters.put("Background1", getResourceUrl("/images/FinalReport1.png"));
        parameters.put("Background2", getResourceUrl("/images/FinalReport2.png"));
        fillReportRows(parameters, finalReportData.getFinalReportRows());
        return parameters;
    }

    private Object getResourceUrl(String resourcePath) {
        var resource = getClass().getResource(resourcePath);
        if (resource == null) {
            logger.log(Level.WARNING, "No se encontró el recurso: " + resourcePath);
        }
        return resource;
    }

    private void fillReportRows(Map<String, Object> parameters, List<FinalReportRow> rows) {
        for (int i = 0; i < 9; i++) {
            String index = String.valueOf(i + 1);
            FinalReportRow row = i < rows.size() ? rows.get(i) : new FinalReportRow("", "", "", "", "", "");
            parameters.put("Activity" + index, row.getActivity() != null ? row.getActivity() : "");
            parameters.put("Advance" + index, row.getAdvance() != null ? row.getAdvance() : "");
            parameters.put("Observation" + index, row.getObservation() != null ? row.getObservation() : "");
            parameters.put("Product" + index, row.getProduct() != null ? row.getProduct() : "");
            parameters.put("Advancep" + index, row.getAdvancep() != null ? row.getAdvancep() : "");
            parameters.put("Observationp" + index, row.getObservationp() != null ? row.getObservationp() : "");
        }
    }

    private boolean validateFinalReport() {
        boolean validated = true;
        List<String> errors = new ArrayList<>();
        GUIUtils.validateLongText(finalReportData.getGeneralObjectives(), "Objetivos generales", errors);
        GUIUtils.validateLongText(finalReportData.getMethodology(), "Metodología", errors);
        int rowIndex = 1;
        for (FinalReportRow row : finalReportData.getFinalReportRows()) {
            validateOptionalShortText(row.getActivity(), "Actividad " + rowIndex, errors);
            validateOptionalShortText(row.getAdvance(), "Avance " + rowIndex, errors);
            validateOptionalShortText(row.getObservation(), "Observación " + rowIndex, errors);
            validateOptionalShortText(row.getProduct(), "Producto " + rowIndex, errors);
            validateOptionalShortText(row.getAdvancep(), "Avance p. " + rowIndex, errors);
            validateOptionalShortText(row.getObservationp(), "Observación p. " + rowIndex, errors);
            rowIndex++;
        }
        if (!errors.isEmpty()) {
            GUIUtils.showErrors(errors);
            validated = false;
        }
        return validated;
    }

    private void validateOptionalShortText(String value, String fieldName, List<String> errors) {
        if (value != null && !value.isBlank()) {
            GUIUtils.validateShortText(value, fieldName, errors);
        }
    }

    private void handleCancel() {
        finalReportView.closeWindow();
    }

    private String getPracticeNrc() {
        String practiceNrc = "";
        if (practice != null && practice.getEducationalExperience() != null) {
            String nrc = practice.getEducationalExperience().getNrc();
            if (nrc != null) {
                practiceNrc = nrc;
            }
        }
        return practiceNrc;
    }

    private String getPracticePeriod() {
        String period = "";
        if (practice != null) {
            if (practice.getPeriod() != null && !practice.getPeriod().isBlank()) {
                period = practice.getPeriod();
            } else if (practice.getEducationalExperience() != null && practice.getEducationalExperience().getPeriod() != null) {
                period = practice.getEducationalExperience().getPeriod();
            }
        }
        return period;
    }

    private String getProfessorName() {
        String professorName = "N/A";
        if (practice != null && practice.getEducationalExperience() != null && practice.getEducationalExperience().getProfessor() != null) {
            String name = practice.getEducationalExperience().getProfessor().getName();
            if (name != null) {
                professorName = name;
            }
        }
        return professorName;
    }

    private String getStudentName() {
        String studentName = "N/A";
        if (student != null) {
            studentName = student.getName() + " " + student.getLastName();
        }
        return studentName;
    }

    private String getEnterpriseName() {
        String enterpriseName = "N/A";
        if (student != null && student.getAssignedProject() != null && student.getAssignedProject().getEnterprise() != null) {
            String projectEnterpriseName = student.getAssignedProject().getEnterprise().getName();
            if (projectEnterpriseName != null) {
                enterpriseName = projectEnterpriseName;
            }
        }
        return enterpriseName;
    }

    private String getProjectName() {
        String projectName = "N/A";
        if (student != null && student.getAssignedProject() != null) {
            String name = student.getAssignedProject().getNameProject();
            if (name != null) {
                projectName = name;
            }
        }
        return projectName;
    }

    private String getProjectGeneralObjectives() {
        String generalObjectives = "";
        if (student != null && student.getAssignedProject() != null) {
            String projectObjectives = student.getAssignedProject().getGeneralObjective();
            if (projectObjectives != null) {
                generalObjectives = projectObjectives;
            }
        }
        return generalObjectives;
    }

    private String getProjectMethodology() {
        String methodology = "";
        if (student != null && student.getAssignedProject() != null) {
            String projectMethodology = student.getAssignedProject().getMethodology();
            if (projectMethodology != null) {
                methodology = projectMethodology;
            }
        }
        return methodology;
    }

    private String getEducationalProgram() {
        String educationalProgram = "";
        if (practice != null && practice.getEducationalExperience() != null) {
            String program = practice.getEducationalExperience().getEducationalProgram();
            if (program != null) {
                educationalProgram = program;
            }
        }
        return educationalProgram;
    }

    private float getRealizedHours() {
        float totalHours = 0f;
        if (student != null) {
            try {
                List<StudentAdvance> advances = studentAdvanceDAO.getAdvancesByStudentId(student.getUserId());
                for (StudentAdvance advance : advances) {
                    totalHours += advance.getRealizedHours();
                }
            } catch (DataOperationException e) {
                logger.log(Level.WARNING, "No se pudo obtener las horas realizadas del estudiante", e);
            }
        }
        return totalHours;
    }

    private String getCurrentReportDate() {
        return new SimpleDateFormat("dd/MM/yyyy", new Locale("es", "ES")).format(new Date());
    }
}
