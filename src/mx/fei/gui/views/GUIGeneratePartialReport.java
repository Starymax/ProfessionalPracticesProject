package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;
import mx.fei.gui.controllers.ControllerGeneratePartialReport;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.logic.dto.PartialActivityRow;
import mx.fei.logic.dto.Student;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class GUIGeneratePartialReport extends Application {

    private Stage stage;
    private ControllerGeneratePartialReport controller;
    private Student student;
    private Label labelCareer;
    private Label labelNrc;
    private Label labelProfessor;
    private Label labelPeriod;
    private Label labelStudentName;
    private Label labelEnrollment;
    private Label labelOrganization;
    private Label labelProject;
    private Label labelObjective;
    private Label labelMethodology;
    private TableView<PartialActivityRow> tableActivities;
    private TextArea textAreaResults;
    private TextArea textAreaObservations;
    private Button buttonExportPDF, buttonCancel;
    private final int totalWeeks = 8;
    private final int amountOfActivities = 6;

    public GUIGeneratePartialReport(Student student) {
        this.student = student;
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        BorderPane mainPane = new BorderPane();
        mainPane.setPadding(new Insets(24));
        Label title = new Label("Generar Reporte Parcial");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 20));
        VBox header = new VBox(8, title);
        header.setAlignment(Pos.CENTER_LEFT);
        controller = new ControllerGeneratePartialReport(this, stage, student);
        VBox content = new VBox(15);
        HBox infoSection = new HBox(15, createGeneralSection(), createProjectSection(), createObjectiveMethodologySection());
        content.getChildren().addAll(infoSection, createActivitiesSection(), createResultsSection(), createButtonRow());
        content.setPadding(new Insets(12));
        mainPane.setTop(header);
        mainPane.setCenter(new ScrollPane(content));
        Scene scene = new Scene(mainPane, 1100, 750);
        GUIStyle.apply(scene);
        stage.setTitle("Reporte Parcial de Prácticas");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        controller.loadData();
    }

    private VBox createGeneralSection() {
        VBox section = new VBox(8);
        section.setStyle("-fx-border-color: #ddd; -fx-padding: 12;");
        Label title = new Label("Datos Generales de la EE");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(10);
        grid.add(new Label("Carrera:"), 0, 0);
        labelCareer = new Label("-");
        grid.add(labelCareer, 1, 0);
        grid.add(new Label("NRC:"), 0, 1);
        labelNrc = new Label("-");
        grid.add(labelNrc, 1, 1);
        grid.add(new Label("Profesor:"), 0, 2);
        labelProfessor = new Label("-");
        grid.add(labelProfessor, 1, 2);
        grid.add(new Label("Período escolar:"), 0, 3);
        labelPeriod = new Label("-");
        grid.add(labelPeriod, 1, 3);
        section.getChildren().addAll(title, grid);
        return section;
    }

    private VBox createProjectSection() {
        VBox section = new VBox(8);
        section.setStyle("-fx-border-color: #ddd; -fx-padding: 12;");
        Label title = new Label("Datos del Proyecto");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(10);
        grid.add(new Label("Alumno(s):"), 0, 0);
        labelStudentName = new Label("-");
        grid.add(labelStudentName, 1, 0);
        grid.add(new Label("Matrícula:"), 0, 1);
        labelEnrollment = new Label("-");
        grid.add(labelEnrollment, 1, 1);
        grid.add(new Label("Organización vinculada:"), 0, 2);
        labelOrganization = new Label("-");
        grid.add(labelOrganization, 1, 2);
        grid.add(new Label("Proyecto:"), 0, 3);
        labelProject = new Label("-");
        grid.add(labelProject, 1, 3);
        section.getChildren().addAll(title, grid);
        return section;
    }

    private VBox createObjectiveMethodologySection() {
        VBox section = new VBox(8);
        section.setStyle("-fx-border-color: #ddd; -fx-padding: 12;");
        Label titleObj = new Label("Objetivo(s) general del proyecto");
        titleObj.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        labelObjective = new Label();
        labelObjective.setWrapText(true);
        Label titleMet = new Label("Metodología");
        titleMet.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        labelMethodology = new Label();
        labelMethodology.setWrapText(true);
        section.getChildren().addAll(titleObj, labelObjective, titleMet, labelMethodology);
        return section;
    }

    private VBox createActivitiesSection() {
        VBox section = new VBox(8);
        section.setStyle("-fx-border-color: #ddd; -fx-padding: 12;");
        Label title = new Label("Avance de actividades realizadas (Semanas 1 a 8)");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        tableActivities = new TableView<>();
        tableActivities.setEditable(false);
        TableColumn<PartialActivityRow, String> colAct = new TableColumn<>("Actividades");
        colAct.setCellValueFactory(cellData -> cellData.getValue().activityNameProperty());
        TableColumn<PartialActivityRow, String> colTime = new TableColumn<>("Tiempo");
        colTime.setCellValueFactory(cellData -> cellData.getValue().plannedTimeProperty());
        tableActivities.getColumns().addAll(colAct, colTime);
        for (int i = 1; i <= totalWeeks; i++) {
            TableColumn<PartialActivityRow, String> colPlan = new TableColumn<>("S" + i + " Plan");
            int finalI = i;
            colPlan.setCellValueFactory(cellData -> getPlanProperty(cellData.getValue(), finalI));
            TableColumn<PartialActivityRow, String> colReal = new TableColumn<>("S" + i + " Real");
            int finalI1 = i;
            colReal.setCellValueFactory(cellData -> getRealProperty(cellData.getValue(), finalI1));
            tableActivities.getColumns().addAll(colPlan, colReal);
        }
        tableActivities.setPrefHeight(300);
        section.getChildren().addAll(title, tableActivities);
        return section;
    }

    private StringProperty getPlanProperty(PartialActivityRow row, int week) {
        switch (week) {
            case 1: return row.week1PlanProperty();
            case 2: return row.week2PlanProperty();
            case 3: return row.week3PlanProperty();
            case 4: return row.week4PlanProperty();
            case 5: return row.week5PlanProperty();
            case 6: return row.week6PlanProperty();
            case 7: return row.week7PlanProperty();
            case 8: return row.week8PlanProperty();
            default: return new SimpleStringProperty("");
        }
    }

    private StringProperty getRealProperty(PartialActivityRow row, int week) {
        switch (week) {
            case 1: return row.week1RealProperty();
            case 2: return row.week2RealProperty();
            case 3: return row.week3RealProperty();
            case 4: return row.week4RealProperty();
            case 5: return row.week5RealProperty();
            case 6: return row.week6RealProperty();
            case 7: return row.week7RealProperty();
            case 8: return row.week8RealProperty();
            default: return new SimpleStringProperty("");
        }
    }

    private VBox createResultsSection() {
        VBox section = new VBox(8);
        section.setStyle("-fx-border-color: #ddd; -fx-padding: 12;");
        Label title = new Label("Resultados obtenidos al momento");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        textAreaResults = new TextArea();
        textAreaResults.setPrefRowCount(5);
        textAreaResults.setWrapText(true);
        Label observations = new Label("Observaciones:");
        observations.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        textAreaObservations = new TextArea();
        textAreaObservations.setPrefRowCount(5);
        textAreaObservations.setWrapText(true);
        section.getChildren().addAll(title, textAreaResults, observations,textAreaObservations);
        return section;
    }

    private HBox createButtonRow() {
        buttonExportPDF = new Button("Exportar PDF");
        buttonExportPDF.setPrefWidth(120);
        buttonExportPDF.setId("buttonExportPdf");
        buttonCancel = new Button("Cancelar");
        buttonCancel.setPrefWidth(120);
        buttonCancel.setId("buttonCancel");
        buttonExportPDF.setOnAction(controller::handlePartialReportButtons);
        buttonCancel.setOnAction(controller::handlePartialReportButtons);
        HBox box = new HBox(12, buttonExportPDF, buttonCancel);
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setPadding(new Insets(18, 0, 0, 0));
        return box;
    }

    public boolean validateFields() {
        boolean validated = true;
        List<String> errors = new ArrayList<>();
        GUIUtils.validateLongText(textAreaObservations.getText(),"Observaciones", errors);
        GUIUtils.validateLongText(textAreaResults.getText(),"Observaciones", errors);
        if (!errors.isEmpty()) {
            GUIUtils.showErrors(errors);
            validated = false;
        }
        return validated;
    }

    public void setCareer(String career) {
        labelCareer.setText(career);
    }

    public void setNrc(String nrc) {
        labelNrc.setText(nrc);
    }

    public void setProfessor(String professor) {
        labelProfessor.setText(professor);
    }

    public void setPeriod(String period) {
        labelPeriod.setText(period);
    }

    public void setStudentName(String studentName) {
        labelStudentName.setText(studentName);
    }

    public void setEnrollment(String enrollment) {
        labelEnrollment.setText(enrollment);
    }

    public void setOrganization(String organization) {
        labelOrganization.setText(organization);
    }

    public void setProjectName(String projectName) {
        labelProject.setText(projectName);
    }

    public void setObjectiveAndMethodology(String objective, String methodology) {
        labelObjective.setText(objective);
        labelMethodology.setText(methodology);
    }

    public void setActivities(ObservableList<PartialActivityRow> rows) {
        tableActivities.setItems(rows);
    }

    public String getResultsObtained() {
        return textAreaResults.getText();
    }

    public void showError(String msg) {
        GUIUtils.showError(msg);
    }

    public void showSuccess(String msg) {
        GUIUtils.showSuccess(msg);
    }

    public void closeWindow() {
        GUIUtils.closeWindow(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public ControllerGeneratePartialReport getController() {
        return controller;
    }

    public void setController(ControllerGeneratePartialReport controller) {
        this.controller = controller;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Label getLabelCareer() {
        return labelCareer;
    }

    public void setLabelCareer(Label labelCareer) {
        this.labelCareer = labelCareer;
    }

    public Label getLabelNrc() {
        return labelNrc;
    }

    public void setLabelNrc(Label labelNrc) {
        this.labelNrc = labelNrc;
    }

    public Label getLabelProfessor() {
        return labelProfessor;
    }

    public void setLabelProfessor(Label labelProfessor) {
        this.labelProfessor = labelProfessor;
    }

    public Label getLabelPeriod() {
        return labelPeriod;
    }

    public void setLabelPeriod(Label labelPeriod) {
        this.labelPeriod = labelPeriod;
    }

    public Label getLabelStudentName() {
        return labelStudentName;
    }

    public void setLabelStudentName(Label labelStudentName) {
        this.labelStudentName = labelStudentName;
    }

    public Label getLabelEnrollment() {
        return labelEnrollment;
    }

    public void setLabelEnrollment(Label labelEnrollment) {
        this.labelEnrollment = labelEnrollment;
    }

    public Label getLabelOrganization() {
        return labelOrganization;
    }

    public void setLabelOrganization(Label labelOrganization) {
        this.labelOrganization = labelOrganization;
    }

    public Label getLabelProject() {
        return labelProject;
    }

    public void setLabelProject(Label labelProject) {
        this.labelProject = labelProject;
    }

    public Label getLabelObjective() {
        return labelObjective;
    }

    public void setLabelObjective(Label labelObjective) {
        this.labelObjective = labelObjective;
    }

    public Label getLabelMethodology() {
        return labelMethodology;
    }

    public void setLabelMethodology(Label labelMethodology) {
        this.labelMethodology = labelMethodology;
    }

    public TableView<PartialActivityRow> getTableActivities() {
        return tableActivities;
    }

    public void setTableActivities(TableView<PartialActivityRow> tableActivities) {
        this.tableActivities = tableActivities;
    }

    public TextArea getTextAreaResults() {
        return textAreaResults;
    }

    public void setTextAreaResults(TextArea textAreaResults) {
        this.textAreaResults = textAreaResults;
    }

    public Button getButtonExportPDF() {
        return buttonExportPDF;
    }

    public void setButtonExportPDF(Button buttonExportPDF) {
        this.buttonExportPDF = buttonExportPDF;
    }

    public Button getButtonCancel() {
        return buttonCancel;
    }

    public void setButtonCancel(Button buttonCancel) {
        this.buttonCancel = buttonCancel;
    }

    public int getTotalWeeks() {
        return totalWeeks;
    }

    public int getAmountOfActivities() {
        return amountOfActivities;
    }

    public TextArea getTextAreaObservations() {
        return textAreaObservations;
    }
}