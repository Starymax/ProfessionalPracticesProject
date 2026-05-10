package mx.fei.gui.views;

import javafx.scene.control.Alert;
import mx.fei.gui.controllers.ControllerManageProjects;
import mx.fei.gui.utils.GUIUtils;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class GUIManageProjects extends Application {

    private Button buttonRegisterProject;
    private Button buttonManageProject;
    private Button buttonGoBack;
    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        Label labelTitle = new Label("Gestionar proyectos");
        labelTitle.setFont(Font.font("SansSerif", 22));

        buttonRegisterProject = buildMenuButton("Registrar proyecto");
        buttonManageProject = buildMenuButton("Gestionar proyecto");
        buttonGoBack = buildMenuButton("Regresar");

        ControllerManageProjects controller = new ControllerManageProjects(this);
        buttonRegisterProject.setOnAction(controller);
        buttonManageProject.setOnAction(controller);
        buttonGoBack.setOnAction(controller);

        VBox mainPanel = new VBox(20, labelTitle, buttonRegisterProject, buttonManageProject, buttonGoBack);
        mainPanel.setAlignment(Pos.CENTER);
        mainPanel.setPadding(new Insets(32));
        mainPanel.setStyle("-fx-background-color: #d8d8d8;");

        Scene scene = new Scene(mainPanel, 560, 420);
        stage.setTitle("Gestión proyectos");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private Button buildMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(320);
        button.setPrefHeight(60);
        button.setFont(Font.font("SansSerif", 15));
        button.setStyle("-fx-background-color: #2e2e2e; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 16;");
        return button;
    }

    public void showError(String message) {
        GUIUtils.showError(message);
    }

    public Button getButtonRegisterProject() {
        return buttonRegisterProject;
    }

    public Button getButtonManageProject() {
        return buttonManageProject;
    }

    public Button getButtonGoBack() {
        return buttonGoBack;
    }

    public Stage getStage() {
        return stage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}