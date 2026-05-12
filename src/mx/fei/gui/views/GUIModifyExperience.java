package mx.fei.gui.views;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import mx.fei.gui.controllers.ControllerModifyExperience;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.dto.Professor;
import java.util.ArrayList;
import java.util.List;

public class GUIModifyExperience extends Application {
    private EducationalExperience experience;
    private List<Professor> professors = new ArrayList<>();
    private TextField textFieldName;
    private TextField textFieldCareer;
    private TextField textFieldPeriod;
    private TextField textFieldCurrentProfessor;
    private ComboBox<String> comboBoxProfessors;
    private Button buttonUpdate;
    private Button buttonBack;

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
        formGrid.add(new Label("Periodo:"), 0, 2);
        textFieldPeriod = new TextField();
        formGrid.add(textFieldPeriod, 1, 2);
        formGrid.add(new Label("Profesor:"), 0, 3);
        textFieldCurrentProfessor = new TextField();
        textFieldCurrentProfessor.setDisable(true);
        textFieldCurrentProfessor.setStyle("-fx-opacity: 1;");
        formGrid.add(textFieldCurrentProfessor, 1, 3);
        if (experience != null) {
            textFieldName.setText(experience.getName());
            textFieldCareer.setText(experience.getEducationalProgram());
            textFieldPeriod.setText(experience.getEscolarPeriod());
            if (experience.getProfessor() != null) {
                textFieldCurrentProfessor.setText(experience.getProfessor().getName() + " " + experience.getProfessor().getLastName());
            }
        }
        comboBoxProfessors = new ComboBox<>();
        comboBoxProfessors.setPrefWidth(180);
        comboBoxProfessors.setPromptText("profesores");
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
        ControllerModifyExperience controllerModifyExperience = new ControllerModifyExperience(this);
        buttonUpdate.setOnAction(event -> controllerModifyExperience.handleButtonsUpdateReturn(event));
        buttonBack.setOnAction(event -> controllerModifyExperience.handleButtonsUpdateReturn(event));
        Scene scene = new Scene(mainPanel, 580, 380);
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
        int selectedIndex = comboBoxProfessors.getSelectionModel().getSelectedIndex();
        Professor professor = null;
        if (professors != null && selectedIndex >= 0 && selectedIndex < professors.size()) {
            professor = professors.get(selectedIndex);
        }
        return professor;
    }

    public boolean validateFields() {
        boolean valid = false;
        List<String> errors = new ArrayList<String>();
        GUIUtils.validateNames(textFieldName.getText().trim(), "Nombre", errors);
        GUIUtils.validateShortText(textFieldCareer.getText().trim(), "Carrera", errors);
        GUIUtils.validatePeriod(textFieldPeriod.getText().trim(), errors);
        if (errors.isEmpty()) {
            valid = true;
        } else {
            GUIUtils.showErrors(errors);
        }
        return valid;
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

    public EducationalExperience getExperience() { return experience; }
    public TextField getTextFieldName() { return textFieldName; }
    public TextField getTextFieldCareer() { return textFieldCareer; }
    public TextField getTextFieldPeriod() { return textFieldPeriod; }
    public ComboBox<String> getComboBoxProfessors() { return comboBoxProfessors; }
    public Button getButtonUpdate() { return buttonUpdate; }
    public Button getButtonBack() { return buttonBack; }
}