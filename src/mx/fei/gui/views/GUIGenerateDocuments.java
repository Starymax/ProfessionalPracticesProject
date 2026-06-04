package mx.fei.gui.views;

import mx.fei.logic.dto.Practice;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import mx.fei.gui.controllers.ControllerGenerateDocuments;
import mx.fei.gui.utils.GUIUtils;

public class GUIGenerateDocuments extends Application {

    private Practice practice;
    private Button buttonGenerateAcceptanceLetter;
    private Button buttonGenerateSelfEvaluation;
    private Button buttonBack;

    public GUIGenerateDocuments(Practice practice) {
        this.practice = practice;
    }

    public GUIGenerateDocuments() {}

    @Override
    public void start(Stage stage) {
        stage.setTitle("Generar documentos");
        stage.setResizable(false);
        VBox formPanel = new VBox(25);
        formPanel.setPadding(new Insets(40, 40, 40, 40));
        formPanel.setAlignment(Pos.TOP_CENTER);
        formPanel.setBackground(new Background(new BackgroundFill(Color.rgb(220, 220, 220), CornerRadii.EMPTY, Insets.EMPTY)));
        formPanel.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
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
        mainPanel.setBackground(new Background(new BackgroundFill(Color.rgb(200, 200, 200), CornerRadii.EMPTY, Insets.EMPTY)));
        ControllerGenerateDocuments controllerGenerateDocuments = new ControllerGenerateDocuments(this);
        buttonGenerateAcceptanceLetter.setOnAction(controllerGenerateDocuments::handleButtonsGenerateDocuments);
        buttonGenerateSelfEvaluation.setOnAction(controllerGenerateDocuments::handleButtonsGenerateDocuments);
        buttonBack.setOnAction(controllerGenerateDocuments::handleButtonsGenerateDocuments);
        Scene scene = new Scene(mainPanel, 560, 500);
        stage.setScene(scene);
        stage.show();
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(320);
        button.setPrefHeight(60);
        button.setStyle("-fx-background-color: #323232; -fx-text-fill: white; -fx-background-radius: 20; -fx-font-size: 14px;");
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

    public Button getButtonGenerateAcceptanceLetter() {
        return buttonGenerateAcceptanceLetter;
    }

    public Button getButtonGenerateSelfEvaluation() {
        return buttonGenerateSelfEvaluation;
    }

    public Button getButtonBack() {
        return buttonBack;
    }
}