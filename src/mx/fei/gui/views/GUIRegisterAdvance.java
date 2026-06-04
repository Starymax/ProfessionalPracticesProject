package mx.fei.gui.views;

import mx.fei.gui.controllers.ControllerRegisterAdvance;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.logic.dto.Student;
import mx.fei.logic.dto.WeeklyLog;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
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

public class GUIRegisterAdvance {

    private final Student student;
    private Stage stage;

    private VBox currentWeekSection;
    private Label labelCurrentWeek;
    private int currentWeek;
    private ComboBox<String> comboBoxActivities;
    private Label labelPlanned;
    private Label labelCurrent;
    private TextField textFieldRealized;
    private Button buttonSave;

    private ComboBox<String> comboBoxPastWeeks;
    private ComboBox<String> comboBoxPastActivities;
    private Label labelPastPlanned;
    private Label labelPastCurrent;
    private Label labelPastMissing;
    private TextField textFieldPastRealized;

    private final Map<Integer, List<WeeklyLog>> weeklyLogsByWeek = new HashMap<>();
    private final Map<String, WeeklyLog> logByActivityName = new HashMap<>();
    private final Map<String, WeeklyLog> pastLogByActivityName = new HashMap<>();
    private final Map<Integer, String> pendingHoursByLogId = new HashMap<>();
    private WeeklyLog previouslySelectedLog;

    private ControllerRegisterAdvance controllerRegisterAdvance;

    public GUIRegisterAdvance(Student student) {
        this.student = student;
    }

    public void start(Stage stage) {
        this.stage = stage;
        controllerRegisterAdvance = new ControllerRegisterAdvance(this);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(24));

