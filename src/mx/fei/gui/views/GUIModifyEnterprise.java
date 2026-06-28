package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.gui.controllers.ControllerModifyEnterprise;
import mx.fei.gui.utils.CountryCityLoader;
import mx.fei.logic.dto.Enterprise;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class GUIModifyEnterprise extends Application {

    private Enterprise enterprise;
    private TextField textFieldName;
    private TextField textFieldPhone;
    private TextField textFieldMail;
    private ComboBox<String> comboBoxSector;
    private TextField textFieldDirectUsers;
    private TextField textFieldIndirectUsers;
    private ToggleButton toggleState;
    private ComboBox<String> comboBoxCountry;
    private ComboBox<String> comboBoxCity;
    private Button buttonUpdate;
    private Button buttonCancel;

    public GUIModifyEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }

    public GUIModifyEnterprise() {}

    @Override
    public void start(Stage stage) {
        stage.setTitle("Modificar organización vinculada");
        stage.setResizable(false);
        VBox formPanel = new VBox(12);
        formPanel.setPadding(new Insets(20, 30, 20, 30));
        formPanel.setAlignment(Pos.TOP_LEFT);
        formPanel.getStyleClass().add("form-panel");
        Label labelTitle = new Label("Datos de la Organización Vinculada:");
        labelTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setPadding(new Insets(5, 0, 5, 0));
        formGrid.setAlignment(Pos.TOP_LEFT);
        formGrid.add(new Label("Nombre:"), 0, 0);
        textFieldName = new TextField();
        textFieldName.setPrefWidth(280);
        formGrid.add(textFieldName, 1, 0);
        formGrid.add(new Label("Telefono:"), 0, 1);
        textFieldPhone = new TextField();
        formGrid.add(textFieldPhone, 1, 1);
        formGrid.add(new Label("Correo:"), 0, 2);
        textFieldMail = new TextField();
        formGrid.add(textFieldMail, 1, 2);
        formGrid.add(new Label("Sector:"), 0, 3);
        comboBoxSector = new ComboBox<>(FXCollections.observableArrayList("Primario", "Secundario", "Terciario"));
        comboBoxSector.setPromptText("Seleccionar sector");
        comboBoxSector.setPrefWidth(280);
        formGrid.add(comboBoxSector, 1, 3);
        formGrid.add(new Label("No. de usuarios directos:"), 0, 4);
        textFieldDirectUsers = new TextField();
        textFieldDirectUsers.setPrefWidth(150);
        formGrid.add(textFieldDirectUsers, 1, 4);
        formGrid.add(new Label("No. de usuarios indirectos:"), 0, 5);
        textFieldIndirectUsers = new TextField();
        textFieldIndirectUsers.setPrefWidth(150);
        formGrid.add(textFieldIndirectUsers, 1, 5);
        toggleState = new ToggleButton("Inactivo");
        toggleState.setPrefWidth(130);
        toggleState.setOnAction(e -> toggleState.setText(toggleState.isSelected() ? "Activo" : "Inactivo"));
        comboBoxCountry = new ComboBox<>();
        comboBoxCountry.setPromptText("Países");
        comboBoxCountry.setPrefWidth(180);
        comboBoxCity = new ComboBox<>();
        comboBoxCity.setPromptText("Ciudades");
        comboBoxCity.setPrefWidth(180);
        comboBoxCity.setDisable(true);
        loadCountries();
        configureLocationCombos();
        populateFields();
        HBox locationBox = new HBox(20, comboBoxCountry, comboBoxCity);
        locationBox.setAlignment(Pos.CENTER_LEFT);
        buttonUpdate = new Button("Aceptar");
        buttonCancel = new Button("Cancelar");
        buttonUpdate.setPrefWidth(120);
        buttonCancel.setPrefWidth(120);
        buttonUpdate.setPrefHeight(35);
        buttonCancel.setPrefHeight(35);
        HBox buttonsBox = new HBox(20, buttonUpdate, buttonCancel);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(10, 0, 0, 0));
        formPanel.getChildren().addAll(labelTitle, formGrid, toggleState, locationBox, buttonsBox);
        StackPane mainPanel = new StackPane(formPanel);
        mainPanel.setPadding(new Insets(20));
        ControllerModifyEnterprise controllerModifyEnterprise = new ControllerModifyEnterprise(this);
        buttonUpdate.setOnAction(controllerModifyEnterprise::handleAcceptCancel);
        buttonCancel.setOnAction(controllerModifyEnterprise::handleAcceptCancel);
        Scene scene = new Scene(mainPanel, 520, 540);
        GUIStyle.apply(scene);
        stage.setScene(scene);
        stage.show();
    }

    private void loadCountries() {
        comboBoxCountry.setItems(FXCollections.observableArrayList(
                CountryCityLoader.getCountries()));
    }

    public boolean validateFields() {
        boolean fieldsValidated = true;
        List<String> errors = new ArrayList<>();
        GUIUtils.validateNames(textFieldName.getText().trim(), "Nombre", errors);
        GUIUtils.validatePhone(textFieldPhone.getText().trim(),"Telefono", errors);
        GUIUtils.validateEmail(textFieldMail.getText().trim(), errors);
        GUIUtils.validateComboBoxSelection(comboBoxSector.getValue(),"Sector", errors);
        GUIUtils.validateComboBoxSelection(comboBoxCountry.getValue(),"Paises", errors);
        GUIUtils.validateComboBoxSelection(comboBoxCity.getValue(),"Ciudades", errors);
        GUIUtils.validateLong(textFieldDirectUsers.getText().trim(), "Usuarios directos", errors);
        GUIUtils.validateLong(textFieldIndirectUsers.getText().trim(), "Usuarios indirectos", errors);
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

    public Enterprise getEnterprise() {
        return enterprise;
    }

    public TextField getTextFieldName() {
        return textFieldName;
    }

    public TextField getTextFieldPhone() {
        return textFieldPhone;
    }

    public TextField getTextFieldMail() {
        return textFieldMail;
    }

    public ComboBox<String> getComboBoxSector() {
        return comboBoxSector;
    }

    public TextField getTextFieldDirectUsers() {
        return textFieldDirectUsers;
    }

    public TextField getTextFieldIndirectUsers() {
        return textFieldIndirectUsers;
    }

    public ToggleButton getToggleState() {
        return toggleState;
    }

    public ComboBox<String> getComboBoxCountry() {
        return comboBoxCountry;
    }

    public ComboBox<String> getComboBoxCity() {
        return comboBoxCity;
    }

    public Button getButtonUpdate() {
        return buttonUpdate;
    }

    public Button getButtonCancel() {
        return buttonCancel;
    }

    private void configureLocationCombos() {
        comboBoxCountry.setOnAction(e -> {
            String selectedCountry = comboBoxCountry.getValue();
            if (selectedCountry != null) {
                List<String> cities = CountryCityLoader.getCitiesByCountry(selectedCountry);
                comboBoxCity.setItems(FXCollections.observableArrayList(cities));
                comboBoxCity.setDisable(false);
                comboBoxCity.setValue(null);
                comboBoxCity.setPromptText("Ciudades");
            }
        });
    }

    private void populateFields() {
        if (enterprise != null) {
            textFieldName.setText(enterprise.getName());
            textFieldPhone.setText(enterprise.getPhoneNumber());
            textFieldMail.setText(enterprise.getContactEmail());
            comboBoxSector.setValue(enterprise.getSector());
            textFieldDirectUsers.setText(String.valueOf(enterprise.getDirectUsers()));
            textFieldIndirectUsers.setText(String.valueOf(enterprise.getIndirectUsers()));
            toggleState.setSelected(enterprise.isActiveStatus());
            toggleState.setText(enterprise.isActiveStatus() ? "Activo" : "Inactivo");
            if (enterprise.getCountry() != null) {
                comboBoxCountry.setValue(enterprise.getCountry());
                List<String> cities = CountryCityLoader.getCitiesByCountry(enterprise.getCountry());
                comboBoxCity.setItems(FXCollections.observableArrayList(cities));
                comboBoxCity.setDisable(false);
                comboBoxCity.setValue(enterprise.getCity());
            }
        }
    }
}