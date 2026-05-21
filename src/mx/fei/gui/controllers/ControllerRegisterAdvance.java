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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
                    List<WeeklyLog> logs = activityDAO.getWeeklyLogsByActivityId(activity.getActivityId());
                    for (WeeklyLog log : logs) {
                        weeks.add(log.getWeek());
                        guiRegisterAdvance.addWeeklyLog(log.getWeek(), log);
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
            List<WeeklyLog> logs = guiRegisterAdvance.getWeeklyLogsForWeek(week);
            int totalPlanned = 0;
            for (WeeklyLog log : logs) {
                totalPlanned += log.getPlannedHours();
            }
            float existingRealized = 0f;
            try {
                List<StudentAdvance> advances = advanceDAO.getAdvancesByStudentId(guiRegisterAdvance.getStudent().getUserId());
                Set<Integer> logIds = new HashSet<>();
                for (WeeklyLog log : logs) {
                    logIds.add(log.getWeeklyLogId());
                }
                for (StudentAdvance advance : advances) {
                    if (advance.getWeeklyLog() != null && logIds.contains(advance.getWeeklyLog().getWeeklyLogId())) {
                        existingRealized += advance.getRealizedHours();
                    }
                }
            } catch (DataOperationException e) {
                guiRegisterAdvance.showError(e.getMessage());
            }
            guiRegisterAdvance.setPlannedHours(totalPlanned);
            guiRegisterAdvance.setCurrentRealized((int) existingRealized);
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
        String selected = guiRegisterAdvance.getSelectedWeek();
        if (selected == null || selected.isEmpty()) {
            guiRegisterAdvance.showError("Seleccione una semana.");
        } else {
            int week = parseWeekNumber(selected);
            List<WeeklyLog> logs = guiRegisterAdvance.getWeeklyLogsForWeek(week);
            int totalPlanned = sumPlannedHours(logs);
            List<String> errors = validateNewHours();

            if (errors.isEmpty()) {
                int entered = Integer.parseInt(guiRegisterAdvance.getFieldRealized().getText().trim());
                try {
                    List<StudentAdvance> advances = getAdvancesForLogs(logs);
                    float currentRealized = calculateCurrentRealized(advances);

                    if (currentRealized + entered > totalPlanned) {
                        guiRegisterAdvance.showError("La suma de horas actuales y nuevas no puede exceder las horas planeadas totales.");
                    } else {
                        applyNewHours(logs, entered, advances);
                        guiRegisterAdvance.showSuccess("Avances guardados correctamente.");
                        guiRegisterAdvance.closeWindow();
                    }
                } catch (DataOperationException e) {
                    guiRegisterAdvance.showError(e.getMessage());
                }
            } else {
                GUIUtils.showErrors(errors);
            }
        }
    }

    private int sumPlannedHours(List<WeeklyLog> logs) {
        int total = 0;
        for (WeeklyLog log : logs) {
            total += log.getPlannedHours();
        }
        return total;
    }

    private List<String> validateNewHours() {
        List<String> errors = new ArrayList<>();
        GUIUtils.validateInt(guiRegisterAdvance.getFieldRealized().getText().trim(), "Horas nuevas", errors);
        return errors;
    }

    private List<StudentAdvance> getAdvancesForLogs(List<WeeklyLog> logs) throws DataOperationException {
        List<StudentAdvance> advances = advanceDAO.getAdvancesByStudentId(guiRegisterAdvance.getStudent().getUserId());
        Set<Integer> logIds = new HashSet<>();
        for (WeeklyLog log : logs) {
            logIds.add(log.getWeeklyLogId());
        }
        List<StudentAdvance> filteredAdvances = new ArrayList<>();
        for (StudentAdvance advance : advances) {
            if (advance.getWeeklyLog() != null && logIds.contains(advance.getWeeklyLog().getWeeklyLogId())) {
                filteredAdvances.add(advance);
            }
        }
        return filteredAdvances;
    }

    private float calculateCurrentRealized(List<StudentAdvance> advances) {
        float currentRealized = 0f;
        for (StudentAdvance advance : advances) {
            currentRealized += advance.getRealizedHours();
        }
        return currentRealized;
    }

    private void applyNewHours(List<WeeklyLog> logs, int entered, List<StudentAdvance> advances) throws DataOperationException {
        Map<Integer, StudentAdvance> advanceByLogId = new HashMap<>();
        for (StudentAdvance advance : advances) {
            advanceByLogId.put(advance.getWeeklyLog().getWeeklyLogId(), advance);
        }

        int remaining = entered;
        for (WeeklyLog log : logs) {
            if (remaining <= 0) {
                break;
            }
            int planned = log.getPlannedHours();
            StudentAdvance existing = advanceByLogId.get(log.getWeeklyLogId());
            int currentLogRealized = existing != null ? (int) existing.getRealizedHours() : 0;
            int available = planned - currentLogRealized;
            if (available > 0) {
                int assign = Math.min(available, remaining);
                int newRealized = currentLogRealized + assign;
                if (existing != null) {
                    advanceDAO.updateRealizedHours(existing.getAdvanceId(), newRealized);
                } else {
                    StudentAdvance newAdvance = new StudentAdvance(0, newRealized, log, guiRegisterAdvance.getStudent());
                    advanceDAO.createAdvance(newAdvance);
                }
                remaining -= assign;
            }
        }
    }

    private int parseWeekNumber(String selected) {
        return Integer.parseInt(selected.replaceAll("[^0-9]", ""));
    }
}
