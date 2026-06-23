package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;
import mx.fei.gui.controllers.ControllerRegisterAdvance;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.logic.dto.Activity;
import mx.fei.logic.dto.Student;
import mx.fei.logic.dto.WeeklyLog;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GUIRegisterAdvance {

    private static final String COMPLETED_COLOR = "#f4a9a8";
    private final Student student;
    private Stage stage;
    private ListView<Activity> listViewActivities;
    private Set<Integer> completedActivityIds = new HashSet<>();
    private Label labelMonth;
    private Label labelWeek;
    private Label labelPlannedHours;
    private Label labelRealizedHours;
    private Label labelRemainingHours;
    private TextField textFieldNewHours;
    private Button buttonSave;

    private final Map<Integer, List<WeeklyLog>> weeklyLogsByWeek = new HashMap<>();
    private final Map<Integer, String> pendingHoursByActivityId = new HashMap<>();
    private Activity previouslySelectedActivity;

    private ControllerRegisterAdvance controllerRegisterAdvance;

    public GUIRegisterAdvance(Student student) {
        this.student = student;
    }

    public void start(Stage stage) {
        this.stage = stage;
        controllerRegisterAdvance = new ControllerRegisterAdvance(this);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(24));

        HBox content = new HBox(32, buildLeftPanel(), buildRightPanel());
        content.setPadding(new Insets(8));
        HBox.setHgrow(content, Priority.ALWAYS);

        root.setCenter(content);

        Scene scene = new Scene(root, 820, 560);
        GUIStyle.apply(scene);
        stage.setTitle("Registro de Avances");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        controllerRegisterAdvance.loadWeeks();
    }


    public void addWeeklyLog(int week, WeeklyLog weeklyLog) {
        weeklyLogsByWeek.computeIfAbsent(week, k -> new ArrayList<>()).add(weeklyLog);
    }

    public void clearWeeklyLogs() {
        weeklyLogsByWeek.clear();
    }

    public List<WeeklyLog> getWeeklyLogsForWeek(int week) {
        return weeklyLogsByWeek.getOrDefault(week, new ArrayList<>());
    }

    public void setAllActivities(List<Activity> activities, Set<Integer> completedIds) {
        this.completedActivityIds = completedIds != null ? completedIds : new HashSet<>();
        pendingHoursByActivityId.clear();
        previouslySelectedActivity = null;
        listViewActivities.getItems().setAll(activities);
        if (!activities.isEmpty()) {
            listViewActivities.getSelectionModel().selectFirst();
        }
        listViewActivities.refresh();
    }

    public Activity getSelectedActivity() {
        return listViewActivities.getSelectionModel().getSelectedItem();
    }

    public List<Activity> getAllActivities() {
        return listViewActivities.getItems();
    }

    public void setCompletedActivityIds(Set<Integer> ids) {
        this.completedActivityIds = ids != null ? ids : new HashSet<>();
        listViewActivities.refresh();
    }

    public void setActivityInfo(int firstWeek, int totalPlanned, int totalRealized) {
        int month = (firstWeek - 1) / 4 + 1;
        labelMonth.setText(String.valueOf(month));
        labelWeek.setText("Semana " + firstWeek);
        labelPlannedHours.setText(String.valueOf(totalPlanned));
        labelRealizedHours.setText(String.valueOf(totalRealized));
        labelRemainingHours.setText(String.valueOf(Math.max(totalPlanned - totalRealized, 0)));
    }

    public void clearActivityInfo() {
        labelMonth.setText("-");
        labelWeek.setText("-");
        labelPlannedHours.setText("-");
        labelRealizedHours.setText("-");
        labelRemainingHours.setText("-");
        textFieldNewHours.setText("");
    }

    public void disableCurrentWeekSection() {
        buttonSave.setDisable(true);
        textFieldNewHours.setDisable(true);
    }

    public void savePendingHoursForPreviousActivity() {
        if (previouslySelectedActivity != null) {
            String text = textFieldNewHours.getText().trim();
            if (!text.isEmpty()) {
                pendingHoursByActivityId.put(previouslySelectedActivity.getActivityId(), text);
            } else {
                pendingHoursByActivityId.remove(previouslySelectedActivity.getActivityId());
            }
        }
    }

    public void restorePendingHoursForCurrentActivity(Activity selected) {
        previouslySelectedActivity = selected;
        if (selected != null) {
            textFieldNewHours.setText(pendingHoursByActivityId.getOrDefault(selected.getActivityId(), ""));
        } else {
            textFieldNewHours.setText("");
        }
    }

    public Map<Integer, String> getPendingHoursByActivityId() {
        savePendingHoursForPreviousActivity();
        return pendingHoursByActivityId;
    }

    public void clearPendingHours() {
        pendingHoursByActivityId.clear();
        previouslySelectedActivity = null;
        textFieldNewHours.setText("");
    }

    public String getNewHoursText() {
        return textFieldNewHours.getText().trim();
    }

    public void showError(String message) {
        GUIUtils.showError(message);
    }

    public void showSuccess(String message) {
        GUIUtils.showSuccess(message);
    }

    public void closeWindow() {
        GUIUtils.closeWindow(stage);
    }

    public Student getStudent() {
        return student;
    }

    private VBox buildLeftPanel() {
        Label title = new Label("Plan de Actividades");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 18));
        listViewActivities = new ListView<>();
        listViewActivities.setPrefWidth(380);
        listViewActivities.setPrefHeight(420);
        listViewActivities.setCellFactory(lv -> new ListCell<Activity>() {
            @Override
            protected void updateItem(Activity item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.getName());
                    setFont(Font.font(14));
                    setPadding(new Insets(8, 12, 8, 12));
                    if (completedActivityIds.contains(item.getActivityId())) {
                        setStyle("-fx-background-color: " + COMPLETED_COLOR + ";");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
        listViewActivities.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            savePendingHoursForPreviousActivity();
            controllerRegisterAdvance.handleActivitySelection(newVal);
        });

        VBox left = new VBox(12, title, listViewActivities);
        left.setAlignment(Pos.TOP_LEFT);
        return left;
    }

    private VBox buildRightPanel() {
        Label labelMesTitle = new Label("Mes: ");
        labelMesTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        labelMonth = new Label("-");
        labelMonth.setFont(Font.font(14));
        HBox rowMes = new HBox(4, labelMesTitle, labelMonth);
        rowMes.setAlignment(Pos.CENTER_LEFT);

        Label labelSemanaTitle = new Label("Semana: ");
        labelSemanaTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        labelWeek = new Label("-");
        labelWeek.setFont(Font.font(14));
        HBox rowSemana = new HBox(4, labelSemanaTitle, labelWeek);
        rowSemana.setAlignment(Pos.CENTER_LEFT);

        Label labelPlannedTitle = new Label("Horas planeadas: ");
        labelPlannedTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        labelPlannedHours = new Label("-");
        labelPlannedHours.setFont(Font.font(14));
        HBox rowPlanned = new HBox(4, labelPlannedTitle, labelPlannedHours);
        rowPlanned.setAlignment(Pos.CENTER_LEFT);

        Label labelRealizedTitle = new Label("Horas realizadas: ");
        labelRealizedTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        labelRealizedHours = new Label("-");
        labelRealizedHours.setFont(Font.font(14));
        HBox rowRealized = new HBox(4, labelRealizedTitle, labelRealizedHours);
        rowRealized.setAlignment(Pos.CENTER_LEFT);

        Label labelRemainingTitle = new Label("Horas restantes: ");
        labelRemainingTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        labelRemainingHours = new Label("-");
        labelRemainingHours.setFont(Font.font(14));
        HBox rowRemaining = new HBox(4, labelRemainingTitle, labelRemainingHours);
        rowRemaining.setAlignment(Pos.CENTER_LEFT);

        Label labelNewHours = new Label("Horas nuevas: ");
        labelNewHours.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        textFieldNewHours = new TextField();
        textFieldNewHours.setPrefWidth(100);
        textFieldNewHours.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.matches("\\d*")) {
                textFieldNewHours.setText(newVal.replaceAll("\\D", ""));
            }
        });
        HBox rowNewHours = new HBox(4, labelNewHours, textFieldNewHours);
        rowNewHours.setAlignment(Pos.CENTER_LEFT);

        buttonSave = new Button("Guardar");
        buttonSave.setPrefWidth(180);
        buttonSave.setPrefHeight(44);
        buttonSave.setFont(Font.font(15));
        buttonSave.setOnAction(controllerRegisterAdvance::handleSaveCancelButtons);

        Button buttonCancel = new Button("Cancelar");
        buttonCancel.setPrefWidth(180);
        buttonCancel.setPrefHeight(44);
        buttonCancel.setFont(Font.font(15));
        buttonCancel.setOnAction(controllerRegisterAdvance::handleSaveCancelButtons);

        VBox right = new VBox(16,
                rowMes, rowSemana,
                new Separator(),
                rowPlanned, rowRealized, rowRemaining,
                new Separator(),
                rowNewHours,
                buttonSave, buttonCancel);
        right.setAlignment(Pos.TOP_LEFT);
        right.setPadding(new Insets(48, 12, 12, 12));
        right.setPrefWidth(340);
        return right;
    }
}
