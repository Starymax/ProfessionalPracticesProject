package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;

import mx.fei.gui.controllers.ControllerEvaluateStudent;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.logic.dto.Document;
import mx.fei.logic.dto.Student;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
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
    private Button buttonPreview;
    private Button buttonClose;
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

        labelEnrollment = buildInfoLabel("Matrícula: ");
        labelName = buildInfoLabel("Nombre: ");
        labelProject = buildInfoLabel("Proyecto: ");
        labelRealizedHours = buildInfoLabel("Horas realizadas: ");
        labelRemainingHours = buildInfoLabel("Horas faltantes: ");
        VBox infoBox = new VBox(8, labelEnrollment, labelName, labelProject, labelRealizedHours, labelRemainingHours);
        infoBox.setPadding(new Insets(0, 0, 10, 0));

        Label reportsTitle = new Label("Reportes subidos por el alumno:");
        reportsTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
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
        listViewReports.setPrefHeight(220);
        listViewReports.setStyle("-fx-font-size: 13px;");

        buttonPreview = new Button("Vista previa");
        buttonClose = new Button("Cerrar");
        buttonPreview.setPrefWidth(160);
        buttonClose.setPrefWidth(160);
        HBox buttonsBox = new HBox(15, buttonPreview, buttonClose);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);

        controllerEvaluateStudent = new ControllerEvaluateStudent(this, student);
        buttonPreview.setOnAction(controllerEvaluateStudent::handlePreviewCloseButtons);
        buttonClose.setOnAction(controllerEvaluateStudent::handlePreviewCloseButtons);

        VBox centerBox = new VBox(10, reportsTitle, listViewReports);
        VBox topBox = new VBox(15, title, infoBox);

        BorderPane mainPanel = new BorderPane();
        mainPanel.setPadding(new Insets(28, 36, 28, 36));
        mainPanel.setTop(topBox);
        mainPanel.setCenter(centerBox);
        mainPanel.setBottom(buttonsBox);
        BorderPane.setMargin(topBox, new Insets(0, 0, 15, 0));
        BorderPane.setMargin(buttonsBox, new Insets(20, 0, 0, 0));

        Scene scene = new Scene(mainPanel, 640, 620);
        GUIStyle.apply(scene);
        stage.setTitle("Evaluar alumno");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private Label buildInfoLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("SansSerif", 13));
        return label;
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

    public void showError(String message) {
        GUIUtils.showError(message);
    }

    public Button getButtonPreview() {
        return buttonPreview;
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
}
