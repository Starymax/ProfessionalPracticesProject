package mx.fei.gui.controllers;

import mx.fei.gui.utils.GUIUtils;
import mx.fei.gui.views.GUIRegisterAdvance;
import mx.fei.logic.dao.ActivityDAO;
import mx.fei.logic.dao.NotificationDAO;
import mx.fei.logic.dao.ReportDAO;
import mx.fei.logic.dao.StudentAdvanceDAO;
import mx.fei.logic.dto.Activity;
import mx.fei.logic.dto.Notification;
import mx.fei.logic.dto.ReportType;
import mx.fei.logic.dto.StudentAdvance;
import mx.fei.logic.dto.WeeklyLog;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerRegisterAdvance {

    private static final Logger LOGGER = Logger.getLogger(ControllerRegisterAdvance.class.getName());
    private static final int WEEKS_PER_MONTH = 4;
    private static final int PARTIAL_REPORT_HOURS_THRESHOLD = 210;
    private static final int FINAL_REPORT_HOURS_THRESHOLD = 420;

    private final GUIRegisterAdvance guiRegisterAdvance;
    private final ActivityDAO activityDAO;
    private final StudentAdvanceDAO advanceDAO;
    private final NotificationDAO notificationDAO;
    private final ReportDAO reportDAO;
    private int pastWeeksLimit;
    private List<StudentAdvance> studentAdvances = new ArrayList<>();

    public ControllerRegisterAdvance(GUIRegisterAdvance guiRegisterAdvance) {
        this.guiRegisterAdvance = guiRegisterAdvance;
        activityDAO = new ActivityDAO();
        advanceDAO = new StudentAdvanceDAO();
        notificationDAO = new NotificationDAO();
        reportDAO = new ReportDAO();
    }

    public void loadWeeks() {
        if (guiRegisterAdvance.getStudent().getAssignedProject() == null) {
            guiRegisterAdvance.showError("No hay proyecto asignado.");
            guiRegisterAdvance.closeWindow();
        } else {
            try {
                refreshAdvances();
                guiRegisterAdvance.clearWeeklyLogs();
                List<Activity> activities = activityDAO.getActivitiesByProjectId(
                        guiRegisterAdvance.getStudent().getAssignedProject().getProjectId());
                int maxWeek = 0;
                for (Activity activity : activities) {
                    List<WeeklyLog> weeklyLogs = activityDAO.getWeeklyLogsByActivityId(activity.getActivityId());
                    for (WeeklyLog weeklyLog : weeklyLogs) {
                        guiRegisterAdvance.addWeeklyLog(weeklyLog.getWeek(), weeklyLog);
                        if (weeklyLog.getWeek() > maxWeek) {
                            maxWeek = weeklyLog.getWeek();
                        }
                    }
                }
                int currentWeek = resolveCurrentWeek();
                while (currentWeek <= maxWeek && guiRegisterAdvance.getWeeklyLogsForWeek(currentWeek).isEmpty()) {
                    currentWeek++;
                }
                if (currentWeek > maxWeek) {
                    guiRegisterAdvance.showError("Ya completaste los avances de todas las semanas del proyecto.");
                    guiRegisterAdvance.disableCurrentWeekSection();
                    pastWeeksLimit = maxWeek + 1;
                } else {
                    guiRegisterAdvance.setCurrentWeek(currentWeek);
                    guiRegisterAdvance.setActivityOptions(guiRegisterAdvance.getWeeklyLogsForWeek(currentWeek));
                    pastWeeksLimit = currentWeek;
                }
                loadPastIncompleteWeeks(pastWeeksLimit);
            } catch (DataOperationException e) {
                guiRegisterAdvance.showError(e.getMessage());
            }
        }
    }

    public void handleActivitySelection() {
        guiRegisterAdvance.savePendingHoursForPreviousActivity();
        WeeklyLog weeklyLog = guiRegisterAdvance.getSelectedWeeklyLog();
        if (weeklyLog == null) {
            guiRegisterAdvance.setPlannedHours(0);
            guiRegisterAdvance.setCurrentRealized(0);
            guiRegisterAdvance.resetRealizedField();
        } else {
            guiRegisterAdvance.setPlannedHours(weeklyLog.getPlannedHours());
            guiRegisterAdvance.setCurrentRealized((int) getExistingRealized(weeklyLog));
            guiRegisterAdvance.restorePendingHoursForCurrentActivity();
        }
    }

    public void handlePastWeekSelection() {
        String selectedWeek = guiRegisterAdvance.getSelectedPastWeek();
        if (selectedWeek != null && !selectedWeek.isEmpty()) {
            int week = parseWeekNumber(selectedWeek);
            guiRegisterAdvance.setPastActivityOptions(getIncompleteLogsForWeek(week));
        }
    }

    public void handlePastActivitySelection() {
        WeeklyLog weeklyLog = guiRegisterAdvance.getSelectedPastWeeklyLog();
        if (weeklyLog == null) {
            guiRegisterAdvance.setPastPlannedHours(0);
            guiRegisterAdvance.setPastCurrentRealized(0);
            guiRegisterAdvance.setPastMissingHours(0);
            guiRegisterAdvance.resetPastRealizedField();
        } else {
            int plannedHours = weeklyLog.getPlannedHours();
            int realizedHours = (int) getExistingRealized(weeklyLog);
            guiRegisterAdvance.setPastPlannedHours(plannedHours);
            guiRegisterAdvance.setPastCurrentRealized(realizedHours);
            guiRegisterAdvance.setPastMissingHours(plannedHours - realizedHours);
            guiRegisterAdvance.resetPastRealizedField();
        }
    }

    public void handleSaveButton(ActionEvent event) {
        saveAllPendingAdvances();
    }

    public void handleSavePastButton(ActionEvent event) {
        if (saveAdvance(guiRegisterAdvance.getSelectedPastWeeklyLog(), guiRegisterAdvance.getTextFieldPastRealized())) {
            loadPastIncompleteWeeks(pastWeeksLimit);
        }
    }

    public void handleBackButton(ActionEvent event) {
        guiRegisterAdvance.closeWindow();
    }

    private void refreshAdvances() throws DataOperationException {
        studentAdvances = advanceDAO.getAdvancesByStudentId(guiRegisterAdvance.getStudent().getUserId());
    }

    private void saveAllPendingAdvances() {
        Map<Integer, String> pendingHours = guiRegisterAdvance.getPendingHoursByLogId();
        if (pendingHours.isEmpty()) {
            guiRegisterAdvance.showError("No hay horas nuevas ingresadas para ninguna actividad.");
        } else {
            Map<String, WeeklyLog> logByActivityName = guiRegisterAdvance.getLogByActivityName();
            List<String> errors = new ArrayList<>();
            for (WeeklyLog weeklyLog : logByActivityName.values()) {
                String pendingText = pendingHours.get(weeklyLog.getWeeklyLogId());
                if (pendingText != null && !pendingText.isEmpty()) {
                    GUIUtils.validateInt(pendingText, "Horas nuevas (" + weeklyLog.getActivity().getName() + ")", errors);
                    if (errors.isEmpty()) {
                        int enteredHours = Integer.parseInt(pendingText);
                        int currentRealized = (int) getExistingRealized(weeklyLog);
                        if (currentRealized + enteredHours > weeklyLog.getPlannedHours()) {
                            errors.add("La suma de horas para \"" + weeklyLog.getActivity().getName() + "\" excede las horas planeadas (" + weeklyLog.getPlannedHours() + ").");
                        }
                    }
                }
            }
            if (!errors.isEmpty()) {
                GUIUtils.showErrors(errors);
            } else {
                try {
                    for (WeeklyLog weeklyLog : logByActivityName.values()) {
                        String pendingText = pendingHours.get(weeklyLog.getWeeklyLogId());
                        if (pendingText != null && !pendingText.isEmpty()) {
                            int enteredHours = Integer.parseInt(pendingText);
                            int currentRealized = (int) getExistingRealized(weeklyLog);
                            saveOrUpdateAdvance(weeklyLog, enteredHours, currentRealized);
                        }
                    }
                    guiRegisterAdvance.showSuccess("Avances guardados correctamente.");
                    guiRegisterAdvance.clearPendingHours();
                    handleActivitySelection();
                } catch (DataOperationException e) {
                    guiRegisterAdvance.showError(e.getMessage());
                }
            }
        }
    }

    private int resolveCurrentWeek() {
        int maxSavedWeek = 0;
        for (StudentAdvance advance : studentAdvances) {
            if (advance.getWeeklyLog() != null) {
                int week = advance.getWeeklyLog().getWeek();
                if (week > maxSavedWeek) {
                    maxSavedWeek = week;
                }
            }
        }
        return maxSavedWeek + 1;
    }

    private boolean saveAdvance(WeeklyLog weeklyLog, TextField hoursField) {
        boolean saved = false;
        if (weeklyLog == null) {
            guiRegisterAdvance.showError("Seleccione una actividad.");
        } else {
            List<String> errors = new ArrayList<>();
            GUIUtils.validateInt(hoursField.getText().trim(), "Horas nuevas", errors);
            if (!errors.isEmpty()) {
                GUIUtils.showErrors(errors);
            } else {
                saved = trySaveValidatedAdvance(weeklyLog, Integer.parseInt(hoursField.getText().trim()));
            }
        }
        return saved;
    }

    private boolean trySaveValidatedAdvance(WeeklyLog weeklyLog, int enteredHours) {
        boolean saved = false;
        try {
            int currentRealized = (int) getExistingRealized(weeklyLog);
            if (currentRealized + enteredHours > weeklyLog.getPlannedHours()) {
                guiRegisterAdvance.showError("La suma de horas actuales y nuevas no puede exceder las horas planeadas.");
            } else {
                saveOrUpdateAdvance(weeklyLog, enteredHours, currentRealized);
                guiRegisterAdvance.showSuccess("Avance guardado correctamente.");
                saved = true;
            }
        } catch (DataOperationException e) {
            guiRegisterAdvance.showError(e.getMessage());
        }
        return saved;
    }

    private float getExistingRealized(WeeklyLog weeklyLog) {
        float realizedHours = 0;
        for (StudentAdvance advance : studentAdvances) {
            if (advance.getWeeklyLog() != null && advance.getWeeklyLog().getWeeklyLogId() == weeklyLog.getWeeklyLogId()) {
                realizedHours = advance.getRealizedHours();
            }
        }
        return realizedHours;
    }

    private void saveOrUpdateAdvance(WeeklyLog weeklyLog, int newHours, int currentRealized) throws DataOperationException {
        StudentAdvance existingAdvance = null;
        for (StudentAdvance advance : studentAdvances) {
            if (advance.getWeeklyLog() != null && advance.getWeeklyLog().getWeeklyLogId() == weeklyLog.getWeeklyLogId()) {
                existingAdvance = advance;
            }
        }
        float totalHoursBeforeSave = calculateTotalRealizedHours();
        if (existingAdvance == null) {
            advanceDAO.createAdvance(new StudentAdvance(0, currentRealized + newHours, weeklyLog, guiRegisterAdvance.getStudent()));
        } else {
            advanceDAO.updateRealizedHours(existingAdvance.getAdvanceId(), currentRealized + newHours);
        }
        refreshAdvances();
        checkAndNotifyMonthlyReport(weeklyLog.getWeek());
        checkAndNotifyPartialReport(totalHoursBeforeSave);
        checkAndNotifyFinalReport(totalHoursBeforeSave);
    }

    private void checkAndNotifyMonthlyReport(int savedWeek) {
        if (savedWeek % WEEKS_PER_MONTH == 0) {
            try {
                int completedBlocks = savedWeek / WEEKS_PER_MONTH;
                int generatedReports = reportDAO.countReportsByTypeAndStudent(ReportType.MONTHLY_REPORT.getReportType(), guiRegisterAdvance.getStudent().getUserId());
                if (completedBlocks > generatedReports) {
                    int blockStart = savedWeek - WEEKS_PER_MONTH + 1;
                    String title = "Reporte mensual disponible";
                    String message = "Has completado las semanas " + blockStart + " a " + savedWeek + ". Ya puedes generar tu reporte mensual " + completedBlocks + ".";
                    Notification notification = new Notification(0, title, message, new Date(), false, guiRegisterAdvance.getStudent());
                    notificationDAO.sendNotification(notification);
                }
            } catch (DataOperationException e) {
                LOGGER.log(Level.WARNING, "No se pudo enviar la notificación de reporte mensual", e);
            }
        }
    }

    private float calculateTotalRealizedHours() {
        float total = 0;
        for (StudentAdvance advance : studentAdvances) {
            total += advance.getRealizedHours();
        }
        return total;
    }

    private void checkAndNotifyPartialReport(float totalHoursBeforeSave) {
        try {
            float totalHoursAfterSave = calculateTotalRealizedHours();
            boolean crossedThreshold = totalHoursBeforeSave < PARTIAL_REPORT_HOURS_THRESHOLD && totalHoursAfterSave >= PARTIAL_REPORT_HOURS_THRESHOLD;
            if (crossedThreshold) {
                int generatedPartialReports = reportDAO.countReportsByTypeAndStudent(
                        ReportType.PARTIAL_REPORT.getReportType(), guiRegisterAdvance.getStudent().getUserId());
                if (generatedPartialReports == 0) {
                    String title = "Reporte parcial disponible";
                    String message = "Has alcanzado " + (int) totalHoursAfterSave + " horas de avance. Ya puedes generar tu reporte parcial.";
                    Notification notification = new Notification(0, title, message, new Date(), false, guiRegisterAdvance.getStudent());
                    notificationDAO.sendNotification(notification);
                }
            }
        } catch (DataOperationException e) {
            LOGGER.log(Level.WARNING, "No se pudo enviar la notificación de reporte parcial", e);
        }
    }

    private void checkAndNotifyFinalReport(float totalHoursBeforeSave) {
        try {
            float totalHoursAfterSave = calculateTotalRealizedHours();
            boolean crossedThreshold = totalHoursBeforeSave < FINAL_REPORT_HOURS_THRESHOLD && totalHoursAfterSave >= FINAL_REPORT_HOURS_THRESHOLD;
            if (crossedThreshold) {
                int generatedFinalReports = reportDAO.countReportsByTypeAndStudent(
                        ReportType.FINAL_REPORT.getReportType(), guiRegisterAdvance.getStudent().getUserId());
                if (generatedFinalReports == 0) {
                    String title = "Reporte final disponible";
                    String message = "Has alcanzado " + (int) totalHoursAfterSave + " horas de avance. Ya puedes generar tu reporte final.";
                    Notification notification = new Notification(0, title, message, new Date(), false, guiRegisterAdvance.getStudent());
                    notificationDAO.sendNotification(notification);
                }
            }
        } catch (DataOperationException e) {
            LOGGER.log(Level.WARNING, "No se pudo enviar la notificación de reporte final", e);
        }
    }

    private List<WeeklyLog> getIncompleteLogsForWeek(int week) {
        List<WeeklyLog> weeklyLogs = guiRegisterAdvance.getWeeklyLogsForWeek(week);
        List<WeeklyLog> incompleteLogs = new ArrayList<>();
        for (WeeklyLog weeklyLog : weeklyLogs) {
            if (getExistingRealized(weeklyLog) < weeklyLog.getPlannedHours()) {
                incompleteLogs.add(weeklyLog);
            }
        }
        return incompleteLogs;
    }

    private void loadPastIncompleteWeeks(int currentWeek) {
        List<String> pastWeekOptions = new ArrayList<>();
        for (int week = 1; week < currentWeek; week++) {
            if (!getIncompleteLogsForWeek(week).isEmpty()) {
                pastWeekOptions.add("Semana " + week);
            }
        }
        guiRegisterAdvance.setPastWeekOptions(pastWeekOptions);
    }

    private int parseWeekNumber(String selectedWeek) {
        return Integer.parseInt(selectedWeek.replaceAll("[^0-9]", ""));
    }
}
