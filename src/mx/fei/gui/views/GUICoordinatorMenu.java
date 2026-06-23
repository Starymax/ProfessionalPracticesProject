package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;
import mx.fei.gui.controllers.ControllerCoordinatorMenu;
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

public class GUICoordinatorMenu extends Application {

    private Professor coordinator;
    private Label labelCoordinatorName;
    private Label labelCoordinatorShift;
    private Button buttonManageStudents;
    private Button buttonManageProjects;
    private Button buttonManageOrganizations;
    private Button buttonManageEducationalExperience;
    private Button buttonValidateDocuments;
    private Button buttonConsultProfessor;
    private Button buttonLogOut;
    private Stage stage;

    public GUICoordinatorMenu(Professor coordinator) {
        this.coordinator = coordinator;
    }
    public GUICoordinatorMenu() {

    }

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
        formPanel.getStyleClass().add("form-panel");

        Region spacer = new Region();
        spacer.setPrefHeight(10);

        buttonManageStudents = createMenuButton("Gestionar alumnos");
        buttonManageProjects = createMenuButton("Gestionar proyectos");
        buttonManageOrganizations = createMenuButton("Gestionar organizaciones");
        buttonManageEducationalExperience = createMenuButton("Gestionar experiencia educativa");
        buttonValidateDocuments = createMenuButton("Validar documentos");
        buttonConsultProfessor = createMenuButton("Consultar profesor");
        buttonLogOut = createMenuButton("Cerrar Sesión");
        VBox buttonsBox = new VBox(12, buttonManageStudents, buttonManageProjects, buttonManageOrganizations, buttonManageEducationalExperience, buttonValidateDocuments, buttonConsultProfessor, buttonLogOut);
        buttonsBox.setAlignment(Pos.CENTER);
        formPanel.getChildren().addAll(labelCoordinatorName, labelCoordinatorShift, spacer, buttonsBox);
        StackPane mainPanel = new StackPane(formPanel);
        mainPanel.setPadding(new Insets(20));
        ControllerCoordinatorMenu controllerCoordinatorMenu = new ControllerCoordinatorMenu(this);
        buttonManageStudents.setOnAction(controllerCoordinatorMenu::handleButtonsMenu);
        buttonManageProjects.setOnAction(controllerCoordinatorMenu::handleButtonsMenu);
        buttonManageOrganizations.setOnAction(controllerCoordinatorMenu::handleButtonsMenu);
        buttonManageEducationalExperience.setOnAction(controllerCoordinatorMenu::handleButtonsMenu);
        buttonValidateDocuments.setOnAction(controllerCoordinatorMenu::handleButtonsMenu);
        buttonConsultProfessor.setOnAction(controllerCoordinatorMenu::handleButtonsMenu);
        buttonLogOut.setOnAction(controllerCoordinatorMenu::handleButtonsMenu);
        Scene scene = new Scene(mainPanel, 500, 520);
        GUIStyle.apply(scene);
        stage.setScene(scene);
        stage.show();
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

    public Button getButtonValidateDocuments() {
        return buttonValidateDocuments;
    }

    public Button getButtonConsultProfessor() {
        return buttonConsultProfessor;
    }

    public Button getButtonLogOut() {
        return buttonLogOut;
    }

    public Stage getStage() {
        return stage;
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(250);
        return button;
    }
}