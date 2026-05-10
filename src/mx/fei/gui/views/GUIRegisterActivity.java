package mx.fei.gui.views;

import mx.fei.gui.controllers.ControllerRegisterActivity;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUIRegisterActivity extends Application {

    private static final int TOTAL_WEEKS = 8;

    private TextField textFieldActivityName;
    private TextArea textAreaDescription;
    private ComboBox<Integer> comboBoxWeek;
    private Spinner<Integer> spinnerPlannedHours;
    private Button buttonSave;
    private Button buttonCancel;
    private Stage stage;
    private final Map<Integer, Integer> plannedHoursPerWeek = new HashMap<>();
    private Project project;
    private GUIActivityPlan guiActivityPlan;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        for (int week = 1; week <= TOTAL_WEEKS; week++) {
            plannedHoursPerWeek.put(week, 0);
        }
        Label labelTitle = new Label("Añadir Actividad");
        labelTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 16));
        textFieldActivityName = new TextField();
        textFieldActivityName.setMaxWidth(Double.MAX_VALUE);
        textAreaDescription = new TextArea();
        textAreaDescription.setPrefRowCount(6);
        textAreaDescription.setWrapText(true);
        textAreaDescription.setMaxWidth(Double.MAX_VALUE);
        GridPane formGrid = new GridPane();
        formGrid.setHgap(12);
        formGrid.setVgap(16);
        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setMinWidth(160);
        labelColumn.setPrefWidth(160);
        ColumnConstraints fieldColumn = new ColumnConstraints();
        fieldColumn.setHgrow(Priority.ALWAYS);
        formGrid.getColumnConstraints().addAll(labelColumn, fieldColumn);
        Label labelName = new Label("Nombre de la actividad:");
        labelName.setFont(Font.font("SansSerif", 14));
        formGrid.add(labelName, 0, 0);
        formGrid.add(textFieldActivityName, 1, 0);
        Label labelDescription = new Label("Descripción:");
        labelDescription.setFont(Font.font("SansSerif", 14));
        formGrid.add(labelDescription, 0, 1);
        formGrid.add(textAreaDescription, 1, 1);
        Label labelWeek = new Label("Semana:");
        labelWeek.setFont(Font.font("SansSerif", 14));
        comboBoxWeek = new ComboBox<>();
        for (int week = 1; week <= TOTAL_WEEKS; week++) {
            comboBoxWeek.getItems().add(week);
        }
        comboBoxWeek.setConverter(new StringConverter<>() {
            @Override public String toString(Integer week) {
                return week == null ? "" : "S" + week;
            }
            @Override public Integer fromString(String string) {
                return Integer.parseInt(string.replace("S", ""));
            }
        });
        comboBoxWeek.getSelectionModel().selectFirst();
        Label labelPlannedHours = new Label("Horas Planeadas:");
        labelPlannedHours.setFont(Font.font("SansSerif", 14));
        spinnerPlannedHours = new Spinner<>();
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 240, 0);
        spinnerPlannedHours.setValueFactory(valueFactory);
        spinnerPlannedHours.setPrefWidth(100);
        spinnerPlannedHours.setEditable(true);
        spinnerPlannedHours.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue >= 0) {
                int currentWeek = comboBoxWeek.getValue();
                plannedHoursPerWeek.put(currentWeek, newValue);
            }
        });
        comboBoxWeek.valueProperty().addListener((observable, oldWeek, newWeek) -> {
            if (oldWeek != null) {
                plannedHoursPerWeek.put(oldWeek, spinnerPlannedHours.getValue());
            }
            if (newWeek != null) {
                spinnerPlannedHours.getValueFactory().setValue(
                        plannedHoursPerWeek.getOrDefault(newWeek, 0));
            }
        });
        HBox weekRow = new HBox(24, new HBox(10, labelWeek, comboBoxWeek), new HBox(10, labelPlannedHours, spinnerPlannedHours));
        weekRow.setAlignment(Pos.CENTER_LEFT);
        buttonSave = new Button("Guardar");
        buttonCancel = new Button("Cancelar");
        String buttonStyle = "-fx-background-color: #1e1e23; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-background-radius: 10;";
        buttonSave.setStyle(buttonStyle);
        buttonCancel.setStyle(buttonStyle);
        buttonSave.setPrefWidth(130);
        buttonCancel.setPrefWidth(130);
        ControllerRegisterActivity controller = new ControllerRegisterActivity(this);
        buttonSave.setOnAction(controller);
        buttonCancel.setOnAction(controller);
        HBox buttonPanel = new HBox(12, buttonSave, buttonCancel);
        buttonPanel.setAlignment(Pos.CENTER_RIGHT);
        VBox mainPanel = new VBox(24, labelTitle, formGrid, weekRow, buttonPanel);
        mainPanel.setPadding(new Insets(32, 40, 32, 40));
        Scene scene = new Scene(mainPanel, 580, 320);
        stage.setTitle("GUIRegistroActividad");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public boolean validateFields() {
        boolean fieldsValidated = true;
        List<String> errors = new ArrayList<>();
        GUIUtils.validateShortText(textFieldActivityName.getText(), "Nombre", errors);
        GUIUtils.validateLongText(textAreaDescription.getText(), "Apellidos", errors);
        long weeksWithHours = plannedHoursPerWeek.values().stream().filter(hours -> hours > 0).count();
        if (weeksWithHours == 0) {
            errors.add("Debe asignar horas planeadas en al menos una semana.");
        }
        if (!errors.isEmpty()) {
            GUIUtils.showErrors(errors);
            fieldsValidated = false;
        }
        return fieldsValidated;
    }

    public ArrayList<WeeklyLog> buildWeeklyLogs(Activity activity) {
        plannedHoursPerWeek.put(comboBoxWeek.getValue(), spinnerPlannedHours.getValue());
        ArrayList<WeeklyLog> weeklyLogs = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : plannedHoursPerWeek.entrySet()) {
            if (entry.getValue() > 0) {
                weeklyLogs.add(new WeeklyLog(0, entry.getKey(), 0, entry.getValue(), activity));
            }
        }
        return weeklyLogs;
    }

    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void updateActivitiesList(Activity activity, ArrayList<WeeklyLog> weeklyLogs) {
        guiActivityPlan.addActivity(activity,  weeklyLogs);
    }

    public void setGuiActivityPlan(GUIActivityPlan guiActivityPlan) {
        this.guiActivityPlan = guiActivityPlan;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public TextField getTextFieldActivityName() {
        return textFieldActivityName;
    }

    public TextArea getTextAreaDescription() {
        return textAreaDescription;
    }

    public Stage getStage() {
        return stage;
    }

    public Button getButtonSave() {
        return buttonSave;
    }

    public Button getButtonCancel() {
        return buttonCancel;
    }

    public static void main(String[] args) {
        launch(args);
    }
}