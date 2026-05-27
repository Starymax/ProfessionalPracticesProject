package mx.fei.gui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import mx.fei.gui.views.GUIGenerateMonthlyReport;
import mx.fei.logic.dao.ActivityDAO;
import mx.fei.logic.dao.PracticeDAO;
import mx.fei.logic.dao.ReportDAO;
import mx.fei.logic.dao.StudentAdvanceDAO;
import mx.fei.logic.dto.Student;
import mx.fei.logic.dto.Report;
import mx.fei.logic.dto.ReportType;
import mx.fei.logic.dto.Activity;
import mx.fei.logic.dto.StudentAdvance;
import mx.fei.logic.dto.ReportActivityProgress;
import mx.fei.logic.dto.ActivityRow;
import mx.fei.logic.dto.WeeklyLog;
import mx.fei.logic.dto.Practice;
import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.dao.EducationalExperienceDAO;
import mx.fei.logic.exceptions.DataOperationException;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import mx.fei.gui.utils.MonthlyReportGenerator;
import mx.fei.gui.utils.GUIUtils;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerGenerateMonthlyReport {

    private static final Logger logger = Logger.getLogger(ControllerGenerateMonthlyReport.class.getName());
    private final GUIGenerateMonthlyReport monthlyReportView;
    private final Stage stage;
    private final ReportDAO reportDAO;
    private final ActivityDAO activityDAO;
    private final StudentAdvanceDAO studentAdvanceDAO;
    private final PracticeDAO practiceDAO;
    private final Student student;
    private Report currentReport;

    public ControllerGenerateMonthlyReport(GUIGenerateMonthlyReport monthlyReportView, Stage stage, Student student) {
        this.monthlyReportView = monthlyReportView;
        this.stage = stage;
        this.reportDAO = new ReportDAO();
        this.activityDAO = new ActivityDAO();
        this.studentAdvanceDAO = new StudentAdvanceDAO();
        this.practiceDAO = new PracticeDAO();
        this.student = student;
        initialize();
    }

    private void initialize() {
        if (student == null) {
            monthlyReportView.showError("No hay estudiante seleccionado para generar el reporte.");
        } else {
            monthlyReportView.setStudentInfo(student.getName() + " " + student.getLastName(), student.getEnrollment(), student.getEmail());
            if (student.getAssignedProject() != null) {
                String projectName = student.getAssignedProject().getNameProject();
                String enterpriseName = student.getAssignedProject().getEnterprise() != null ? student.getAssignedProject().getEnterprise().getName() : "-";
                String professorName = student.getAssignedProject().getProjectManager() != null ? student.getAssignedProject().getProjectManager().getName() : "-";
                monthlyReportView.setProjectInfo(projectName, enterpriseName, professorName);
            } else {
                monthlyReportView.setProjectInfo("No asignado", "-", "-");
                monthlyReportView.showError("El estudiante no tiene proyecto asignado.");
            }
        }
        loadStudentActivitiesWithProgress();
    }

    public void handleMonthlyReportButtons(ActionEvent event) {
        Button sourceButton = (Button) event.getSource();
        String buttonId = sourceButton.getId();
        switch (buttonId) {
            case "buttonExportPdf" -> handleExportPDF();
            case "buttonCancel" -> handleCancel();
        }
    }

    private void loadStudentActivitiesWithProgress() {
        try {
            if (student.getAssignedProject() == null) {
                monthlyReportView.setActivities(FXCollections.observableArrayList());
            } else {
                List<Activity> activities = activityDAO.getActivitiesByProjectId(student.getAssignedProject().getProjectId());
                List<StudentAdvance> advances = studentAdvanceDAO.getAdvancesByStudentId(student.getUserId());
                Map<Integer, Float> hoursWorkedByLog = getHoursWorkedByLog(advances);

                List<ReportActivityProgress> reportActivityProgressList = new ArrayList<>();
                ObservableList<ActivityRow> activityRows = FXCollections.observableArrayList();
                for (Activity activity : activities) {
                    List<WeeklyLog> weeklyLogs = activityDAO.getWeeklyLogsByActivityId(activity.getActivityId());
                    ReportActivityProgress activityProgress = calculateActivityProgress(activity, weeklyLogs, hoursWorkedByLog);
                    if (activityProgress != null) {
                        reportActivityProgressList.add(activityProgress);
                        activityRows.add(createActivityRow(activity.getName(), activityProgress.getProgressPercentage(), activityProgress));
                    }
                }
                currentReport = buildReport(reportActivityProgressList);
                monthlyReportView.setActivities(activityRows);
            }
        } catch (DataOperationException e) {
            logger.log(Level.SEVERE, "Error al procesar y cargar las actividades con progreso del estudiante", e);
            monthlyReportView.showError("Error al cargar las actividades con progreso");
        }
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

    private ReportActivityProgress calculateActivityProgress(Activity activity, List<WeeklyLog> weeklyLogs, Map<Integer, Float> realizedHours) {
        float totalPlanned = 0f;
        float totalWorked = 0f;
        boolean validAdvances = false;
        ReportActivityProgress reportActivityProgress = null;
        for (WeeklyLog weeklyLog : weeklyLogs) {
            totalPlanned += weeklyLog.getPlannedHours();
            float workedHours = realizedHours.getOrDefault(weeklyLog.getWeeklyLogId(), 0f);
            if (workedHours > 0) {
                validAdvances = true;
            }
            weeklyLog.setWorkedHours((int) workedHours);
            totalWorked += workedHours;
        }
        if (validAdvances) {
            float progressPercentage = totalPlanned > 0 ? (totalWorked / totalPlanned) * 100f : 100f;
            reportActivityProgress = new ReportActivityProgress(progressPercentage, "", activity, weeklyLogs);
        }
        return reportActivityProgress;
    }

    private ActivityRow createActivityRow(String activityName, float percentage, ReportActivityProgress progress) {
        float totalWorked = 0f;
        if (progress.getWeeklyProgressList() != null) {
            for (WeeklyLog log : progress.getWeeklyProgressList()) {
                totalWorked += log.getWorkedHours();
            }
        }
        String percentageText = String.format("%.1f%%", percentage);
        String hoursText = String.format("%.1f horas", totalWorked);
        return new ActivityRow(activityName, percentageText, hoursText, "", progress);
    }

    private Report buildReport(List<ReportActivityProgress> activityProgressList) {
        ReportType reportType = ReportType.MONTHLY_REPORT;
        Report report = new Report(0, reportType.getReportType(), new Date(), "", "", student, "");
        try {
            report.setReportNumber(reportDAO.countReportsByTypeAndStudent(reportType.getReportType(), student.getUserId()) + 1);
        } catch (DataOperationException e) {
            logger.log(Level.WARNING, "No se pudo calcular el número de reporte");
            report.setReportNumber(1);
        }
        report.setMonth(getCurrentMonthName());
        report.setNrc(getPracticeNrc());
        report.setActivityProgressList(activityProgressList);
        float totalWorked = 0f;
        for (ReportActivityProgress activityProgress : activityProgressList) {
            if (activityProgress.getWeeklyProgressList() != null) {
                for (WeeklyLog weeklyLog : activityProgress.getWeeklyProgressList()) {
                    totalWorked += weeklyLog.getWorkedHours();
                }
            }
        }
        report.setWorkedHours(totalWorked);
        report.setAccumulatedHours(totalWorked);
        return report;
    }

    private String getPracticeNrc() {
        String practiceNrc = "";
        if (student != null && student.getEnrollment() != null && !student.getEnrollment().isBlank()) {
            try {
                Practice practice = practiceDAO.getPracticeByEnrollment(student.getEnrollment());
                if (practice != null && practice.getEducationalExperience() != null) {
                    practiceNrc = practice.getEducationalExperience().getNrc();
                }
            } catch (DataOperationException e) {
                logger.log(Level.WARNING, "No se pudo obtener la práctica del estudiante para determinar el NRC");
            }
        }
        return practiceNrc;
    }

    private String getCurrentMonthName() {
        LocalDate today = LocalDate.now();
        return today.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES")) + " " + today.getYear();
    }

    private void syncActivityObservationsFromTable() {
        if (currentReport != null && currentReport.getActivityProgressList() != null) {
            ObservableList<ActivityRow> activityRows = monthlyReportView.getActivityRows();
            if (activityRows != null) {
                for (ActivityRow activityRow : activityRows) {
                    if (activityRow.getActivityProgress() != null) {
                        activityRow.getActivityProgress().setObservations(activityRow.getObservations() != null ? activityRow.getObservations() : "");
                    }
                }
            }
        }
    }

    private void handleCancel() {
        monthlyReportView.closeWindow();
    }

    private boolean validateObservations() {
        boolean validated = true;
        List<String> errors = new ArrayList<>();
        if (currentReport != null && currentReport.getActivityProgressList() != null) {
            int activityIndex = 1;
            for (ReportActivityProgress activityProgress : currentReport.getActivityProgressList()) {
                GUIUtils.validateShortText(activityProgress.getObservations(), "Observaciones de la actividad " + activityIndex, errors);
                activityIndex++;
            }
        }
        if (!errors.isEmpty()) {
            GUIUtils.showErrors(errors);
            validated = false;
        }
        return validated;
    }

    public void handleExportPDF() {
        if (currentReport == null || currentReport.getActivityProgressList() == null || currentReport.getActivityProgressList().isEmpty()) {
            monthlyReportView.showError("No hay actividades con avance para exportar.");
        } else {
            try {
                monthlyReportView.commitTableEdits();
                syncActivityObservationsFromTable();
                if (validateObservations()) {
                    if (currentReport.getReportId() == 0) {
                        boolean created = reportDAO.createMonthlyReport(currentReport);
                        if (!created) {
                            monthlyReportView.showError("No se pudo guardar el reporte en la base de datos antes de exportar.");
                        }
                    } else {
                        reportDAO.setObservations(currentReport.getReportId(), currentReport.getObservations());
                        reportDAO.updateActivityObservations(currentReport.getReportId(), currentReport.getActivityProgressList());
                    }
                    DirectoryChooser directoryChooser = new DirectoryChooser();
                    directoryChooser.setTitle("Selecciona dónde guardar el reporte");
                    File selectedDirectory = directoryChooser.showDialog(stage);
                    if (selectedDirectory != null) {
                        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(System.currentTimeMillis());
                        String fileName = String.format("ReporteMensual_%s_%s.pdf", student.getEnrollment(), timestamp);
                        String outputPath = new File(selectedDirectory, fileName).getAbsolutePath();
                        MonthlyReportGenerator monthlyReportGenerator = new MonthlyReportGenerator();
                        boolean generated = monthlyReportGenerator.generate(buildParameters(currentReport), outputPath);
                        if (generated) {
                            monthlyReportView.showSuccess("Reporte exportado a PDF exitosamente en:\n" + outputPath);
                        } else {
                            monthlyReportView.showError("Error al generar el PDF del reporte.");
                        }
                    }
                }
            } catch (DataOperationException e) {
                monthlyReportView.showError("Error al exportar el reporte");
            }
        }
    }

    private Map<String, Object> buildParameters(Report report) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("Period", getPeriod(report));
        parameters.put("NoReport", report.getReportNumber());
        parameters.put("Month", report.getMonth() != null ? report.getMonth() : "");
        parameters.put("ReportedHours", String.valueOf(report.getWorkedHours()));
        parameters.put("AcumulatedHours", String.valueOf(report.getAccumulatedHours()));
        parameters.put("AlumnName", getStudentName(report));
        parameters.put("ResponsibleName", getResponsibleName(report));
        parameters.put("ProfessorName", getProfessorName(report));
        URL logoResource = getClass().getResource("/images/MonthlyReport.png");
        if (logoResource == null) {
            logger.log(Level.WARNING, "No se encontró el recurso del logo en classpath: /images/MonthlyReport.png");
        }
        parameters.put("Logo", logoResource);
        fillWeeklyLogs(parameters, report);
        return parameters;
    }

    private String getPeriod(Report report) {
        String period = "";
        try {
            EducationalExperienceDAO educationalExperienceDAO = new EducationalExperienceDAO();
            EducationalExperience educationalExperience = educationalExperienceDAO.getEducationalExperienceByNrc(report.getNrc());
            if (educationalExperience != null) {
                period = educationalExperience.getPeriod();
            }
        } catch (DataOperationException e) {
            monthlyReportView.showError("Error al obtener los datos del periodo");
        }
        return period;
    }

    private String getProfessorName(Report report) {
        String professorName = "N/A";
        try {
            EducationalExperienceDAO educationalExperienceDAO = new EducationalExperienceDAO();
            EducationalExperience educationalExperience = educationalExperienceDAO.getEducationalExperienceByNrc(report.getNrc());
            if (educationalExperience != null && educationalExperience.getProfessor() != null) {
                professorName = educationalExperience.getProfessor().getName();
            }
        } catch (DataOperationException e) {
            monthlyReportView.showError("Error al obtener los datos del profesor");
        }
        return professorName;
    }

    private String getStudentName(Report report) {
        String fullStudentName = "N/A";
        if (report.getStudent() != null) {
            Student student = report.getStudent();
            fullStudentName = student.getName() + " " + student.getLastName();
        }
        return fullStudentName;
    }

    private String getResponsibleName(Report report) {
        String responsibleName = "N/A";
        if (report.getStudent() != null) {
            Project project = report.getStudent().getAssignedProject();
            if (project != null && project.getProjectManager() != null) {
                responsibleName = project.getProjectManager().getName();
            }
        }
        return responsibleName;
    }

    private void fillWeeklyLogs(Map<String, Object> parameters, Report report) {
        List<WeeklyLog> weeklyLogs = new ArrayList<>();
        Map<WeeklyLog, String> observationsMap = new HashMap<>();
        if (report.getActivityProgressList() != null) {
            for (ReportActivityProgress progress : report.getActivityProgressList()) {
                String observations = progress.getObservations() != null ? progress.getObservations() : "";
                if (progress.getWeeklyProgressList() != null) {
                    for (WeeklyLog weeklyLog : progress.getWeeklyProgressList()) {
                        weeklyLogs.add(weeklyLog);
                        observationsMap.put(weeklyLog, observations);
                    }
                }
            }
        }
        for (int i = 0; i < 7; i++) {
            String index = String.valueOf(i + 1);
            if (i < weeklyLogs.size()) {
                WeeklyLog weeklyLog = weeklyLogs.get(i);
                parameters.put("Period" + index, "Semana " + weeklyLog.getWeek());
                parameters.put("Activity" + index, weeklyLog.getActivity() != null ? weeklyLog.getActivity().getName() : "");
                parameters.put("Observaciones" + index, observationsMap.getOrDefault(weeklyLog, ""));
            } else {
                parameters.put("Period" + index, "");
                parameters.put("Activity" + index, "");
                parameters.put("Observaciones" + index, "");
            }
        }
}
}