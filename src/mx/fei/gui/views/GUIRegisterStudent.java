package mx.fei.gui.views;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import mx.fei.gui.controllers.ControllerRegisterStudent;

public class GUIRegisterStudent extends Application {

    private TextField textFieldNames;
    private TextField textFieldLastName;
    private TextField textFieldMail;
    private PasswordField textFieldPassword;
    private TextField textFieldEnrollment;
    private TextField textFieldPeriod;
    private RadioButton radioButtonMan;
    private RadioButton radioButtonWoman;
    private RadioButton radioButtonSpeakIndigenousLanguage;
    private RadioButton radioButtonDontSpeakIndigenousLanguage;
    private ToggleButton toggleState;
    private Button buttonConfirm;
    private Button buttonCancel;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Registrar alumno");
        stage.setResizable(false);

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(12);
        formGrid.setPadding(new Insets(20, 30, 20, 30));
        formGrid.setBackground(new Background(new BackgroundFill(Color.rgb(220, 220, 220), CornerRadii.EMPTY, Insets.EMPTY)));
        formGrid.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        Label labelTitle = new Label("Registrar alumno");
        labelTitle.setFont(new Font("SansSerif", 14));
        GridPane.setColumnSpan(labelTitle, 2);
        GridPane.setHalignment(labelTitle, HPos.CENTER);
        formGrid.add(labelTitle, 0, 0);

        formGrid.add(new Label("Nombres:"), 0, 1);
        textFieldNames = new TextField();
        textFieldNames.setPrefWidth(250);
        formGrid.add(textFieldNames, 1, 1);

        formGrid.add(new Label("Apellidos:"), 0, 2);
        textFieldLastName = new TextField();
        formGrid.add(textFieldLastName, 1, 2);

        formGrid.add(new Label("Correo:"), 0, 3);
        textFieldMail = new TextField();
        formGrid.add(textFieldMail, 1, 3);

        formGrid.add(new Label("Contraseña:"), 0, 4);
        textFieldPassword = new PasswordField();
        formGrid.add(textFieldPassword, 1, 4);

        formGrid.add(new Label("Matricula:"), 0, 5);
        textFieldEnrollment = new TextField();
        formGrid.add(textFieldEnrollment, 1, 5);

        formGrid.add(new Label("Periodo:"), 0, 6);
        textFieldPeriod = new TextField();
        textFieldPeriod.setPrefWidth(100);
        formGrid.add(textFieldPeriod, 1, 6);

        formGrid.add(new Label("Genero:"), 0, 7);
        radioButtonMan = new RadioButton("Hombre");
        radioButtonWoman = new RadioButton("Mujer");
        ToggleGroup toggleGroupGender = new ToggleGroup();
        radioButtonMan.setToggleGroup(toggleGroupGender);
        radioButtonWoman.setToggleGroup(toggleGroupGender);
        HBox genderBox = new HBox(15, radioButtonMan, radioButtonWoman);
        genderBox.setAlignment(Pos.CENTER_LEFT);
        formGrid.add(genderBox, 1, 7);

        formGrid.add(new Label("Lengua indigena:"), 0, 8);
        radioButtonSpeakIndigenousLanguage = new RadioButton("Habla");
        radioButtonDontSpeakIndigenousLanguage = new RadioButton("No habla");
        ToggleGroup toggleGroupLanguage = new ToggleGroup();
        radioButtonSpeakIndigenousLanguage.setToggleGroup(toggleGroupLanguage);
        radioButtonDontSpeakIndigenousLanguage.setToggleGroup(toggleGroupLanguage);
        HBox languageBox = new HBox(15, radioButtonSpeakIndigenousLanguage, radioButtonDontSpeakIndigenousLanguage);
        languageBox.setAlignment(Pos.CENTER_LEFT);
        formGrid.add(languageBox, 1, 8);

        formGrid.add(new Label("Estado:"), 0, 9);
        toggleState = new ToggleButton("Inactivo");
        toggleState.setPrefWidth(100);
        toggleState.setOnAction(e ->
                toggleState.setText(toggleState.isSelected() ? "Activo" : "Inactivo")
        );
        formGrid.add(toggleState, 1, 9);

