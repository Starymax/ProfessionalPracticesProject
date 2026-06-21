package mx.fei.gui.views;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mx.fei.gui.controllers.ControllerActivityPlan;
import mx.fei.gui.utils.GUIStyle;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.logic.dto.Activity;
import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.WeeklyLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

public class GUIActivityPlan extends Application {

    public static final int TOTAL_PLAN_HOURS = 420;
    public static final int TOTAL_WEEKS = 24;
    public static final int MAX_HOURS_PER_WEEK = 25;

    private final List<Activity> activities = new ArrayList<>();
    private final Map<Activity, Map<Integer, Integer>> activityWeekHours = new IdentityHashMap<>();
    private Activity selectedActivity;

    private VBox activityListBox;
    private TextField textFieldName;
    private TextArea textAreaDescription;
    private GridPane weekGrid;
    private Label labelTotals;
    private Button buttonDelete;

    private Project project;
    private Stage stage;
    private ControllerActivityPlan controller;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        controller = new ControllerActivityPlan(this);
        controller.setProject(project);

        Label labelTitle = new Label("Plan de Actividades");
        labelTitle.getStyleClass().add("title-label");

        VBox leftPanel = buildLeftPanel();
        VBox rightPanel = buildRightPanel();
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        HBox contentBox = new HBox(14, leftPanel, rightPanel);

        Button buttonCancel = new Button("Cancelar");
        Button buttonSave = new Button("Guardar plan");
        buttonCancel.setOnAction(controller::handleAddActivityDeleteSaveCancelButtons);
        buttonSave.setOnAction(controller::handleAddActivityDeleteSaveCancelButtons);

        HBox bottomBox = new HBox(8, buttonCancel, buttonSave);
        bottomBox.setAlignment(Pos.CENTER_RIGHT);

        VBox mainBox = new VBox(14, labelTitle, contentBox, bottomBox);
        mainBox.setPadding(new Insets(24, 32, 24, 32));

        Scene scene = new Scene(mainBox, 900, 525);
        GUIStyle.apply(scene);
        stage.setTitle("Plan de Actividades");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private VBox buildLeftPanel() {
        Label labelActivitiesHeader = new Label("Actividades");
        labelActivitiesHeader.getStyleClass().add("label-secondary");

        activityListBox = new VBox(6);
        ScrollPane scrollActivities = new ScrollPane(activityListBox);
        scrollActivities.setFitToWidth(true);
        scrollActivities.setPrefHeight(380);
        scrollActivities.getStyleClass().add("scroll-pane");

        Button buttonNewActivity = new Button("+ Nueva actividad");
        buttonNewActivity.setMaxWidth(Double.MAX_VALUE);
        buttonNewActivity.setOnAction(controller::handleAddActivityDeleteSaveCancelButtons);

        VBox leftPanel = new VBox(6, labelActivitiesHeader, scrollActivities, buttonNewActivity);
        leftPanel.setPrefWidth(200);
        return leftPanel;
    }

