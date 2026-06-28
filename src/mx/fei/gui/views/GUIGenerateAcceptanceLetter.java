package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;
import mx.fei.gui.controllers.ControllerGenerateAcceptanceLetter;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.logic.dto.Student;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;


public class GUIGenerateAcceptanceLetter extends Application {

    private Stage stage;
    private ControllerGenerateAcceptanceLetter controllerGenerateAcceptanceLetter;
    private Student student;
    private Label labelDate;
    private Label labelStudentName;
    private Label labelEnrollment;
    private Label labelEnterprise;
    private Label labelStartDate;
    private Label labelEndDate;
    private Label labelResponsibleEmail;
    private Label labelResponsiblePhone;
    private ComboBox<String>[] startHourCombo = new ComboBox[5];
    private ComboBox<String>[] endHourCombo = new ComboBox[5];
    private final String[] DAYS = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes"};
    private Button buttonPrint;
    private Button buttonBack;
    private ObservableList<String> hourOptions;
    private final int HOUR_START = 8;
    private final int HOUR_END = 19;
    private final int MOUNT_OF_DAYS = 5;
    private final int SECONDS = 60;

    public GUIGenerateAcceptanceLetter(Student student) {
        this.student = student;
    }

    @Override
    public void start(Stage stage) {
        controllerGenerateAcceptanceLetter = new ControllerGenerateAcceptanceLetter(this, stage, student);
        this.stage = stage;
        stage.setTitle("Carta de Aceptación de Prácticas");
        stage.setResizable(false);
        BorderPane mainPane = new BorderPane();
        mainPane.setPadding(new Insets(24));
        mainPane.setStyle("-fx-background-color: white;");
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        Label title = new Label("Carta de Aceptación de Prácticas Profesionales");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 16));
        content.getChildren().add(title);
        content.getChildren().add(createLetterBody());
        HBox buttonRow = createButtonRow();
        mainPane.setTop(new VBox(title));
        mainPane.setCenter(new ScrollPane(content));
        mainPane.setBottom(buttonRow);
        BorderPane.setAlignment(buttonRow, Pos.CENTER_RIGHT);
        Scene scene = new Scene(mainPane, 700, 800);
        GUIStyle.apply(scene);
        stage.setScene(scene);
        controllerGenerateAcceptanceLetter.loadData();
        stage.show();
    }

    public void setDate(String date) {
        labelDate.setText(date);
    }

    public void setStudentName(String name) {
        labelStudentName.setText(name);
    }

    public void setEnrollment(String enrollment) {
        labelEnrollment.setText(enrollment);
    }

    public void setEnterprise(String enterprise) {
        labelEnterprise.setText(enterprise);
    }

    public void setStartDate(String startDate) {
        labelStartDate.setText(startDate);
    }

    public void setEndDate(String endDate) {
        labelEndDate.setText(endDate);
    }

    public void setResponsibleEmail(String email) {
        labelResponsibleEmail.setText(email);
    }

    public void setResponsiblePhone(String phone) {
        labelResponsiblePhone.setText(phone);
    }

    public void showError(String msg) {
        GUIUtils.showError(msg);
    }

    public void showSuccess(String msg) {
        GUIUtils.showSuccess(msg);
    }

    public void closeWindow() {
        stage.close();
    }

    public static void main(String[] args) { launch(args); }

    public boolean validateSchedule() {
        boolean scheduleValid = true;
        for (int i = 0; i < DAYS.length && scheduleValid; i++) {
            String start = startHourCombo[i].getValue();
            String end = endHourCombo[i].getValue();
            if (start == null || end == null) {
                showError("Debe seleccionar horario para " + DAYS[i]);
                scheduleValid = false;
            } else if (timeToMinutes(start) >= timeToMinutes(end)) {
                showError("En " + DAYS[i] + ", la hora de inicio debe ser menor que la hora de fin.");
                scheduleValid = false;
            }
        }
        return scheduleValid;
    }

    public String[] getScheduleStrings() {
        String[] horarios = new String[MOUNT_OF_DAYS];
        for (int i = 0; i < DAYS.length; i++) {
            horarios[i] = startHourCombo[i].getValue() + " - " + endHourCombo[i].getValue();
        }
        return horarios;
    }

    private int timeToMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * SECONDS + Integer.parseInt(parts[1]);
    }

    private ObservableList<String> getHourOptions() {
        ObservableList<String> hours = FXCollections.observableArrayList();
        for (int h = HOUR_START; h < HOUR_END; h++) {
            hours.add(String.format("%02d:00", h));
            hours.add(String.format("%02d:30", h));
        }
        hours.add("20:00");
        return hours;
    }

    private void updateEndHourOptions(int index, String newStartHour) {
        if (newStartHour != null) {
            ObservableList<String> filtered = hourOptions.filtered(hour -> timeToMinutes(hour) > timeToMinutes(newStartHour));
            endHourCombo[index].setItems(filtered);
            String currentEnd = endHourCombo[index].getValue();
            if (currentEnd != null && !filtered.contains(currentEnd)) {
                if (!filtered.isEmpty()) {
                    endHourCombo[index].setValue(filtered.get(0));
                } else {
                    endHourCombo[index].setValue(null);
                }
            }
        } else {
            endHourCombo[index].setItems(hourOptions);
        }
    }

    private HBox createButtonRow() {
        buttonPrint = new Button("Imprimir");
        buttonPrint.setPrefWidth(120);
        buttonPrint.setId("buttonPrint");
        buttonBack = new Button("Regresar");
        buttonBack.setPrefWidth(120);
        buttonBack.setId("buttonBack");
        buttonPrint.setOnAction(controllerGenerateAcceptanceLetter::handleAcceptanceLetterButtons);
        buttonBack.setOnAction(controllerGenerateAcceptanceLetter::handleAcceptanceLetterButtons);
        HBox box = new HBox(12, buttonPrint, buttonBack);
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setPadding(new Insets(18, 0, 0, 0));
        return box;
    }

    private VBox createLetterBody() {
        VBox body = new VBox(12);
        body.setPadding(new Insets(20));
        body.getStyleClass().add("report-border");
        Label labelDate = new Label("Xalapa-Enríquez, Ver., a _____");
        labelDate.getStyleClass().add("label-bold");
        body.getChildren().add(labelDate);
        this.labelDate = new Label();
        body.getChildren().add(this.labelDate);
        TextFlow coordinator = new TextFlow(new Text("DR. ÁNGEL JUAN SÁNCHEZ GARCÍA\nCOORDINADOR DE PRÁCTICAS PROFESIONALES\nLICENCIATURA EN INGENIERÍA DE SOFTWARE\nFACULTAD DE ESTADISTICA E INFORMATICA\nUNIVERSIDAD VERACRUZANA\n\nP R E S E N T E\n\n"));
        body.getChildren().add(coordinator);
        GridPane gridPaneInformation = new GridPane();
        gridPaneInformation.setHgap(10);
        gridPaneInformation.setVgap(8);
        gridPaneInformation.add(new Label("Nombre del alumno:"), 0, 0);
        labelStudentName = new Label();
        gridPaneInformation.add(labelStudentName, 1, 0);
        gridPaneInformation.add(new Label("Matrícula:"), 0, 1);
        labelEnrollment = new Label();
        gridPaneInformation.add(labelEnrollment, 1, 1);
        gridPaneInformation.add(new Label("Empresa/Institución:"), 0, 2);
        labelEnterprise = new Label();
        gridPaneInformation.add(labelEnterprise, 1, 2);
        gridPaneInformation.add(new Label("Fecha de inicio:"), 0, 3);
        labelStartDate = new Label();
        gridPaneInformation.add(labelStartDate, 1, 3);
        gridPaneInformation.add(new Label("Fecha de terminación (aprox):"), 0, 4);
        labelEndDate = new Label();
        gridPaneInformation.add(labelEndDate, 1, 4);
        body.getChildren().add(gridPaneInformation);
        body.getChildren().add(new Label("El horario pactado es el siguiente:"));
        GridPane scheduleGrid = new GridPane();
        scheduleGrid.setHgap(15);
        scheduleGrid.setVgap(8);
        scheduleGrid.getStyleClass().add("report-header-lg");
        for (int i = 0; i < DAYS.length; i++) {
            scheduleGrid.add(new Label(DAYS[i]), i, 0);
        }
        hourOptions = getHourOptions();
        for (int i = 0; i < DAYS.length; i++) {
            startHourCombo[i] = new ComboBox<>(hourOptions);
            endHourCombo[i] = new ComboBox<>(hourOptions);
            startHourCombo[i].setValue("08:00");
            endHourCombo[i].setValue("12:00");
            final int index = i;
            startHourCombo[i].valueProperty().addListener((observableValue, oldValue, newValue) -> updateEndHourOptions(index, newValue));
            HBox box = new HBox(5);
            box.getChildren().addAll(new Label("Inicio:"), startHourCombo[i], new Label("Fin:"), endHourCombo[i]);
            scheduleGrid.add(box, i, 1);
        }
        body.getChildren().add(scheduleGrid);
        GridPane gridPaneContact = new GridPane();
        gridPaneContact.setHgap(10);
        gridPaneContact.setVgap(8);
        gridPaneContact.add(new Label("Correo electrónico del responsable:"), 0, 0);
        labelResponsibleEmail = new Label();
        gridPaneContact.add(labelResponsibleEmail, 1, 0);
        gridPaneContact.add(new Label("Teléfono:"), 0, 1);
        labelResponsiblePhone = new Label();
        gridPaneContact.add(labelResponsiblePhone, 1, 1);
        body.getChildren().add(gridPaneContact);
        Label firma = new Label("\nAtentamente\n\n______________________\n(Nombre y firma del responsable)");
        body.getChildren().add(firma);
        return body;
    }
}
