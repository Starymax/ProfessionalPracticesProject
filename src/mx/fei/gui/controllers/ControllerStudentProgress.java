package mx.fei.gui.controllers;

import mx.fei.gui.views.GUIStudentProgress;
import mx.fei.logic.dao.ActivityDAO;
import mx.fei.logic.dao.DocumentDAO;
import mx.fei.logic.dao.ReportDAO;
import mx.fei.logic.dao.StudentAdvanceDAO;
import mx.fei.logic.dto.Activity;
import mx.fei.logic.dto.ActivityProgressRow;
import mx.fei.logic.dto.Document;
import mx.fei.logic.dto.Report;
import mx.fei.logic.dto.Practice;
import mx.fei.logic.dto.StudentAdvance;
import mx.fei.logic.dto.WeeklyLog;
import mx.fei.logic.exceptions.DataOperationException;


import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerStudentProgress {

    private static final Logger LOGGER = Logger.getLogger(ControllerStudentProgress.class.getName());
    private static final int TOTAL_HOURS_GOAL = 420;

    private final GUIStudentProgress guiStudentProgress;
    private final Practice practice;
    private final StudentAdvanceDAO studentAdvanceDAO;
    private final DocumentDAO documentDAO;
    private final ReportDAO reportDAO;
    private final ActivityDAO activityDAO;

    public ControllerStudentProgress(GUIStudentProgress guiStudentProgress, Practice practice) {
        this.guiStudentProgress = guiStudentProgress;
        this.practice = practice;
        studentAdvanceDAO = new StudentAdvanceDAO();
        documentDAO = new DocumentDAO();
        reportDAO = new ReportDAO();
        activityDAO = new ActivityDAO();
    }

    public void handleBackButton() {
        guiStudentProgress.closeWindow();
    }

    public void loadData() {
        loadHours();
        loadDocuments();
        loadReports();
        loadActivities();
        guiStudentProgress.updateGrade(practice.getGrade());
    }

    private void loadHours() {
        try {
            float realized = studentAdvanceDAO.getTotalHoursByIdStudent(practice.getStudent().getUserId());
            float remaining = Math.max(0, TOTAL_HOURS_GOAL - realized);
            guiStudentProgress.updateHours((int) realized, (int) remaining, TOTAL_HOURS_GOAL);
        } catch (DataOperationException e) {
            LOGGER.log(Level.WARNING, "Error al cargar horas", e.getMessage());
            guiStudentProgress.showError(e.getMessage());
        }
    }

    private void loadDocuments() {
        try {
            List<Document> allDocuments = documentDAO.getDocumentsForValidation(practice);
            List<Document> nonReports = allDocuments.stream().filter(document -> !document.getDocumentType().isReport()).toList();
            guiStudentProgress.updateDocuments(nonReports);
        } catch (DataOperationException e) {
            LOGGER.log(Level.WARNING, "Error al cargar documentos", e.getMessage());
            guiStudentProgress.showError(e.getMessage());
        }
    }

    private void loadActivities() {
        if (practice.getStudent().getAssignedProject() != null) {
            try {
                int projectId = practice.getStudent().getAssignedProject().getProjectId();
                int studentId = practice.getStudent().getUserId();
                List<Activity> activities = activityDAO.getActivitiesByProjectId(projectId);
                List<StudentAdvance> advances = studentAdvanceDAO.getAdvancesByStudentId(studentId);
                List<ActivityProgressRow> rows = new java.util.ArrayList<>();
                for (Activity activity : activities) {
                    List<WeeklyLog> weeklyLogs = activityDAO.getWeeklyLogsByActivityId(activity.getActivityId());
                    int totalPlanned = 0;
                    int totalRealized = 0;
                    for (WeeklyLog log : weeklyLogs) {
                        totalPlanned += log.getPlannedHours();
                        for (StudentAdvance advance : advances) {
                            if (advance.getWeeklyLog().getWeeklyLogId() == log.getWeeklyLogId()) {
                                totalRealized += advance.getRealizedHours();
                            }
                        }
                    }
                    String status = activityStatus(totalPlanned, totalRealized);
                    rows.add(new ActivityProgressRow(activity.getName(), String.valueOf(totalPlanned), String.valueOf(totalRealized), status));
                }
                guiStudentProgress.updateActivities(rows);
            } catch (DataOperationException e) {
                LOGGER.log(Level.WARNING, "Error al cargar actividades", e.getMessage());
                guiStudentProgress.showError(e.getMessage());
            }
        }
    }

    private String activityStatus(int planned, int realized) {
        String status;
        if (realized >= planned && planned > 0) {
            status = "Completa";
        } else if (realized > 0) {
            status = "En curso";
        } else {
            status = "Pendiente";
        }
        return status;
    }

    private void loadReports() {
        try {
            List<Report> reports = reportDAO.getReportsByStudentEnrollment(practice.getStudent().getEnrollment());
            guiStudentProgress.updateReports(reports);
        } catch (DataOperationException e) {
            LOGGER.log(Level.WARNING, "Error al cargar reportes", e.getMessage());
            guiStudentProgress.showError(e.getMessage());
        }
    }
}
