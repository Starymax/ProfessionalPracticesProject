package mx.fei.gui.views;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import mx.fei.gui.controllers.ControllerModifyStudent;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.logic.dto.Student;

import java.util.ArrayList;
import java.util.List;

public class GUIModifyStudent extends Application {
    private Student student;
    private TextField textFieldNames;
    private TextField textFieldLastName;
    private TextField textFieldMail;
    private TextField textFieldGrade;
    private RadioButton radioButtonMan;
    private RadioButton radioButtonWoman;
    private RadioButton radioButtonSpeakIndigenousLanguage;
    private RadioButton radioButtonDontSpeakIndigenousLanguage;
    private ToggleButton toggleState;
    private Button buttonUpdate;
    private Button buttonCancel;

    public GUIModifyStudent(Student student) {
        this.student = student;
    }

    public GUIModifyStudent() {}

    @Override
    public void start(Stage stage) {
        stage.setTitle("Modificar alumno");
        stage.setResizable(false);
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(12);
        formGrid.setPadding(new Insets(25, 30, 25, 30));
        formGrid.setBackground(new Background(new BackgroundFill(Color.rgb(220, 220, 220), CornerRadii.EMPTY, Insets.EMPTY)));
        formGrid.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        formGrid.add(new Label("Nombres:"), 0, 0);
        textFieldNames = new TextField();
        textFieldNames.setPrefWidth(370);
        GridPane.setColumnSpan(textFieldNames, 3);
        formGrid.add(textFieldNames, 1, 0);
        formGrid.add(new Label("Apellidos:"), 0, 1);
        textFieldLastName = new TextField();
        GridPane.setColumnSpan(textFieldLastName, 3);
        formGrid.add(textFieldLastName, 1, 1);
        formGrid.add(new Label("Correo:"), 0, 2);
        textFieldMail = new TextField();
        GridPane.setColumnSpan(textFieldMail, 3);
        formGrid.add(textFieldMail, 1, 2);
        formGrid.add(new Label("Calificación:"), 0, 3);
        textFieldGrade = new TextField();
        textFieldGrade.setPromptText("0.0 - 100.0");
        GridPane.setColumnSpan(textFieldGrade, 3);
        formGrid.add(textFieldGrade, 1, 3);
        formGrid.add(new Label("Genero:"), 0, 4);
        radioButtonMan = new RadioButton("Hombre");
        radioButtonWoman = new RadioButton("Mujer");
        ToggleGroup toggleGroupGender = new ToggleGroup();
        radioButtonMan.setToggleGroup(toggleGroupGender);
        radioButtonWoman.setToggleGroup(toggleGroupGender);
        HBox genderBox = new HBox(20, radioButtonMan, radioButtonWoman);
        genderBox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setColumnSpan(genderBox, 3);
        formGrid.add(genderBox, 1, 4);
        formGrid.add(new Label("Lengua indigena:"), 0, 5);
        radioButtonSpeakIndigenousLanguage = new RadioButton("Habla");
        radioButtonDontSpeakIndigenousLanguage = new RadioButton("No habla");
        ToggleGroup toggleGroupLanguage = new ToggleGroup();
        radioButtonSpeakIndigenousLanguage.setToggleGroup(toggleGroupLanguage);
        radioButtonDontSpeakIndigenousLanguage.setToggleGroup(toggleGroupLanguage);
        HBox languageBox = new HBox(20, radioButtonSpeakIndigenousLanguage, radioButtonDontSpeakIndigenousLanguage);
        languageBox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setColumnSpan(languageBox, 3);
        formGrid.add(languageBox, 1, 5);
        formGrid.add(new Label("Estado:"), 0, 6);
        toggleState = new ToggleButton("Inactivo");
        toggleState.setPrefWidth(110);
        toggleState.setOnAction(e -> toggleState.setText(toggleState.isSelected() ? "Activo" : "Inactivo"));
        formGrid.add(toggleState, 1, 6);
        if (student != null) {
            textFieldNames.setText(student.getName());
            textFieldLastName.setText(student.getLastName());
            textFieldMail.setText(student.getEmail());
            if (student.getGender() != null && student.getGender().equals("Hombre")) {
                radioButtonMan.setSelected(true);
            } else {
                radioButtonWoman.setSelected(true);
            }
            if (student.isIndigenousLanguage()) {
                radioButtonSpeakIndigenousLanguage.setSelected(true);
            } else {
                radioButtonDontSpeakIndigenousLanguage.setSelected(true);
            }
            textFieldGrade.setText(String.valueOf(student.getGrade()));
            toggleState.setSelected(student.isActive());
            toggleState.setText(student.isActive() ? "Activo" : "Inactivo");
        }
        buttonUpdate = new Button("Actualizar");
        buttonCancel = new Button("Cancelar");
        buttonUpdate.setPrefWidth(120);
        buttonCancel.setPrefWidth(120);
        buttonUpdate.setPrefHeight(35);
        buttonCancel.setPrefHeight(35);
        buttonUpdate.setStyle("-fx-background-color: #323232; -fx-text-fill: white; -fx-background-radius: 8;");
        buttonCancel.setStyle("-fx-background-color: #323232; -fx-text-fill: white; -fx-background-radius: 8;");
        HBox buttonsBox = new HBox(20, buttonUpdate, buttonCancel);
        buttonsBox.setAlignment(Pos.CENTER_LEFT);
        buttonsBox.setPadding(new Insets(10,0,0,0));
        GridPane.setColumnSpan(buttonsBox, 4);
        formGrid.add(buttonsBox,0,8);
        ControllerModifyStudent controllerModifyStudent = new ControllerModifyStudent(this);
        buttonUpdate.setOnAction(controllerModifyStudent::handleUpdateCancelButtons);
        buttonCancel.setOnAction(controllerModifyStudent::handleUpdateCancelButtons);
        StackPane mainPanel = new StackPane(formGrid);
        mainPanel.setPadding(new Insets(20));
        mainPanel.setBackground(new Background(new BackgroundFill(Color.rgb(200, 200, 200), CornerRadii.EMPTY, Insets.EMPTY)));
        Scene scene = new Scene(mainPanel, 430, 380);
        stage.setScene(scene);
        stage.show();
    }

