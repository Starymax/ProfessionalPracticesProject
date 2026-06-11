package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.logic.dto.Student;
import mx.fei.gui.controllers.ControllerSelectStudents;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class GUISelectStudents extends Application {

    private GUIAddStudents guiAddStudents;
    private List<Student> availableStudents;
    private ListView<CheckBox> listViewStudents;
    private Button buttonSelect;
    private Button buttonBack;

    public GUISelectStudents(GUIAddStudents guiAddStudents) {
        this.guiAddStudents = guiAddStudents;
        this.availableStudents = new ArrayList<>();
    }

    public GUISelectStudents() {
        this.availableStudents = new ArrayList<>();
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Seleccione un estudiante:");
        stage.setResizable(false);
        VBox formPanel = new VBox(15);
        formPanel.setPadding(new Insets(25, 25, 25, 25));
        formPanel.setAlignment(Pos.TOP_LEFT);
        formPanel.getStyleClass().add("form-panel");
        Label labelTitle = new Label("Seleccione un alumno:");
        labelTitle.setFont(new Font("SansSerif", 14));
        listViewStudents = new ListView<>();
        listViewStudents.setPrefWidth(390);
        listViewStudents.setPrefHeight(260);
        listViewStudents.setItems(FXCollections.observableArrayList());
        buttonSelect = new Button("Seleccionar");
        buttonBack = new Button("Regresar");
        buttonSelect.setPrefWidth(130);
        buttonBack.setPrefWidth(130);
        buttonSelect.setPrefHeight(35);
        buttonBack.setPrefHeight(35);
        VBox buttonsBox = new VBox(20, buttonSelect, buttonBack);
        buttonsBox.setAlignment(Pos.TOP_CENTER);
        buttonsBox.setPadding(new Insets(10, 0, 0, 0));
        HBox contentBox = new HBox(20, listViewStudents, buttonsBox);
        contentBox.setAlignment(Pos.TOP_LEFT);
        formPanel.getChildren().addAll(labelTitle, contentBox);
        StackPane mainPanel = new StackPane(formPanel);
        mainPanel.setPadding(new Insets(20));
        ControllerSelectStudents controllerSelectStudents = new ControllerSelectStudents(this, stage);
        buttonSelect.setOnAction(controllerSelectStudents::handleSelectGoBackButtons);
        buttonBack.setOnAction(controllerSelectStudents::handleSelectGoBackButtons);
        Scene scene = new Scene(mainPanel, 620, 390);
        GUIStyle.apply(scene);
        stage.setScene(scene);
        stage.show();
    }

    public void setStudents(List<Student> students, List<Student> studentsAlreadySelected) {
        this.availableStudents = students;
        ObservableList<CheckBox> items = FXCollections.observableArrayList();
        for (Student student : students) {
            CheckBox checkBox = new CheckBox(student.getEnrollment() + " - " + student.getName() + " " + student.getLastName());
            checkBox.getStyleClass().add("checkbox-item");
            if (studentsAlreadySelected != null && studentsAlreadySelected.contains(student)) {
                checkBox.setSelected(true);
            }
            items.add(checkBox);
        }
        listViewStudents.setItems(items);
    }

    public List<Student> getCheckedStudents() {
        List<Student> checked = new ArrayList<>();
        for (int i = 0; i < listViewStudents.getItems().size(); i++) {
            if (listViewStudents.getItems().get(i).isSelected()) {
                checked.add(availableStudents.get(i));
            }
        }
        return checked;
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

    public GUIAddStudents getGuiAddStudents() { 
        return guiAddStudents; 
    }

    public List<Student> getAvailableStudents() { 
        return availableStudents; 
    }

    public ListView<CheckBox> getListViewStudents() { 
        return listViewStudents; 
    }

    public Button getButtonSelect() { 
        return buttonSelect; 
    }

    public Button getButtonBack() { 
        return buttonBack; 
    }

}