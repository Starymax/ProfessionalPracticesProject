package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;
import mx.fei.gui.controllers.ControllerChooseStudent;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.gui.utils.StudentStatusFilter;
import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.dto.PracticeStatus;
import mx.fei.logic.dto.Student;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GUIChooseStudent extends Application {
    private ListView<String> listViewStudents;
    private TextField searchField;
    private ComboBox<String> comboBoxStatusFilter;
    private Button buttonSelect;
    private Button buttonBack;
    private List<Student> students;
    private List<Student> allStudents;
    private Set<Integer> concludedStudentIds = new HashSet<>();
    private Set<Integer> enrolledStudentIds = new HashSet<>();
    private boolean isToModifyStudent = false;
    private boolean isConsultByExperience = false;
    private EducationalExperience educationalExperience;;

    public GUIChooseStudent() {

    }

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
            applyFilters();
        });
        comboBoxStatusFilter = new ComboBox<>(FXCollections.observableArrayList(StudentStatusFilter.filterLabels()));
        comboBoxStatusFilter.setValue(StudentStatusFilter.ALL_LABEL);
        comboBoxStatusFilter.setPrefWidth(180);
        comboBoxStatusFilter.setOnAction(event -> applyFilters());
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
        Label labelFilter = new Label("Estado:");
        HBox filterBox = new HBox(10, labelFilter, comboBoxStatusFilter);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        formPanel.getChildren().addAll(labelTitle, searchField, filterBox, contentBox);
        StackPane mainPanel = new StackPane(formPanel);
        mainPanel.setPadding(new Insets(20));
        ControllerChooseStudent controllerChooseStudent = new ControllerChooseStudent(this);
        buttonSelect.setOnAction(event -> controllerChooseStudent.handleSelectStudent());
        buttonBack.setOnAction(event -> closeWindow());
        Scene scene = new Scene(mainPanel, 620, 390);
        GUIStyle.apply(scene);
        stage.setScene(scene);
        stage.show();
    }

    public void setStudents(List<Student> students, Set<Integer> concludedStudentIds, Set<Integer> enrolledStudentIds) {
        this.allStudents = students;
        this.concludedStudentIds = concludedStudentIds;
        this.enrolledStudentIds = enrolledStudentIds;
        applyFilters();
    }

    public Student getSelectedStudent() {
        int selectedIndex = listViewStudents.getSelectionModel().getSelectedIndex();
        return students.get(selectedIndex);
    }

    public boolean isToModifyStudent() {
        return isToModifyStudent;
    }

    public void setToModifyStudent(boolean toModifyStudent) {
        isToModifyStudent = toModifyStudent;
    }

    public boolean isConsultByExperience() {
        return isConsultByExperience;
    }

    public void setConsultByExperience(boolean consultByExperience) {
        isConsultByExperience = consultByExperience;
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

    public EducationalExperience getEducationalExperience() {
        return educationalExperience;
    }

    public void setEducationalExperience(EducationalExperience educationalExperience) {
        this.educationalExperience = educationalExperience;
    }

    private void showStudents(List<Student> studentsToShow) {
        this.students = studentsToShow;
        ObservableList<String> items = FXCollections.observableArrayList();
        for (Student student : studentsToShow) {
            items.add(buildStudentLabel(student));
        }
        listViewStudents.setItems(items);
    }

    private void applyFilters() {
        if (allStudents != null) {
            PracticeStatus status = PracticeStatus.fromLabel(comboBoxStatusFilter.getValue());
            List<Student> statusFiltered = StudentStatusFilter.filterByStatus(allStudents, status, concludedStudentIds, enrolledStudentIds);
            String search = GUIUtils.sanitizeSearch(searchField.getText());
            List<Student> filteredStudents = new ArrayList<>();
            for (Student student : statusFiltered) {
                if (GUIUtils.matchesSearch(buildStudentLabel(student), search)) {
                    filteredStudents.add(student);
                }
            }
            showStudents(filteredStudents);
        }
    }

    private String buildStudentLabel(Student student) {
        return student.getEnrollment() + " - " + student.getName() + " " + student.getLastName();
    }
}
