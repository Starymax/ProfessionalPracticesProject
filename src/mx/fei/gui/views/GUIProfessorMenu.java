package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;
import mx.fei.gui.controllers.ControllerProfessorMenu;
import mx.fei.logic.dto.Professor;

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
import javafx.stage.Stage;

public class GUIProfessorMenu extends Application {

    private Professor professor;
    private Label labelProfessorName;
    private Label labelProfessorShift;
    private Button buttonEvaluateReports;
    private Button buttonGoBack;
    private Button buttonLogout;
    private Stage stage;

    public GUIProfessorMenu(Professor professor) {
        this.professor = professor;
    }

    public GUIProfessorMenu() {}

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("Profesor");
        stage.setResizable(false);

        labelProfessorName = new Label("Bienvenido profesor: Nombre del profesor");
        labelProfessorName.setFont(new Font("SansSerif", 13));
        labelProfessorShift = new Label("Turno: Turno del profesor");
        labelProfessorShift.setFont(new Font("SansSerif", 13));

        VBox formPanel = new VBox(15);
        formPanel.setPadding(new Insets(30, 40, 30, 40));
        formPanel.setAlignment(Pos.TOP_LEFT);
        formPanel.getStyleClass().add("form-panel");

        Region spacer = new Region();
        spacer.setPrefHeight(10);
        buttonEvaluateReports = createMenuButton("Evaluar");
        buttonGoBack = createMenuButton("Regresar");
        buttonLogout = createMenuButton("Cerrar Sesión");
        VBox buttonsBox = new VBox(12, buttonEvaluateReports, buttonGoBack, buttonLogout);
        buttonsBox.setAlignment(Pos.CENTER);
        formPanel.getChildren().addAll(labelProfessorName, labelProfessorShift, spacer, buttonsBox);
        StackPane mainPanel = new StackPane(formPanel);
        mainPanel.setPadding(new Insets(20));
        ControllerProfessorMenu controllerProfessorMenu = new ControllerProfessorMenu(this);
        buttonEvaluateReports.setOnAction(controllerProfessorMenu::handleButtonsMenu);
        buttonGoBack.setOnAction(controllerProfessorMenu::handleButtonsMenu);
        buttonLogout.setOnAction(controllerProfessorMenu::handleButtonsMenu);
        Scene scene = new Scene(mainPanel, 500, 420);
        GUIStyle.apply(scene);
        stage.setScene(scene);
        stage.show();
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(250);
        return button;
    }

    public void setProfessorInfo(Professor professor) {
        this.professor = professor;
        if (labelProfessorName != null) {
            labelProfessorName.setText("Bienvenido profesor: " + professor.getName());
        }
        if (labelProfessorShift != null) {
            labelProfessorShift.setText("Turno: " + professor.getShift());
        }
    }

    public void closeWindow() {
        stage.close();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public Professor getProfessor() {
        return professor;
    }

    public Button getButtonEvaluateReports() {
        return buttonEvaluateReports;
    }

    public Button getButtonLogout() {
        return buttonLogout;
    }

    public Stage getStage() {
        return stage;
    }
}