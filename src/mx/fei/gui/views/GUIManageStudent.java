package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;
import mx.fei.gui.controllers.ControllerManageStudent;
import mx.fei.gui.utils.GUIUtils;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class GUIManageStudent extends Application {
    private Button buttonRegisterStudent;
    private Button buttonModifyStudent;
    private Button buttonAssignProject;
    private Button buttonConsultPractices;
    private Button buttonBack;

    public GUIManageStudent() {

    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Gestión estudiantes");
        stage.setResizable(false);
        VBox formPanel = new VBox(20);
        formPanel.setPadding(new Insets(40, 40, 40, 40));
        formPanel.setAlignment(Pos.TOP_CENTER);
        formPanel.getStyleClass().add("form-panel");
        Label labelTitle = new Label("Gestionar estudiantes");
        labelTitle.setFont(Font.font("SansSerif", FontWeight.NORMAL, 26));
        Region spacer = new Region();
        spacer.setPrefHeight(10);
        buttonRegisterStudent = createMenuButton("Registrar estudiante");
        buttonModifyStudent = createMenuButton("Modificar estudiante");
        buttonAssignProject = createMenuButton("Asignar proyecto");
        buttonConsultPractices = createMenuButton("Consultar practicas");
        buttonBack = createMenuButton("Regresar");
        VBox buttonsBox = new VBox(18, buttonRegisterStudent, buttonModifyStudent, buttonAssignProject, buttonConsultPractices, buttonBack);
        buttonsBox.setAlignment(Pos.CENTER);
        formPanel.getChildren().addAll(labelTitle, spacer, buttonsBox);
        StackPane mainPanel = new StackPane(formPanel);
        mainPanel.setPadding(new Insets(20));
        ControllerManageStudent controllerManageStudent = new ControllerManageStudent(this);
        buttonRegisterStudent.setOnAction(event -> controllerManageStudent.openRegisterStudent());
        buttonModifyStudent.setOnAction(event -> controllerManageStudent.handleModifyStudentButtonAction());
        buttonAssignProject.setOnAction(event -> controllerManageStudent.handleAssignProjectButtonAction());
        buttonBack.setOnAction(event -> closeWindow());
        buttonConsultPractices.setOnAction(event -> controllerManageStudent.handleConsultPracticesButtonAction());
        Scene scene = new Scene(mainPanel, 550, 500);
        GUIStyle.apply(scene);
        stage.setScene(scene);
        stage.show();
    }

    public void showError(String message) {
        GUIUtils.showError(message);
    }

    public void closeWindow() {
        ((Stage) buttonBack.getScene().getWindow()).close();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public Button getButtonRegisterStudent() {
        return buttonRegisterStudent;
    }

    public Button getButtonModifyStudent() {
        return buttonModifyStudent;
    }

    public Button getButtonAssignProject() {
        return buttonAssignProject;
    }

    public Button getButtonBack() {
        return buttonBack;
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(260);
        button.setPrefHeight(55);
        return button;
    }
}