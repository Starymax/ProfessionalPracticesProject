package mx.fei.gui.views;

import mx.fei.gui.controllers.ControllerAddStudents;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.logic.dto.EducationalExperience;
import mx.fei.logic.dto.Student;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class GUIAddStudents extends Application {
    private EducationalExperience experience;
    private List<Student> studentsToAdd;
    private Label labelNrcValue;
    private Label labelNameValue;
    private Label labelCareerValue;
    private Button buttonAdd;
    private Button buttonConfirm;
    private Button buttonBack;

    public GUIAddStudents(EducationalExperience experience) {
        this.experience = experience;
        this.studentsToAdd = new ArrayList<>();
    }

    public GUIAddStudents() {
        this.studentsToAdd = new ArrayList<>();
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Añadir estudiantes");
        stage.setResizable(false);
        VBox formPanel = new VBox(15);
        formPanel.setPadding(new Insets(25, 30, 25, 30));
        formPanel.setAlignment(Pos.TOP_LEFT);
        formPanel.setBackground(new Background(new BackgroundFill(Color.rgb(220, 220, 220), CornerRadii.EMPTY, Insets.EMPTY)));
        formPanel.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        Label labelTitle = new Label("Dar de alta experiencia educativa");
        labelTitle.setFont(Font.font("SansSerif", FontWeight.NORMAL, 14));
        labelTitle.setAlignment(Pos.CENTER);
        VBox titleBox = new VBox(labelTitle);
        titleBox.setAlignment(Pos.CENTER);
        Label labelSubtitle = new Label("Experiencia educativa:");
        labelSubtitle.setFont(new Font("SansSerif", 13));
        GridPane dataGrid = new GridPane();
        dataGrid.setHgap(15);
        dataGrid.setVgap(15);
        dataGrid.setPadding(new Insets(5, 0, 10, 20));
        Label labelNrc = new Label("NRC:");
        Label labelName = new Label("Nombre:");
        Label labelCareer = new Label("Carrera:");
        labelNrc.setFont(new Font("SansSerif", 13));
        labelName.setFont(new Font("SansSerif", 13));
        labelCareer.setFont(new Font("SansSerif", 13));
        labelNrcValue = new Label();
        labelNameValue = new Label();
        labelCareerValue = new Label();
        labelNrcValue.setFont(Font.font("SansSerif", FontWeight.NORMAL, 13));
        labelNameValue.setFont(Font.font("SansSerif", FontWeight.NORMAL, 13));
        labelCareerValue.setFont(Font.font("SansSerif", FontWeight.NORMAL, 13));
        labelNrcValue.setStyle("-fx-underline: true;");
        labelNameValue.setStyle("-fx-underline: true;");
        labelCareerValue.setStyle("-fx-underline: true;");
        if (experience != null) {
            labelNrcValue.setText(experience.getNrc());
            labelNameValue.setText(experience.getName());
            labelCareerValue.setText(experience.getEducationalProgram());
        } else {
            labelNrcValue.setText("nrcExperiencia");
            labelNameValue.setText("nombreExperiencia");
            labelCareerValue.setText("programaEducativo");
        }
        dataGrid.add(labelNrc, 0, 0);
        dataGrid.add(labelNrcValue, 1, 0);
        dataGrid.add(labelName, 0, 1);
        dataGrid.add(labelNameValue, 1, 1);
        dataGrid.add(labelCareer, 0, 2);
        dataGrid.add(labelCareerValue, 1, 2);
        buttonAdd = createActionButton("Agregar");
        buttonConfirm = createActionButton("Confirmar");
        buttonBack = createActionButton("Regresar");
        HBox buttonsBox = new HBox(20, buttonAdd, buttonConfirm, buttonBack);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(15, 0, 5, 0));
        formPanel.getChildren().addAll(titleBox, labelSubtitle, dataGrid, buttonsBox);
        StackPane mainPanel = new StackPane(formPanel);
        mainPanel.setPadding(new Insets(20));
        mainPanel.setBackground(new Background(new BackgroundFill(Color.rgb(200, 200, 200), CornerRadii.EMPTY, Insets.EMPTY)));
        ControllerAddStudents controllerAddStudents = new ControllerAddStudents(this, stage);
        buttonAdd.setOnAction(controllerAddStudents::handleAddConfirmReturnButtons);
        buttonConfirm.setOnAction(controllerAddStudents::handleAddConfirmReturnButtons);
        buttonBack.setOnAction(controllerAddStudents::handleAddConfirmReturnButtons);
        Scene scene = new Scene(mainPanel, 560, 360);
        stage.setScene(scene);
        stage.show();
    }

    private Button createActionButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(120);
        button.setPrefHeight(40);
        button.setStyle("-fx-background-color: #323232; -fx-text-fill: white; " + "-fx-background-radius: 8; -fx-font-size: 13px;");
        return button;
    }

    public void addStudents(List<Student> students) {
        for (Student student : students) {
            if (!studentsToAdd.contains(student)) {
                studentsToAdd.add(student);
            }
        }
    }

    public boolean showConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }

    public void showError(String message) {GUIUtils.showError(message);}

    public void showSuccess(String message) {GUIUtils.showSuccess(message);}

    public void closeWindow() {
        ((Stage) buttonBack.getScene().getWindow()).close();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public EducationalExperience getExperience() { return experience; }
    public List<Student> getStudentsToAdd() { return studentsToAdd; }
    public Button getButtonAdd() { return buttonAdd; }
    public Button getButtonConfirm() { return buttonConfirm; }
    public Button getButtonBack() { return buttonBack; }
}