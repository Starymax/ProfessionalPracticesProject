package mx.fei.gui.views;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import mx.fei.gui.controllers.ControllerGenerateMonthlyReport;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.logic.dto.Student;
import javafx.scene.control.ScrollPane;

public class GUIGenerateMonthlyReport extends Application {

    private Stage stage;
    private ControllerGenerateMonthlyReport controller;
    private Student student;
    private Label labelStudentName;
    private Label labelStudentMatricule;
    private Label labelStudentEmail;
    private Label labelProjectName;
    private Label labelEnterprise;
    private Label labelProfessor;
    private TableView<ActivityRow> tableActivities;
    private TextArea textAreaObservations;
    private Button buttonSave;
    private Button buttonExportPDF;
    private Button buttonCancel;

    public GUIGenerateMonthlyReport() {
    }

    public GUIGenerateMonthlyReport(Student student) {
        this.student = student;
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        BorderPane mainPane = new BorderPane();
        mainPane.setPadding(new Insets(24));
        Label title = new Label("Generar Reporte Mensual");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 20));
        VBox header = new VBox(8, title);
        header.setAlignment(Pos.CENTER_LEFT);
        VBox studentSection = createStudentInfoSection();
        VBox projectSection = createProjectInfoSection();
        VBox activitiesSection = createActivitiesSection();
        VBox observationsSection = createObservationsSection();
        HBox buttonRow = createButtonRow();
        VBox center = new VBox(10, studentSection, projectSection, activitiesSection, observationsSection, buttonRow);
        center.setPadding(new Insets(12));
        mainPane.setTop(header);
        mainPane.setCenter(new ScrollPane(center));
        Scene scene = new Scene(mainPane, 900, 780);
        stage.setTitle("Generación Reporte Mensual");
        stage.setResizable(false);
        stage.setScene(scene);
        controller = new ControllerGenerateMonthlyReport(this, stage, student);
        stage.show();
    }

    private VBox createStudentInfoSection() {
        VBox section = new VBox(8);
        section.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 12;");
        Label title = new Label("Información del Estudiante");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(10);
        grid.add(new Label("Nombre:"), 0, 0);
        labelStudentName = new Label("-");
        grid.add(labelStudentName, 1, 0);
        grid.add(new Label("Matrícula:"), 0, 1);
        labelStudentMatricule = new Label("-");
        grid.add(labelStudentMatricule, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        labelStudentEmail = new Label("-");
        grid.add(labelStudentEmail, 1, 2);
        section.getChildren().addAll(title, grid);
        return section;
    }

    private VBox createProjectInfoSection() {
        VBox section = new VBox(8);
        section.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 12;");
        Label title = new Label("Información del Proyecto y Práctica");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(10);
        grid.add(new Label("Proyecto:"), 0, 0);
        labelProjectName = new Label("-");
        grid.add(labelProjectName, 1, 0);
        grid.add(new Label("Empresa/Institución:"), 0, 1);
        labelEnterprise = new Label("-");
        grid.add(labelEnterprise, 1, 1);
        grid.add(new Label("Profesor Responsable:"), 0, 2);
        labelProfessor = new Label("-");
        grid.add(labelProfessor, 1, 2);
        section.getChildren().addAll(title, grid);
        return section;
    }

    private VBox createActivitiesSection() {
        VBox section = new VBox(8);
        section.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 12;");
        Label title = new Label("Actividades Realizadas");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        tableActivities = new TableView<>();
        tableActivities.setPrefHeight(150);
        TableColumn<ActivityRow, String> colName = new TableColumn<>("Actividad");
        colName.setCellValueFactory(parameter -> new javafx.beans.property.SimpleStringProperty(parameter.getValue().name));
        colName.setPrefWidth(200);
        TableColumn<ActivityRow, String> colProgress = new TableColumn<>("Progreso");
        colProgress.setCellValueFactory(parameter -> new javafx.beans.property.SimpleStringProperty(parameter.getValue().progress));
        colProgress.setPrefWidth(100);
        TableColumn<ActivityRow, String> colHours = new TableColumn<>("Horas Trabajadas");
        colHours.setCellValueFactory(parameter -> new javafx.beans.property.SimpleStringProperty(parameter.getValue().workedHours));
        colHours.setPrefWidth(120);
        TableColumn<ActivityRow, String> colObservations = new TableColumn<>("Observaciones");
        colObservations.setCellValueFactory(parameter -> new javafx.beans.property.SimpleStringProperty(parameter.getValue().observations));
        colObservations.setPrefWidth(300);
        tableActivities.getColumns().addAll(colName, colProgress, colHours, colObservations);
        section.getChildren().addAll(title, tableActivities);
        return section;
    }

    private VBox createObservationsSection() {
        VBox section = new VBox(8);
        section.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 12;");
        Label title = new Label("Observaciones Generales");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        textAreaObservations = new TextArea();
        textAreaObservations.setWrapText(true);
        textAreaObservations.isEditable();
        textAreaObservations.setPrefHeight(120);
        textAreaObservations.setStyle("-fx-control-inner-background: #fff; -fx-font-size: 12;");
        section.getChildren().addAll(title, textAreaObservations);
        return section;
    }

    private HBox createButtonRow() {
        buttonSave = new Button("Guardar");
        buttonSave.setPrefWidth(120);
        buttonSave.setOnAction(e -> controller.handleSave());
        buttonExportPDF = new Button("Exportar PDF");
        buttonExportPDF.setPrefWidth(120);
        buttonExportPDF.setOnAction(e -> controller.handleExportPDF());
        buttonCancel = new Button("Cancelar");
        buttonCancel.setPrefWidth(120);
        buttonCancel.setOnAction(e -> closeWindow());
        HBox buttonRow = new HBox(12, buttonSave, buttonExportPDF, buttonCancel);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);
        buttonRow.setPadding(new Insets(18, 0, 0, 0));
        return buttonRow;
    }

    public void setStudentInfo(String name, String matricule, String email) {
        labelStudentName.setText(name != null ? name : "-");
        labelStudentMatricule.setText(matricule != null ? matricule : "-");
        labelStudentEmail.setText(email != null ? email : "-");
    }

    public void setProjectInfo(String projectName, String enterprise, String professor) {
        labelProjectName.setText(projectName != null ? projectName : "-");
        labelEnterprise.setText(enterprise != null ? enterprise : "-");
        labelProfessor.setText(professor != null ? professor : "-");
    }

    public void setActivities(javafx.collections.ObservableList<ActivityRow> activities) {
        tableActivities.setItems(activities);
    }

    public String getObservations() {
        return textAreaObservations.getText().trim();
    }

    public void setObservations(String observations) {
        textAreaObservations.setText(observations != null ? observations : "");
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

    public static class ActivityRow {
        public String name;
        public String progress;
        public String workedHours;
        public String observations;

        public ActivityRow(String name, String progress, String workedHours, String observations) {
            this.name = name;
            this.progress = progress;
            this.workedHours = workedHours;
            this.observations = observations;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
