package mx.fei.gui.views;

import mx.fei.gui.controllers.ControllerRegisterEnterprise;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class GUIRegisterEnterprise extends Application {

    private TextField nameTextField;
    private TextField addressTextField;
    private TextField phoneNumberTextField;
    private TextField emailTextField;
    private TextField sectorTextField;
    private TextField directUsersTextField;
    private TextField indirectUsersTextField;
    private Button registerButton;
    private Button cancelButton;
    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        Label title = new Label("Datos de la Organización Vinculada:");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 16));
        nameTextField = new TextField();
        addressTextField = new TextField();
        phoneNumberTextField = new TextField();
        emailTextField = new TextField();
        sectorTextField = new TextField();
        directUsersTextField = new TextField();
        indirectUsersTextField = new TextField();

        String[] labels = {"Nombre:", "Dirección:", "Telefono:", "Correo Electrónico:", "Sector:", "No. de Usuarios directos:", "No. de Usuarios indirectos:"};
        TextField[] fields = {nameTextField, addressTextField, phoneNumberTextField, emailTextField, sectorTextField, directUsersTextField, indirectUsersTextField};

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(12);
        for (int i = 0; i < labels.length; i++) {
            Label label = new Label(labels[i]);
            label.setFont(Font.font("SansSerif", 14));
            fields[i].setPrefWidth(340);
            formGrid.add(label, 0, i);
            formGrid.add(fields[i], 1, i);
        }

        registerButton = new Button("Registrar");
        cancelButton = new Button("Cancelar");

        String btnStyle = "-fx-background-color: #1e1e23; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;";
        registerButton.setStyle(btnStyle);
        cancelButton.setStyle(btnStyle);

        ControllerRegisterEnterprise controller = new ControllerRegisterEnterprise(this);
        registerButton.setOnAction(controller);
        cancelButton.setOnAction(controller);

        HBox buttonPanel = new HBox(10, registerButton, cancelButton);
        buttonPanel.setAlignment(Pos.CENTER_RIGHT);

        VBox mainPanel = new VBox(20, title, formGrid, buttonPanel);
        mainPanel.setPadding(new Insets(24, 32, 24, 32));

        Scene scene = new Scene(mainPanel, 580, 440);
        stage.setTitle("GUIRegisterEnterprise");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public boolean validateFields() {
        boolean validated = true;
        java.util.List<java.util.Map.Entry<Boolean, String>> validations = java.util.List.of(
                java.util.Map.entry(nameTextField.getText().isEmpty(), "El campo Nombre es obligatorio."),
                java.util.Map.entry(addressTextField.getText().isEmpty(), "El campo Dirección es obligatorio."),
                java.util.Map.entry(phoneNumberTextField.getText().isEmpty(), "El campo Teléfono es obligatorio."),
                java.util.Map.entry(emailTextField.getText().isEmpty(), "El campo Correo es obligatorio."),
                java.util.Map.entry(sectorTextField.getText().isEmpty(), "El campo Sector es obligatorio."),
                java.util.Map.entry(directUsersTextField.getText().isEmpty(), "El campo Usuarios Directos es obligatorio."),
                java.util.Map.entry(indirectUsersTextField.getText().isEmpty(), "El campo Usuarios Indirectos es obligatorio.")
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

    public boolean validateFieldsInt() {
        boolean intValidated = true;
        String regex = "^\\d+$";
        if (!phoneNumberTextField.getText().trim().matches(regex)) {
            showError("El campo Teléfono debe incluir solo números.");
            intValidated = false;
        } else if (!directUsersTextField.getText().trim().matches(regex)) {
            showError("El campo Usuarios Directos debe incluir solo números.");
            intValidated = false;
        } else if (!indirectUsersTextField.getText().trim().matches(regex)) {
            showError("El campo Usuarios Indirectos debe incluir solo números.");
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

    public TextField getNameTextField() {
        return nameTextField;
    }

    public TextField getAddressTextField() {
        return addressTextField;
    }

    public TextField getPhoneNumberTextField() {
        return phoneNumberTextField;
    }

    public TextField getEmailTextField() {
        return emailTextField;
    }

    public TextField getSectorTextField() {
        return sectorTextField;
    }

    public TextField getDirectUsersTextField() {
        return directUsersTextField;
    }

    public TextField getIndirectUsersTextField() {
        return indirectUsersTextField;
    }

    public Button getRegisterButton() {
        return registerButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public Stage getStage() {
        return stage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}