package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;
import mx.fei.gui.controllers.ControllerChooseStudent;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.logic.dto.Student;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class GUIChooseStudent extends Application {
    private ListView<String> listViewStudents;
    private TextField searchField;
    private Button buttonSelect;
    private Button buttonBack;
    private List<Student> students;
    private List<Student> allStudents;

    public GUIChooseStudent() {}

    @Override
    public void start(Stage stage) {
        stage.setTitle("Seleccionar alumno");
        stage.setResizable(false);
        VBox formPanel = new VBox(15);
        formPanel.setPadding(new Insets(25, 25, 25, 25));
        formPanel.setAlignment(Pos.TOP_LEFT);
        formPanel.getStyleClass().add("form-panel");
        Label labelTitle = new Label("Seleccione un alumno:");
        labelTitle.setFont(new Font("SansSerif", 14));
        searchField = new TextField();
        searchField.setPromptText("Buscar por matrícula o nombre...");
        searchField.setPrefWidth(390);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterStudents(newValue);
        });
        listViewStudents = new ListView<>();
        listViewStudents.setPrefWidth(390);
        listViewStudents.setPrefHeight(260);
        listViewStudents.setItems(FXCollections.observableArrayList());
        buttonSelect = new Button("Seleccionar");
        buttonBack = new Button("Regresar");
        buttonSelect.setPrefWidth(130);
        buttonBack.setPrefWidth(130);
        buttonSelect.setPrefHeight(35);
        buttonBack.setPrefHeight(35);
        VBox buttonsBox = new VBox(20, buttonSelect, buttonBack);
        buttonsBox.setAlignment(Pos.TOP_CENTER);
        buttonsBox.setPadding(new Insets(10, 0, 0, 0));
        HBox contentBox = new HBox(20, listViewStudents, buttonsBox);
        contentBox.setAlignment(Pos.TOP_LEFT);
        formPanel.getChildren().addAll(labelTitle, searchField, contentBox);
        StackPane mainPanel = new StackPane(formPanel);
        mainPanel.setPadding(new Insets(20));
        ControllerChooseStudent controllerChooseStudent = new ControllerChooseStudent(this);
        buttonSelect.setOnAction(controllerChooseStudent::handleSelectAndReturnButtons);
        buttonBack.setOnAction(controllerChooseStudent::handleSelectAndReturnButtons);
        Scene scene = new Scene(mainPanel, 620, 390);
        GUIStyle.apply(scene);
        stage.setScene(scene);
        stage.show();
    }

    public void setStudents(List<Student> students) {
        this.allStudents = students;
        showStudents(students);
    }

    private void showStudents(List<Student> studentsToShow) {
        this.students = studentsToShow;
        ObservableList<String> items = FXCollections.observableArrayList();
        for (Student student : studentsToShow) {
            items.add(buildStudentLabel(student));
        }
        listViewStudents.setItems(items);
    }

    private void filterStudents(String query) {
        String search = GUIUtils.sanitizeSearch(query);
        List<Student> filteredStudents = new ArrayList<>();
        for (Student student : allStudents) {
            if (GUIUtils.matchesSearch(buildStudentLabel(student), search)) {
                filteredStudents.add(student);
            }
        }
        showStudents(filteredStudents);
    }

    private String buildStudentLabel(Student student) {
        return student.getEnrollment() + " - " + student.getName() + " " + student.getLastName();
    }

    public Student getSelectedStudent() {
        int selectedIndex = listViewStudents.getSelectionModel().getSelectedIndex();
        return students.get(selectedIndex);
    }

    public void showError(String message) {
        GUIUtils.showError(message);
    }

    public void closeWindow() {
        ((Stage) buttonBack.getScene().getWindow()).close();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public ListView<String> getListViewStudents() {
        return listViewStudents;
    }

    public TextField getSearchField() {
        return searchField;
    }

    public Button getButtonSelect() {
        return buttonSelect;
    }

    public Button getButtonBack() {
        return buttonBack;
    }

    public List<Student> getStudents() {
        return students;
    }
}