    private VBox buildRightPanel() {
        textFieldName = new TextField();
        textFieldName.setDisable(true);
        textFieldName.textProperty().addListener((observableValue, oldValue, newValue) -> onActivityNameChanged(newValue));
        textAreaDescription = new TextArea();
        textAreaDescription.setPrefRowCount(2);
        textAreaDescription.setWrapText(true);
        textAreaDescription.setDisable(true);
        textAreaDescription.textProperty().addListener((observableValue, oldValue, newValue) -> onActivityDescriptionChanged(newValue));
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(8);
        ColumnConstraints labelColumn = new ColumnConstraints(90);
        ColumnConstraints fieldColumn = new ColumnConstraints();
        fieldColumn.setHgrow(Priority.ALWAYS);
        formGrid.getColumnConstraints().addAll(labelColumn, fieldColumn);
        formGrid.add(new Label("Nombre"), 0, 0);
        formGrid.add(textFieldName, 1, 0);
        formGrid.add(new Label("Descripción"), 0, 1);
        formGrid.add(textAreaDescription, 1, 1);

        Label labelWeekHeader = new Label("Horas por semana");
        labelWeekHeader.getStyleClass().add("label-secondary");

        Region weekHeaderSpacer = new Region();
        HBox.setHgrow(weekHeaderSpacer, Priority.ALWAYS);
        HBox weekHeaderRow = new HBox(labelWeekHeader, weekHeaderSpacer);
        weekHeaderRow.setAlignment(Pos.CENTER_LEFT);

        weekGrid = new GridPane();
        weekGrid.setHgap(5);
        weekGrid.setVgap(5);
        for (int i = 0; i < 6; i++) {
            ColumnConstraints weekColumn = new ColumnConstraints(95, 95, 95);
            weekGrid.getColumnConstraints().add(weekColumn);
        }

        labelTotals = new Label("Selecciona o crea una actividad");
        labelTotals.getStyleClass().add("label-secondary");

        buttonDelete = new Button("Eliminar");
        buttonDelete.getStyleClass().add("button-danger");
        buttonDelete.setOnAction(controller::handleAddActivityDeleteSaveCancelButtons);
        buttonDelete.setDisable(true);

        Region panelBottomSpacer = new Region();
        HBox.setHgrow(panelBottomSpacer, Priority.ALWAYS);
        HBox panelBottomRow = new HBox(8, labelTotals, panelBottomSpacer, buttonDelete);
        panelBottomRow.setAlignment(Pos.CENTER_LEFT);

        VBox rightPanel = new VBox(10, formGrid, weekHeaderRow, weekGrid, new Separator(), panelBottomRow);
        rightPanel.setPadding(new Insets(10));
        rightPanel.getStyleClass().add("form-panel");
        return rightPanel;
    }

    public void refreshWeekGrid() {
        weekGrid.getChildren().clear();
        for (int week = 1; week <= TOTAL_WEEKS; week++) {
            int columnIndex = (week - 1) % 6;
            int rowIndex = (week - 1) / 6;
            weekGrid.add(buildWeekCell(week), columnIndex, rowIndex);
        }
    }

