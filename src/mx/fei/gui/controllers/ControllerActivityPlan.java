package mx.fei.gui.controllers;

import javafx.stage.Modality;
import mx.fei.gui.views.GUIActivityPlan;
import mx.fei.gui.views.GUIRegisterActivity;
import mx.fei.logic.dao.ActivityDAO;
import mx.fei.logic.dao.ProjectDAO;
import mx.fei.logic.dto.Activity;
import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.WeeklyLog;
import mx.fei.logic.exceptions.DataOperationException;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ControllerActivityPlan {

    private final GUIActivityPlan guiActivityPlan;
    private Project project;

    public ControllerActivityPlan(GUIActivityPlan guiActivityPlan) {
        this.guiActivityPlan = guiActivityPlan;
    }

    public void handleButtons(ActionEvent event) {
        if (event.getSource() == guiActivityPlan.getButtonAddActivity()) {
            openAddActivity();
        } else if (event.getSource() == guiActivityPlan.getButtonSave()) {
            savePlan();
        } else if (event.getSource() == guiActivityPlan.getButtonCancel()) {
            cancel();
        }
    }

    private void openAddActivity() {
        try {
            int remainingHours = guiActivityPlan.getRemainingPlannedHours();
            if (remainingHours == 0) {
                guiActivityPlan.showError("El plan ya tiene 240 horas planeadas. No se pueden agregar más actividades.");
                return;
            }
            GUIRegisterActivity guiRegisterActivity = new GUIRegisterActivity();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            guiRegisterActivity.start(stage);
            guiRegisterActivity.setProject(project);
            guiRegisterActivity.setGuiActivityPlan(guiActivityPlan);
            guiRegisterActivity.setRemainingAllowedHours(remainingHours);
        } catch (Exception exception) {
            guiActivityPlan.showError(exception.getMessage());
        }
    }

    private void savePlan() {
        List<Activity> activities = guiActivityPlan.getActivities();
        if (activities.isEmpty()) {
            guiActivityPlan.showError("Debe añadir al menos una actividad para guardar.");
        } else if (guiActivityPlan.getTotalPlannedHours() != GUIActivityPlan.TOTAL_PLAN_HOURS) {
            guiActivityPlan.showError("La suma total de horas planeadas debe ser exactamente " + GUIActivityPlan.TOTAL_PLAN_HOURS + " horas para guardar el plan.");
        } else {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmar");
            confirmation.setHeaderText(null);
            confirmation.setContentText("¿Seguro que desea guardar el proyecto con este plan");
            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                saveProject(project);
                saveActivities(activities);
                guiActivityPlan.showSuccess("Proyecto Registrado correctamente");
            }
        }
    }

    private void saveActivities(List<Activity> activities) {
        try {
            ActivityDAO activityDAO = new ActivityDAO();
            Map<Activity, ArrayList<WeeklyLog>> weeklyLogsMap = guiActivityPlan.getWeeklyLogsMap();
            for (Activity activity : activities) {
                ArrayList<WeeklyLog> weeklyLogs = weeklyLogsMap.get(activity);
                activityDAO.insertActivity(activity, project, weeklyLogs);
            }
            guiActivityPlan.getStage().close();
        } catch (DataOperationException exception) {
            guiActivityPlan.showError("Error al guardar el plan de actividades: " + exception.getMessage());
        }
    }

    private void saveProject(Project project) {
        try {
            ProjectDAO projectDAO = new ProjectDAO();
            project.setProjectId(projectDAO.registerProject(project));
        } catch (DataOperationException e) {
            guiActivityPlan.showError(e.getMessage());
        }
    }

    private void cancel() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Cancelar");
        confirmation.setHeaderText(null);
        confirmation.setContentText("¿Seguro que desea cancelar? Se perderá la información ingresada.");
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            guiActivityPlan.getStage().close();
        }
    }

    public void setProject(Project project) {
        this.project = project;
    }
}