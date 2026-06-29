package mx.fei.gui.views;

import mx.fei.gui.controllers.ControllerPracticeInfo;
import mx.fei.gui.utils.GUIStyle;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.logic.dto.Practice;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import mx.fei.logic.dto.Project;

public class GUIPracticeInfo extends Application {

    private Practice practice;
    private Label labelEnrollment;
    private Label labelStudentName;
    private Label labelEmail;
    private Label labelActiveStatus;
    private Label labelEnterprise;
    private Label labelProjectName;
    private Label labelProjectManager;
    private Label labelStartDate;
    private Label labelFinalDate;
    private Label labelGrade;
    private Label labelExperienceName;
    private Label labelNrc;
    private Label labelCareer;
    private Label labelPeriod;
    private Button buttonBack;
    private Stage stage;

    public GUIPracticeInfo(Practice practice) {
        this.practice = practice;
    }

    public GUIPracticeInfo() {}

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("Información de práctica");
        stage.setResizable(false);

        VBox formPanel = new VBox(12);
        formPanel.setPadding(new Insets(25, 40, 25, 40));
        formPanel.setAlignment(Pos.TOP_LEFT);
        formPanel.getStyleClass().add("form-panel");

        Label labelTitle = new Label("Información de práctica");
        labelTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 18));
        labelTitle.setAlignment(Pos.CENTER);
        labelTitle.setMaxWidth(Double.MAX_VALUE);
        formPanel.getChildren().addAll(labelTitle, buildSectionTitle("Estudiante:"), buildStudentGrid(), buildSectionTitle("Proyecto:"), buildProjectGrid(), buildSectionTitle("Experiencia educativa:"), buildExperienceGrid());
        buttonBack = new Button("Regresar");
        buttonBack.setPrefWidth(120);
        buttonBack.setPrefHeight(35);
        formPanel.getChildren().add(buttonBack);
        ScrollPane scrollPane = new ScrollPane(formPanel);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        StackPane mainPanel = new StackPane(scrollPane);
        mainPanel.setPadding(new Insets(20));
        ControllerPracticeInfo controller = new ControllerPracticeInfo(this);
        buttonBack.setOnAction(controller::handleBackButton);
        Scene scene = new Scene(mainPanel, 560, 620);
        GUIStyle.apply(scene);
        stage.setScene(scene);
        stage.show();
        populateFields();
    }

    private Label buildSectionTitle(String text) {
        Label labelSection = new Label(text);
        labelSection.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        labelSection.setPadding(new Insets(10, 0, 2, 0));
        return labelSection;
    }

    private GridPane buildStudentGrid() {
        GridPane gridStudent = buildBaseGrid();
        gridStudent.add(buildFieldLabel("Matrícula:"), 0, 0);
        labelEnrollment = buildValueLabel();
        gridStudent.add(labelEnrollment, 1, 0);
        gridStudent.add(buildFieldLabel("Nombre:"), 0, 1);
        labelStudentName = buildValueLabel();
        gridStudent.add(labelStudentName, 1, 1);
        gridStudent.add(buildFieldLabel("Correo:"), 0, 2);
        labelEmail = buildValueLabel();
        gridStudent.add(labelEmail, 1, 2);
        gridStudent.add(buildFieldLabel("Estado actual:"), 0, 3);
        labelActiveStatus = buildValueLabel();
        gridStudent.add(labelActiveStatus, 1, 3);
        return gridStudent;
    }

    private GridPane buildProjectGrid() {
        GridPane gridProject = buildBaseGrid();
        gridProject.add(buildFieldLabel("Organización vinculada:"), 0, 0);
        labelEnterprise = buildValueLabel();
        gridProject.add(labelEnterprise, 1, 0);
        gridProject.add(buildFieldLabel("Nombre del proyecto:"), 0, 1);
        labelProjectName = buildValueLabel();
        gridProject.add(labelProjectName, 1, 1);
        gridProject.add(buildFieldLabel("Responsable:"), 0, 2);
        labelProjectManager = buildValueLabel();
        gridProject.add(labelProjectManager, 1, 2);
        gridProject.add(buildFieldLabel("Fecha inicio:"), 0, 3);
        labelStartDate = buildValueLabel();
        gridProject.add(labelStartDate, 1, 3);
        gridProject.add(buildFieldLabel("Fecha fin:"), 0, 4);
        labelFinalDate = buildValueLabel();
        gridProject.add(labelFinalDate, 1, 4);
        gridProject.add(buildFieldLabel("Calificación:"), 0, 5);
        labelGrade = buildValueLabel();
        gridProject.add(labelGrade, 1, 5);
        return gridProject;
    }

    private GridPane buildExperienceGrid() {
        GridPane gridExperience = buildBaseGrid();
        gridExperience.add(buildFieldLabel("Nombre de la experiencia:"), 0, 0);
        labelExperienceName = buildValueLabel();
        gridExperience.add(labelExperienceName, 1, 0);
        gridExperience.add(buildFieldLabel("NRC:"), 0, 1);
        labelNrc = buildValueLabel();
        gridExperience.add(labelNrc, 1, 1);
        gridExperience.add(buildFieldLabel("Carrera:"), 0, 2);
        labelCareer = buildValueLabel();
        gridExperience.add(labelCareer, 1, 2);
        gridExperience.add(buildFieldLabel("Periodo:"), 0, 3);
        labelPeriod = buildValueLabel();
        gridExperience.add(labelPeriod, 1, 3);
        return gridExperience;
    }

    private GridPane buildBaseGrid() {
        GridPane gridBase = new GridPane();
        gridBase.setHgap(10);
        gridBase.setVgap(8);
        gridBase.setPadding(new Insets(4, 0, 4, 10));
        return gridBase;
    }

    private Label buildFieldLabel(String text) {
        Label labelField = new Label(text);
        labelField.setFont(Font.font("SansSerif", FontWeight.NORMAL, 12));
        labelField.setMinWidth(170);
        return labelField;
    }

    private Label buildValueLabel() {
        Label labelValue = new Label("-");
        labelValue.setFont(Font.font("SansSerif", FontWeight.NORMAL, 12));
        return labelValue;
    }

    private void populateFields() {
        if (practice == null) {
            return;
        }
        if (practice.getStudent() != null) {
            labelEnrollment.setText(practice.getStudent().getEnrollment());
            labelStudentName.setText(practice.getStudent().getName() + " " + practice.getStudent().getLastName());
            labelEmail.setText(practice.getStudent().getEmail());
            labelActiveStatus.setText(practice.getStudent().isActive() ? "Activo" : "Inactivo");
        }
        if (practice.getStudent() != null && practice.getStudent().getAssignedProject() != null) {
            Project project = practice.getStudent().getAssignedProject();
            labelEnterprise.setText(project.getEnterprise() != null ? project.getEnterprise().getName() : "-");
            labelProjectName.setText(project.getNameProject());
            labelProjectManager.setText(project.getProjectManager() != null ? project.getProjectManager().getName() : "-");
            labelStartDate.setText(project.getStartDate() != null ? project.getStartDate().toString() : "-");
            labelFinalDate.setText(project.getFinalDate() != null ? project.getFinalDate().toString() : "-");
        } else {
            labelEnterprise.setText("Sin proyecto asignado");
            labelProjectName.setText("-");
            labelProjectManager.setText("-");
            labelStartDate.setText("-");
            labelFinalDate.setText("-");
        }
        labelGrade.setText(practice.getGrade() > 0 ? String.valueOf(practice.getGrade()) : "Sin calificación");
        if (practice.getEducationalExperience() != null) {
            labelExperienceName.setText(practice.getEducationalExperience().getName());
            labelNrc.setText(practice.getEducationalExperience().getNrc());
            labelCareer.setText(practice.getEducationalExperience().getEducationalProgram());
            labelPeriod.setText(practice.getEducationalExperience().getPeriod());
        }
    }

    public void showError(String message) {
        GUIUtils.showError(message);
    }

    public void closeWindow() {
        if (stage != null) {
            stage.close();
        }
    }

    public Stage getStage() {
        return stage;
    }

    public Button getButtonBack() {
        return buttonBack;
    }

    public static void main(String[] args) {
        launch(args);
    }
}