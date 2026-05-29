package mx.fei.gui.controllers;

import javafx.event.ActionEvent;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.gui.views.GUIRegisterAdvance;
import mx.fei.logic.dao.ActivityDAO;
import mx.fei.logic.dao.StudentAdvanceDAO;
import mx.fei.logic.dto.Activity;
import mx.fei.logic.dto.StudentAdvance;
import mx.fei.logic.dto.WeeklyLog;
import mx.fei.logic.exceptions.DataOperationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ControllerRegisterAdvance {

    private final GUIRegisterAdvance guiRegisterAdvance;
    private final ActivityDAO activityDAO;
    private final StudentAdvanceDAO advanceDAO;

    public ControllerRegisterAdvance(GUIRegisterAdvance guiRegisterAdvance) {
        this.guiRegisterAdvance = guiRegisterAdvance;
        activityDAO = new ActivityDAO();
        advanceDAO = new StudentAdvanceDAO();
    }

    public void loadWeeks() {
        if (guiRegisterAdvance.getStudent().getAssignedProject() == null) {
            guiRegisterAdvance.showError("No hay proyecto asignado.");
        } else {
            try {
                guiRegisterAdvance.clearWeeklyLogs();
                List<Activity> activities = activityDAO.getActivitiesByProjectId(guiRegisterAdvance.getStudent().getAssignedProject().getProjectId());
                Set<Integer> weeks = new HashSet<>();
                for (Activity activity : activities) {
                    List<WeeklyLog> weeklyLogs = activityDAO.getWeeklyLogsByActivityId(activity.getActivityId());
                    for (WeeklyLog weeklyLog : weeklyLogs) {
                        weeks.add(weeklyLog.getWeek());
                        guiRegisterAdvance.addWeeklyLog(weeklyLog.getWeek(), weeklyLog);
                    }
                }
                if (weeks.isEmpty()) {
                    guiRegisterAdvance.showError("No hay registros semanales disponibles.");
                } else {
                    List<Integer> sortedWeeks = new ArrayList<>(weeks);
                    Collections.sort(sortedWeeks);
                    List<String> items = new ArrayList<>();
                    for (Integer week : sortedWeeks) {
                        items.add("Semana " + week);
                    }
                    guiRegisterAdvance.setWeekOptions(items);
                    guiRegisterAdvance.selectFirstWeek();
                }
            } catch (DataOperationException e) {
                guiRegisterAdvance.showError(e.getMessage());
            }
        }
    }

    public void handleWeekSelection() {
        String selected = guiRegisterAdvance.getSelectedWeek();
        if (selected == null || selected.isEmpty()) {
            guiRegisterAdvance.setPlannedHours(0);
            guiRegisterAdvance.setCurrentRealized(0);
            guiRegisterAdvance.resetRealizedField();
        } else {
            int week = parseWeekNumber(selected);
            List<WeeklyLog> weeklyLogs = guiRegisterAdvance.getWeeklyLogsForWeek(week);
            guiRegisterAdvance.setActivityOptions(weeklyLogs);
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
            try {
                float existingRealized = getExistingRealized(weeklyLog);
                guiRegisterAdvance.setCurrentRealized((int) existingRealized);
            } catch (DataOperationException e) {
                guiRegisterAdvance.showError(e.getMessage());
            }
            guiRegisterAdvance.resetRealizedField();
        }
    }

    public void handleSaveButton(ActionEvent event) {
        saveAdvance();
    }

    public void handleBackButton(ActionEvent event) {
        guiRegisterAdvance.closeWindow();
    }

    private void saveAdvance() {
        WeeklyLog weeklyLog = guiRegisterAdvance.getSelectedWeeklyLog();
        if (weeklyLog == null) {
            guiRegisterAdvance.showError("Seleccione una semana y una actividad.");
        } else {
            List<String> errors = validateNewHours();
            if (!errors.isEmpty()) {
                GUIUtils.showErrors(errors);
            } else {
                int entered = Integer.parseInt(guiRegisterAdvance.getFieldRealized().getText().trim());
                try {
                    float currentRealized = getExistingRealized(weeklyLog);
                    int totalPlanned = weeklyLog.getPlannedHours();
                    if (currentRealized + entered > totalPlanned) {
                        guiRegisterAdvance.showError("La suma de horas actuales y nuevas no puede exceder las horas planeadas");
                    } else {
                        saveOrUpdateAdvance(weeklyLog, entered, (int) currentRealized);
                        guiRegisterAdvance.showSuccess("Avance guardado correctamente.");
                        guiRegisterAdvance.closeWindow();
                    }
                } catch (DataOperationException e) {
                    guiRegisterAdvance.showError(e.getMessage());
                }
            }
        }
    }

    private float getExistingRealized(WeeklyLog weeklyLog) throws DataOperationException {
        float existingRealized = 0;
        List<StudentAdvance> advances = advanceDAO.getAdvancesByStudentId(guiRegisterAdvance.getStudent().getUserId());
        for (StudentAdvance advance : advances) {
            if (advance.getWeeklyLog() != null && advance.getWeeklyLog().getWeeklyLogId() == weeklyLog.getWeeklyLogId()) {
                existingRealized = advance.getRealizedHours();
            }
        }
        return existingRealized;
    }

    private void saveOrUpdateAdvance(WeeklyLog weeklyLog, int newHours, int currentRealized) throws DataOperationException {
        List<StudentAdvance> advances = advanceDAO.getAdvancesByStudentId(guiRegisterAdvance.getStudent().getUserId());
        StudentAdvance existing = null;
        for (StudentAdvance advance : advances) {
            if (advance.getWeeklyLog() != null && advance.getWeeklyLog().getWeeklyLogId() == weeklyLog.getWeeklyLogId()) {
                existing = advance;
                break;
            }
        }
        int newTotal = currentRealized + newHours;
        if (existing != null) {
            advanceDAO.updateRealizedHours(existing.getAdvanceId(), newTotal);
        } else {
            StudentAdvance newAdvance = new StudentAdvance(0, newTotal, weeklyLog, guiRegisterAdvance.getStudent());
            advanceDAO.createAdvance(newAdvance);
        }
    }

    private List<String> validateNewHours() {
        List<String> errors = new ArrayList<>();
        GUIUtils.validateInt(guiRegisterAdvance.getFieldRealized().getText().trim(), "Horas nuevas", errors);
        return errors;
    }

    private int parseWeekNumber(String selected) {
        return Integer.parseInt(selected.replaceAll("[^0-9]", ""));
    }
}