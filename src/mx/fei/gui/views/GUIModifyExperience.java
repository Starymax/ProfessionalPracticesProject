package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;
import mx.fei.gui.controllers.ControllerModifyExperience;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.dto.Professor;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GUIModifyExperience extends Application {
    private EducationalExperience experience;
    private List<Professor> professors = new ArrayList<>();
    private TextField textFieldName;
    private TextField textFieldCareer;
    private TextField textFieldCurrentProfessor;
    private ComboBox<String> comboBoxSemester;
    private ComboBox<Integer> comboBoxYear;
    private ComboBox<String> comboBoxProfessors;
    private Button buttonUpdate;
    private Button buttonBack;
    private ToggleButton toggleActiveState;

    public GUIModifyExperience(EducationalExperience experience) {
        this.experience = experience;
    }

    public GUIModifyExperience() {

    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Modificar experiencia");
        stage.setResizable(false);
        VBox formPanel = new VBox(15);
        formPanel.setPadding(new Insets(25, 40, 25, 40));
        formPanel.setAlignment(Pos.TOP_CENTER);
        formPanel.getStyleClass().add("form-panel");
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
        formGrid.add(new Label("Semestre:"), 0, 2);
        comboBoxSemester = new ComboBox<>(FXCollections.observableArrayList("Febrero - Julio", "Agosto - Enero"));
        comboBoxSemester.setPrefWidth(220);
        comboBoxSemester.setPromptText("Seleccione semestre");
        formGrid.add(comboBoxSemester, 1, 2);
        formGrid.add(new Label("Año:"), 0, 3);
        int currentYear = LocalDate.now().getYear();
        comboBoxYear = new ComboBox<>(FXCollections.observableArrayList(currentYear, currentYear + 1));
        comboBoxYear.setPrefWidth(220);
        comboBoxYear.setPromptText("Seleccione año");
        formGrid.add(comboBoxYear, 1, 3);
        formGrid.add(new Label("Profesor actual:"), 0, 4);
        textFieldCurrentProfessor = new TextField();
        textFieldCurrentProfessor.setDisable(true);
        textFieldCurrentProfessor.setStyle("-fx-opacity: 1;");
        formGrid.add(textFieldCurrentProfessor, 1, 4);
        formGrid.add(new Label("Estado:"), 0, 5);
        toggleActiveState = new ToggleButton("Inactiva");
        toggleActiveState.setPrefWidth(110);
        toggleActiveState.setOnAction(event -> {toggleActiveState.setText(toggleActiveState.isSelected() ? "Activa" : "Inactiva");});
        formGrid.add(toggleActiveState, 1, 5);
        populateFields();
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
        HBox bottomBox = new HBox(30, comboBoxProfessors, buttonUpdate, buttonBack);
        bottomBox.setAlignment(Pos.CENTER_LEFT);
        bottomBox.setPadding(new Insets(10, 0, 0, 0));
        formPanel.getChildren().addAll(labelTitle, formGrid, bottomBox);
        StackPane mainPanel = new StackPane(formPanel);
        mainPanel.setPadding(new Insets(20));
        ControllerModifyExperience controller = new ControllerModifyExperience(this);
        buttonUpdate.setOnAction(event -> controller.handleUpdate());
        buttonBack.setOnAction(event -> controller.handleBackButtonAction());
        Scene scene = new Scene(mainPanel, 580, 370);
        GUIStyle.apply(scene);
        stage.setScene(scene);
        stage.show();
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

    public String getSelectedPeriod() {
        String period = "";
        if (comboBoxSemester != null && comboBoxYear != null) {
            Integer year = comboBoxYear.getValue();
            String month = resolveMonth(comboBoxSemester.getValue());
            if (year != null && !month.isEmpty()) {
                period = String.format("%d-%s", year, month);
            }
        }
        return period;
    }

    private String resolveMonth(String semester) {
        String month = "";
        if ("Febrero - Julio".equals(semester)) {
            month = "02";
        } else if ("Agosto - Enero".equals(semester)) {
            month = "08";
        }
        return month;
    }

    public ToggleButton getToggleActiveState() {
        return toggleActiveState;
    }

    private void populateFields() {
        if (experience != null) {
            textFieldName.setText(experience.getName());
            textFieldCareer.setText(experience.getEducationalProgram());
            if (experience.getProfessor() != null) {
                textFieldCurrentProfessor.setText(experience.getProfessor().getName() + " " + experience.getProfessor().getLastName());
            }
            toggleActiveState.setSelected(experience.isActiveStatus());
            toggleActiveState.setText(experience.isActiveStatus() ? "Activa" : "Inactiva");
            String period = experience.getPeriod();
            if (period != null && period.contains("-")) {
                String[] parts = period.split("-");
                try {
                    int year = Integer.parseInt(parts[0]);
                    String month = parts[1];
                    comboBoxYear.setValue(year);
                    if ("02".equals(month)) {
                        comboBoxSemester.setValue("Febrero - Julio");
                    } else if ("08".equals(month)) {
                        comboBoxSemester.setValue("Agosto - Enero");
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
    }

    public boolean validateFields() {
        boolean fieldsValid = false;
        List<String> errors = new ArrayList<>();
        GUIUtils.validateNames(textFieldName.getText().trim(), "Nombre", errors);
        GUIUtils.validateShortText(textFieldCareer.getText().trim(), "Carrera", errors);
        if (comboBoxSemester.getValue() == null || comboBoxSemester.getValue().isBlank()) {
            errors.add("Seleccione el semestre.");
        }
        if (comboBoxYear.getValue() == null) {
            errors.add("Seleccione el año.");
        }
        String selectedPeriod = getSelectedPeriod();
        if (selectedPeriod.isBlank()) {
            errors.add("El periodo seleccionado no es valido.");
        } else {
            GUIUtils.validatePeriod(selectedPeriod, errors);
        }
        if (errors.isEmpty()) {
            fieldsValid = true;
        } else {
            GUIUtils.showErrors(errors);
        }
        return fieldsValid;
    }

    public void showError(String message) {
        GUIUtils.showError(message);
    }

    public void showSuccess(String message) {
        GUIUtils.showSuccess(message);
    }

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