    private VBox buildWeekCell(int week) {
        int otherActivitiesHours = getOtherActivitiesHoursForWeek(week);
        int currentActivityHours = selectedActivity != null ? activityWeekHours.getOrDefault(selectedActivity, Collections.emptyMap()).getOrDefault(week, 0) : 0;
        boolean isWeekFull = otherActivitiesHours >= MAX_HOURS_PER_WEEK;
        boolean hasCurrentActivityHours = currentActivityHours > 0;
        int maxAllowedHours = Math.max(0, MAX_HOURS_PER_WEEK - otherActivitiesHours);

        Label labelWeekNumber = new Label("S" + week);
        labelWeekNumber.getStyleClass().add(hasCurrentActivityHours ? "week-label-active" : "week-label");

        HBox cellHeaderRow = new HBox(labelWeekNumber);
        cellHeaderRow.setAlignment(Pos.CENTER_LEFT);
        cellHeaderRow.setSpacing(3);

        if (otherActivitiesHours > 0) {
            String statusText = isWeekFull ? "Lleno" : maxAllowedHours + "h restantes";
            String statusColor = isWeekFull ? "#c62828" : "#854F0B";
            Label labelStatus = new Label(statusText);
            labelStatus.setStyle("-fx-font-size: 9px; -fx-text-fill: " + statusColor + ";");
            Region statusSpacer = new Region();
            HBox.setHgrow(statusSpacer, Priority.ALWAYS);
            cellHeaderRow.getChildren().addAll(statusSpacer, labelStatus);
        }

        TextField textFieldHours = new TextField(currentActivityHours > 0 ? String.valueOf(currentActivityHours) : "0");
        textFieldHours.setPrefHeight(24);
        textFieldHours.setMaxWidth(Double.MAX_VALUE);
        textFieldHours.setAlignment(Pos.CENTER);
        textFieldHours.setFont(javafx.scene.text.Font.font("SansSerif", 12));
        textFieldHours.setDisable(isWeekFull || selectedActivity == null);

        VBox weekCell = new VBox(2, cellHeaderRow, textFieldHours);
        weekCell.setPadding(new Insets(4, 5, 4, 5));
        weekCell.setMaxWidth(Double.MAX_VALUE);
        weekCell.getStyleClass().add("week-cell");
        if (hasCurrentActivityHours) {
            weekCell.getStyleClass().add("week-cell-active");
        }
        if (isWeekFull) {
            weekCell.getStyleClass().add("week-cell-full");
        }

        textFieldHours.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (selectedActivity != null && !newValue.matches("\\d*")) {
                Platform.runLater(() -> resetHoursField(textFieldHours, oldValue));
            } else {
                int parsedHours = newValue.isEmpty() ? 0 : Integer.parseInt(newValue);
                int clampedHours = clampHoursToLimits(parsedHours, oldValue, maxAllowedHours);
                if (clampedHours != parsedHours) {
                    Platform.runLater(() -> textFieldHours.setText(String.valueOf(clampedHours)));
                } else {
                    activityWeekHours.computeIfAbsent(selectedActivity, activityKey -> new HashMap<>()).put(week, parsedHours);
                    updateWeekCellStyle(weekCell, labelWeekNumber, parsedHours > 0);
                    refreshActivityList();
                    refreshTotalsLabel();
                }
            }
        });
        return weekCell;
    }

    private void onActivityNameChanged(String newValue) {
        if (selectedActivity != null) {
            selectedActivity.setName(newValue);
            refreshActivityList();
        }
    }

    private void onActivityDescriptionChanged(String newValue) {
        if (selectedActivity != null) {
            selectedActivity.setObservationsActivity(newValue);
        }
    }

    private void resetHoursField(TextField textFieldHours, String oldValue) {
        textFieldHours.setText(oldValue.matches("\\d*") ? oldValue : "0");
    }

    private int clampHoursToLimits(int parsedHours, String oldValue, int maxAllowedHours) {
        int previousHours = oldValue.matches("\\d+") ? Integer.parseInt(oldValue) : 0;
        int remainingPlanHours = TOTAL_PLAN_HOURS - getTotalPlannedHours() + previousHours;
        int effectiveMaximum = Math.min(maxAllowedHours, remainingPlanHours);
        return Math.min(parsedHours, effectiveMaximum);
    }

    private void updateWeekCellStyle(VBox weekCell, Label labelWeekNumber, boolean cellHasHours) {
        weekCell.getStyleClass().removeAll("week-cell-active");
        if (cellHasHours) {
            weekCell.getStyleClass().add("week-cell-active");
        }
        labelWeekNumber.getStyleClass().removeAll("week-label-active", "week-label");
        labelWeekNumber.getStyleClass().add(cellHasHours ? "week-label-active" : "week-label");
    }

    public void refreshActivityList() {
        activityListBox.getChildren().clear();
        for (Activity activity : activities) {
            activityListBox.getChildren().add(buildActivityCard(activity));
        }
    }

    private VBox buildActivityCard(Activity activity) {
        Map<Integer, Integer> activityHours = activityWeekHours.getOrDefault(activity, Collections.emptyMap());
        int totalActivityHours = activityHours.values().stream().mapToInt(Integer::intValue).sum();

        String displayName = (activity.getName() == null || activity.getName().isBlank()) ? "(sin nombre)" : activity.getName();
        Label labelActivityName = new Label(displayName);
        labelActivityName.getStyleClass().add("label-bold");

        String weekRange = computeWeekRange(activityHours);
        Label labelActivityInfo = new Label(weekRange + " · " + totalActivityHours + " h");
        labelActivityInfo.getStyleClass().add("label-hint");

        VBox activityCard = new VBox(2, labelActivityName, labelActivityInfo);
        activityCard.setPadding(new Insets(7, 9, 7, 9));
        activityCard.setCursor(Cursor.HAND);
        if (activity == selectedActivity) {
            activityCard.getStyleClass().add("activity-card-selected");
        } else {
            activityCard.getStyleClass().add("activity-card");
        }
        activityCard.setOnMouseClicked(mouseEvent -> selectActivity(activity));
        return activityCard;
    }

    public void selectActivity(Activity activity) {
        selectedActivity = activity;
        textFieldName.setDisable(false);
        textAreaDescription.setDisable(false);
        buttonDelete.setDisable(false);
        textFieldName.setText(activity.getName() != null ? activity.getName() : "");
        textAreaDescription.setText(activity.getObservationsActivity() != null ? activity.getObservationsActivity() : "");
        refreshWeekGrid();
        refreshActivityList();
        refreshTotalsLabel();
    }

    public void refreshTotalsLabel() {
        if (selectedActivity == null) {
            labelTotals.setText("Selecciona o crea una actividad");
        } else {
            int activityTotalHours = activityWeekHours.getOrDefault(selectedActivity, Collections.emptyMap()).values().stream().mapToInt(Integer::intValue).sum();
            int totalPlanHours = getTotalPlannedHours();
            int remainingPlanHours = Math.max(0, TOTAL_PLAN_HOURS - totalPlanHours);
            labelTotals.setText("Total actividad: " + activityTotalHours + " h  ·  Restante del plan: " + remainingPlanHours + " h");
        }
    }

    private String computeWeekRange(Map<Integer, Integer> activityHours) {
        OptionalInt minimumWeek = activityHours.entrySet().stream().filter(entry -> entry.getValue() > 0).mapToInt(Map.Entry::getKey).min();
        OptionalInt maximumWeek = activityHours.entrySet().stream().filter(entry -> entry.getValue() > 0).mapToInt(Map.Entry::getKey).max();
        String weekRange = "Sin semanas";
        if (!minimumWeek.isEmpty()) {
            weekRange = minimumWeek.getAsInt() == maximumWeek.getAsInt() ? "S" + minimumWeek.getAsInt() : "S" + minimumWeek.getAsInt() + "–S" + maximumWeek.getAsInt();
        }
        return weekRange;
    }

    private int getOtherActivitiesHoursForWeek(int week) {
        int totalOtherHours = 0;
        for (Map.Entry<Activity, Map<Integer, Integer>> entry : activityWeekHours.entrySet()) {
            if (entry.getKey() != selectedActivity) {
                totalOtherHours += entry.getValue().getOrDefault(week, 0);
            }
        }
        return totalOtherHours;
    }

    public int getTotalPlannedHours() {
        return activityWeekHours.values().stream().flatMap(weekHoursMap -> weekHoursMap.values().stream()).mapToInt(Integer::intValue).sum();
    }

    public Map<Activity, ArrayList<WeeklyLog>> getWeeklyLogsMap() {
        Map<Activity, ArrayList<WeeklyLog>> weeklyLogsMap = new HashMap<>();
        for (Activity activity : activities) {
            ArrayList<WeeklyLog> activityWeeklyLogs = new ArrayList<>();
            Map<Integer, Integer> activityHours = activityWeekHours.getOrDefault(activity, Collections.emptyMap());
            for (Map.Entry<Integer, Integer> entry : activityHours.entrySet()) {
                if (entry.getValue() > 0) {
                    activityWeeklyLogs.add(new WeeklyLog(0, entry.getKey(), 0, entry.getValue(), activity));
                }
            }
            weeklyLogsMap.put(activity, activityWeeklyLogs);
        }
        return weeklyLogsMap;
    }

    public void addActivity(Activity activity) {
        activities.add(activity);
        activityWeekHours.put(activity, new HashMap<>());
        selectActivity(activity);
        refreshActivityList();
    }

    public void removeSelectedActivity() {
        if (selectedActivity != null) {
            activities.remove(selectedActivity);
            activityWeekHours.remove(selectedActivity);
            selectedActivity = null;
            textFieldName.setText("");
            textFieldName.setDisable(true);
            textAreaDescription.setText("");
            textAreaDescription.setDisable(true);
            buttonDelete.setDisable(true);
            weekGrid.getChildren().clear();
            refreshActivityList();
            refreshTotalsLabel();
        }
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public Activity getSelectedActivity() {
        return selectedActivity;
    }

    public Map<Activity, Map<Integer, Integer>> getActivityWeekHours() {
        return activityWeekHours;
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

    public Stage getStage() {
        return stage;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
