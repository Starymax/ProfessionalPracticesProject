package mx.fei.gui.views;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import mx.fei.logic.dto.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class GUIModifyStudent extends Application {
    private Student student;
    private TextField textFieldNames;
    private TextField textFieldLastName;
    private TextField textFieldMail;
    private TextField textFieldPeriod;
    private TextField textFieldGrade;
    private RadioButton radioButtonMan;
    private RadioButton radioButtonWoman;
    private RadioButton radioButtonSpeakIndigenousLanguage;
    private RadioButton radioButtonDontSpeakIndigenousLanguage;
    private ToggleButton toggleState;
    private Button buttonUpdate;
    private Button buttonCancel;
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}\\s]{3,50}$");
    private static final Pattern REPETITION_PATTERN = Pattern.compile("(\\p{L})\\1{3,}",Pattern.CASE_INSENSITIVE);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PERIOD_PATTERN = Pattern.compile("^(19|20)\\d{2}-(0[1-9]|1[0-2])$");

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
        formGrid.add(new Label("Periodo:"), 0, 3);
        textFieldPeriod = new TextField();
        textFieldPeriod.setPrefWidth(220);
        formGrid.add(textFieldPeriod, 1, 3);
        formGrid.add(new Label("Calificación:"), 0, 4);
        textFieldGrade = new TextField();
        textFieldGrade.setPrefWidth(120);
        formGrid.add(textFieldGrade, 1, 4);
        formGrid.add(new Label("Genero"), 0, 5);
        radioButtonMan = new RadioButton("Hombre");
        radioButtonWoman = new RadioButton("Mujer");
        ToggleGroup toggleGroupGender = new ToggleGroup();
        radioButtonMan.setToggleGroup(toggleGroupGender);
        radioButtonWoman.setToggleGroup(toggleGroupGender);
        HBox genderBox = new HBox(20, radioButtonMan, radioButtonWoman);
        genderBox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setColumnSpan(genderBox, 3);
        formGrid.add(genderBox, 1, 5);
        formGrid.add(new Label("Lengua indigena:"), 0, 6);
        radioButtonSpeakIndigenousLanguage = new RadioButton("Habla");
        radioButtonDontSpeakIndigenousLanguage = new RadioButton("No habla");
        ToggleGroup toggleGroupLanguage = new ToggleGroup();
        radioButtonSpeakIndigenousLanguage.setToggleGroup(toggleGroupLanguage);
        radioButtonDontSpeakIndigenousLanguage.setToggleGroup(toggleGroupLanguage);
        HBox languageBox = new HBox(20, radioButtonSpeakIndigenousLanguage, radioButtonDontSpeakIndigenousLanguage);
        languageBox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setColumnSpan(languageBox, 3);
        formGrid.add(languageBox, 1, 6);
        formGrid.add(new Label("Estado:"), 0, 7);
        toggleState = new ToggleButton("Inactivo");
        toggleState.setPrefWidth(110);
        toggleState.setOnAction(e -> toggleState.setText(toggleState.isSelected() ? "Activo" : "Inactivo"));
        formGrid.add(toggleState, 1, 7);
        if (student != null) {
            textFieldNames.setText(student.getName());
            textFieldLastName.setText(student.getLastName());
            textFieldMail.setText(student.getEmail());
            textFieldPeriod.setText(student.getPeriod());
            textFieldGrade.setText(String.valueOf(student.getGrade()));
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
        buttonUpdate.setOnAction(event -> controllerModifyStudent.handleButtons(event));
        buttonCancel.setOnAction(event ->  controllerModifyStudent.handleButtons(event));
        StackPane mainPanel = new StackPane(formGrid);
        mainPanel.setPadding(new Insets(20));
        mainPanel.setBackground(new Background(new BackgroundFill(Color.rgb(200, 200, 200), CornerRadii.EMPTY, Insets.EMPTY)));
        Scene scene = new Scene(mainPanel, 620, 460);
        stage.setScene(scene);
        stage.show();
    }

    public boolean validateFields() {
        boolean valid = false;
        List<String> errors = new ArrayList<String>();
        validateNames(textFieldNames.getText().trim(), "Nombres", errors);
        validateNames(textFieldLastName.getText().trim(), "Apellidos", errors);
        validateEmail(textFieldMail.getText().trim(), errors);
        validatePeriod(textFieldPeriod.getText().trim(), errors);
        validateGrade(textFieldGrade.getText().trim(), errors);
        validateGenderSelection(errors);
        validateIndigenousLanguageSelection(errors);
        if (errors.isEmpty()) {
            valid = true;
        } else {
            showErrors(errors);
        }
        return valid;
    }

    private void validateNames(String name, String fieldName, List<String> errors) {
        if (name.isEmpty()) {
            errors.add("El campo de " + fieldName + " es obligatorio.");
        } else if (!NAME_PATTERN.matcher(name).matches()) {
            errors.add(fieldName + " solo debe contener letras, espacios y un mínimo de 3 caracteres y máximo de 50.");
        } else if (REPETITION_PATTERN.matcher(name).find()) {
            errors.add(fieldName + " no puede tener 3 veces consecutivas la misma letra.");
        }
    }

    private void validateEmail(String email, List<String> errors) {
        if (email.isEmpty()) {
            errors.add("El campo de correo es obligatorio.");
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            errors.add("El correo electrónico no tiene un formato válido (ejemplo: usuario@dominio.com).");
        }
    }

    private void validatePeriod(String period, List<String> errors) {
        if (period.isEmpty()) {
            errors.add("El campo de periodo es obligatorio.");
        } else if (!PERIOD_PATTERN.matcher(period).matches()) {
            errors.add("El periodo debe tener formato YYYY-MM (ejemplo: 2026-01).");
        }
    }

    private void validateGrade(String gradeText, List<String> errors) {
        if (gradeText.isEmpty()) {
            errors.add("El campo de califición es obligatorio.");
        }
        try {
            double grade = Double.parseDouble(gradeText);
            if (grade < 0 || grade > 10) {
                errors.add("La calificación no puede ser mayor a 10 o menor a 0.");
            } else if (gradeText.matches(".*\\.[0-9]{3,}")) {
                errors.add("La calificación no puede tener más de dos decimales.");
            }
        } catch (NumberFormatException e) {
            errors.add("La calificación debe de ser un número válido.");
        }
    }

    private void validateGenderSelection(List<String> errors) {
        if (radioButtonMan.getToggleGroup().getSelectedToggle() == null) {
            errors.add("Selecciona un género.");
        }
    }

    private void validateIndigenousLanguageSelection(List<String> errors) {
        if (radioButtonSpeakIndigenousLanguage.getToggleGroup().getSelectedToggle() == null) {
            errors.add("Selecciona si el alumno habla lengua indígena.");
        }
    }

    private void showErrors(List<String> errors) {
        String combinedMessage = String.join("\n- ", errors);
        showError("- " + combinedMessage);
    }

    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Exito");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void closeWindow() {
        ((Stage) buttonCancel.getScene().getWindow()).close();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public Student getStudent() { return student; }
    public TextField getTextFieldNames() { return textFieldNames; }
    public TextField getTextFieldLastName() { return textFieldLastName; }
    public TextField getTextFieldMail() { return textFieldMail; }
    public TextField getTextFieldPeriod() { return textFieldPeriod; }
    public TextField getTextFieldGrade() { return textFieldGrade; }
    public RadioButton getRadioButtonMan() { return radioButtonMan; }
    public RadioButton getRadioButtonWoman() { return radioButtonWoman; }
    public RadioButton getRadioButtonSpeakIndigenousLanguage() { return radioButtonSpeakIndigenousLanguage; }
    public RadioButton getRadioButtonDontSpeakIndigenousLanguage() { return radioButtonDontSpeakIndigenousLanguage; }
    public ToggleButton getToggleState() { return toggleState; }
    public Button getButtonUpdate() { return buttonUpdate; }
    public Button getButtonCancel() { return buttonCancel; }
}
