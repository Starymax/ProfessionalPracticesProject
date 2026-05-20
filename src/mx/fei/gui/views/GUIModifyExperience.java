package mx.fei.gui.views;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import mx.fei.gui.controllers.ControllerModifyExperience;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.dto.Professor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GUIModifyExperience extends Application {
    private EducationalExperience experience;
    private List<Professor> professors = new ArrayList<>();
    private TextField textFieldName;
    private TextField textFieldCareer;
    private TextField textFieldCurrentProfessor;
    private TextField textFieldCurrentPeriod;
    private ComboBox<String> comboBoxSemester;
    private ComboBox<Integer> comboBoxYear;
    private ComboBox<String> comboBoxProfessors;
    private Button buttonUpdate;
    private Button buttonBack;

    public GUIModifyExperience(EducationalExperience experience) {
        this.experience = experience;
    }

    public GUIModifyExperience() {}

    @Override
    public void start(Stage stage) {
        stage.setTitle("Modificar experiencia");
        stage.setResizable(false);
        VBox formPanel = new VBox(15);
        formPanel.setPadding(new Insets(25, 40, 25, 40));
        formPanel.setAlignment(Pos.TOP_CENTER);
        formPanel.setBackground(new Background(new BackgroundFill(Color.rgb(220, 220, 220), CornerRadii.EMPTY, Insets.EMPTY)));
        formPanel.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        Label labelTitle = new Label("Modificar experiencia educativa");
        labelTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 18));
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(12);
        formGrid.setPadding(new Insets(10, 0, 10, 0));
        formGrid.setAlignment(Pos.TOP_LEFT);
        formGrid.add(new Label("Nombre:"), 0, 0);
        textFieldName = new TextField();
        textFieldName.setPrefWidth(290);
        formGrid.add(textFieldName, 1, 0);
        formGrid.add(new Label("Carrera:"), 0, 1);
        textFieldCareer = new TextField();
        formGrid.add(textFieldCareer, 1, 1);
        formGrid.add(new Label("Periodo actual:"), 0, 2);
        textFieldCurrentPeriod = new TextField();
        textFieldCurrentPeriod.setDisable(true);
        textFieldCurrentPeriod.setStyle("-fx-opacity: 1;");
        formGrid.add(textFieldCurrentPeriod, 1, 2);
        formGrid.add(new Label("Nuevo semestre:"), 0, 3);
        comboBoxSemester = new ComboBox<>(FXCollections.observableArrayList("Febrero - Julio", "Agosto - Diciembre"));
        comboBoxSemester.setPromptText("Seleccionar semestre");
        comboBoxSemester.setPrefWidth(290);
        formGrid.add(comboBoxSemester, 1, 3);
        formGrid.add(new Label("Nuevo año:"), 0, 4);
        int currentYear = LocalDate.now().getYear();
        comboBoxYear = new ComboBox<>(FXCollections.observableArrayList(currentYear, currentYear + 1));
        comboBoxYear.setPromptText("Seleccionar año");
        comboBoxYear.setPrefWidth(290);
        formGrid.add(comboBoxYear, 1, 4);
        formGrid.add(new Label("Profesor actual:"), 0, 5);
        textFieldCurrentProfessor = new TextField();
        textFieldCurrentProfessor.setDisable(true);
        textFieldCurrentProfessor.setStyle("-fx-opacity: 1;");
        formGrid.add(textFieldCurrentProfessor, 1, 5);
        if (experience != null) {
            textFieldName.setText(experience.getName());
            textFieldCareer.setText(experience.getEducationalProgram());
            if (experience.getPeriod() != null) {
                textFieldCurrentPeriod.setText(experience.getPeriod().getName());
                comboBoxYear.setValue(experience.getPeriod().getYear());
                comboBoxSemester.setValue(experience.getPeriod().getNumber() == 1 ? "Febrero - Julio" : "Agosto - Enero");
            }
            if (experience.getProfessor() != null) {
                textFieldCurrentProfessor.setText(experience.getProfessor().getName() + " " + experience.getProfessor().getLastName());
            }
        }
        comboBoxProfessors = new ComboBox<>();
        comboBoxProfessors.setPrefWidth(180);
        comboBoxProfessors.setPromptText("Cambiar profesor");
        comboBoxProfessors.setItems(FXCollections.observableArrayList());
        buttonUpdate = new Button("Actualizar");
        buttonBack = new Button("Regresar");
        buttonUpdate.setPrefWidth(120);
        buttonBack.setPrefWidth(120);
        buttonUpdate.setPrefHeight(35);
        buttonBack.setPrefHeight(35);
        buttonUpdate.setStyle("-fx-background-color: #323232; -fx-text-fill: white; -fx-background-radius: 8;");
        buttonBack.setStyle("-fx-background-color: #323232; -fx-text-fill: white; -fx-background-radius: 8;");
        HBox bottomBox = new HBox(30, comboBoxProfessors, buttonUpdate, buttonBack);
        bottomBox.setAlignment(Pos.CENTER_LEFT);
        bottomBox.setPadding(new Insets(10, 0, 0, 0));
        formPanel.getChildren().addAll(labelTitle, formGrid, bottomBox);
        StackPane mainPanel = new StackPane(formPanel);
        mainPanel.setPadding(new Insets(20));
        mainPanel.setBackground(new Background(new BackgroundFill(Color.rgb(200, 200, 200), CornerRadii.EMPTY, Insets.EMPTY)));
        ControllerModifyExperience controller = new ControllerModifyExperience(this);
        buttonUpdate.setOnAction(controller::handleUpdateReturnButtons);
        buttonBack.setOnAction(controller::handleUpdateReturnButtons);
        Scene scene = new Scene(mainPanel, 580, 460);
        stage.setScene(scene);
        stage.show();
    }

    public int getSelectedSemesterNumber() {
        int semesterNumber = -1;
        String semester = comboBoxSemester.getValue();
        if ("Febrero - Julio".equals(semester)) {
            semesterNumber = 1;
        } else if ("Agosto - Enero".equals(semester)) {
            semesterNumber = 2;
        }
        return semesterNumber;
    }

    public void setProfessors(List<Professor> professors) {
        this.professors = professors;
        ObservableList<String> items = FXCollections.observableArrayList();
        for (Professor professor : professors) {
            items.add(professor.getName() + " " + professor.getLastName());
        }
        comboBoxProfessors.setItems(items);
    }

    public Professor getSelectedProfessor() {
        Professor selectedProfessor = null;
        int selectedIndex = comboBoxProfessors.getSelectionModel().getSelectedIndex();
        if (professors != null && selectedIndex >= 0 && selectedIndex < professors.size()) {
            selectedProfessor = professors.get(selectedIndex);
        }
        return selectedProfessor;
    }

    public boolean validateFields() {
        boolean valid = false;
        List<String> errors = new ArrayList<>();
        GUIUtils.validateNames(textFieldName.getText().trim(), "Nombre", errors);
        GUIUtils.validateShortText(textFieldCareer.getText().trim(), "Carrera", errors);
        if (comboBoxSemester.getValue() == null) {
            errors.add("Debe seleccionar un semestre.");
        }
        if (comboBoxYear.getValue() == null) {
            errors.add("Debe seleccionar un año.");
        }
        if (errors.isEmpty()) {
            valid = true;
        } else {
            GUIUtils.showErrors(errors);
        }
        return valid;
    }

    public void showError(String message) { GUIUtils.showError(message); }

    public void showSuccess(String message) { GUIUtils.showSuccess(message); }

    public void closeWindow() {
        ((Stage) buttonBack.getScene().getWindow()).close();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public EducationalExperience getExperience() {
        return experience;
    }

    public TextField getTextFieldName() {
        return textFieldName;
    }

    public TextField getTextFieldCareer() {
        return textFieldCareer;
    }

    public ComboBox<String> getComboBoxSemester() {
        return comboBoxSemester;
    }

    public ComboBox<Integer> getComboBoxYear() {
        return comboBoxYear;
    }

    public ComboBox<String> getComboBoxProfessors() {
        return comboBoxProfessors;
    }

    public Button getButtonUpdate() {
        return buttonUpdate;
    }

    public Button getButtonBack() {
        return buttonBack;
    }
}