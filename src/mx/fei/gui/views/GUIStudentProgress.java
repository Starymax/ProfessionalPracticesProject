package mx.fei.gui.views;

import mx.fei.gui.controllers.ControllerStudentProgress;
import mx.fei.gui.utils.GUIStyle;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.logic.dto.ActivityProgressRow;
import mx.fei.logic.dto.Document;
import mx.fei.logic.dto.Practice;
import mx.fei.logic.dto.Report;
import mx.fei.logic.dto.ValidationStatus;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

public class GUIStudentProgress {

    private static final int TABLE_ROW_HEIGHT = 24;
    private static final int TABLE_VISIBLE_ROWS = 5;
    private static final int TABLE_HEIGHT = TABLE_ROW_HEIGHT * TABLE_VISIBLE_ROWS + 30;

    private final Practice practice;
    private Stage stage;
    private ControllerStudentProgress controllerStudentProgress;

    private Label labelRealizedHours;
    private Label labelRemainingHours;
    private Label labelGoalHours;
    private Label labelGrade;
    private TableView<Document> tableViewDocuments;
    private TableView<Report> tableViewReports;
    private TableView<ActivityProgressRow> tableViewActivities;
    private Button buttonBack;

    public GUIStudentProgress(Practice practice) {
        this.practice = practice;
    }

    public void start(Stage stage) {
        this.stage = stage;
        controllerStudentProgress = new ControllerStudentProgress(this, practice);

        BorderPane mainPanel = new BorderPane();
        mainPanel.setPadding(new Insets(24));
        mainPanel.setTop(buildTopPanel());
        BorderPane.setMargin(mainPanel.getTop(), new Insets(0, 0, 12, 0));

        ScrollPane scrollPane = new ScrollPane(buildCenterPanel());
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        mainPanel.setCenter(scrollPane);

        mainPanel.setBottom(buildBottomPanel());
        BorderPane.setMargin(mainPanel.getBottom(), new Insets(12, 0, 0, 0));
        buttonBack.setOnAction(event -> controllerStudentProgress.handleBackButton());

        Scene scene = new Scene(mainPanel, 680, 700);
        GUIStyle.apply(scene);
        stage.setTitle("Mi Avance");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        controllerStudentProgress.loadData();
    }

    private VBox buildTopPanel() {
        Label labelNameBold = new Label("Bienvenido Alumno:");
        labelNameBold.setFont(Font.font("SansSerif", FontWeight.BOLD, 15));
        Label labelName = new Label(practice.getStudent().getName());
        labelName.setFont(Font.font("SansSerif", 15));
        HBox rowName = new HBox(8, labelNameBold, labelName);
        rowName.setAlignment(Pos.CENTER_LEFT);

        String projectName = "Sin proyecto asignado";
        if (practice.getStudent().getAssignedProject() != null) {
            projectName = practice.getStudent().getAssignedProject().getNameProject();
        }
        Label labelProjectBold = new Label("Proyecto asignado:");
        labelProjectBold.setFont(Font.font("SansSerif", FontWeight.BOLD, 15));
        Label labelProject = new Label(projectName);
        labelProject.setFont(Font.font("SansSerif", 15));
        HBox rowProject = new HBox(8, labelProjectBold, labelProject);
        rowProject.setAlignment(Pos.CENTER_LEFT);

        return new VBox(12, rowName, rowProject);
    }

    private VBox buildCenterPanel() {
        VBox centerPanel = new VBox(14);
        centerPanel.setPadding(new Insets(4, 4, 4, 0));
        centerPanel.getChildren().addAll(
                buildHoursSection(),
                new Separator(),
                buildTableSection("Documentos", buildDocumentsTableView()),
                new Separator(),
                buildTableSection("Reportes", buildReportsTableView()),
                new Separator(),
                buildTableSection("Actividades", buildActivitiesTableView())
        );
        return centerPanel;
    }

