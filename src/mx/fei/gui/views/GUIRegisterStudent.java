package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;
import mx.fei.gui.controllers.ControllerRegisterStudent;
import mx.fei.gui.utils.GUIUtils;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class GUIRegisterStudent extends Application {

    private TextField textFieldNames;
    private TextField textFieldLastName;
    private TextField textFieldMail;
    private PasswordField textFieldPassword;
    private TextField textFieldEnrollment;
    private RadioButton radioButtonMan;
    private RadioButton radioButtonWoman;
    private RadioButton radioButtonSpeakIndigenousLanguage;
    private RadioButton radioButtonDontSpeakIndigenousLanguage;
    private Button buttonConfirm;
    private Button buttonCancel;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Registrar alumno");
        stage.setResizable(false);

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(13);
        formGrid.setPadding(new Insets(20, 30, 20, 30));
        formGrid.getStyleClass().add("form-panel");

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

        formGrid.add(new Label("Genero:"), 0, 6);
        radioButtonMan = new RadioButton("Hombre");
        radioButtonWoman = new RadioButton("Mujer");
        ToggleGroup toggleGroupGender = new ToggleGroup();
        radioButtonMan.setToggleGroup(toggleGroupGender);
        radioButtonWoman.setToggleGroup(toggleGroupGender);
        HBox genderBox = new HBox(15, radioButtonMan, radioButtonWoman);
        genderBox.setAlignment(Pos.CENTER_LEFT);
        formGrid.add(genderBox, 1, 6);

        formGrid.add(new Label("Lengua indigena:"), 0, 7);
        radioButtonSpeakIndigenousLanguage = new RadioButton("Habla");
        radioButtonDontSpeakIndigenousLanguage = new RadioButton("No habla");
        ToggleGroup toggleGroupLanguage = new ToggleGroup();
        radioButtonSpeakIndigenousLanguage.setToggleGroup(toggleGroupLanguage);
        radioButtonDontSpeakIndigenousLanguage.setToggleGroup(toggleGroupLanguage);
        HBox languageBox = new HBox(15, radioButtonSpeakIndigenousLanguage, radioButtonDontSpeakIndigenousLanguage);
        languageBox.setAlignment(Pos.CENTER_LEFT);
        formGrid.add(languageBox, 1, 7);

        buttonConfirm = new Button("Confirmar");
        buttonCancel = new Button("Cancelar");
        buttonConfirm.setPrefWidth(110);
        buttonCancel.setPrefWidth(110);

        HBox buttonsBox = new HBox(30, buttonConfirm, buttonCancel);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(15, 0, 5, 0));
        GridPane.setColumnSpan(buttonsBox, 2);
        GridPane.setHalignment(buttonsBox, HPos.CENTER);
        formGrid.add(buttonsBox, 0, 10);

        ControllerRegisterStudent controllerRegisterStudent = new ControllerRegisterStudent(this);
        buttonConfirm.setOnAction(controllerRegisterStudent::handleConfirmCancelButtons);
        buttonCancel.setOnAction(controllerRegisterStudent::handleConfirmCancelButtons);

        StackPane mainPanel = new StackPane(formGrid);
        mainPanel.setPadding(new Insets(20));

        Scene scene = new Scene(mainPanel);
        GUIStyle.apply(scene);
        stage.setScene(scene);
        stage.show();
    }

    public boolean validateFields() {
        boolean fieldsValidated = true;
        List<String> errors = new ArrayList<>();
        GUIUtils.validateNames(textFieldNames.getText(), "Nombre", errors);
        GUIUtils.validateNames(textFieldLastName.getText(), "Apellidos", errors);
        GUIUtils.validateEmail(textFieldMail.getText().trim(), errors);
        GUIUtils.validateEnrollment(textFieldEnrollment.getText().trim(), "Matrícula", errors);
        GUIUtils.validateStrongPassword(textFieldPassword.getText().trim(), errors);
        boolean genderSelected = radioButtonMan.isSelected() || radioButtonWoman.isSelected();
        GUIUtils.validateRadioSelection(genderSelected, "un género", errors);
        boolean languageSelected = radioButtonSpeakIndigenousLanguage.isSelected() || radioButtonDontSpeakIndigenousLanguage.isSelected();
        GUIUtils.validateRadioSelection(languageSelected, "si habla lengua indígena", errors);
        if (!errors.isEmpty()) {
            GUIUtils.showErrors(errors);
            fieldsValidated = false;
        }
        return fieldsValidated;
    }

    public void showError(String message) {
        GUIUtils.showError(message);
    }

    public void showSuccess(String message) {
        GUIUtils.showSuccess(message);
    }

    public void closeWindow() {
        ((Stage) buttonCancel.getScene().getWindow()).close();
    }

    public static void main(String[] args) {
        launch(args);
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

    public PasswordField getTextFieldPassword() {
        return textFieldPassword;
    }

    public TextField getTextFieldEnrollment() {
        return textFieldEnrollment;
    }

    public RadioButton getRadioButtonMan() {
        return radioButtonMan;
    }

    public RadioButton getRadioButtonWoman() {
        return radioButtonWoman;
    }

    public RadioButton getRadioButtonSpeakIndigenousLanguage() {
        return radioButtonSpeakIndigenousLanguage;
    }

    public RadioButton getRadioButtonDontSpeakIndigenousLanguage() {
        return radioButtonDontSpeakIndigenousLanguage;
    }

    public Button getButtonConfirm() {
        return buttonConfirm;
    }

    public Button getButtonCancel() {
        return buttonCancel;
    }
}