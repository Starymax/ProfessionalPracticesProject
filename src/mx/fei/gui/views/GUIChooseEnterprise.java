package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;

import javafx.collections.ObservableList;
import mx.fei.gui.controllers.ControllerChooseEnterprise;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.logic.dto.Enterprise;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.List;

public class GUIChooseEnterprise extends Application {
    private ListView<String> listViewEnterprises;
    private Button buttonSelect;
    private Button buttonBack;
    private List<Enterprise> enterprises;

    public GUIChooseEnterprise() {}

    @Override
    public void start(Stage stage) {
        stage.setTitle("Seleccionar organización vinculada:");
        stage.setResizable(false);
        VBox formPanel = new VBox(15);
        formPanel.setPadding(new Insets(25, 25, 25, 25));
        formPanel.setAlignment(Pos.TOP_LEFT);
        formPanel.getStyleClass().add("form-panel");
        Label labelTitle = new Label("Seleccione una organización:");
        labelTitle.setFont(new Font("SansSerif", 14));
        listViewEnterprises = new ListView<>();
        listViewEnterprises.setPrefWidth(430);
        listViewEnterprises.setPrefHeight(300);
        listViewEnterprises.setItems(FXCollections.observableArrayList());
        buttonSelect = new Button("Seleccionar");
        buttonBack = new Button("Regresar");
        buttonSelect.setPrefWidth(130);
        buttonBack.setPrefWidth(130);
        buttonSelect.setPrefHeight(35);
        buttonBack.setPrefHeight(35);
        VBox buttonsBox = new VBox(20, buttonSelect, buttonBack);
        buttonsBox.setAlignment(Pos.TOP_CENTER);
        buttonsBox.setPadding(new Insets(10, 0, 0, 0));
        HBox contentBox = new HBox(20, listViewEnterprises, buttonsBox);
        contentBox.setAlignment(Pos.TOP_LEFT);
        formPanel.getChildren().addAll(labelTitle, contentBox);
        StackPane mainPanel = new StackPane(formPanel);
        mainPanel.setPadding(new Insets(20));
        ControllerChooseEnterprise controllerChooseEnterprise = new ControllerChooseEnterprise(this);
        buttonSelect.setOnAction(controllerChooseEnterprise::handleSelectReturn);
        buttonBack.setOnAction(controllerChooseEnterprise::handleSelectReturn);
        Scene scene = new Scene(mainPanel, 660, 430);
        GUIStyle.apply(scene);
        stage.setScene(scene);
        stage.show();
    }

    public void setEnterprises(List<Enterprise> enterprises) {
        this.enterprises = enterprises;
        ObservableList<String> items = FXCollections.observableArrayList();
        for (Enterprise enterprise : enterprises) {
            items.add(enterprise.getName() + " - " + enterprise.getCity() + ", " + enterprise.getCountry());
        }
        listViewEnterprises.setItems(items);
    }

    public Enterprise getSelectedEnterprise() {
        Enterprise selectedEnterprise = null;
        int selectedIndex = listViewEnterprises.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && enterprises != null) {
            selectedEnterprise = enterprises.get(selectedIndex);
        }
        return selectedEnterprise;
    }

    public void showError(String message) {
        GUIUtils.showError(message);
    }

    public void closeWindow() {
        ((Stage) buttonBack.getScene().getWindow()).close();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public ListView<String> getListViewEnterprises() {
        return listViewEnterprises;
    }

    public Button getButtonSelect() {
        return buttonSelect;
    }

    public Button getButtonBack() {
        return buttonBack;
    }

    public List<Enterprise> getEnterprises() {
        return enterprises;
    }
}