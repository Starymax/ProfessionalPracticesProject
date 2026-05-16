package mx.fei.gui.views;

import mx.fei.gui.utils.GUIUtils;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import mx.fei.gui.controllers.ControllerSelectStudentForAssignProject;
import mx.fei.logic.dto.Student;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

public class GUISelectStudentForAssignProject extends Application {

    private Button buttonSelect;
    private Button buttonCancel;
    private ListView<Student> studentListView;
    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        Label title = new Label("Seleccione un alumno sin Proyecto asignado:");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 15));
        studentListView = new ListView<>();
        studentListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Student student, boolean empty) {
                super.updateItem(student, empty);
                if (empty || student == null) {
                    setText(null);
                } else {
                    setText(student.getName() + " " + student.getLastName());
                }
            }
        });
        studentListView.setPrefHeight(400);
        studentListView.setStyle("-fx-font-size: 14px;");

        buttonSelect = new Button("Seleccionar");
        buttonCancel = new Button("Cancelar");

        String buttonStyle = "-fx-background-color: #1e1e23; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-background-radius: 10;";
        buttonSelect.setStyle(buttonStyle);
        buttonCancel.setStyle(buttonStyle);
        buttonSelect.setPrefWidth(160);
        buttonCancel.setPrefWidth(160);

        ControllerSelectStudentForAssignProject controllerSelectStudentForAssignProject = new ControllerSelectStudentForAssignProject(this);
        buttonSelect.setOnAction(event -> controllerSelectStudentForAssignProject.handleButtonAction(event));
        buttonCancel.setOnAction(event -> controllerSelectStudentForAssignProject.handleButtonAction(event));

        VBox buttonPanel = new VBox(12, buttonSelect, buttonCancel);
        buttonPanel.setAlignment(Pos.BOTTOM_RIGHT);

        BorderPane mainPanel = new BorderPane();
        mainPanel.setPadding(new Insets(32, 40, 32, 40));
        mainPanel.setTop(title);
        mainPanel.setCenter(studentListView);
        mainPanel.setBottom(buttonPanel);
        BorderPane.setMargin(title, new Insets(0, 0, 20, 0));
        BorderPane.setMargin(buttonPanel, new Insets(20, 0, 0, 0));

        Scene scene = new Scene(mainPanel, 720, 560);
        stage.setTitle("Asignar proyecto");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public void loadStudents(List<Student> students) {
        studentListView.getItems().clear();
        studentListView.getItems().addAll(students);
    }

    public Student getSelectedStudent() {
        return studentListView.getSelectionModel().getSelectedItem();
    }

    public void showError(String message) {
        GUIUtils.showError(message);
    }

    public Button getButtonSelect() {
        return buttonSelect;
    }

    public Button getButtonCancelar() {
        return buttonCancel;
    }

    public Stage getStage() {
        return stage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}