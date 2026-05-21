package mx.fei.gui.views;

import mx.fei.gui.controllers.ControllerActivityPlan;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.logic.dto.Activity;
import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.WeeklyLog;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUIActivityPlan extends Application {

    public static final int TOTAL_PLAN_HOURS = 420;

    private ListView<Activity> listViewActivities;
    private Label labelActivityName;
    private Label labelHoursSummary;
    private VBox weeklyLogsPanel;
    private Button buttonAddActivity;
    private Button buttonSave;
    private Button buttonCancel;
    private Stage stage;
    private final Map<Activity, ArrayList<WeeklyLog>> weeklyLogsMap = new HashMap<>();
    private Project project;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        Label labelTitle = new Label("Plan de Actividades");
        labelTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 18));
        labelHoursSummary = new Label();
        labelHoursSummary.setFont(Font.font("SansSerif", FontWeight.NORMAL, 14));
        updateHoursSummary();
        listViewActivities = new ListView<>();
        listViewActivities.setPrefWidth(360);
        listViewActivities.setPrefHeight(340);
        listViewActivities.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Activity activity, boolean empty) {
                super.updateItem(activity, empty);
                setText(empty || activity == null ? null : activity.getName());
                setFont(Font.font("SansSerif", 14));
            }
        });
        listViewActivities.getSelectionModel().selectedItemProperty().addListener((observable, oldActivity, newActivity) -> {
            if (newActivity != null) {
                    loadActivityDetail(newActivity);
                }
            }
        );

        labelActivityName = new Label("");
        labelActivityName.setFont(Font.font("SansSerif", FontWeight.NORMAL, 16));

        weeklyLogsPanel = new VBox(8);
        weeklyLogsPanel.setAlignment(Pos.CENTER);

        VBox detailPanel = new VBox(16, labelActivityName, weeklyLogsPanel);
        detailPanel.setAlignment(Pos.TOP_CENTER);
        detailPanel.setPadding(new Insets(0, 0, 0, 40));
        detailPanel.setPrefWidth(320);

        HBox contentPanel = new HBox(listViewActivities, detailPanel);
        contentPanel.setAlignment(Pos.TOP_LEFT);

        buttonAddActivity = new Button("Añadir Actividad");
        buttonSave = new Button("Guardar");
        buttonCancel = new Button("Cancelar");

        String buttonStyle = "-fx-background-color: #1e1e23; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-background-radius: 10;";
        buttonAddActivity.setStyle(buttonStyle);
        buttonSave.setStyle(buttonStyle);
        buttonCancel.setStyle(buttonStyle);
        buttonAddActivity.setPrefWidth(200);
        buttonSave.setPrefWidth(140);
        buttonCancel.setPrefWidth(140);

        ControllerActivityPlan controllerActivityPlan = new ControllerActivityPlan(this);
        buttonAddActivity.setOnAction(controllerActivityPlan::handleAddActivitySavePlanCancelButtons);
        buttonSave.setOnAction(controllerActivityPlan::handleAddActivitySavePlanCancelButtons);
        buttonCancel.setOnAction(controllerActivityPlan::handleAddActivitySavePlanCancelButtons);
        controllerActivityPlan.setProject(project);

        HBox buttonRightPanel = new HBox(12, buttonSave, buttonCancel);
        buttonRightPanel.setAlignment(Pos.CENTER_RIGHT);

        BorderPane buttonPanel = new BorderPane();
        buttonPanel.setLeft(buttonAddActivity);
        buttonPanel.setRight(buttonRightPanel);

        VBox mainPanel = new VBox(16, labelTitle, labelHoursSummary, contentPanel, buttonPanel);
        mainPanel.setPadding(new Insets(32, 40, 32, 40));

        Scene scene = new Scene(mainPanel, 780, 460);
        stage.setTitle("Plan de Actividades");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private void loadActivityDetail(Activity activity) {
        labelActivityName.setText(activity.getName());
        weeklyLogsPanel.getChildren().clear();
        ArrayList<WeeklyLog> weeklyLogs = weeklyLogsMap.get(activity);
        for (WeeklyLog weeklyLog : weeklyLogs) {
            Label labelWeeklyLog = new Label("S" + weeklyLog.getWeek() + " - " + weeklyLog.getPlannedHours() + " hrs");
            labelWeeklyLog.setFont(Font.font("SansSerif", 14));
            weeklyLogsPanel.getChildren().add(labelWeeklyLog);
        }
    }

    public void addActivity(Activity activity, ArrayList<WeeklyLog> weeklyLogs) {
        listViewActivities.getItems().add(activity);
        weeklyLogsMap.put(activity, weeklyLogs);
        updateHoursSummary();
    }

    public List<Activity> getActivities() {
        return listViewActivities.getItems();
    }

    public Map<Activity, ArrayList<WeeklyLog>> getWeeklyLogsMap() {
        return weeklyLogsMap;
    }

    public int getTotalPlannedHours() {
        return weeklyLogsMap.values().stream().flatMap(List::stream).mapToInt(WeeklyLog::getPlannedHours).sum();
    }

    public int getRemainingPlannedHours() {
        return Math.max(TOTAL_PLAN_HOURS - getTotalPlannedHours(), 0);
    }

    public void updateHoursSummary() {
        int total = getTotalPlannedHours();
        int remaining = getRemainingPlannedHours();
        labelHoursSummary.setText("Horas planeadas: " + total + " / " + TOTAL_PLAN_HOURS + "    Restantes: " + remaining);
    }

    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showSuccess(String message) {
        GUIUtils.showSuccess(message);
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Button getButtonAddActivity() {
        return buttonAddActivity;
    }

    public Button getButtonSave() {
        return buttonSave;
    }

    public Button getButtonCancel() {
        return buttonCancel;
    }

    public Stage getStage() {
        return stage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}