    public boolean validateFields() {
        boolean valid = true;
        List<String> errors = new ArrayList<String>();
        GUIUtils.validateNames(textFieldNames.getText().trim(), "Nombres", errors);
        GUIUtils.validateNames(textFieldLastName.getText().trim(), "Apellidos", errors);
        GUIUtils.validateEmail(textFieldMail.getText().trim(), errors);
        GUIUtils.validateGrade(textFieldGrade.getText().trim(), "Calificación", errors);
        GUIUtils.validateRadioSelection(radioButtonMan.getToggleGroup().getSelectedToggle() != null, "un género", errors);
        GUIUtils.validateRadioSelection(radioButtonSpeakIndigenousLanguage.getToggleGroup().getSelectedToggle() != null, "si el alumno habla lengua indígena", errors);
        if (!errors.isEmpty()) {
            valid = false;
            GUIUtils.showErrors(errors);
        }
        return valid;
    }

    public void closeWindow() {
        ((Stage) buttonCancel.getScene().getWindow()).close();
    }

    public void showError(String message) {
        GUIUtils.showError(message);
    }

    public void showSuccess(String message) {
        GUIUtils.showSuccess(message);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public Student getStudent() {
        return student;
    }

    public TextField getTextFieldNames() {
        return textFieldNames;
    }

    public TextField getTextFieldLastName() {
        return textFieldLastName;
    }

    public TextField getTextFieldMail() {
        return textFieldMail;
    }

    public RadioButton getRadioButtonMan() {
        return radioButtonMan;
    }

    public RadioButton getRadioButtonWoman() {
        return radioButtonWoman;
    }

    public TextField getTextFieldGrade() {
        return textFieldGrade;
    }

    public RadioButton getRadioButtonSpeakIndigenousLanguage() {
        return radioButtonSpeakIndigenousLanguage;
    }

    public RadioButton getRadioButtonDontSpeakIndigenousLanguage() {
        return radioButtonDontSpeakIndigenousLanguage;
    }

    public ToggleButton getToggleState() {
        return toggleState;
    }

    public Button getButtonUpdate() {
        return buttonUpdate;
    }

    public Button getButtonCancel() {
        return buttonCancel;
    }
}