        Label title = new Label("Avance de actividades:");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 22));

        Label labelWeek = new Label("Semana:");
        labelCurrentWeek = new Label();
        labelCurrentWeek.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        HBox rowWeeks = new HBox(10, labelWeek, labelCurrentWeek);
        rowWeeks.setAlignment(Pos.CENTER_LEFT);

        Label labelActivity = new Label("Actividad:");
        comboBoxActivities = new ComboBox<>();
        comboBoxActivities.setPrefWidth(220);
        comboBoxActivities.setOnAction(event -> controllerRegisterAdvance.handleActivitySelection());
        HBox rowActivities = new HBox(10, labelActivity, comboBoxActivities);
        rowActivities.setAlignment(Pos.CENTER_LEFT);

        labelPlanned = new Label("Horas planeadas: 0");
        labelPlanned.setFont(Font.font(14));
        labelCurrent = new Label("Horas realizadas: 0");
        labelCurrent.setFont(Font.font(14));
        Label labelNew = new Label("Horas nuevas:");
        textFieldRealized = new TextField();
        textFieldRealized.setPrefWidth(80);
        textFieldRealized.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.matches("\\d*")) {
                textFieldRealized.setText(newValue.replaceAll("\\D", ""));
            }
        });
        HBox rowInfo = new HBox(12, labelPlanned, labelCurrent, labelNew, textFieldRealized);
        rowInfo.setAlignment(Pos.CENTER_LEFT);

        buttonSave = new Button("Guardar");
        buttonSave.setPrefWidth(120);
        buttonSave.setOnAction(controllerRegisterAdvance::handleSaveButton);

        currentWeekSection = new VBox(10, rowWeeks, rowActivities, rowInfo, buttonSave);

        Separator separator = new Separator();

        Label titlePending = new Label("Semanas con horas pendientes:");
        titlePending.setFont(Font.font("SansSerif", FontWeight.BOLD, 15));

        Label labelPastWeek = new Label("Semana:");
        comboBoxPastWeeks = new ComboBox<>();
        comboBoxPastWeeks.setPrefWidth(160);
        comboBoxPastWeeks.setOnAction(event -> controllerRegisterAdvance.handlePastWeekSelection());
        HBox rowPastWeeks = new HBox(10, labelPastWeek, comboBoxPastWeeks);
        rowPastWeeks.setAlignment(Pos.CENTER_LEFT);

        Label labelPastActivity = new Label("Actividad:");
        comboBoxPastActivities = new ComboBox<>();
        comboBoxPastActivities.setPrefWidth(220);
        comboBoxPastActivities.setOnAction(event -> controllerRegisterAdvance.handlePastActivitySelection());
        HBox rowPastActivities = new HBox(10, labelPastActivity, comboBoxPastActivities);
        rowPastActivities.setAlignment(Pos.CENTER_LEFT);

        labelPastPlanned = new Label("Horas planeadas: 0");
        labelPastPlanned.setFont(Font.font(14));
        labelPastCurrent = new Label("Horas realizadas: 0");
        labelPastCurrent.setFont(Font.font(14));
        labelPastMissing = new Label("Horas faltantes: 0");
        labelPastMissing.setFont(Font.font(14));
        labelPastMissing.setStyle("-fx-text-fill: #b71c1c;");
        HBox rowPastInfo = new HBox(12, labelPastPlanned, labelPastCurrent, labelPastMissing);
        rowPastInfo.setAlignment(Pos.CENTER_LEFT);

        Label labelPastNew = new Label("Horas nuevas:");
        textFieldPastRealized = new TextField();
        textFieldPastRealized.setPrefWidth(80);
        textFieldPastRealized.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.matches("\\d*")) {
                textFieldPastRealized.setText(newValue.replaceAll("\\D", ""));
            }
        });
        HBox rowPastNew = new HBox(10, labelPastNew, textFieldPastRealized);
        rowPastNew.setAlignment(Pos.CENTER_LEFT);

        Button buttonSavePast = new Button("Guardar");
        buttonSavePast.setPrefWidth(120);
        buttonSavePast.setOnAction(controllerRegisterAdvance::handleSavePastButton);

        Button buttonBack = new Button("Regresar");
        buttonBack.setPrefWidth(120);
        buttonBack.setOnAction(controllerRegisterAdvance::handleBackButton);

        HBox buttons = new HBox(12, buttonBack);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        VBox pendingSection = new VBox(10, titlePending, rowPastWeeks, rowPastActivities, rowPastInfo, rowPastNew, buttonSavePast);

        VBox center = new VBox(14, title, currentWeekSection, separator, pendingSection, buttons);
        center.setPadding(new Insets(12));
        root.setCenter(center);

        Scene scene = new Scene(root, 720, 580);
        stage.setTitle("Registro de Avances");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        controllerRegisterAdvance.loadWeeks();
    }

    public void addWeeklyLog(int week, WeeklyLog weeklyLog) {
        weeklyLogsByWeek.computeIfAbsent(week, key -> new ArrayList<>()).add(weeklyLog);
    }

    public void clearWeeklyLogs() {
        weeklyLogsByWeek.clear();
    }

    public void disableCurrentWeekSection() {
        labelCurrentWeek.setText("Proyecto finalizado");
        comboBoxActivities.setDisable(true);
        textFieldRealized.setDisable(true);
        buttonSave.setDisable(true);
    }

    public void setCurrentWeek(int week) {
        this.currentWeek = week;
        labelCurrentWeek.setText("Semana " + week);
    }

    public List<WeeklyLog> getWeeklyLogsForWeek(int week) {
        return weeklyLogsByWeek.getOrDefault(week, new ArrayList<>());
    }

    public void setActivityOptions(List<WeeklyLog> weeklyLogs) {
        logByActivityName.clear();
        pendingHoursByLogId.clear();
        previouslySelectedLog = null;
        List<String> activityNames = new ArrayList<>();
        for (WeeklyLog weeklyLog : weeklyLogs) {
            String activityName = weeklyLog.getActivity().getName();
            activityNames.add(activityName);
            logByActivityName.put(activityName, weeklyLog);
        }
        comboBoxActivities.setItems(FXCollections.observableArrayList(activityNames));
        if (!activityNames.isEmpty()) {
            comboBoxActivities.getSelectionModel().selectFirst();
            controllerRegisterAdvance.handleActivitySelection();
        }
    }

    public WeeklyLog getSelectedWeeklyLog() {
        String selectedActivity = comboBoxActivities.getSelectionModel().getSelectedItem();
        return (selectedActivity != null && !selectedActivity.isEmpty()) ? logByActivityName.get(selectedActivity) : null;
    }

    public void savePendingHoursForPreviousActivity() {
        if (previouslySelectedLog != null) {
            String currentText = textFieldRealized.getText().trim();
            if (!currentText.isEmpty()) {
                pendingHoursByLogId.put(previouslySelectedLog.getWeeklyLogId(), currentText);
            } else {
                pendingHoursByLogId.remove(previouslySelectedLog.getWeeklyLogId());
            }
        }
    }

    public void restorePendingHoursForCurrentActivity() {
        WeeklyLog selected = getSelectedWeeklyLog();
        previouslySelectedLog = selected;
        if (selected != null) {
            String pending = pendingHoursByLogId.getOrDefault(selected.getWeeklyLogId(), "");
            textFieldRealized.setText(pending);
        } else {
            textFieldRealized.setText("");
        }
    }

    public Map<Integer, String> getPendingHoursByLogId() {
        savePendingHoursForPreviousActivity();
        return pendingHoursByLogId;
    }

    public Map<String, WeeklyLog> getLogByActivityName() {
        return logByActivityName;
    }

    public void clearPendingHours() {
        pendingHoursByLogId.clear();
        previouslySelectedLog = null;
        textFieldRealized.setText("");
    }

    public TextField getTextFieldRealized() {
        return textFieldRealized;
    }

    public void setPlannedHours(int hours) {
        labelPlanned.setText("Horas planeadas: " + hours);
    }

    public void setCurrentRealized(int hours) {
        labelCurrent.setText("Horas realizadas: " + hours);
    }

    public void resetRealizedField() {
        textFieldRealized.setText("");
    }

    public void setPastWeekOptions(List<String> weekOptions) {
        comboBoxPastWeeks.setItems(FXCollections.observableArrayList(weekOptions));
        if (!weekOptions.isEmpty()) {
            comboBoxPastWeeks.getSelectionModel().selectFirst();
            controllerRegisterAdvance.handlePastWeekSelection();
        }
    }

    public String getSelectedPastWeek() {
        return comboBoxPastWeeks.getSelectionModel().getSelectedItem();
    }

    public void setPastActivityOptions(List<WeeklyLog> weeklyLogs) {
        pastLogByActivityName.clear();
        List<String> activityNames = new ArrayList<>();
        for (WeeklyLog weeklyLog : weeklyLogs) {
            String activityName = weeklyLog.getActivity().getName();
            activityNames.add(activityName);
            pastLogByActivityName.put(activityName, weeklyLog);
        }
        comboBoxPastActivities.setItems(FXCollections.observableArrayList(activityNames));
        if (!activityNames.isEmpty()) {
            comboBoxPastActivities.getSelectionModel().selectFirst();
            controllerRegisterAdvance.handlePastActivitySelection();
        }
    }

    public WeeklyLog getSelectedPastWeeklyLog() {
        String selectedActivity = comboBoxPastActivities.getSelectionModel().getSelectedItem();
        return (selectedActivity != null && !selectedActivity.isEmpty()) ? pastLogByActivityName.get(selectedActivity) : null;
    }

    public TextField getTextFieldPastRealized() {
        return textFieldPastRealized;
    }

    public void setPastPlannedHours(int hours) {
        labelPastPlanned.setText("Horas planeadas: " + hours);
    }

    public void setPastCurrentRealized(int hours) {
        labelPastCurrent.setText("Horas realizadas: " + hours);
    }

    public void setPastMissingHours(int hours) {
        labelPastMissing.setText("Horas faltantes: " + hours);
    }

    public void resetPastRealizedField() {
        textFieldPastRealized.setText("");
    }

    public Student getStudent() {
        return student;
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
}
