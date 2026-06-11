package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.gui.controllers.ControllerSelectStudentForAssignProject;
import mx.fei.logic.dto.Student;

import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
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
    private ListView<Student> listViewStudent;
    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        Label title = new Label("Seleccione un alumno sin Proyecto asignado:");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 15));
        listViewStudent = new ListView<>();
        listViewStudent.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Student student, boolean empty) {
                super.updateItem(student, empty);
                if (empty || student == null) {
                    setText(null);
                } else {
                        String grade = student.getGrade() != 0 ? " (" + student.getGrade() + ")" : "";
                        setText(student.getName() + " " + student.getLastName() + grade);
                }
            }
        });
        listViewStudent.setPrefHeight(400);
        listViewStudent.getStyleClass().add("list-view-large");

        buttonSelect = new Button("Seleccionar");
        buttonCancel = new Button("Cancelar");

        buttonSelect.setPrefWidth(160);
        buttonCancel.setPrefWidth(160);

        ControllerSelectStudentForAssignProject controllerSelectStudentForAssignProject = new ControllerSelectStudentForAssignProject(this);
        buttonSelect.setOnAction(controllerSelectStudentForAssignProject::handleSelectCancelButtons);
        buttonCancel.setOnAction(controllerSelectStudentForAssignProject::handleSelectCancelButtons);

        VBox buttonPanel = new VBox(12, buttonSelect, buttonCancel);
        buttonPanel.setAlignment(Pos.BOTTOM_RIGHT);

        BorderPane mainPanel = new BorderPane();
        mainPanel.setPadding(new Insets(32, 40, 32, 40));
        mainPanel.setTop(title);
        mainPanel.setCenter(listViewStudent);
        mainPanel.setBottom(buttonPanel);
        BorderPane.setMargin(title, new Insets(0, 0, 20, 0));
        BorderPane.setMargin(buttonPanel, new Insets(20, 0, 0, 0));

        Scene scene = new Scene(mainPanel, 720, 560);
        GUIStyle.apply(scene);
        stage.setTitle("Asignar proyecto");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public void loadStudents(List<Student> students) {
        listViewStudent.getItems().clear();
        listViewStudent.getItems().addAll(students);
    }

    public Student getSelectedStudent() {
        return listViewStudent.getSelectionModel().getSelectedItem();
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
