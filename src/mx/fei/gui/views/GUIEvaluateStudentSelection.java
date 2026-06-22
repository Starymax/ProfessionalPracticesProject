package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;
import mx.fei.gui.controllers.ControllerEvaluateStudentSelection;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.dto.Professor;
import mx.fei.logic.dto.Student;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

public class GUIEvaluateStudentSelection extends Application {

    private Professor professor;
    private ComboBox<EducationalExperience> comboBoxExperience;
    private ListView<Student> listViewStudent;
    private Button buttonEvaluate;
    private Button buttonCancel;
    private Stage stage;

    public GUIEvaluateStudentSelection(Professor professor) {
        this.professor = professor;
    }

    public GUIEvaluateStudentSelection() {
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        Label title = new Label("Seleccione una experiencia educativa y un alumno para evaluar:");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 15));

        comboBoxExperience = new ComboBox<>();
        comboBoxExperience.setPromptText("Seleccione una experiencia educativa");
        comboBoxExperience.setPrefWidth(620);
        comboBoxExperience.setButtonCell(buildExperienceCell());
        comboBoxExperience.setCellFactory(listView -> buildExperienceCell());

        listViewStudent = new ListView<>();
        listViewStudent.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Student student, boolean empty) {
                super.updateItem(student, empty);
                if (empty || student == null) {
                    setText(null);
                } else {
                    setText(student.getEnrollment() + " - " + student.getName() + " " + student.getLastName());
                }
            }
        });
        listViewStudent.setPrefHeight(360);
        listViewStudent.getStyleClass().add("list-view-large");

        buttonEvaluate = new Button("Evaluar");
        buttonCancel = new Button("Cancelar");
        buttonEvaluate.setPrefWidth(160);
        buttonCancel.setPrefWidth(160);

        ControllerEvaluateStudentSelection controllerEvaluateStudentSelection = new ControllerEvaluateStudentSelection(this);
        comboBoxExperience.setOnAction(controllerEvaluateStudentSelection::handleExperienceSelection);
        buttonEvaluate.setOnAction(controllerEvaluateStudentSelection::handleEvaluateCancelButtons);
        buttonCancel.setOnAction(controllerEvaluateStudentSelection::handleEvaluateCancelButtons);

        VBox topBox = new VBox(15, title, comboBoxExperience);
        VBox buttonPanel = new VBox(12, buttonEvaluate, buttonCancel);
        buttonPanel.setAlignment(Pos.BOTTOM_RIGHT);

        BorderPane mainPanel = new BorderPane();
        mainPanel.setPadding(new Insets(32, 40, 32, 40));
        mainPanel.setTop(topBox);
        mainPanel.setCenter(listViewStudent);
        mainPanel.setBottom(buttonPanel);
        BorderPane.setMargin(topBox, new Insets(0, 0, 20, 0));
        BorderPane.setMargin(buttonPanel, new Insets(20, 0, 0, 0));

        Scene scene = new Scene(mainPanel, 720, 600);
        GUIStyle.apply(scene);
        stage.setTitle("Evaluar alumno");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private ListCell<EducationalExperience> buildExperienceCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(EducationalExperience experience, boolean empty) {
                super.updateItem(experience, empty);
                if (empty || experience == null) {
                    setText(null);
                } else {
                    String period = (experience.getPeriod() == null || experience.getPeriod().isBlank()) ? "Sin periodo" : experience.getPeriod();
                    setText(experience.getName() + " (" + experience.getNrc() + ") - " + period);
                }
            }
        };
    }

    public void loadExperiences(List<EducationalExperience> experiences) {
        comboBoxExperience.getItems().clear();
        comboBoxExperience.getItems().addAll(experiences);
    }

    public void loadStudents(List<Student> students) {
        listViewStudent.getItems().clear();
        listViewStudent.getItems().addAll(students);
    }

    public EducationalExperience getSelectedExperience() {
        return comboBoxExperience.getValue();
    }

    public Student getSelectedStudent() {
        return listViewStudent.getSelectionModel().getSelectedItem();
    }

    public Professor getProfessor() {
        return professor;
    }

    public void showError(String message) {
        GUIUtils.showError(message);
    }

    public Button getButtonEvaluate() {
        return buttonEvaluate;
    }

    public Button getButtonCancel() {
        return buttonCancel;
    }

    public ComboBox<EducationalExperience> getComboBoxExperience() {
        return comboBoxExperience;
    }

    public Stage getStage() {
        return stage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
