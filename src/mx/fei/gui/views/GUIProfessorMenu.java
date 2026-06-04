package mx.fei.gui.views;

import mx.fei.gui.controllers.ControllerProfessorMenu;
import mx.fei.logic.dto.Professor;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.Region;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class GUIProfessorMenu extends Application {

    private Professor professor;
    private Label labelProfessorName;
    private Label labelProfessorShift;
    private Button buttonManageActivities;
    private Button buttonManageReports;
    private Button buttonEvaluate;
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
        formPanel.setBackground(new Background(new BackgroundFill(Color.rgb(220, 220, 220), CornerRadii.EMPTY, Insets.EMPTY)));
        formPanel.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        Region spacer = new Region();
        spacer.setPrefHeight(10);
        buttonManageActivities = createMenuButton("Gestionar actividades");
        buttonManageReports = createMenuButton("Gestionar reportes");
        buttonEvaluate = createMenuButton("Evaluar");
        buttonLogout = createMenuButton("Cerrar Sesión");
        VBox buttonsBox = new VBox(12, buttonManageActivities, buttonManageReports, buttonEvaluate, buttonLogout);
        buttonsBox.setAlignment(Pos.CENTER);
        formPanel.getChildren().addAll(labelProfessorName, labelProfessorShift, spacer, buttonsBox);
        StackPane mainPanel = new StackPane(formPanel);
        mainPanel.setPadding(new Insets(20));
        mainPanel.setBackground(new Background(new BackgroundFill(Color.rgb(200, 200, 200), CornerRadii.EMPTY, Insets.EMPTY)));
        ControllerProfessorMenu controllerProfessorMenu = new ControllerProfessorMenu(this);
        buttonManageActivities.setOnAction(controllerProfessorMenu::handleButtonsMenu);
        buttonManageReports.setOnAction(controllerProfessorMenu::handleButtonsMenu);
        buttonEvaluate.setOnAction(controllerProfessorMenu::handleButtonsMenu);
        buttonLogout.setOnAction(controllerProfessorMenu::handleButtonsMenu);
        Scene scene = new Scene(mainPanel, 500, 420);
        stage.setScene(scene);
        stage.show();
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(250);
        button.setStyle("-fx-background-color: #323232; -fx-text-fill: white; " + "-fx-background-radius: 20; -fx-font-size: 13px;");
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

    public Button getButtonManageActivities() {
        return buttonManageActivities;
    }

    public Button getButtonManageReports() {
        return buttonManageReports;
    }

    public Button getButtonEvaluate() {
        return buttonEvaluate;
    }

    public Button getButtonLogout() {
        return buttonLogout;
    }

    public Stage getStage() {
        return stage;
    }
}