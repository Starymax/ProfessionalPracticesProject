package mx.fei.gui.views;

import mx.fei.logic.dto.Practice;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import mx.fei.gui.controllers.ControllerGenerateReport;
import mx.fei.gui.utils.GUIUtils;

public class GUIGenerateReport extends Application {

    private Practice practice;
    private Label labelName;
    private Label labelEducationalExperience;
    private Label labelProject;
    private Label labelReportType;
    private Button buttonPartial;
    private Button buttonMonthly;
    private Button buttonFinal;
    private Button buttonBack;

    public GUIGenerateReport(Practice practice) {
        this.practice = practice;
    }

    public GUIGenerateReport() {}

    @Override
    public void start(Stage stage) {
        stage.setTitle("GUIGenrarReporte");
        stage.setResizable(false);
        VBox formPanel = new VBox(20);
        formPanel.setPadding(new Insets(30, 40, 30, 40));
        formPanel.setAlignment(Pos.TOP_LEFT);
        formPanel.setBackground(new Background(new BackgroundFill(Color.rgb(220, 220, 220), CornerRadii.EMPTY, Insets.EMPTY)));
        formPanel.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        Label labelTitle = new Label("Generar reporte");
        labelTitle.setFont(Font.font("SansSerif", FontWeight.NORMAL, 22));
        labelTitle.setMaxWidth(Double.MAX_VALUE);
        labelTitle.setAlignment(Pos.CENTER);
        String studentName = practice != null && practice.getStudent() != null ? practice.getStudent().getName() + " " + practice.getStudent().getLastName() : "Nombre alumno";
        String experienceName = practice != null && practice.getEducationalExperience() != null ? practice.getEducationalExperience().getName() : "Nombre experiencia educativa";
        String projectName = practice != null && practice.getStudent() != null && practice.getStudent().getAssignedProject() != null ? practice.getStudent().getAssignedProject().getNameProject() : "Nombre proyecto";
        labelName = new Label("Nombre: " + studentName);
        labelName.setFont(new Font("SansSerif", 14));
        labelEducationalExperience = new Label("Experiencia educativa: " + experienceName);
        labelEducationalExperience.setFont(new Font("SansSerif", 14));
        labelProject = new Label("Proyecto: " + projectName);
        labelProject.setFont(new Font("SansSerif", 14));
        labelReportType = new Label("Tipo de reporte:");
        labelReportType.setFont(new Font("SansSerif", 14));
        buttonPartial  = createReportButton("Parcial");
        buttonMonthly = createReportButton("Mensual");
        buttonFinal = createReportButton("Final");
        buttonBack = createReportButton("Regresar");
        HBox buttonsBox = new HBox(15, buttonPartial, buttonMonthly, buttonFinal);
        buttonsBox.setAlignment(Pos.CENTER_LEFT);
        HBox bottomBox = new HBox();
        bottomBox.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        bottomBox.getChildren().addAll(buttonsBox, spacer, buttonBack);
        formPanel.getChildren().addAll(labelTitle, labelName, labelEducationalExperience, labelProject, labelReportType, bottomBox);
        StackPane mainPanel = new StackPane(formPanel);
        mainPanel.setPadding(new Insets(20));
        mainPanel.setBackground(new Background(new BackgroundFill(Color.rgb(200, 200, 200), CornerRadii.EMPTY, Insets.EMPTY)));
        ControllerGenerateReport controllerGenerateReport = new ControllerGenerateReport(this);
        buttonPartial.setOnAction(controllerGenerateReport::handleReportOptions);
        buttonMonthly.setOnAction(controllerGenerateReport::handleReportOptions);
        buttonFinal.setOnAction(controllerGenerateReport::handleReportOptions);
        buttonBack.setOnAction(controllerGenerateReport::handleReportOptions);
        Scene scene = new Scene(mainPanel, 700, 380);
        stage.setScene(scene);
        stage.show();
    }

    private Button createReportButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(110);
        button.setPrefHeight(40);
        button.setStyle("-fx-background-color: #323232; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-size: 13px;");
        return button;
    }

    public void showError(String message) {
        GUIUtils.showError(message);
    }

    public void showSuccess(String message) {
        GUIUtils.showSuccess(message);
    }

    public void closeWindow() {
        ((Stage) buttonBack.getScene().getWindow()).close();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public Practice getPractice() {
        return practice;
    }

    public Button getButtonPartial() {
        return buttonPartial;
    }

    public Button getButtonMonthly() {
        return buttonMonthly;
    }

    public Button getButtonFinal() {
        return buttonFinal;
    }

    public Button getButtonBack() {
        return buttonBack;
    }
}