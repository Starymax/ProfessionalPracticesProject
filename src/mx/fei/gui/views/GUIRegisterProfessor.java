package mx.fei.gui.views;

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
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

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

        ControllerRegisterProfessor controller = new ControllerRegisterProfessor(this);
        buttonRegister.setOnAction(controller);
        buttonCancel.setOnAction(controller);

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
        List<Map.Entry<Boolean, String>> validations = List.of(
                Map.entry(textFieldPersonalNumber.getText().isEmpty(), "El campo No. de personal es obligatorio."),
                Map.entry(textFieldName.getText().isEmpty(), "El campo nombre es obligatorio."),
                Map.entry(textFieldLastName.getText().isEmpty(), "El campo apellidos es obligatorio."),
                Map.entry(textFieldEmail.getText().isEmpty(), "El campo correo es obligatorio."),
                Map.entry(textFieldPassword.getText().isEmpty(), "El campo contraseña es obligatorio."),
                Map.entry(comboBoxGender.getSelectionModel().isEmpty(), "El campo género es obligatorio.")
        );
        for (var validation : validations) {
            if (validation.getKey()) {
                showError(validation.getValue());
                validated = false;
                break;
            }
        }
        return validated;
    }

    public boolean validateFieldPassword() {
        boolean passwordValidated = true;
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?,.&]).{8,}$";
        if (!textFieldPassword.getText().trim().matches(regex)) {
            showError("Favor de que su contraseña tenga mínimo un carácter especial, una mayúscula, una minúscula, un número y que sea de 8 dígitos.");
            passwordValidated = false;
        }
        return passwordValidated;
    }

    public boolean validateFieldInt() {
        boolean intValidated = true;
        if (!textFieldPersonalNumber.getText().trim().matches("^\\d+$")) {
            showError("El campo No. de personal debe incluir solo números.");
            intValidated = false;
        }
        return intValidated;
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
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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