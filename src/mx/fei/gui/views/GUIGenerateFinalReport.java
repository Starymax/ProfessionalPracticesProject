package mx.fei.gui.views;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;
import mx.fei.gui.controllers.ControllerGenerateFinalReport;
import mx.fei.logic.dto.FinalReportRow;
import mx.fei.logic.dto.Practice;
import mx.fei.logic.dto.Student;
import mx.fei.gui.utils.GUIUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class GUIGenerateFinalReport extends Application {

    private Stage stage;
    private ControllerGenerateFinalReport controllerGenerateFinalReport;
    private Student student;
    private Practice practice;
    private Label labelStudentName;
    private Label labelStudentEnrollment;
    private Label labelStudentEmail;
    private Label labelStudentNrc;
    private Label labelPeriod;
    private Label labelProjectName;
    private Label labelEnterprise;
    private Label labelProfessor;
    private Label labelEducationalProgram;
    private Label labelGeneralObjectivesContent;
    private Label labelMethodologyContent;
    private TextArea textAreaObservations;
    private TableView<FinalReportRow> tableActivities;
    private Button buttonExportPdf;
    private Button buttonCancel;

    public GUIGenerateFinalReport() {
    }

    public GUIGenerateFinalReport(Student student, Practice practice) {
        this.student = student;
        this.practice = practice;
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        BorderPane mainPane = new BorderPane();
        mainPane.setPadding(new Insets(24));

        Label title = new Label("Generar Reporte Final");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 20));
        VBox header = new VBox(8, title);
        header.setAlignment(Pos.CENTER_LEFT);

        HBox infoSections = new HBox(16, createStudentInfoSection(), createProjectInfoSection());
        infoSections.setAlignment(Pos.TOP_LEFT);
        infoSections.setPrefWidth(Double.MAX_VALUE);
        HBox.setHgrow(infoSections, Priority.ALWAYS);

        VBox objectivesSection = createObjectivesSection();
        VBox activitiesSection = createActivitiesSection();

        controllerGenerateFinalReport = new ControllerGenerateFinalReport(this, stage, student, practice);

        HBox buttonRow = createButtonRow();
        VBox center = new VBox(10, infoSections, objectivesSection, activitiesSection, buttonRow);
        center.setPadding(new Insets(12));
        center.setMaxWidth(Double.MAX_VALUE);

        ScrollPane scrollPane = new ScrollPane(center);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        mainPane.setTop(header);
        mainPane.setCenter(scrollPane);

        Scene scene = new Scene(mainPane, 1100, 820);
        stage.setTitle("Generación Reporte Final");
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
        gridPane.add(new Label("NRC:"), 2, 0);
        labelStudentNrc = new Label("-");
        gridPane.add(labelStudentNrc, 3, 0);
        gridPane.add(new Label("Periodo:"), 2, 1);
        labelPeriod = new Label("-");
        gridPane.add(labelPeriod, 3, 1);
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
        gridPane.add(new Label("Programa educativo:"), 0, 3);
        labelEducationalProgram = new Label("-");
        gridPane.add(labelEducationalProgram, 1, 3);
        section.getChildren().addAll(title, gridPane);
        return section;
    }

    private VBox createObjectivesSection() {
        VBox section = new VBox(8);
        section.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 12;");
        Label title = new Label("Objetivos y Metodología");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        Label labelGeneralObjectives = new Label("Objetivos generales:");
        labelGeneralObjectives.setFont(Font.font("SansSerif", FontWeight.BOLD, 12));
        labelGeneralObjectivesContent = new Label();
        labelGeneralObjectivesContent.setWrapText(true);
        labelGeneralObjectivesContent.setStyle("-fx-border-color: #ccc; -fx-background-color: #f9f9f9; -fx-padding: 10;");
        labelGeneralObjectivesContent.setMinHeight(120);
        labelGeneralObjectivesContent.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(labelGeneralObjectivesContent, Priority.ALWAYS);
        GridPane.setVgrow(labelGeneralObjectivesContent, Priority.ALWAYS);
        Label labelMethodology = new Label("Metodología:");
        labelMethodology.setFont(Font.font("SansSerif", FontWeight.BOLD, 12));
        labelMethodologyContent = new Label();
        labelMethodologyContent.setWrapText(true);
        labelMethodologyContent.setStyle("-fx-border-color: #ccc; -fx-background-color: #f9f9f9; -fx-padding: 10;");
        labelMethodologyContent.setMinHeight(120);
        labelMethodologyContent.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(labelMethodologyContent, Priority.ALWAYS);
        GridPane.setVgrow(labelMethodologyContent, Priority.ALWAYS);
        GridPane objectivesGrid = new GridPane();
        objectivesGrid.setHgap(16);
        objectivesGrid.setVgap(10);
        objectivesGrid.setMaxWidth(Double.MAX_VALUE);
        ColumnConstraints leftColumn = new ColumnConstraints();
        leftColumn.setPercentWidth(50);
        ColumnConstraints rightColumn = new ColumnConstraints();
        rightColumn.setPercentWidth(50);
        objectivesGrid.getColumnConstraints().addAll(leftColumn, rightColumn);
        objectivesGrid.add(labelGeneralObjectives, 0, 0);
        objectivesGrid.add(labelMethodology, 1, 0);
        objectivesGrid.add(labelGeneralObjectivesContent, 0, 1);
        objectivesGrid.add(labelMethodologyContent, 1, 1);
        GridPane.setHgrow(objectivesGrid, Priority.ALWAYS);
        Label labelObservations = new Label("Observaciones generales:");
        labelObservations.setFont(Font.font("SansSerif", FontWeight.BOLD, 12));
        textAreaObservations = new TextArea();
        textAreaObservations.setWrapText(true);
        textAreaObservations.setPrefRowCount(4);
        section.getChildren().addAll(title, objectivesGrid, labelObservations, textAreaObservations);
        return section;
    }

    private VBox createActivitiesSection() {
        VBox section = new VBox(8);
        section.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 12;");
        Label title = new Label("Actividades del Reporte Final");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));

        tableActivities = new TableView<>();
        tableActivities.setPrefHeight(260);
        tableActivities.setEditable(true);

        TableColumn<FinalReportRow, String> activityColumn = new TableColumn<>("Actividad");
        activityColumn.setCellValueFactory(parameter -> parameter.getValue().activityProperty());
        activityColumn.setPrefWidth(220);
        activityColumn.setEditable(false);

        TableColumn<FinalReportRow, String> advanceColumn = new TableColumn<>("Avance");
        advanceColumn.setCellValueFactory(parameter -> parameter.getValue().advanceProperty());
        advanceColumn.setPrefWidth(150);
        advanceColumn.setEditable(false);

        TableColumn<FinalReportRow, String> observationColumn = new TableColumn<>("Observación");
        observationColumn.setCellValueFactory(parameter -> parameter.getValue().observationProperty());
        configureEditableColumn(observationColumn, (row, value) -> row.setObservation(value));
        observationColumn.setPrefWidth(220);

        TableColumn<FinalReportRow, String> productColumn = new TableColumn<>("Producto");
        productColumn.setCellValueFactory(parameter -> parameter.getValue().productProperty());
        configureEditableColumn(productColumn, (row, value) -> row.setProduct(value));
        productColumn.setPrefWidth(180);

        TableColumn<FinalReportRow, String> advancepColumn = new TableColumn<>("Avance p.");
        advancepColumn.setCellValueFactory(parameter -> parameter.getValue().advancepProperty());
        configureEditableColumn(advancepColumn, (row, value) -> row.setAdvancep(value));
        advancepColumn.setPrefWidth(140);

        TableColumn<FinalReportRow, String> observationpColumn = new TableColumn<>("Observación p.");
        observationpColumn.setCellValueFactory(parameter -> parameter.getValue().observationpProperty());
        configureEditableColumn(observationpColumn, (row, value) -> row.setObservationp(value));
        observationpColumn.setPrefWidth(200);

        tableActivities.getColumns().addAll(activityColumn, advanceColumn, observationColumn, productColumn, advancepColumn, observationpColumn);
        tableActivities.setRowFactory(table -> new TableRow<FinalReportRow>() {
            @Override
            protected void updateItem(FinalReportRow item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setDisable(false);
                } else {
                    boolean blankRow = item.getActivity() == null || item.getActivity().isBlank();
                    setDisable(blankRow);
                    setOpacity(blankRow ? 0.65 : 1.0);
                }
            }
        });
        section.getChildren().addAll(title, tableActivities);
        return section;
    }

    private void configureEditableColumn(TableColumn<FinalReportRow, String> column, BiConsumer<FinalReportRow, String> setter) {
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setOnEditCommit(event -> handleEditableColumnCommit(event, setter));
        column.setEditable(true);
    }

    private void handleEditableColumnCommit(TableColumn.CellEditEvent<FinalReportRow, String> event, BiConsumer<FinalReportRow, String> setter) {
        FinalReportRow row = event.getRowValue();
        String fieldName = event.getTableColumn().getText();
        String oldValue = event.getOldValue() != null ? event.getOldValue() : "";
        String newValue = event.getNewValue() != null ? event.getNewValue().trim() : "";
        if (newValue.isBlank()) {
            setter.accept(row, "");
        } else {
            List<String> errors = new ArrayList<>();
            GUIUtils.validateShortText(newValue, fieldName, errors);
            if (errors.isEmpty()) {
                setter.accept(row, newValue);
            } else {
                GUIUtils.showErrors(errors);
                setter.accept(row, oldValue);
                tableActivities.refresh();
            }
        }
    }

    private HBox createButtonRow() {
        buttonExportPdf = new Button("Exportar PDF");
        buttonExportPdf.setPrefWidth(120);
        buttonExportPdf.setId("buttonExportPdf");
        buttonExportPdf.setOnAction(controllerGenerateFinalReport::handleFinalReportButtons);

        buttonCancel = new Button("Cancelar");
        buttonCancel.setPrefWidth(120);
        buttonCancel.setId("buttonCancel");
        buttonCancel.setOnAction(controllerGenerateFinalReport::handleFinalReportButtons);

        HBox buttonRow = new HBox(12, buttonExportPdf, buttonCancel);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);
        buttonRow.setPadding(new Insets(18, 0, 0, 0));
        return buttonRow;
    }

    public void setStudentName(String name) {
        labelStudentName.setText(name != null ? name : "-");
    }

    public void setStudentEnrollment(String enrollment) {
        labelStudentEnrollment.setText(enrollment != null ? enrollment : "-");
    }

    public void setStudentEmail(String email) {
        labelStudentEmail.setText(email != null ? email : "-");
    }

    public void setStudentNrc(String nrc) {
        labelStudentNrc.setText(nrc != null ? nrc : "-");
    }

    public void setPeriod(String period) {
        labelPeriod.setText(period != null ? period : "-");
    }

    public void setProjectInfo(String projectName, String enterprise, String professor) {
        labelProjectName.setText(projectName != null ? projectName : "-");
        labelEnterprise.setText(enterprise != null ? enterprise : "-");
        labelProfessor.setText(professor != null ? professor : "-");
    }

    public void setEducationalProgram(String educationalProgram) {
        labelEducationalProgram.setText(educationalProgram != null ? educationalProgram : "-");
    }

    public String getEducationalProgram() {
        return labelEducationalProgram.getText();
    }

    public void setGeneralObjectives(String generalObjectives) {
        labelGeneralObjectivesContent.setText(generalObjectives != null ? generalObjectives : "");
    }

    public String getGeneralObjectives() {
        return labelGeneralObjectivesContent.getText();
    }

    public void setMethodology(String methodology) {
        labelMethodologyContent.setText(methodology != null ? methodology : "");
    }

    public String getMethodology() {
        return labelMethodologyContent.getText();
    }

    public void setObservations(String observations) {
        textAreaObservations.setText(observations != null ? observations : "");
    }

    public String getObservations() {
        return textAreaObservations.getText();
    }

    public void setRows(ObservableList<FinalReportRow> rows) {
        tableActivities.setItems(rows);
    }

    public ObservableList<FinalReportRow> getFinalReportRows() {
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
