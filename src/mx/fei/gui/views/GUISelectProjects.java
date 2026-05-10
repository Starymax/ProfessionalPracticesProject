package mx.fei.gui.views;

import mx.fei.gui.utils.GUIUtils;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Alert;
import javafx.scene.layout.Priority;
import mx.fei.gui.controllers.ControllerSelectProjects;
import mx.fei.logic.dto.Project;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import mx.fei.logic.dto.Student;

import java.util.ArrayList;
import java.util.List;

public class GUISelectProjects extends Application {

    private Button selectButton;
    private Button cancelButton;
    private VBox projectList;
    private Stage stage;
    private final List<CheckBox> checkBoxes = new ArrayList<>();
    private final List<Project> projects = new ArrayList<>();
    private Student student;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        Label title = new Label("Proyectos disponibles");
        title.setFont(Font.font("SansSerif", FontWeight.NORMAL, 14));

        projectList = new VBox(6);
        projectList.setPadding(new Insets(10));
        projectList.setStyle("-fx-background-color: white;");

        ScrollPane scrollPane = new ScrollPane(projectList);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white; -fx-border-color: #cccccc;");
        scrollPane.setPrefHeight(280);

        VBox listPanel = new VBox(10, title, scrollPane);
        listPanel.setPadding(new Insets(16));
        listPanel.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #bbbbbb;");

        selectButton = new Button("Seleccionar");
        cancelButton = new Button("Cancelar");

        String buttonStyle = "-fx-background-color: #1e1e23; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;";
        selectButton.setStyle(buttonStyle);
        cancelButton.setStyle(buttonStyle);
        selectButton.setPrefWidth(140);
        cancelButton.setPrefWidth(140);

        ControllerSelectProjects controllerSelectProjects = new ControllerSelectProjects(this);
        selectButton.setOnAction(event -> controllerSelectProjects.handleButtonAction(event));
        cancelButton.setOnAction(event -> controllerSelectProjects.handleButtonAction(event));

        HBox buttonRow = new HBox(40, selectButton, cancelButton);
        buttonRow.setAlignment(Pos.CENTER);

        VBox mainPanel = new VBox(20, listPanel, buttonRow);
        mainPanel.setPadding(new Insets(24));
        mainPanel.setStyle("-fx-background-color: #d0d0d0;");

        Scene scene = new Scene(mainPanel, 680, 460);
        stage.setTitle("GUISeleccionarProyectos");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private HBox buildProjectRow(Project project) {
        Label label = new Label(project.getNameProject());
        label.setFont(Font.font("SansSerif", 13));
        label.setMaxWidth(Double.MAX_VALUE);
        label.setPadding(new Insets(8, 12, 8, 12));
        label.setStyle("-fx-background-color: #e8e8e8;");
        HBox.setHgrow(label, Priority.ALWAYS);

        CheckBox checkBox = new CheckBox();
        checkBox.setUserData(project);
        checkBox.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            long selected = checkBoxes.stream().filter(CheckBox::isSelected).count();
            if (newValue && selected > 3) {
                checkBox.setSelected(false);
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Límite alcanzado");
                alert.setHeaderText(null);
                alert.setContentText("Solo puedes seleccionar 3 proyectos.");
                alert.showAndWait();
            }
        });
        checkBoxes.add(checkBox);
        HBox row = new HBox(6, label, checkBox);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    public void showError(String message) {
        GUIUtils.showError(message);
    }

    public void showSuccess(String message) {
        GUIUtils.showSuccess(message);
    }

    public void loadProjects(List<Project> projectsToLoad) {
        projectList.getChildren().clear();
        checkBoxes.clear();
        projects.clear();
        projects.addAll(projectsToLoad);
        for (Project project : projectsToLoad) {
            projectList.getChildren().add(buildProjectRow(project));
        }
    }

    public List<Project> getSelectedProjects() {
        List<Project> selected = new ArrayList<>();
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                selected.add((Project) checkBox.getUserData());
            }
        }
        return selected;
    }

    public int getSelectedCount() {
        int count = (int) checkBoxes.stream().filter(CheckBox::isSelected).count();
        return count;
    }

    public Button getSelectButton() {
        return selectButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public Stage getStage() {
        return stage;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public static void main(String[] args) {
        launch(args);
    }
}