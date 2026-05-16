package mx.fei.gui.views;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.Region;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import mx.fei.gui.controllers.ControllerManageExperience;

public class GUIManageExperience extends Application {
    private Button buttonRegisterExperience;
    private Button buttonModifyExperience;
    private Button buttonActivateExperience;
    private Button buttonBack;

    public GUIManageExperience() {}

    @Override
    public void start(Stage stage) {
        stage.setTitle("GUIManageExperience");
        stage.setResizable(false);
        VBox formPanel = new VBox(20);
        formPanel.setPadding(new Insets(40, 40, 40, 40));
        formPanel.setAlignment(Pos.TOP_CENTER);
        formPanel.setBackground(new Background(new BackgroundFill(Color.rgb(220, 220, 220), CornerRadii.EMPTY, Insets.EMPTY)));
        formPanel.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        Label labelTitle = new Label("Gestionar experiencias educativas");
        labelTitle.setFont(Font.font("SansSerif", FontWeight.NORMAL, 24));
        Region spacer = new Region();
        spacer.setPrefHeight(10);
        buttonRegisterExperience = createMenuButton("Registrar nueva experiencia");
        buttonModifyExperience = createMenuButton("Modificar experiencia");
        buttonActivateExperience = createMenuButton("Dar de alta experiencia");
        buttonBack = createMenuButton("Regresar");
        VBox buttonsBox = new VBox(18,buttonRegisterExperience, buttonModifyExperience, buttonActivateExperience, buttonBack);
        buttonsBox.setAlignment(Pos.CENTER);
        formPanel.getChildren().addAll(labelTitle, spacer, buttonsBox);
        StackPane mainPanel = new StackPane(formPanel);
        mainPanel.setPadding(new Insets(20));
        mainPanel.setBackground(new Background(new BackgroundFill(Color.rgb(200, 200, 200), CornerRadii.EMPTY, Insets.EMPTY)));
        ControllerManageExperience controllerManageExperience = new ControllerManageExperience(this);
        buttonRegisterExperience.setOnAction(controllerManageExperience::handleRegisterModifyButtons);
        buttonModifyExperience.setOnAction(controllerManageExperience::handleRegisterModifyButtons);
        buttonActivateExperience.setOnAction(controllerManageExperience::handleRegisterModifyButtons);
        buttonBack.setOnAction(controllerManageExperience::handleRegisterModifyButtons);
        Scene scene = new Scene(mainPanel, 600, 500);
        stage.setScene(scene);
        stage.show();
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(340);
        button.setPrefHeight(60);
        button.setStyle("-fx-background-color: #323232; -fx-text-fill: white; " + "-fx-background-radius: 20; -fx-font-size: 14px;");
        return button;
    }

    public void closeWindow() {
        ((Stage) buttonBack.getScene().getWindow()).close();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public Button getButtonRegisterExperience() {
        return buttonRegisterExperience;
    }

    public Button getButtonModifyExperience() {
        return buttonModifyExperience;
    }

    public Button getButtonActivateExperience() {
        return buttonActivateExperience;
    }

    public Button getButtonBack() {
        return buttonBack;
    }
}
