package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;
import mx.fei.logic.dto.Practice;
import mx.fei.gui.controllers.ControllerGenerateDocuments;
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

public class GUIGenerateDocuments extends Application {

    private Practice practice;
    private Button buttonGenerateAcceptanceLetter;
    private Button buttonGenerateSelfEvaluation;
    private Button buttonBack;

    public GUIGenerateDocuments(Practice practice) {
        this.practice = practice;
    }

    public GUIGenerateDocuments() {

    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Generar documentos");
        stage.setResizable(false);
        VBox formPanel = new VBox(25);
        formPanel.setPadding(new Insets(40, 40, 40, 40));
        formPanel.setAlignment(Pos.TOP_CENTER);
        formPanel.getStyleClass().add("form-panel");
        Label labelTitle = new Label("Generar documentos");
        labelTitle.setFont(Font.font("SansSerif", FontWeight.NORMAL, 24));
        Region spacer = new Region();
        spacer.setPrefHeight(10);
        buttonGenerateAcceptanceLetter = createMenuButton("Generar oficio de aceptación");
        buttonGenerateSelfEvaluation = createMenuButton("Generar autoevaluación");
        buttonBack = createMenuButton("Regresar");
        VBox buttonsBox = new VBox(18, buttonGenerateAcceptanceLetter, buttonGenerateSelfEvaluation, buttonBack);
        buttonsBox.setAlignment(Pos.CENTER);
        formPanel.getChildren().addAll(labelTitle, spacer, buttonsBox);
        StackPane mainPanel = new StackPane(formPanel);
        mainPanel.setPadding(new Insets(20));
        ControllerGenerateDocuments controllerGenerateDocuments = new ControllerGenerateDocuments(this);
        buttonGenerateAcceptanceLetter.setOnAction(controllerGenerateDocuments::handleButtonsGenerateDocuments);
        buttonGenerateSelfEvaluation.setOnAction(controllerGenerateDocuments::handleButtonsGenerateDocuments);
        buttonBack.setOnAction(controllerGenerateDocuments::handleButtonsGenerateDocuments);
        Scene scene = new Scene(mainPanel, 560, 500);
        GUIStyle.apply(scene);
        stage.setScene(scene);
        stage.show();
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

    public Button getButtonGenerateAcceptanceLetter() {
        return buttonGenerateAcceptanceLetter;
    }

    public Button getButtonGenerateSelfEvaluation() {
        return buttonGenerateSelfEvaluation;
    }

    public Button getButtonBack() {
        return buttonBack;
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(320);
        button.setPrefHeight(60);
        return button;
    }
}