        buttonConfirm = new Button("Confirmar");
        buttonCancel = new Button("Cancelar");
        buttonConfirm.setPrefWidth(110);
        buttonCancel.setPrefWidth(110);
        buttonConfirm.setStyle("-fx-background-color: #323232; -fx-text-fill: white;");
        buttonCancel.setStyle("-fx-background-color: #323232; -fx-text-fill: white;");

        HBox buttonsBox = new HBox(30, buttonConfirm, buttonCancel);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(15, 0, 5, 0));
        GridPane.setColumnSpan(buttonsBox, 2);
        GridPane.setHalignment(buttonsBox, HPos.CENTER);
        formGrid.add(buttonsBox, 0, 10);

        ControllerRegisterStudent buttonsHandler = new ControllerRegisterStudent(this);
        buttonConfirm.setOnAction(buttonsHandler);
        buttonCancel.setOnAction(buttonsHandler);

        StackPane mainPanel = new StackPane(formGrid);
        mainPanel.setPadding(new Insets(20));
        mainPanel.setBackground(new Background(new BackgroundFill(Color.rgb(200, 200, 200), CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(mainPanel);
        stage.setScene(scene);
        stage.show();
    }

    public boolean validateFields() {
        boolean fieldsValidated = true;
        java.util.List<java.util.Map.Entry<Boolean, String>> validations = java.util.List.of(
                java.util.Map.entry(textFieldNames.getText().trim().isEmpty(),"El campo nombres es obligatorio."),
                java.util.Map.entry(textFieldLastName.getText().trim().isEmpty(),"El campo apellidos es obligatorio."),
                java.util.Map.entry(textFieldMail.getText().trim().isEmpty(),"El campo correo es obligatorio."),
                java.util.Map.entry(textFieldPassword.getText().isEmpty(),"El campo contraseña es obligatorio."),
                java.util.Map.entry(textFieldEnrollment.getText().trim().isEmpty(),"El campo matricula es obligatorio."),
                java.util.Map.entry(textFieldPeriod.getText().trim().isEmpty(),"El campo periodo es obligatorio."),
                java.util.Map.entry(radioButtonMan.getToggleGroup().getSelectedToggle() == null,"Selecciona un genero."),
                java.util.Map.entry(radioButtonSpeakIndigenousLanguage.getToggleGroup().getSelectedToggle() == null,"Selecciona si el alumno habla lengua indigena.")
        );
        for (java.util.Map.Entry<Boolean, String> validation : validations) {
            if (validation.getKey()) {
                showError(validation.getValue());
                fieldsValidated = false;
                break;
            }
        }
        return fieldsValidated;
    }

    public boolean validateFieldPassword() {
        boolean passwordsValidated = true;
        String password = textFieldPassword.getText().trim();
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.,]).{8,}$";
        if (!password.matches(regex)) {
            showError("La contraseña debe tener minimo 8 caracteres, una mayuscula, una minuscula, un numero y un caracter especial.");
            passwordsValidated = false;
        }
        return passwordsValidated;
    }

    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Campo requerido");
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

    public TextField getTextFieldNames() { return textFieldNames; }
    public TextField getTextFieldLastName() { return textFieldLastName; }
    public TextField getTextFieldMail() { return textFieldMail; }
    public PasswordField getTextFieldPassword() { return textFieldPassword; }
    public TextField getTextFieldEnrollment() { return textFieldEnrollment; }
    public TextField getTextFieldPeriod() { return textFieldPeriod; }
    public RadioButton getRadioButtonMan() { return radioButtonMan; }
    public RadioButton getRadioButtonWoman() { return radioButtonWoman; }
    public RadioButton getRadioButtonSpeakIndigenousLanguage() { return radioButtonSpeakIndigenousLanguage; }
    public RadioButton getRadioButtonDontSpeakIndigenousLanguage() { return radioButtonDontSpeakIndigenousLanguage; }
    public ToggleButton getToggleState() { return toggleState; }
    public Button getButtonConfirm() { return buttonConfirm; }
    public Button getButtonCancel() { return buttonCancel; }
}