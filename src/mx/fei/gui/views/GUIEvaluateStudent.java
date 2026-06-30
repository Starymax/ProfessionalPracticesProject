package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;
import mx.fei.gui.controllers.ControllerEvaluateStudent;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.logic.dto.Document;
import mx.fei.logic.dto.DocumentReviewItem;
import mx.fei.logic.dto.Student;
import mx.fei.logic.dto.ValidationStatus;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

public class GUIEvaluateStudent extends Application {

    private Student student;
    private Label labelEnrollment;
    private Label labelName;
    private Label labelProject;
    private Label labelRealizedHours;
    private Label labelRemainingHours;
    private ListView<Document> listViewReports;
    private ListView<DocumentReviewItem> listViewDocuments;
    private TextField textFieldGrade;
    private Button buttonGrade;
    private Button buttonPreview;
    private Button buttonViewDocument;
    private Button buttonClose;
    private Label labelGradeHint;
    private Stage stage;
    private ControllerEvaluateStudent controllerEvaluateStudent;

    public GUIEvaluateStudent(Student student) {
        this.student = student;
    }

    public GUIEvaluateStudent() {
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        Label title = new Label("Evaluación del alumno");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 16));

        VBox infoBox = buildInfoBox();

        Label reportsTitle = new Label("Reportes subidos por el alumno:");
        reportsTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        configureReportsListView();

        buttonPreview = new Button("Vista previa");
        buttonPreview.setPrefWidth(160);
        HBox reportsButtonRow = new HBox(buttonPreview);
        reportsButtonRow.setAlignment(Pos.CENTER_RIGHT);

        Label documentsTitle = new Label("Documentos del alumno:");
        documentsTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        configureDocumentsListView();

        buttonViewDocument = new Button("Ver documento");
        buttonViewDocument.setPrefWidth(160);
        HBox documentsButtonRow = new HBox(buttonViewDocument);
        documentsButtonRow.setAlignment(Pos.CENTER_RIGHT);

        Label labelGrade = new Label("Calificación (0-10):");
        labelGrade.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        textFieldGrade = new TextField();
        textFieldGrade.setPrefWidth(80);
        buttonGrade = new Button("Calificar práctica");
        buttonGrade.setPrefWidth(180);
        HBox gradeRow = new HBox(10, labelGrade, textFieldGrade, buttonGrade);
        gradeRow.setAlignment(Pos.CENTER_LEFT);
        labelGradeHint = new Label();
        labelGradeHint.getStyleClass().add("label-hint");
        VBox gradeBox = new VBox(4, gradeRow, labelGradeHint);

        buttonClose = new Button("Cerrar");
        buttonClose.setPrefWidth(160);
        HBox closeRow = new HBox(buttonClose);
        closeRow.setAlignment(Pos.CENTER_RIGHT);

        controllerEvaluateStudent = new ControllerEvaluateStudent(this, student);
        buttonPreview.setOnAction(event -> controllerEvaluateStudent.previewReport());
        buttonViewDocument.setOnAction(event -> controllerEvaluateStudent.openDocumentPreview());
        buttonGrade.setOnAction(event -> controllerEvaluateStudent.handleGradeButton());
        buttonClose.setOnAction(event -> getStage().close());

        VBox centerBox = new VBox(10, reportsTitle, listViewReports, reportsButtonRow, documentsTitle, listViewDocuments, documentsButtonRow);
        VBox topBox = new VBox(15, title, infoBox);
        VBox bottomBox = new VBox(12, gradeBox, closeRow);

        BorderPane mainPanel = new BorderPane();
        mainPanel.setPadding(new Insets(28, 36, 28, 36));
        mainPanel.setTop(topBox);
        mainPanel.setCenter(centerBox);
        mainPanel.setBottom(bottomBox);
        BorderPane.setMargin(topBox, new Insets(0, 0, 15, 0));
        BorderPane.setMargin(bottomBox, new Insets(20, 0, 0, 0));

        Scene scene = new Scene(mainPanel, 680, 760);
        GUIStyle.apply(scene);
        stage.setTitle("Evaluar alumno");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private VBox buildInfoBox() {
        labelEnrollment = buildInfoLabel("Matrícula: ");
        labelName = buildInfoLabel("Nombre: ");
        labelProject = buildInfoLabel("Proyecto: ");
        labelRealizedHours = buildInfoLabel("Horas realizadas: ");
        labelRemainingHours = buildInfoLabel("Horas faltantes: ");
        VBox infoBox = new VBox(8, labelEnrollment, labelName, labelProject, labelRealizedHours, labelRemainingHours);
        infoBox.setPadding(new Insets(0, 0, 10, 0));
        return infoBox;
    }

    private void configureReportsListView() {
        listViewReports = new ListView<>();
        listViewReports.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Document report, boolean empty) {
                super.updateItem(report, empty);
                if (empty || report == null) {
                    setText(null);
                } else {
                    String status = report.isAccepted() ? "✔ Aceptado" : "✘ Pendiente";
                    setText(report.getDocumentType().getDocumentType() + " - " + report.getName() + "   [" + status + "]");
                }
            }
        });
        listViewReports.setPrefHeight(180);
    }

    private void configureDocumentsListView() {
        listViewDocuments = new ListView<>();
        listViewDocuments.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(DocumentReviewItem item, boolean isEmpty) {
                super.updateItem(item, isEmpty);
                if (isEmpty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.getDocumentType().getDocumentType() + "   [" + statusLabel(item.getStatus()) + "]");
                    setStyle("-fx-text-fill: " + statusColor(item.getStatus()) + ";");
                }
            }
        });
        listViewDocuments.setPrefHeight(180);
    }

    public void loadData() {
        controllerEvaluateStudent.loadData();
    }

    public void reloadReports() {
        controllerEvaluateStudent.loadReports();
    }

    public void setStudentInfo(String enrollment, String fullName, String project) {
        labelEnrollment.setText("Matrícula: " + enrollment);
        labelName.setText("Nombre: " + fullName);
        labelProject.setText("Proyecto: " + project);
    }

    public void setHours(float realizedHours, float remainingHours) {
        labelRealizedHours.setText(String.format("Horas realizadas: %.1f", realizedHours));
        labelRemainingHours.setText(String.format("Horas faltantes: %.1f", remainingHours));
    }

    public void setReports(List<Document> reports) {
        listViewReports.getItems().clear();
        listViewReports.getItems().addAll(reports);
    }

    public Document getSelectedReport() {
        return listViewReports.getSelectionModel().getSelectedItem();
    }

    public void setDocuments(List<DocumentReviewItem> documents) {
        listViewDocuments.getItems().clear();
        listViewDocuments.getItems().addAll(documents);
    }

    public DocumentReviewItem getSelectedDocument() {
        return listViewDocuments.getSelectionModel().getSelectedItem();
    }

    public String getGradeText() {
        return textFieldGrade.getText().trim();
    }

    public void setGradeValue(String text) {
        textFieldGrade.setText(text);
    }

    public void setGradeEnabled(boolean enabled) {
        textFieldGrade.setDisable(!enabled);
        buttonGrade.setDisable(!enabled);
    }

    public void setGradeHint(String hint) {
        labelGradeHint.setText(hint);
    }

    public void showError(String message) {
        GUIUtils.showError(message);
    }

    public void showSuccess(String message) {
        GUIUtils.showSuccess(message);
    }

    public Button getButtonGrade() {
        return buttonGrade;
    }

    public Button getButtonPreview() {
        return buttonPreview;
    }

    public Button getButtonViewDocument() {
        return buttonViewDocument;
    }

    public Button getButtonClose() {
        return buttonClose;
    }

    public Stage getStage() {
        return stage;
    }

    public static void main(String[] args) {
        launch(args);
    }

    private Label buildInfoLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("SansSerif", 13));
        return label;
    }

    private String statusLabel(ValidationStatus status) {
        String statusString;
        switch (status) {
            case VALIDATED -> statusString = "Validado";
            case REJECTED -> statusString = "Rechazado";
            case NOT_UPLOADED -> statusString = "No subido";
            default -> statusString = "Subido (sin revisión)";
        };
        return statusString;
    }

    private String statusColor(ValidationStatus status) {
        String statusColor;
        switch (status) {
            case VALIDATED -> statusColor = "#2e7d32";
            case REJECTED -> statusColor = "#b71c1c";
            case NOT_UPLOADED -> statusColor = "#757575";
            default -> statusColor = "#e65100";
        };
        return statusColor;
    }
}