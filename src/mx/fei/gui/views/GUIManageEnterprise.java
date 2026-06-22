package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;
import mx.fei.gui.controllers.ControllerManageEnterprise;
import mx.fei.gui.utils.GUIUtils;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;


public class GUIManageEnterprise extends Application {
    private Button buttonRegisterEnterprise;
    private Button buttonModifyEnterprise;
    private Button buttonBack;

    public GUIManageEnterprise() {}

    @Override
    public void start(Stage stage) {
        stage.setTitle("Gestionar organización");
        stage.setResizable(false);
        VBox formPanel = new VBox(25);
        formPanel.setPadding(new Insets(40, 40, 40, 40));
        formPanel.setAlignment(Pos.TOP_CENTER);
        formPanel.getStyleClass().add("form-panel");
        Label labelTitle = new Label("Gestionar organizaciones vinculadas");
        labelTitle.setFont(Font.font("SansSerif", FontWeight.NORMAL, 24));
        Region spacer = new Region();
        spacer.setPrefHeight(10);
        buttonRegisterEnterprise = createMenuButton("Registrar organización vinculada");
        buttonModifyEnterprise = createMenuButton("Modificar organización vinculada");
        buttonBack = createMenuButton("Regresar");
        VBox buttonsBox = new VBox(20, buttonRegisterEnterprise, buttonModifyEnterprise, buttonBack);
        buttonsBox.setAlignment(Pos.CENTER);
        formPanel.getChildren().addAll(labelTitle, spacer, buttonsBox);
        StackPane mainPanel = new StackPane(formPanel);
        mainPanel.setPadding(new Insets(20));
        ControllerManageEnterprise controllerManageEnterprise = new ControllerManageEnterprise(this);
        buttonRegisterEnterprise.setOnAction(controllerManageEnterprise::handleRegisterModifyReturnButtons);
        buttonModifyEnterprise.setOnAction(controllerManageEnterprise::handleRegisterModifyReturnButtons);
        buttonBack.setOnAction(controllerManageEnterprise::handleRegisterModifyReturnButtons);
        Scene scene = new Scene(mainPanel, 600, 480);
        GUIStyle.apply(scene);
        stage.setScene(scene);
        stage.show();
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(340);
        button.setPrefHeight(60);
        return button;
    }

    public void closeWindow() {
        GUIUtils.closeWindow((Stage) buttonBack.getScene().getWindow());
    }

    public static void main(String[] args) {
        launch(args);
    }

    public Button getButtonRegisterEnterprise() {
        return buttonRegisterEnterprise;
    }

    public Button getButtonModifyEnterprise() {
        return buttonModifyEnterprise;
    }

    public Button getButtonBack() {
        return buttonBack;
    }
}