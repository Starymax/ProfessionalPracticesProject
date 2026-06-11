package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;
import mx.fei.gui.controllers.ControllerAdministratorMenu;
import mx.fei.logic.dto.Professor;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class GUIAdministratorMenu extends Application {

    private Professor professor;
    private Label labelAdministratorName;
    private Button buttonRegisterProfessor;
    private Button buttonModifyProfessor;
    private Button buttonProfessorView;
    private Button buttonLogout;
    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        Label bold = new Label("Bienvenido Administrador:");
        bold.setFont(Font.font("SansSerif", FontWeight.BOLD, 15));

        labelAdministratorName = new Label("Nombre del Administrador");
        labelAdministratorName.setFont(Font.font("SansSerif", 15));

        HBox welcomeRow = new HBox(8, bold, labelAdministratorName);
        welcomeRow.setAlignment(Pos.CENTER_LEFT);

        buttonRegisterProfessor = buildMenuButton("Registrar Profesor");
        buttonModifyProfessor = buildMenuButton("Modificar Profesor");

        VBox centerButtons = new VBox(20, buttonRegisterProfessor, buttonModifyProfessor);
        centerButtons.setAlignment(Pos.CENTER);

        buttonProfessorView = buildMenuButton("Vista de Profesor");
        buttonProfessorView.setPrefWidth(180);

        buttonLogout = buildMenuButton("Cerrar Sesión");
        buttonLogout.setPrefWidth(160);

        VBox bottomRightButtons = new VBox(10, buttonProfessorView, buttonLogout);
        bottomRightButtons.setAlignment(Pos.BOTTOM_RIGHT);

        ControllerAdministratorMenu controllerAdministratorMenu = new ControllerAdministratorMenu(this);
        buttonRegisterProfessor.setOnAction(controllerAdministratorMenu::handleRegisterModifyProfessorViewCancelButtons);
        buttonModifyProfessor.setOnAction(controllerAdministratorMenu::handleRegisterModifyProfessorViewCancelButtons);
        buttonProfessorView.setOnAction(controllerAdministratorMenu::handleRegisterModifyProfessorViewCancelButtons);
        buttonLogout.setOnAction(controllerAdministratorMenu::handleRegisterModifyProfessorViewCancelButtons);

        BorderPane mainPanel = new BorderPane();
        mainPanel.setPadding(new Insets(32, 40, 32, 40));
        mainPanel.setTop(welcomeRow);
        mainPanel.setCenter(centerButtons);
        mainPanel.setBottom(bottomRightButtons);
        BorderPane.setMargin(centerButtons, new Insets(20, 0, 20, 0));

        Scene scene = new Scene(mainPanel, 680, 520);
        GUIStyle.apply(scene);
        stage.setTitle("Administrador");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private Button buildMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(350);
        button.setPrefHeight(52);
        button.setFont(Font.font("SansSerif", 15));
        return button;
    }

    public void setAdministratorInfo(Professor administrator) {
        this.professor = administrator;
        labelAdministratorName.setText(administrator.getName());
    }

    public Button getButtonRegisterProfessor() {
        return buttonRegisterProfessor;
    }

    public Button getButtonModifyProfessor() {
        return buttonModifyProfessor;
    }

    public Button getButtonProfessorView() {
        return buttonProfessorView;
    }

    public Button getButtonLogout() {
        return buttonLogout;
    }

    public Stage getStage() {
        return stage;
    }

    public Professor getProfessor() {
        return professor;
    }

    public static void main(String[] args) {
        launch(args);
    }
}