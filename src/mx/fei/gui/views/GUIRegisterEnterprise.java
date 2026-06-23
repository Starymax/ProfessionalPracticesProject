package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;
import mx.fei.gui.utils.CountryCityLoader;
import mx.fei.gui.controllers.ControllerRegisterEnterprise;
import mx.fei.gui.utils.GUIUtils;

import javafx.collections.FXCollections;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class GUIRegisterEnterprise extends Application {
    private TextField textFieldName;
    private TextField textFieldPhone;
    private TextField textFieldMail;
    private ComboBox<String> comboBoxSector;
    private TextField textFieldDirectUsers;
    private TextField textFieldIndirectUsers;
    private ComboBox<String> comboBoxCountry;
    private ComboBox<String> comboBoxCity;
    private Button buttonRegister;
    private Button buttonCancel;

    public GUIRegisterEnterprise() {

    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Registrar organización vinculada");
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
        textFieldName.setPrefWidth(300);
        formGrid.add(textFieldName, 1, 0);
        formGrid.add(new Label("Telefono:"), 0, 2);
        textFieldPhone = new TextField();
        formGrid.add(textFieldPhone, 1, 2);
        formGrid.add(new Label("Correo:"), 0, 3);
        textFieldMail = new TextField();
        formGrid.add(textFieldMail, 1, 3);
        formGrid.add(new Label("Sector:"), 0, 4);
        comboBoxSector = new ComboBox<>();
        comboBoxSector.setItems(FXCollections.observableArrayList("Primario", "Secundario", "Terciario"));
        comboBoxSector.setPromptText("Seleccionar sector");
        comboBoxSector.setPrefWidth(300);
        formGrid.add(comboBoxSector, 1, 4);
        formGrid.add(new Label("No. de usuarios directos:"), 0, 5);
        textFieldDirectUsers = new TextField();
        textFieldDirectUsers.setPrefWidth(170);
        formGrid.add(textFieldDirectUsers, 1, 5);
        formGrid.add(new Label("No. de usuarios indirectos:"), 0, 6);
        textFieldIndirectUsers = new TextField();
        textFieldIndirectUsers.setPrefWidth(170);
        formGrid.add(textFieldIndirectUsers, 1, 6);
        comboBoxCountry = new ComboBox<>();
        comboBoxCountry.setPromptText("Países");
        comboBoxCountry.setPrefWidth(180);
        comboBoxCity = new ComboBox<>();
        comboBoxCity.setPromptText("Ciudades");
        comboBoxCity.setPrefWidth(180);
        comboBoxCity.setDisable(true);
        loadCountries();
        configureLocationCombos();
        HBox locationBox = new HBox(20, comboBoxCountry, comboBoxCity);
        locationBox.setAlignment(Pos.CENTER_LEFT);
        buttonRegister = new Button("Registrar");
        buttonCancel = new Button("Cancelar");
        buttonRegister.setPrefWidth(120);
        buttonCancel.setPrefWidth(120);
        buttonRegister.setPrefHeight(35);
        buttonCancel.setPrefHeight(35);
        HBox buttonsBox = new HBox(20, buttonRegister, buttonCancel);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(10, 0, 0, 0));
        formPanel.getChildren().addAll(labelTitle, formGrid, locationBox, buttonsBox);
        StackPane mainPanel = new StackPane(formPanel);
        mainPanel.setPadding(new Insets(20));
        ControllerRegisterEnterprise controllerRegisterEnterprise = new ControllerRegisterEnterprise(this);
        buttonRegister.setOnAction(controllerRegisterEnterprise::handleRegisterCancelButtons);
        buttonCancel.setOnAction(controllerRegisterEnterprise::handleRegisterCancelButtons);
        Scene scene = new Scene(mainPanel, 560, 460);
        GUIStyle.apply(scene);
        stage.setScene(scene);
        stage.show();
    }

    public  boolean validatedFields() {
        boolean validated = true;
        List<String> errors = new ArrayList<>();
        GUIUtils.validateShortText(textFieldName.getText(),"Nombre",errors);
        GUIUtils.validatePhone(textFieldPhone.getText(),"Telefono",errors);
        GUIUtils.validateEmail(textFieldMail.getText(),errors);
        GUIUtils.validateComboBoxSelection(comboBoxSector.getValue(),"Sector",errors);
        GUIUtils.validateLong(textFieldDirectUsers.getText(),"Usuarios directos",errors);
        GUIUtils.validateLong(textFieldIndirectUsers.getText(),"Usuarios indirectos",errors);
        GUIUtils.validateComboBoxSelection(comboBoxCountry.getValue(),"Paises",errors);
        GUIUtils.validateComboBoxSelection(comboBoxCity.getValue(),"Ciudades",errors);
        if (!errors.isEmpty()){
            validated = false;
            GUIUtils.showErrors(errors);
        }
        return validated;
    }
    public void showError(String message) {
        GUIUtils.showError(message);
    }

    public void showSuccess(String message) {
        GUIUtils.showSuccess(message);
    }

    public void closeWindow() {
        GUIUtils.closeWindow((Stage) buttonCancel.getScene().getWindow());
    }

    public static void main(String[] args) {
        launch(args);
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

    public ComboBox<String> getComboBoxCountry() {
        return comboBoxCountry;
    }

    public ComboBox<String> getComboBoxCity() {
        return comboBoxCity;
    }

    public Button getButtonRegister() {
        return buttonRegister;
    }

    public Button getButtonCancel() {
        return buttonCancel;
    }

    private void loadCountries() {
        List<String> countries = CountryCityLoader.getCountries();
        comboBoxCountry.setItems(FXCollections.observableArrayList(countries));
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

}