package mx.fei.gui.views;

import mx.fei.gui.controllers.ControllerCoordinatorMenu;
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

public class GUICoordinatorMenu extends Application {

    private Professor coordinator;
    private Label labelCoordinatorName;
    private Label labelCoordinatorShift;
    private Button buttonManageStudents;
    private Button buttonManageProjects;
    private Button buttonManageOrganizations;
    private Button buttonManageEducationalExperience;
    private Button buttonConsultProfessor;
    private Button buttonBack;
    private Stage stage;

    public GUICoordinatorMenu(Professor coordinator) {
        this.coordinator = coordinator;
    }
    public GUICoordinatorMenu() {}

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("Coordinador");
        stage.setResizable(false);
        labelCoordinatorName = new Label("Bienvenido coordinador: nombre del coordinador");
        labelCoordinatorName.setFont(new Font("SansSerif", 13));
        labelCoordinatorShift = new Label("Turno: turno del coordinador");
        labelCoordinatorShift.setFont(new Font("SansSerif", 13));

        VBox formPanel = new VBox(15);
        formPanel.setPadding(new Insets(30, 40, 30, 40));
        formPanel.setAlignment(Pos.TOP_LEFT);
        formPanel.setBackground(new Background(new BackgroundFill(Color.rgb(220, 220, 220), CornerRadii.EMPTY, Insets.EMPTY)));
        formPanel.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        Region spacer = new Region();
        spacer.setPrefHeight(10);

        buttonManageStudents = createMenuButton("Gestionar alumnos");
        buttonManageProjects = createMenuButton("Gestionar proyectos");
        buttonManageOrganizations = createMenuButton("Gestionar organizaciones");
        buttonManageEducationalExperience = createMenuButton("Gestionar experiencia educativa");
        buttonConsultProfessor = createMenuButton("Consultar profesor");
        buttonBack = createMenuButton("Regresar");
        VBox buttonsBox = new VBox(12, buttonManageStudents, buttonManageProjects, buttonManageOrganizations, buttonManageEducationalExperience, buttonConsultProfessor, buttonBack);
        buttonsBox.setAlignment(Pos.CENTER);
        formPanel.getChildren().addAll(labelCoordinatorName, labelCoordinatorShift, spacer, buttonsBox);
        StackPane mainPanel = new StackPane(formPanel);
        mainPanel.setPadding(new Insets(20));
        mainPanel.setBackground(new Background(new BackgroundFill(Color.rgb(200, 200, 200), CornerRadii.EMPTY, Insets.EMPTY)));
        ControllerCoordinatorMenu controllerCoordinatorMenu = new ControllerCoordinatorMenu(this);
        buttonManageStudents.setOnAction(controllerCoordinatorMenu::handleButtonsMenu);
        buttonManageProjects.setOnAction(controllerCoordinatorMenu::handleButtonsMenu);
        buttonManageOrganizations.setOnAction(controllerCoordinatorMenu::handleButtonsMenu);
        buttonManageEducationalExperience.setOnAction(controllerCoordinatorMenu::handleButtonsMenu);
        buttonConsultProfessor.setOnAction(controllerCoordinatorMenu::handleButtonsMenu);
        buttonBack.setOnAction(controllerCoordinatorMenu::handleButtonsMenu);
        Scene scene = new Scene(mainPanel, 500, 470);
        stage.setScene(scene);
        stage.show();
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(250);
        button.setStyle("-fx-background-color: #323232; -fx-text-fill: white; " + "-fx-background-radius: 20; -fx-font-size: 13px;");
        return button;
    }

    public void setCoordinatorInfo(Professor professor) {
        this.coordinator = professor;
        if (labelCoordinatorName != null) {
            labelCoordinatorName.setText("Bienvenido coordinador: " + professor.getName());
        }
        if (labelCoordinatorShift != null) {
            labelCoordinatorShift.setText("Turno: " + professor.getShift());
        }
    }

    public void closeWindow() {
        stage.close();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public Professor getCoordinator() {
        return coordinator;
    }

    public Button getButtonManageStudents() {
        return buttonManageStudents;
    }

    public Button getButtonManageProjects() {
        return buttonManageProjects;
    }

    public Button getButtonManageOrganizations() {
        return buttonManageOrganizations;
    }

    public Button getButtonManageEducationalExperience() {
        return buttonManageEducationalExperience;
    }

    public Button getButtonConsultProfessor() {
        return buttonConsultProfessor;
    }

    public Button getButtonBack() {
        return buttonBack;
    }

    public Stage getStage() {
        return stage;
    }
}