    private VBox buildHoursSection() {
        Label labelTitle = new Label("Horas de práctica");
        labelTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 13));

        labelRealizedHours = buildStatLabel("—");
        labelRemainingHours = buildStatLabel("—");
        labelGoalHours = buildStatLabel("420 hrs");
        labelGrade = buildStatLabel("Pendiente");

        VBox cardRealized = buildStatCard("Horas realizadas", labelRealizedHours);
        VBox cardRemaining = buildStatCard("Horas restantes", labelRemainingHours);
        VBox cardGoal = buildStatCard("Meta total", labelGoalHours);
        VBox cardGrade = buildStatCard("Calificación", labelGrade);

        HBox.setHgrow(cardRealized, Priority.ALWAYS);
        HBox.setHgrow(cardRemaining, Priority.ALWAYS);
        HBox.setHgrow(cardGoal, Priority.ALWAYS);
        HBox.setHgrow(cardGrade, Priority.ALWAYS);

        HBox cardsRow = new HBox(12, cardRealized, cardRemaining, cardGoal, cardGrade);
        return new VBox(8, labelTitle, cardsRow);
    }

    private VBox buildTableSection(String titleText, Node tableView) {
        Label labelTitle = new Label(titleText);
        labelTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 13));
        return new VBox(6, labelTitle, tableView);
    }

    private TableView<Document> buildDocumentsTableView() {
        tableViewDocuments = new TableView<>();
        tableViewDocuments.setFixedCellSize(TABLE_ROW_HEIGHT);
        tableViewDocuments.setPrefHeight(TABLE_HEIGHT);
        tableViewDocuments.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableViewDocuments.setPlaceholder(new Label("Sin documentos subidos"));

        TableColumn<Document, String> columnName = new TableColumn<>("Documento");
        columnName.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDocumentType().getDocumentType())
        );
        columnName.setPrefWidth(400);

        TableColumn<Document, String> columnStatus = new TableColumn<>("Estado");
        columnStatus.setCellValueFactory(data ->
                new SimpleStringProperty(documentStatusLabel(data.getValue().getValidationStatus()))
        );
        columnStatus.setPrefWidth(200);
        tableViewDocuments.getColumns().addAll(columnName, columnStatus);
        return tableViewDocuments;
    }

    private TableView<Report> buildReportsTableView() {
        tableViewReports = new TableView<>();
        tableViewReports.setFixedCellSize(TABLE_ROW_HEIGHT);
        tableViewReports.setPrefHeight(TABLE_HEIGHT);
        tableViewReports.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableViewReports.setPlaceholder(new Label("Sin reportes entregados"));
        TableColumn<Report, String> columnType = new TableColumn<>("Reporte");
        columnType.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getReportType())
        );
        columnType.setPrefWidth(300);
        TableColumn<Report, String> columnDate = new TableColumn<>("Fecha");
        columnDate.setCellValueFactory(data ->
                new SimpleStringProperty(reportDateLabel(data.getValue()))
        );
        columnDate.setPrefWidth(150);
        TableColumn<Report, String> columnStatus = new TableColumn<>("Estado");
        columnStatus.setCellValueFactory(data ->
                new SimpleStringProperty("✔ Entregado")
        );
        columnStatus.setPrefWidth(150);
        tableViewReports.getColumns().addAll(columnType, columnDate, columnStatus);
        return tableViewReports;
    }

    private TableView<ActivityProgressRow> buildActivitiesTableView() {
        tableViewActivities = new TableView<>();
        tableViewActivities.setFixedCellSize(TABLE_ROW_HEIGHT);
        tableViewActivities.setPrefHeight(TABLE_HEIGHT);
        tableViewActivities.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableViewActivities.setPlaceholder(new Label("Sin actividades — no hay proyecto asignado"));

        TableColumn<ActivityProgressRow, String> columnName = new TableColumn<>("Actividad");
        columnName.setCellValueFactory(data -> data.getValue().nameProperty());
        columnName.setPrefWidth(260);

        TableColumn<ActivityProgressRow, String> columnPlanned = new TableColumn<>("Hrs planificadas");
        columnPlanned.setCellValueFactory(data -> data.getValue().plannedHoursProperty());
        columnPlanned.setPrefWidth(130);

        TableColumn<ActivityProgressRow, String> columnRealized = new TableColumn<>("Hrs realizadas");
        columnRealized.setCellValueFactory(data -> data.getValue().realizedHoursProperty());
        columnRealized.setPrefWidth(130);

        TableColumn<ActivityProgressRow, String> columnStatus = new TableColumn<>("Estado");
        columnStatus.setCellValueFactory(data -> data.getValue().statusProperty());
        columnStatus.setPrefWidth(80);

        tableViewActivities.getColumns().addAll(columnName, columnPlanned, columnRealized, columnStatus);
        return tableViewActivities;
    }

    private HBox buildBottomPanel() {
        buttonBack = new Button("Regresar");
        buttonBack.setPrefWidth(160);
        buttonBack.setPrefHeight(40);
        buttonBack.setFont(Font.font("SansSerif", 13));
        HBox bottomPanel = new HBox(buttonBack);
        bottomPanel.setAlignment(Pos.BOTTOM_RIGHT);
        return bottomPanel;
    }

    private VBox buildStatCard(String titleText, Label labelValue) {
        Label labelTitle = new Label(titleText);
        labelTitle.setFont(Font.font("SansSerif", 12));
        labelTitle.getStyleClass().add("label-secondary");
        VBox card = new VBox(6, labelTitle, labelValue);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(12));
        card.getStyleClass().add("card-panel");
        return card;
    }

    private Label buildStatLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("SansSerif", FontWeight.BOLD, 22));
        return label;
    }

    private String documentStatusLabel(ValidationStatus status) {
        String statusText;
        switch (status) {
            case VALIDATED -> statusText = "Aceptado";
            case REJECTED -> statusText = "Rechazado — resubir";
            case PENDING -> statusText = "Pendiente";
            default -> statusText = "No subido";
        }
        return statusText;
    }

    private String reportDateLabel(Report report) {
        String date = "—";
        if (report.getReportDate() != null) {
            date = report.getReportDate().toString();
        }
        return date;
    }

    public void updateHours(int realized, int remaining, int goal) {
        labelRealizedHours.setText(realized + " hrs");
        labelRemainingHours.setText(remaining + " hrs");
        labelGoalHours.setText(goal + " hrs");
    }

    public void updateGrade(float grade) {
        if (grade > 0) {
            labelGrade.setText(String.format("%.1f", grade));
        } else {
            labelGrade.setText("Pendiente");
        }
    }

    public void updateDocuments(List<Document> documents) {
        tableViewDocuments.setItems(FXCollections.observableArrayList(documents));
    }

    public void updateReports(List<Report> reports) {
        tableViewReports.setItems(FXCollections.observableArrayList(reports));
    }

    public void updateActivities(List<ActivityProgressRow> rows) {
        tableViewActivities.setItems(FXCollections.observableArrayList(rows));
    }

    public void closeWindow() {
        stage.close();
    }

    public void showError(String message) {
        GUIUtils.showError(message);
    }

    public Button getButtonBack() {
        return buttonBack;
    }

    public TableView<Document> getTableViewDocuments() {
        return tableViewDocuments;
    }

    public TableView<Report> getTableViewReports() {
        return tableViewReports;
    }
}
