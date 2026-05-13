package mx.fei.gui.views;

import mx.fei.gui.utils.GUIUtils;
import mx.fei.gui.controllers.ControllerRegisterProfessor;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;


public class GUIRegisterProfessor extends Application {

    private TextField textFieldPersonalNumber;
    private TextField textFieldName;
    private TextField textFieldLastName;
    private ComboBox<String> comboBoxGender;
    private ComboBox<String> comboBoxShift;
    private TextField textFieldEmail;
    private PasswordField textFieldPassword;
    private CheckBox checkBoxIsCoordinator;
    private CheckBox checkBoxIsAdministrator;
    private Button buttonRegister;
    private Button buttonCancel;
    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        Label title = new Label("Datos del Profesor:");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 16));

        textFieldPersonalNumber = new TextField();
        textFieldName = new TextField();
        textFieldLastName = new TextField();
        comboBoxGender = new ComboBox<>();
        comboBoxShift = new ComboBox<>();
        textFieldEmail = new TextField();
        textFieldPassword = new PasswordField();

        comboBoxGender.getItems().addAll("Masculino", "Femenino");
        comboBoxGender.getSelectionModel().selectFirst();
        comboBoxShift.getItems().addAll("Matutino", "Vespertino", "Mixto");
        comboBoxShift.getSelectionModel().selectFirst();
        comboBoxGender.setMaxWidth(Double.MAX_VALUE);
        comboBoxShift.setMaxWidth(Double.MAX_VALUE);

        String[] labels = {"No. de personal:", "Nombre:", "Apellidos:", "Género:", "Turno:", "Correo:", "Contraseña:"};
        javafx.scene.Node[] fields = {textFieldPersonalNumber, textFieldName, textFieldLastName, comboBoxGender, comboBoxShift, textFieldEmail, textFieldPassword};

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(12);
        for (int i = 0; i < labels.length; i++) {
            Label label = new Label(labels[i]);
            label.setFont(Font.font("SansSerif", 14));
            if (fields[i] instanceof TextField tf) {
                tf.setPrefWidth(320);
            }
            if (fields[i] instanceof PasswordField pf) {
                pf.setPrefWidth(320);
            }
            formGrid.add(label, 0, i);
            formGrid.add(fields[i], 1, i);
        }

        checkBoxIsCoordinator = new CheckBox("Coordinador");
        checkBoxIsAdministrator = new CheckBox("Administrador");

        checkBoxIsCoordinator.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue) checkBoxIsAdministrator.setSelected(false);
        });

        checkBoxIsAdministrator.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue) checkBoxIsCoordinator.setSelected(false);
        });

        HBox checkBoxPanel = new HBox(20, checkBoxIsCoordinator, checkBoxIsAdministrator);
        checkBoxPanel.setAlignment(Pos.CENTER_LEFT);

        buttonRegister = new Button("Registrar");
        buttonCancel = new Button("Cancelar");

        String buttonStyle = "-fx-background-color: #1e1e23; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;";
        buttonRegister.setStyle(buttonStyle);
        buttonCancel.setStyle(buttonStyle);

        ControllerRegisterProfessor controllerRegisterProfessor = new ControllerRegisterProfessor(this);
        buttonRegister.setOnAction(event -> controllerRegisterProfessor.handleButtonAction(event));
        buttonCancel.setOnAction(event -> controllerRegisterProfessor.handleButtonAction(event));

        HBox buttonPanel = new HBox(10, buttonRegister, buttonCancel);
        buttonPanel.setAlignment(Pos.CENTER_RIGHT);

        BorderPane bottomRow = new BorderPane();
        bottomRow.setLeft(checkBoxPanel);
        bottomRow.setRight(buttonPanel);

        VBox mainPanel = new VBox(20, title, formGrid, bottomRow);
        mainPanel.setPadding(new Insets(24, 32, 24, 32));

        Scene scene = new Scene(mainPanel, 570, 440);
        stage.setTitle("GUIRegisterProfessor");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public boolean validateFields() {
        boolean validated = true;
        java.util.List<String> errors = new java.util.ArrayList<>();
        GUIUtils.validatePersonalNumber(textFieldPersonalNumber.getText().trim(), "No. de personal", errors);
        GUIUtils.validateNames(textFieldName.getText().trim(), "Nombre", errors);
        GUIUtils.validateNames(textFieldLastName.getText().trim(), "Apellidos", errors);
        GUIUtils.validateEmail(textFieldEmail.getText().trim(), errors);
        GUIUtils.validateStrongPassword(textFieldPassword.getText().trim(), errors);
        GUIUtils.validateNotEmpty(textFieldPassword.getText().trim(), "Contraseña", errors);
        GUIUtils.validateComboBoxSelection(comboBoxGender.getValue(), "género", errors);
        GUIUtils.validateComboBoxSelection(comboBoxShift.getValue(), "Turno", errors);
        if (!errors.isEmpty()) {
            GUIUtils.showErrors(errors);
            validated = false;
        }
        return validated;
    }

    public void showError(String message) {
        GUIUtils.showError(message);
    }

    public void showSuccess(String message) {
        GUIUtils.showSuccess(message);
    }

    public TextField getTextFieldPersonalNumber() {
        return textFieldPersonalNumber;
    }

    public TextField getTextFieldName() {
        return textFieldName;
    }

    public TextField getTextFieldLastName() {
        return textFieldLastName;
    }

    public ComboBox<String> getComboBoxGender() {
        return comboBoxGender;
    }

    public ComboBox<String> getComboBoxShift() {
        return comboBoxShift;
    }

    public TextField getTextFieldEmail() {
        return textFieldEmail;
    }

    public PasswordField getTextFieldPassword() {
        return textFieldPassword;
    }

    public CheckBox getCheckBoxIsCoordinator() {
        return checkBoxIsCoordinator;
    }

    public CheckBox getCheckBoxIsAdministrator() {
        return checkBoxIsAdministrator;
    }

    public Button getButtonRegister() {
        return buttonRegister;
    }

    public Button getButtonCancel() {
        return buttonCancel;
    }

    public Stage getStage() {
        return stage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}