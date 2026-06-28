package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIGenerateFinalReport;
import mx.fei.gui.views.GUIGenerateMonthlyReport;
import mx.fei.gui.views.GUIGeneratePartialReport;
import mx.fei.gui.views.GUIGenerateReport;
import mx.fei.logic.dao.DocumentDAO;
import mx.fei.logic.dao.ReportDAO;
import mx.fei.logic.dao.StudentAdvanceDAO;
import mx.fei.logic.dto.ReportType;
import mx.fei.logic.dto.Student;
import mx.fei.logic.dto.StudentAdvance;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerGenerateReport {

    private static final Logger LOGGER = Logger.getLogger(ControllerGenerateReport.class.getName());
    private static final int WEEKS_PER_MONTH = 4;
    private static final int PARTIAL_REPORT_HOURS_THRESHOLD = 210;
    private static final int FINAL_REPORT_HOURS_THRESHOLD = 420;
    private static final int MAX_PARTIAL_REPORTS = 1;
    private static final int MAX_FINAL_REPORTS = 1;

    private final GUIGenerateReport guiGenerateReport;
    private final StudentAdvanceDAO studentAdvanceDAO;
    private final ReportDAO reportDAO;
    private final DocumentDAO documentDAO;

    public ControllerGenerateReport(GUIGenerateReport guiGenerateReport) {
        this.guiGenerateReport = guiGenerateReport;
        this.studentAdvanceDAO = new StudentAdvanceDAO();
        this.reportDAO = new ReportDAO();
        this.documentDAO = new DocumentDAO();
    }

    public void handleMensualPartialFinalBackButtons(ActionEvent event) {
        Button source = (Button) event.getSource();
        switch(source.getText()) {
            case "Mensual" -> {
                openMonthlyReport();
            }
            case "Parcial" -> {
                openPartialReport();
            }
            case "Final" -> {
                openFinalReport();
            }
            case "Regresar" -> {
                guiGenerateReport.closeWindow();
            }
        }
    }

    private boolean arePrerequisitesMet() {
        boolean prerequisitesMet = false;
        try {
            prerequisitesMet = documentDAO.areInitialDocumentsUploaded(guiGenerateReport.getPractice());
            if (!prerequisitesMet) {
                guiGenerateReport.showError("Para generar un reporte primero debes subir la carta de aceptación y el horario, y que el coordinador los valide.");
            }
        } catch (DataOperationException e) {
            LOGGER.log(Level.SEVERE, "Error al verificar los prerrequisitos de los reportes", e);
            guiGenerateReport.showError(e.getMessage());
        }
        return prerequisitesMet;
    }

    private void openMonthlyReport() {
        if (!arePrerequisitesMet()) {
            return;
        }
        Student student = guiGenerateReport.getPractice().getStudent();
        if (student == null) {
            guiGenerateReport.showError("No hay estudiante seleccionado.");
        } else {
            try {
                int maxSavedWeek = resolveMaxSavedWeek(student.getUserId());
                int completedBlocks = maxSavedWeek / WEEKS_PER_MONTH;
                int generatedReports = reportDAO.countReportsByTypeAndStudent(ReportType.MONTHLY_REPORT.getReportType(), student.getUserId());
                if (completedBlocks <= generatedReports) {
                    int weeksNeeded = (generatedReports + 1) * WEEKS_PER_MONTH;
                    guiGenerateReport.showError("Aún no puedes generar el reporte mensual " + (generatedReports + 1) + "\nNecesitas completar hasta la semana " + weeksNeeded + " (actualmente en semana " + maxSavedWeek + ").");
                } else {
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    GUIGenerateMonthlyReport generateMonthlyReport = new GUIGenerateMonthlyReport(student);
                    generateMonthlyReport.start(stage);
                }
            } catch (DataOperationException e) {
                LOGGER.log(Level.SEVERE, "Error al verificar disponibilidad del reporte mensual", e);
                guiGenerateReport.showError(e.getMessage());
            }
        }
    }

    private int resolveMaxSavedWeek(int studentId) throws DataOperationException {
        List<StudentAdvance> advances = studentAdvanceDAO.getAdvancesByStudentId(studentId);
        int maxSavedWeek = 0;
        for (StudentAdvance advance : advances) {
            if (advance.getWeeklyLog() != null && advance.getWeeklyLog().getWeek() > maxSavedWeek) {
                maxSavedWeek = advance.getWeeklyLog().getWeek();
            }
        }
        return maxSavedWeek;
    }

    private float calculateTotalRealizedHours(int studentId) throws DataOperationException {
        List<StudentAdvance> advances = studentAdvanceDAO.getAdvancesByStudentId(studentId);
        float total = 0;
        for (StudentAdvance advance : advances) {
            total += advance.getRealizedHours();
        }
        return total;
    }

    private void openPartialReport() {
        if (!arePrerequisitesMet()) {
            return;
        }
        Student student = guiGenerateReport.getPractice().getStudent();
        if (student == null) {
            guiGenerateReport.showError("No hay estudiante seleccionado.");
        } else {
        try {
            float totalHours = calculateTotalRealizedHours(student.getUserId());
            int generatedPartialReports = reportDAO.countReportsByTypeAndStudent(
                    ReportType.PARTIAL_REPORT.getReportType(), student.getUserId());
            if (generatedPartialReports >= MAX_PARTIAL_REPORTS) {
                guiGenerateReport.showError("Ya generaste tu reporte parcial.");
            } else if (totalHours < PARTIAL_REPORT_HOURS_THRESHOLD) {
                guiGenerateReport.showError("Necesitas al menos " + PARTIAL_REPORT_HOURS_THRESHOLD
                        + " horas de avance para generar el reporte parcial (llevas " + (int) totalHours + ").");
            } else {
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                GUIGeneratePartialReport generatePartialReport = new GUIGeneratePartialReport(student);
                generatePartialReport.start(stage);
            }
        } catch (DataOperationException e) {
            LOGGER.log(Level.SEVERE, "Error al verificar disponibilidad del reporte parcial", e);
            guiGenerateReport.showError(e.getMessage());
        }
        }
    }

    private void openFinalReport() {
        if (!arePrerequisitesMet()) {
            return;
        }
        Student student = guiGenerateReport.getPractice().getStudent();
        if (student == null) {
            guiGenerateReport.showError("No hay estudiante seleccionado.");
        } else {
            try {
                float totalHours = calculateTotalRealizedHours(student.getUserId());
                int generatedFinalReports = reportDAO.countReportsByTypeAndStudent(
                        ReportType.FINAL_REPORT.getReportType(), student.getUserId());
                if (generatedFinalReports >= MAX_FINAL_REPORTS) {
                    guiGenerateReport.showError("Ya generaste tu reporte final.");
                } else if (totalHours < FINAL_REPORT_HOURS_THRESHOLD) {
                    guiGenerateReport.showError("Necesitas al menos " + FINAL_REPORT_HOURS_THRESHOLD
                            + " horas de avance para generar el reporte final (llevas " + (int) totalHours + ").");
                } else {
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    GUIGenerateFinalReport generateFinalReport = new GUIGenerateFinalReport(student, guiGenerateReport.getPractice());
                    generateFinalReport.start(stage);
                }
            } catch (DataOperationException e) {
                LOGGER.log(Level.SEVERE, "Error al verificar disponibilidad del reporte final", e);
                guiGenerateReport.showError(e.getMessage());
            }
        }
    }
}
