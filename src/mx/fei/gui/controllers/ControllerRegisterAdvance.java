package mx.fei.gui.controllers;

import mx.fei.gui.utils.GUIUtils;
import mx.fei.gui.views.GUIRegisterAdvance;
import mx.fei.logic.dao.ActivityDAO;
import mx.fei.logic.dao.StudentAdvanceDAO;
import mx.fei.logic.dto.Activity;
import mx.fei.logic.dto.StudentAdvance;
import mx.fei.logic.dto.WeeklyLog;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

public class ControllerRegisterAdvance {

    private final GUIRegisterAdvance guiRegisterAdvance;
    private final ActivityDAO activityDAO;
    private final StudentAdvanceDAO advanceDAO;
    private int pastWeeksLimit;
    private List<StudentAdvance> studentAdvances = new ArrayList<>();

    public ControllerRegisterAdvance(GUIRegisterAdvance guiRegisterAdvance) {
        this.guiRegisterAdvance = guiRegisterAdvance;
        activityDAO = new ActivityDAO();
        advanceDAO = new StudentAdvanceDAO();
    }

    public void loadWeeks() {
        if (guiRegisterAdvance.getStudent().getAssignedProject() == null) {
            guiRegisterAdvance.showError("No hay proyecto asignado.");
            guiRegisterAdvance.closeWindow();
            return;
        }
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

    public void handleActivitySelection() {
        WeeklyLog weeklyLog = guiRegisterAdvance.getSelectedWeeklyLog();
        if (weeklyLog == null) {
            guiRegisterAdvance.setPlannedHours(0);
            guiRegisterAdvance.setCurrentRealized(0);
            guiRegisterAdvance.resetRealizedField();
        } else {
            guiRegisterAdvance.setPlannedHours(weeklyLog.getPlannedHours());
            guiRegisterAdvance.setCurrentRealized((int) getExistingRealized(weeklyLog));
            guiRegisterAdvance.resetRealizedField();
        }
    }

    public void handlePastWeekSelection() {
        String selectedWeek = guiRegisterAdvance.getSelectedPastWeek();
        if (selectedWeek == null || selectedWeek.isEmpty()) {
            return;
        }
        int week = parseWeekNumber(selectedWeek);
        guiRegisterAdvance.setPastActivityOptions(getIncompleteLogsForWeek(week));
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
        if (saveAdvance(guiRegisterAdvance.getSelectedWeeklyLog(), guiRegisterAdvance.getTextFieldRealized())) {
            handleActivitySelection();
        }
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
        if (existingAdvance == null) {
            advanceDAO.createAdvance(new StudentAdvance(0, currentRealized + newHours, weeklyLog, guiRegisterAdvance.getStudent()));
        } else {
            advanceDAO.updateRealizedHours(existingAdvance.getAdvanceId(), currentRealized + newHours);
        }
        refreshAdvances();
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
