package mx.fei.gui.views;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import mx.fei.gui.controllers.ControllerGenerateMonthlyReport;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.logic.dto.ActivityRow;
import mx.fei.logic.dto.Student;
import javafx.scene.control.ScrollPane;

public class GUIGenerateMonthlyReport extends Application {

    private Stage stage;
    private ControllerGenerateMonthlyReport controllerGenerateMonthlyReport;
    private Student student;
    private Label labelStudentName;
    private Label labelStudentEnrollment;
    private Label labelStudentEmail;
    private Label labelProjectName;
    private Label labelEnterprise;
    private Label labelProfessor;
    private TableView<ActivityRow> tableActivities;
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
        HBox infoSection = new HBox(10, createStudentInfoSection(), createProjectInfoSection());
        VBox activitiesSection = createActivitiesSection();
        controllerGenerateMonthlyReport = new ControllerGenerateMonthlyReport(this, stage, student);
        HBox buttonRow = createButtonRow();
        VBox center = new VBox(10, infoSection, activitiesSection, buttonRow);
        center.setPadding(new Insets(12));
        ScrollPane scrollPane = new ScrollPane(center);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        mainPane.setTop(header);
        mainPane.setCenter(scrollPane);
        Scene scene = new Scene(mainPane, 825, 550);
        stage.setTitle("Generación Reporte Mensual");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private VBox createStudentInfoSection() {
        VBox section = new VBox(8);
        section.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 12;");
        Label title = new Label("Información del Estudiante");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        GridPane gridPane = new GridPane();
        gridPane.setHgap(16);
        gridPane.setVgap(10);
        gridPane.add(new Label("Nombre:"), 0, 0);
        labelStudentName = new Label("-");
        gridPane.add(labelStudentName, 1, 0);
        gridPane.add(new Label("Matrícula:"), 0, 1);
        labelStudentEnrollment = new Label("-");
        gridPane.add(labelStudentEnrollment, 1, 1);
        gridPane.add(new Label("Email:"), 0, 2);
        labelStudentEmail = new Label("-");
        gridPane.add(labelStudentEmail, 1, 2);
        section.getChildren().addAll(title, gridPane);
        return section;
    }

    private VBox createProjectInfoSection() {
        VBox section = new VBox(8);
        section.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 12;");
        Label title = new Label("Información del Proyecto y Práctica");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        GridPane gridPane = new GridPane();
        gridPane.setHgap(16);
        gridPane.setVgap(10);
        gridPane.add(new Label("Proyecto:"), 0, 0);
        labelProjectName = new Label("-");
        gridPane.add(labelProjectName, 1, 0);
        gridPane.add(new Label("Empresa/Institución:"), 0, 1);
        labelEnterprise = new Label("-");
        gridPane.add(labelEnterprise, 1, 1);
        gridPane.add(new Label("Profesor Responsable:"), 0, 2);
        labelProfessor = new Label("-");
        gridPane.add(labelProfessor, 1, 2);
        section.getChildren().addAll(title, gridPane);
        return section;
    }

    private VBox createActivitiesSection() {
        VBox section = new VBox(8);
        section.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 12;");
        Label title = new Label("Actividades Realizadas");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        tableActivities = new TableView<>();
        tableActivities.setPrefHeight(240);
        tableActivities.setEditable(true);
        tableActivities.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<ActivityRow, String> columnName = new TableColumn<>("Actividad");
        columnName.setCellValueFactory(parameter -> parameter.getValue().nameProperty());
        columnName.setPrefWidth(200);
        TableColumn<ActivityRow, String> columnProgress = new TableColumn<>("Progreso");
        columnProgress.setCellValueFactory(parameter -> parameter.getValue().progressProperty());
        columnProgress.setPrefWidth(100);
        TableColumn<ActivityRow, String> columnHours = new TableColumn<>("Horas Trabajadas");
        columnHours.setCellValueFactory(parameter -> parameter.getValue().workedHoursProperty());
        columnHours.setPrefWidth(120);
        TableColumn<ActivityRow, String> columnObservations = getActivityRowStringTableColumn();
        tableActivities.getColumns().addAll(columnName, columnProgress, columnHours, columnObservations);
        section.getChildren().addAll(title, tableActivities);
        return section;
    }

    private TableColumn<ActivityRow, String> getActivityRowStringTableColumn() {
        TableColumn<ActivityRow, String> columnObservations = new TableColumn<>("Observaciones");
        columnObservations.setCellValueFactory(parameter -> parameter.getValue().observationsProperty());
        columnObservations.setCellFactory(TextFieldTableCell.forTableColumn());
        columnObservations.setOnEditCommit(event -> {
            ActivityRow row = event.getRowValue();
            String newValue = event.getNewValue() != null ? event.getNewValue() : "";
            row.setObservations(newValue);
            if (row.getActivityProgress() != null) {
                row.getActivityProgress().setObservations(newValue);
            }
        });
        columnObservations.setPrefWidth(300);
        columnObservations.setEditable(true);
        return columnObservations;
    }

    private HBox createButtonRow() {
        buttonExportPDF = new Button("Exportar PDF");
        buttonExportPDF.setPrefWidth(120);
        buttonExportPDF.setId("buttonExportPdf");
        buttonExportPDF.setOnAction(controllerGenerateMonthlyReport::handleMonthlyReportButtons);
        buttonCancel = new Button("Cancelar");
        buttonCancel.setPrefWidth(120);
        buttonCancel.setId("buttonCancel");
        buttonCancel.setOnAction(controllerGenerateMonthlyReport::handleMonthlyReportButtons);
        HBox buttonRow = new HBox(12, buttonExportPDF, buttonCancel);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);
        buttonRow.setPadding(new Insets(18, 0, 0, 0));
        return buttonRow;
    }

    public void setStudentInfo(String name, String matricule, String email) {
        labelStudentName.setText(name != null ? name : "-");
        labelStudentEnrollment.setText(matricule != null ? matricule : "-");
        labelStudentEmail.setText(email != null ? email : "-");
    }

    public void setProjectInfo(String projectName, String enterprise, String professor) {
        labelProjectName.setText(projectName != null ? projectName : "-");
        labelEnterprise.setText(enterprise != null ? enterprise : "-");
        labelProfessor.setText(professor != null ? professor : "-");
    }

    public void setActivities(ObservableList<ActivityRow> activities) {
        tableActivities.setItems(activities);
    }

    public ObservableList<ActivityRow> getActivityRows() {
        return tableActivities.getItems();
    }

    public void commitTableEdits() {
        if (tableActivities.getEditingCell() != null) {
            tableActivities.edit(-1, null);
        }
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

    public static void main(String[] args) {
        launch(args);
    }
}
