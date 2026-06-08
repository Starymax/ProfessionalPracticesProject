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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    private List<StudentAdvance> studentAdvances = new ArrayList<>();
    private int currentWeek;

    /** activityId → all relevant WeeklyLogs for that activity (current + past incomplete) */
    private final Map<Integer, List<WeeklyLog>> logsByActivity = new LinkedHashMap<>();
    /** activityId → Activity, preserving display order */
    private final Map<Integer, Activity> activitiesById = new LinkedHashMap<>();

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
                logsByActivity.clear();
                activitiesById.clear();
                List<Activity> activities = activityDAO.getActivitiesByProjectId(guiRegisterAdvance.getStudent().getAssignedProject().getProjectId());
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
                currentWeek = resolveCurrentWeek();
                while (currentWeek <= maxWeek && guiRegisterAdvance.getWeeklyLogsForWeek(currentWeek).isEmpty()) {
                    currentWeek++;
                }
                if (currentWeek > maxWeek) {
                    guiRegisterAdvance.disableCurrentWeekSection();
                }
                buildActivityMap(activities, maxWeek);
            } catch (DataOperationException e) {
                guiRegisterAdvance.showError(e.getMessage());
            }
        }
    }

    private void buildActivityMap(List<Activity> allActivities, int maxWeek) {
        Map<Integer, Activity> activityIndex = new HashMap<>();
        for (Activity activity : allActivities) {
            activityIndex.put(activity.getActivityId(), activity);
        }
        for (int week = 1; week <= Math.max(currentWeek, maxWeek); week++) {
            boolean isCurrentWeek = (week == currentWeek);
            for (WeeklyLog weeklyLog : guiRegisterAdvance.getWeeklyLogsForWeek(week)) {
                int actId = weeklyLog.getActivity().getActivityId();
                boolean isPastIncomplete = week < currentWeek && getExistingRealized(weeklyLog) < weeklyLog.getPlannedHours();
                if (isCurrentWeek || isPastIncomplete) {
                    logsByActivity.computeIfAbsent(actId, k -> new ArrayList<>()).add(weeklyLog);
                    activitiesById.putIfAbsent(actId, weeklyLog.getActivity());
                }
            }
        }
        List<Activity> displayList = new ArrayList<>(activitiesById.values());
        Set<Integer> completedIds = buildCompletedActivityIds();
        guiRegisterAdvance.setAllActivities(displayList, completedIds);
        if (!displayList.isEmpty()) {
            handleActivitySelection(displayList.get(0));
        }
    }

    private Set<Integer> buildCompletedActivityIds() {
        Set<Integer> completed = new HashSet<>();
        for (Map.Entry<Integer, List<WeeklyLog>> entry : logsByActivity.entrySet()) {
            boolean allDone = true;
            for (WeeklyLog weeklyLog : entry.getValue()) {
                if (getExistingRealized(weeklyLog) < weeklyLog.getPlannedHours()) {
                    allDone = false;
                    break;
                }
            }
            if (allDone) {
                completed.add(entry.getKey());
            }
        }
        return completed;
    }

    public void handleActivitySelection(Activity activity) {
        if (activity == null) {
            guiRegisterAdvance.clearActivityInfo();
        } else {
            List<WeeklyLog> weeklyLogs = logsByActivity.getOrDefault(activity.getActivityId(), new ArrayList<>());
            int totalPlanned = 0;
            int totalRealized = 0;
            for (WeeklyLog log : weeklyLogs) {
                totalPlanned += log.getPlannedHours();
                totalRealized += (int) getExistingRealized(log);
            }
            guiRegisterAdvance.setActivityInfo(currentWeek, totalPlanned, totalRealized);
            guiRegisterAdvance.restorePendingHoursForCurrentActivity(activity);
        }
    }

    public void handleSaveButton() {
        Map<Integer, String> pendingHours = guiRegisterAdvance.getPendingHoursByActivityId();
        if (pendingHours.isEmpty()) {
            guiRegisterAdvance.showError("No hay horas nuevas ingresadas para ninguna actividad.");
        } else {
            List<Activity> allActivities = guiRegisterAdvance.getAllActivities();
            List<String> errors = new ArrayList<>();
            for (Activity activity : allActivities) {
                String text = pendingHours.get(activity.getActivityId());
                if (text == null || text.isEmpty()) continue;
                GUIUtils.validateInt(text, "Horas nuevas (" + activity.getName() + ")", errors);
                if (!errors.isEmpty()) break;
                int entered = Integer.parseInt(text);
                int remaining = getTotalRemainingHours(activity.getActivityId());
                if (entered > remaining) {
                    errors.add("Las horas para \"" + activity.getName() + "\" exceden las horas restantes (" + remaining + ").");
                }
            }
            if (!errors.isEmpty()) {
                GUIUtils.showErrors(errors);
            } else {
                try {
                    float totalHoursBeforeSave = calculateTotalRealizedHours();
                    int lastSavedWeek = -1;
                    for (Activity activity : allActivities) {
                        String text = pendingHours.get(activity.getActivityId());
                        if (text != null && !text.isEmpty()) {
                            int hoursToDistribute = Integer.parseInt(text);
                            List<WeeklyLog> weeklyLogs = logsByActivity.getOrDefault(activity.getActivityId(), new ArrayList<>());
                            for (WeeklyLog weeklyLog : weeklyLogs) {
                                if (hoursToDistribute <= 0) {
                                    break;
                                }
                                int realized = (int) getExistingRealized(weeklyLog);
                                int logRemaining = weeklyLog.getPlannedHours() - realized;
                                if (logRemaining > 0) {
                                    int toSave = Math.min(hoursToDistribute, logRemaining);
                                    saveOrUpdateAdvance(weeklyLog, toSave, realized);
                                    hoursToDistribute -= toSave;
                                    lastSavedWeek = weeklyLog.getWeek();
                                }
                            }
                        }
                    }
                    refreshAdvances();
                    if (lastSavedWeek != -1) {
                        checkAndNotifyMonthlyReport(lastSavedWeek);
                    }
                    checkAndNotifyPartialReport(totalHoursBeforeSave);
                    checkAndNotifyFinalReport(totalHoursBeforeSave);
                    guiRegisterAdvance.showSuccess("Avances guardados correctamente.");
                    guiRegisterAdvance.closeWindow();
                } catch (DataOperationException e) {
                    guiRegisterAdvance.showError(e.getMessage());
                }
            }
        }
    }

    public void handleCancelButton() {
        guiRegisterAdvance.closeWindow();
    }

    private int getTotalRemainingHours(int activityId) {
        int remaining = 0;
        for (WeeklyLog weeklyLog : logsByActivity.getOrDefault(activityId, new ArrayList<>())) {
            int remainingHours = weeklyLog.getPlannedHours() - (int) getExistingRealized(weeklyLog);
            if (remainingHours > 0) {
                remaining += remainingHours;
            }
        }
        return remaining;
    }

    private void refreshAdvances() throws DataOperationException {
        studentAdvances = advanceDAO.getAdvancesByStudentId(guiRegisterAdvance.getStudent().getUserId());
    }

    private int resolveCurrentWeek() {
        int maxSavedWeek = 0;
        for (StudentAdvance advance : studentAdvances) {
            if (advance.getWeeklyLog() != null) {
                int week = advance.getWeeklyLog().getWeek();
                if (week > maxSavedWeek) maxSavedWeek = week;
            }
        }
        return maxSavedWeek + 1;
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
        StudentAdvance existing = null;
        for (StudentAdvance advance : studentAdvances) {
            if (advance.getWeeklyLog() != null && advance.getWeeklyLog().getWeeklyLogId() == weeklyLog.getWeeklyLogId()) {
                existing = advance;
            }
        }
        if (existing == null) {
            advanceDAO.createAdvance(new StudentAdvance(0, currentRealized + newHours, weeklyLog, guiRegisterAdvance.getStudent()));
        } else {
            advanceDAO.updateRealizedHours(existing.getAdvanceId(), currentRealized + newHours);
        }
    }

    private float calculateTotalRealizedHours() {
        float total = 0;
        for (StudentAdvance advance : studentAdvances) {
            total += advance.getRealizedHours();
        }
        return total;
    }

    private void checkAndNotifyMonthlyReport(int savedWeek) {
        if (savedWeek % WEEKS_PER_MONTH == 0) {
            try {
                int completedBlocks = savedWeek / WEEKS_PER_MONTH;
                int generated = reportDAO.countReportsByTypeAndStudent(ReportType.MONTHLY_REPORT.getReportType(), guiRegisterAdvance.getStudent().getUserId());
                if (completedBlocks > generated) {
                    int blockStart = savedWeek - WEEKS_PER_MONTH + 1;
                    String message = "Has completado las semanas " + blockStart + " a " + savedWeek + ". Ya puedes generar tu reporte mensual " + completedBlocks + ".";
                    notificationDAO.sendNotification(new Notification(0, "Reporte mensual disponible", message, new Date(), false, guiRegisterAdvance.getStudent()));
                }
            } catch (DataOperationException e) {
                LOGGER.log(Level.WARNING, "No se pudo enviar la notificación de reporte mensual", e);
            }
        }
    }

    private void checkAndNotifyPartialReport(float totalHoursBeforeSave) {
        try {
            float totalAfter = calculateTotalRealizedHours();
            if (totalHoursBeforeSave < PARTIAL_REPORT_HOURS_THRESHOLD && totalAfter >= PARTIAL_REPORT_HOURS_THRESHOLD) {
                int generated = reportDAO.countReportsByTypeAndStudent(ReportType.PARTIAL_REPORT.getReportType(), guiRegisterAdvance.getStudent().getUserId());
                if (generated == 0) {
                    String msg = "Has alcanzado " + (int) totalAfter + " horas de avance. Ya puedes generar tu reporte parcial.";
                    notificationDAO.sendNotification(new Notification(0, "Reporte parcial disponible", msg, new Date(), false, guiRegisterAdvance.getStudent()));
                }
            }
        } catch (DataOperationException e) {
            LOGGER.log(Level.WARNING, "No se pudo enviar la notificación de reporte parcial", e);
        }
    }

    private void checkAndNotifyFinalReport(float totalHoursBeforeSave) {
        try {
            float totalAfter = calculateTotalRealizedHours();
            if (totalHoursBeforeSave < FINAL_REPORT_HOURS_THRESHOLD && totalAfter >= FINAL_REPORT_HOURS_THRESHOLD) {
                int generated = reportDAO.countReportsByTypeAndStudent(ReportType.FINAL_REPORT.getReportType(), guiRegisterAdvance.getStudent().getUserId());
                if (generated == 0) {
                    String msg = "Has alcanzado " + (int) totalAfter + " horas de avance. Ya puedes generar tu reporte final.";
                    notificationDAO.sendNotification(new Notification(0, "Reporte final disponible", msg, new Date(), false, guiRegisterAdvance.getStudent()));
                }
            }
        } catch (DataOperationException e) {
            LOGGER.log(Level.WARNING, "No se pudo enviar la notificación de reporte final", e);
        }
    }
}
