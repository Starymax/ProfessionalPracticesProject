package mx.fei.gui.controllers;

import mx.fei.gui.utils.GUIUtils;
import mx.fei.gui.views.GUIActivityPlan;
import mx.fei.logic.dao.ActivityDAO;
import mx.fei.logic.dao.ProjectDAO;
import mx.fei.logic.dto.Activity;
import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.WeeklyLog;
import mx.fei.logic.exceptions.DataOperationException;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ControllerActivityPlan {

    private final GUIActivityPlan guiActivityPlan;
    private Project project;
    private final int NO_HOURS = 0;

    public ControllerActivityPlan(GUIActivityPlan guiActivityPlan) {
        this.guiActivityPlan = guiActivityPlan;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void handleAddActivityDeleteSaveCancelButtons(ActionEvent event) {
        Button button = (Button) event.getSource();
        switch (button.getText()) {
            case "Nueva actividad" -> {
                addNewActivity();
            }
            case "Eliminar" -> {
                deleteSelectedActivity();
            }
            case "Guardar plan" -> {
                savePlan();
            }
            case "Cancelar" -> {
                cancel();
            }
        }
    }

    public void addNewActivity() {
        Activity activity = new Activity(0, "", "", project);
        guiActivityPlan.addActivity(activity);
    }

    public void deleteSelectedActivity() {
        boolean userConfirmed = showDeleteConfirmation();
        if (userConfirmed) {
            guiActivityPlan.removeSelectedActivity();
        }
    }

    public void savePlan() {
        List<String> errors = validate();
        if (!errors.isEmpty()) {
            GUIUtils.showErrors(errors);
        } else {
            boolean userConfirmed = showSaveConfirmation();
            if (userConfirmed && saveProject(project)) {
                saveActivities(guiActivityPlan.getActivities());
            }
        }
    }

    public void cancel() {
        boolean userConfirmed = showCancelConfirmation();
        if (userConfirmed) {
            guiActivityPlan.getStage().close();
        }
    }

    private boolean showDeleteConfirmation() {
        Alert confirmationDialog = new Alert(AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Eliminar actividad");
        confirmationDialog.setHeaderText(null);
        confirmationDialog.setContentText("¿Seguro que desea eliminar esta actividad?");
        Optional<ButtonType> confirmationResult = confirmationDialog.showAndWait();
        return confirmationResult.isPresent() && confirmationResult.get() == ButtonType.OK;
    }

    private boolean showSaveConfirmation() {
        Alert confirmationDialog = new Alert(AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Confirmar");
        confirmationDialog.setHeaderText(null);
        confirmationDialog.setContentText("¿Seguro que desea guardar el proyecto con este plan?");
        Optional<ButtonType> confirmationResult = confirmationDialog.showAndWait();
        return confirmationResult.isPresent() && confirmationResult.get() == ButtonType.OK;
    }

    private boolean showCancelConfirmation() {
        Alert confirmationDialog = new Alert(AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Cancelar");
        confirmationDialog.setHeaderText(null);
        confirmationDialog.setContentText("¿Seguro que desea cancelar? Se perderá la información ingresada.");
        Optional<ButtonType> confirmationResult = confirmationDialog.showAndWait();
        return confirmationResult.isPresent() && confirmationResult.get() == ButtonType.OK;
    }

    private List<String> validate() {
        List<String> errors = new ArrayList<>();
        List<Activity> activities = guiActivityPlan.getActivities();
        if (activities.isEmpty()) {
            errors.add("Debe añadir al menos una actividad.");
        } else {
            validateEachActivity(activities, errors);
            validatePlanTotal(errors);
        }
        return errors;
    }

    private void validateEachActivity(List<Activity> activities, List<String> errors) {
        for (int activityIndex = 0; activityIndex < activities.size(); activityIndex++) {
            Activity activity = activities.get(activityIndex);
            String activityLabel = "Actividad " + (activityIndex + 1);
            validateActivityFields(activity, activityLabel, errors);
        }
    }

    private void validateActivityFields(Activity activity, String activityLabel, List<String> errors) {
        GUIUtils.validateShortText(activity.getName(), "Nombre (" + activityLabel + ")", errors);
        String activityDescription = activity.getObservationsActivity() != null ? activity.getObservationsActivity() : "";
        GUIUtils.validateLongText(activityDescription, "Descripción (" + activityLabel + ")", errors);
        validateActivityHours(activity, activityLabel, errors);
    }

    private void validateActivityHours(Activity activity, String activityLabel, List<String> errors) {
        Map<Integer, Integer> activityWeekHours = guiActivityPlan.getActivityWeekHours().getOrDefault(activity, Collections.emptyMap());
        int activityTotalHours = activityWeekHours.values().stream().mapToInt(Integer::intValue).sum();
        if (activityTotalHours == NO_HOURS) {
            errors.add(activityLabel + " no tiene horas asignadas.");
        }
    }

    private void validatePlanTotal(List<String> errors) {
        if (guiActivityPlan.getTotalPlannedHours() != GUIActivityPlan.TOTAL_PLAN_HOURS) {
            errors.add("La suma total debe ser exactamente " + GUIActivityPlan.TOTAL_PLAN_HOURS + " h (actual: " + guiActivityPlan.getTotalPlannedHours() + " h).");
        }
    }

    private boolean saveProject(Project project) {
        boolean isProjectSaved = project.getProjectId() > 0;
        if (!isProjectSaved) {
            try {
                ProjectDAO projectDAO = new ProjectDAO();
                project.setProjectId(projectDAO.registerProject(project));
                isProjectSaved = project.getProjectId() > 0;
            } catch (DataOperationException e) {
                guiActivityPlan.showError(e.getMessage());
            }
        }
        return isProjectSaved;
    }

    private void saveActivities(List<Activity> activities) {
        try {
            ActivityDAO activityDAO = new ActivityDAO();
            Map<Activity, ArrayList<WeeklyLog>> weeklyLogsMap = guiActivityPlan.getWeeklyLogsMap();
            for (Activity activity : activities) {
                activityDAO.insertActivity(activity, project, weeklyLogsMap.get(activity));
            }
            closeStageWithSuccess();
        } catch (DataOperationException e) {
            guiActivityPlan.showError("Error al guardar: " + e.getMessage());
        }
    }

    private void closeStageWithSuccess() {
        guiActivityPlan.showSuccess("Proyecto registrado correctamente.");
        guiActivityPlan.getStage().close();
    }
}