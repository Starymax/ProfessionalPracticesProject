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
import mx.fei.logic.dto.Activity;
import mx.fei.logic.dto.Report;
import mx.fei.logic.dto.Student;
import mx.fei.logic.dto.StudentAdvance;
import mx.fei.logic.dto.ReportActivityProgress;
import mx.fei.logic.dto.WeeklyLog;
import mx.fei.logic.dto.Practice;
import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.dao.EducationalExperienceDAO;
import mx.fei.logic.exceptions.DataOperationException;
import mx.fei.gui.utils.MonthlyReportGenerator;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerGenerateMonthlyReport {

    private static final Logger logger = Logger.getLogger(ControllerGenerateMonthlyReport.class.getName());
    private final GUIGenerateMonthlyReport guiGenerateMonthlyReport;
    private final Stage stage;
    private final ReportDAO reportDAO;
    private final ActivityDAO activityDAO;
    private final StudentAdvanceDAO studentAdvanceDAO;
    private final PracticeDAO practiceDAO;
    private final Student student;
    private Report currentReport;

    public ControllerGenerateMonthlyReport(GUIGenerateMonthlyReport guiGenerateMonthlyReport, Stage stage, Student student) {
        this.guiGenerateMonthlyReport = guiGenerateMonthlyReport;
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
            guiGenerateMonthlyReport.showError("No hay estudiante seleccionado para generar el reporte.");
        } else {
        guiGenerateMonthlyReport.setStudentInfo(student.getName() + " " + student.getLastName(), student.getEnrollment(), student.getEmail());
        if (student.getAssignedProject() != null) {
            String projectName = student.getAssignedProject().getNameProject();
            String enterpriseName = student.getAssignedProject().getEnterprise() != null ? student.getAssignedProject().getEnterprise().getName() : "-";
            String professorName = student.getAssignedProject().getProjectManager() != null ? student.getAssignedProject().getProjectManager().getName() : "-";
            guiGenerateMonthlyReport.setProjectInfo(projectName, enterpriseName, professorName);
        } else {
            guiGenerateMonthlyReport.setProjectInfo("No asignado", "-", "-");
            guiGenerateMonthlyReport.showError("El estudiante no tiene proyecto asignado.");
        }
}
        loadStudentActivitiesWithProgress();
    }

    private void loadStudentActivitiesWithProgress() {
        try {
            if (student.getAssignedProject() == null) {
                guiGenerateMonthlyReport.setActivities(FXCollections.observableArrayList());
                return;
            }
            List<Activity> activities = activityDAO.getActivitiesByProjectId(student.getAssignedProject().getProjectId());
            List<StudentAdvance> advances = studentAdvanceDAO.getAdvancesByStudentId(student.getUserId());

            Map<Integer, Float> horasTrabajadasPorLog = obtenerHorasTrabajadasPorLog(advances);

            List<ReportActivityProgress> reportActivityProgressList = new ArrayList<>();
            ObservableList<GUIGenerateMonthlyReport.ActivityRow> activityRows = FXCollections.observableArrayList();
            for (Activity activity : activities) {
                List<WeeklyLog> weeklyLogs = activityDAO.getWeeklyLogsByActivityId(activity.getActivityId());
                ReportActivityProgress activityProgress = calcularProgresoActividad(activity, weeklyLogs, horasTrabajadasPorLog);
                if (activityProgress == null) {
                    continue;
                }
                reportActivityProgressList.add(activityProgress);
                activityRows.add(crearFilaUI(activity.getName(), activityProgress.getProgressPercentage(), activityProgress));
            }
            currentReport = buildReport(reportActivityProgressList);
            guiGenerateMonthlyReport.setActivities(activityRows);
            guiGenerateMonthlyReport.setObservations(currentReport.getObservations());

        } catch (DataOperationException e) {
            logger.log(Level.SEVERE, "Error crítico al procesar y cargar las actividades con progreso del estudiante", e);
            guiGenerateMonthlyReport.showError("Error al cargar las actividades con progreso: " + e.getMessage());
        }
    }

    private Map<Integer, Float> obtenerHorasTrabajadasPorLog(List<StudentAdvance> advances) {
        Map<Integer, Float> horasPorLog = new HashMap<>();
        for (StudentAdvance advance : advances) {
            if (advance.getWeeklyLog() != null) {
                int weeklyLogId = advance.getWeeklyLog().getWeeklyLogId();
                float horasExistentes = horasPorLog.getOrDefault(weeklyLogId, 0f);
                horasPorLog.put(weeklyLogId, horasExistentes + advance.getRealizedHours());
            }
        }
        return horasPorLog;
    }

    private ReportActivityProgress calcularProgresoActividad(Activity activity, List<WeeklyLog> weeklyLogs, Map<Integer, Float> realizedHours) {
        float totalPlanned = 0f;
        float totalWorked = 0f;
        boolean tieneAvancesValidos = false;
        ReportActivityProgress reportActivityProgress = null;
        for (WeeklyLog weeklyLog : weeklyLogs) {
            totalPlanned += weeklyLog.getPlannedHours();
            float workedHours = realizedHours.getOrDefault(weeklyLog.getWeeklyLogId(), 0f);
            if (workedHours > 0) {
                tieneAvancesValidos = true;
            }
            weeklyLog.setWorkedHours((int) workedHours);
            totalWorked += workedHours;
        }
        if (tieneAvancesValidos) {
            float progressPercentage = totalPlanned > 0 ? (totalWorked / totalPlanned) * 100f : 100f;
            reportActivityProgress = new ReportActivityProgress(progressPercentage, "", activity, weeklyLogs);
        }
        return reportActivityProgress;
    }

    private GUIGenerateMonthlyReport.ActivityRow crearFilaUI(String nombreActividad, float porcentaje, ReportActivityProgress progress) {
        float totalWorked = 0f;
        if (progress.getWeeklyProgressList() != null) {
            for (WeeklyLog log : progress.getWeeklyProgressList()) {
                totalWorked += log.getWorkedHours();
            }
        }
        String textoPorcentaje = String.format("%.1f%%", porcentaje);
        String textoHoras = String.format("%.1f horas", totalWorked);
        return new GUIGenerateMonthlyReport.ActivityRow(nombreActividad, textoPorcentaje, textoHoras, "");
    }

    private Report buildReport(List<ReportActivityProgress> activityProgressList) {
        Report report = new Report(0, "MENSUAL", new java.util.Date(), "", "", student, "");
        try {
            report.setReportNumber(reportDAO.countReportsByTypeAndStudent("MENSUAL", student.getUserId()) + 1);
        } catch (DataOperationException e) {
            logger.log(Level.WARNING, "No se pudo calcular el número de reporte", e);
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
                logger.log(Level.WARNING, "No se pudo obtener la práctica del estudiante para determinar el NRC", e);
            }
        }
        return practiceNrc;
    }

    private String getCurrentMonthName() {
        LocalDate today = LocalDate.now();
        return today.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES")) + " " + today.getYear();
    }

    public void handleSave() {
        if (currentReport == null || currentReport.getActivityProgressList() == null || currentReport.getActivityProgressList().isEmpty()) {
            guiGenerateMonthlyReport.showError("No hay actividades con avance para guardar.");
            return;
        }

        if (currentReport.getNrc() == null || currentReport.getNrc().isBlank()) {
            guiGenerateMonthlyReport.showError("No se pudo determinar el NRC de la práctica. Verifique que el estudiante tenga una práctica registrada.");
            return;
        }

        try {
            String observations = guiGenerateMonthlyReport.getObservations();
            currentReport.setObservations(observations);

            if (currentReport.getReportId() == 0) {
                boolean created = reportDAO.createPartialReport(currentReport);
                if (created) {
                    guiGenerateMonthlyReport.showSuccess("Reporte guardado en la base de datos exitosamente.");
                } else {
                    guiGenerateMonthlyReport.showError("No se pudo guardar el reporte en la base de datos.");
                }
            } else {
                boolean updated = reportDAO.setObservations(currentReport.getReportId(), observations);
                if (updated) {
                    guiGenerateMonthlyReport.showSuccess("Observaciones guardadas correctamente.");
                } else {
                    guiGenerateMonthlyReport.showError("No se pudo actualizar las observaciones.");
                }
            }
        } catch (DataOperationException e) {
            logger.log(Level.SEVERE, "Error al guardar el reporte", e);
            guiGenerateMonthlyReport.showError("Error al guardar el reporte: " + e.getMessage());
        }
    }

    public void handleExportPDF() {
        if (currentReport == null || currentReport.getActivityProgressList() == null || currentReport.getActivityProgressList().isEmpty()) {
            guiGenerateMonthlyReport.showError("No hay actividades con avance para exportar.");
        } else {
            try {
                String observations = guiGenerateMonthlyReport.getObservations();
                currentReport.setObservations(observations);
                if (currentReport.getReportId() == 0) {
                    boolean created = reportDAO.createPartialReport(currentReport);
                    if (!created) {
                        guiGenerateMonthlyReport.showError("No se pudo guardar el reporte en la base de datos antes de exportar.");
                    }
                } else {
                    reportDAO.setObservations(currentReport.getReportId(), observations);
                }
                DirectoryChooser dirChooser = new DirectoryChooser();
                dirChooser.setTitle("Selecciona dónde guardar el reporte");
                File selectedDir = dirChooser.showDialog(stage);
                if (selectedDir != null) {
                    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(System.currentTimeMillis());
                    String fileName = String.format("ReporteMensual_%s_%s.pdf", student.getEnrollment(), timestamp);
                    String outputPath = new File(selectedDir, fileName).getAbsolutePath();
                    MonthlyReportGenerator generator = new MonthlyReportGenerator();
                    boolean generated = generator.generate(buildParameters(currentReport), outputPath);
                    if (generated) {
                        guiGenerateMonthlyReport.showSuccess("Reporte exportado a PDF exitosamente en:\n" + outputPath);
                    } else {
                        guiGenerateMonthlyReport.showError("Error al generar el PDF del reporte.");
                    }
                }
            } catch (DataOperationException e) {
                logger.log(Level.SEVERE, "Error al exportar el reporte", e);
                guiGenerateMonthlyReport.showError("Error al exportar el reporte: " + e.getMessage());
            }
        }
    }

    private Map<String, Object> buildParameters(Report report) {
        Map<String, Object> parameters = new HashMap<>();
        EducationalExperienceDAO educationalExperienceDAO = new EducationalExperienceDAO();
        String period = "";
        try {
            EducationalExperience educationalExperience = educationalExperienceDAO.getEducationalExperienceByNrc(report.getNrc());
            if (educationalExperience != null) {
                period = educationalExperience.getPeriod();
            }
        } catch (DataOperationException e) {
            guiGenerateMonthlyReport.showError("Error al obtener los datos del periodo");
        }
        parameters.put("Period", period);
        parameters.put("NoReport", report.getReportNumber());
        parameters.put("Month", report.getMonth() != null ? report.getMonth() : "");
        parameters.put("ReportedHours", String.valueOf(report.getWorkedHours()));
        parameters.put("AcumulatedHours", String.valueOf(report.getAccumulatedHours()));
        String fullStudentName = "N/A";
        String responsibleName = "N/A";
        String professorName = "N/A";
        if (report.getStudent() != null) {
            fullStudentName = report.getStudent().getName() + " " + report.getStudent().getLastName();
            if (report.getStudent().getAssignedProject() != null && report.getStudent().getAssignedProject().getProjectManager() != null) {
                responsibleName = report.getStudent().getAssignedProject().getProjectManager().getName();
                professorName = responsibleName;
            }
        }
        parameters.put("\tAlumnName", fullStudentName);
        parameters.put("ResponsibleName", responsibleName);
        parameters.put("ProfessorName", professorName);
        List<WeeklyLog> weeklyLogs = new ArrayList<>();
        if (report.getActivityProgressList() != null) {
            for (ReportActivityProgress activityProgress : report.getActivityProgressList()) {
                if (activityProgress.getWeeklyProgressList() != null) {
                    weeklyLogs.addAll(activityProgress.getWeeklyProgressList());
                }
            }
        }
        for (int i = 0; i < 7; i++) {
            String index = String.valueOf(i + 1);
            if (i < weeklyLogs.size()) {
                WeeklyLog log = weeklyLogs.get(i);
                parameters.put("Period" + index, "Semana " + log.getWeek());
                parameters.put("\tActivity" + index, log.getActivity() != null ? log.getActivity().getName() : "");
                parameters.put("Observaciones" + index, report.getObservations() != null ? report.getObservations() : "");
            } else {
                parameters.put("Period" + index, "");
                parameters.put("\tActivity" + index, "");
                parameters.put("Observaciones" + index, "");
            }
        }
        return parameters;
    }
}