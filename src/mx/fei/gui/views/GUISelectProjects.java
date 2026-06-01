package mx.fei.gui.views;

import mx.fei.gui.utils.GUIUtils;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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

    private Button buttonSelect;
    private Button buttonCancel;
    private VBox vBoxProjectList;
    private Stage stage;
    private final List<CheckBox> checkBoxes = new ArrayList<>();
    private final List<Project> projects = new ArrayList<>();
    private Student student;
    private boolean isModify;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        Label title = new Label("Proyectos disponibles");
        title.setFont(Font.font("SansSerif", FontWeight.NORMAL, 14));

        vBoxProjectList = new VBox(6);
        vBoxProjectList.setPadding(new Insets(10));
        vBoxProjectList.setStyle("-fx-background-color: white;");

        isModify = false;

        ScrollPane scrollPane = new ScrollPane(vBoxProjectList);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white; -fx-border-color: #cccccc;");
        scrollPane.setPrefHeight(280);

        VBox listPanel = new VBox(10, title, scrollPane);
        listPanel.setPadding(new Insets(16));
        listPanel.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #bbbbbb;");

        buttonSelect = new Button("Seleccionar");
        buttonCancel = new Button("Cancelar");

        String buttonStyle = "-fx-background-color: #1e1e23; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;";
        buttonSelect.setStyle(buttonStyle);
        buttonCancel.setStyle(buttonStyle);
        buttonSelect.setPrefWidth(140);
        buttonCancel.setPrefWidth(140);

        ControllerSelectProjects controllerSelectProjects = new ControllerSelectProjects(this);
        buttonSelect.setOnAction(controllerSelectProjects::handleSelectCancelButtons);
        buttonCancel.setOnAction(controllerSelectProjects::handleSelectCancelButtons);

        HBox buttonRow = new HBox(40, buttonSelect, buttonCancel);
        buttonRow.setAlignment(Pos.CENTER);

        VBox mainPanel = new VBox(20, listPanel, buttonRow);
        mainPanel.setPadding(new Insets(24));
        mainPanel.setStyle("-fx-background-color: #d0d0d0;");

        Scene scene = new Scene(mainPanel, 680, 460);
        stage.setTitle("SeleccionarProyectos");
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
        checkBox.selectedProperty().addListener((observableValue, oldValue, newValue) -> enforceSelectionLimit(checkBox, newValue));
        checkBoxes.add(checkBox);
        HBox row = new HBox(6, label, checkBox);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private void enforceSelectionLimit(CheckBox checkBox, boolean isSelected) {
        long selected = checkBoxes.stream().filter(CheckBox::isSelected).count();
        if (isSelected && selected > 3) {
            checkBox.setSelected(false);
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Límite alcanzado");
            alert.setHeaderText(null);
            alert.setContentText("Solo puedes seleccionar 3 proyectos.");
            alert.showAndWait();
        }
    }

    public void showError(String message) {
        GUIUtils.showError(message);
    }

    public void showSuccess(String message) {
        GUIUtils.showSuccess(message);
    }

    public void loadProjects(List<Project> projectsToLoad) {
        vBoxProjectList.getChildren().clear();
        checkBoxes.clear();
        projects.clear();
        projects.addAll(projectsToLoad);
        for (Project project : projectsToLoad) {
            vBoxProjectList.getChildren().add(buildProjectRow(project));
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
        return (int) checkBoxes.stream().filter(CheckBox::isSelected).count();
    }

    public Button getButtonSelect() {
        return buttonSelect;
    }

    public Button getButtonCancel() {
        return buttonCancel;
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

    public boolean isModify() {
        return isModify;
    }

    public void setModify(boolean modify) {
        isModify = modify;
    }
}