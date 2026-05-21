package mx.fei.gui.views;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import mx.fei.gui.controllers.ControllerRegisterAdvance;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.logic.dto.Student;
import mx.fei.logic.dto.WeeklyLog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUIRegisterAdvance {

    private final Student student;
    private Stage stage;
    private ComboBox<String> comboWeeks;
    private Label labelPlanned;
    private Label labelCurrent;
    private TextField fieldRealized;
    private final Map<Integer, List<WeeklyLog>> weeklyLogsByWeek = new HashMap<>();
    private ControllerRegisterAdvance controller;

    public GUIRegisterAdvance(Student student) {
        this.student = student;
    }

    public void start(Stage stage) {
        this.stage = stage;
        controller = new ControllerRegisterAdvance(this);

        BorderPane main = new BorderPane();
        main.setPadding(new Insets(24));

        Label title = new Label("Avance de actividades:");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 22));

        comboWeeks = new ComboBox<>();
        comboWeeks.setPrefWidth(220);
        comboWeeks.setOnAction(e -> controller.handleWeekSelection());

        HBox topRow = new HBox(12, comboWeeks);
        topRow.setAlignment(Pos.CENTER_LEFT);

        labelPlanned = new Label("Horas planeadas: 0");
        labelPlanned.setFont(Font.font(15));

        labelCurrent = new Label("Horas realizadas actuales: 0");
        labelCurrent.setFont(Font.font(15));

        Label labelNew = new Label("Horas nuevas:");
        fieldRealized = new TextField();
        fieldRealized.setPrefWidth(100);
        fieldRealized.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null && !newValue.matches("\\d*")) {
                fieldRealized.setText(newValue.replaceAll("\\D", ""));
            }
        });

        HBox infoBox = new HBox(12, labelPlanned, labelCurrent, labelNew, fieldRealized);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        Button buttonSave = new Button("Guardar");
        buttonSave.setPrefWidth(120);
        buttonSave.setOnAction(controller::handleSaveButton);

        Button buttonBack = new Button("Regresar");
        buttonBack.setPrefWidth(120);
        buttonBack.setOnAction(controller::handleBackButton);

        HBox buttons = new HBox(12, buttonSave, buttonBack);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        VBox center = new VBox(18, title, topRow, infoBox, buttons);
        center.setPadding(new Insets(12));
        main.setCenter(center);

        Scene scene = new Scene(main, 640, 320);
        stage.setTitle("Registro de Avances");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        controller.loadWeeks();
    }

    public void addWeeklyLog(int week, WeeklyLog log) {
        weeklyLogsByWeek.computeIfAbsent(week, k -> new ArrayList<>()).add(log);
    }

    public void clearWeeklyLogs() {
        weeklyLogsByWeek.clear();
    }

    public void setWeekOptions(List<String> items) {
        comboWeeks.setItems(FXCollections.observableArrayList(items));
    }

    public void selectFirstWeek() {
        if (!comboWeeks.getItems().isEmpty()) {
            comboWeeks.getSelectionModel().selectFirst();
            controller.handleWeekSelection();
        }
    }

    public String getSelectedWeek() {
        return comboWeeks.getSelectionModel().getSelectedItem();
    }

    public List<WeeklyLog> getWeeklyLogsForWeek(int week) {
        return weeklyLogsByWeek.getOrDefault(week, new ArrayList<>());
    }

    public TextField getFieldRealized() {
        return fieldRealized;
    }

    public Student getStudent() {
        return student;
    }

    public void setPlannedHours(int hours) {
        labelPlanned.setText("Horas planeadas: " + hours);
    }

    public void setCurrentRealized(int hours) {
        labelCurrent.setText("Horas realizadas actuales: " + hours);
    }

    public void resetRealizedField() {
        fieldRealized.setText("");
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
