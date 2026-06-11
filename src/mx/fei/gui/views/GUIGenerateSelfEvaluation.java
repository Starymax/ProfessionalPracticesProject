package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import mx.fei.gui.controllers.ControllerGenerateSelfEvaluation;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.logic.dto.Student;
import java.util.ArrayList;
import java.util.List;

public class GUIGenerateSelfEvaluation extends Application {

    private Stage stage;
    private ControllerGenerateSelfEvaluation controller;
    private Student student;
    private Label labelStudentName;
    private Label labelEnrollment;
    private Label labelOrganization;
    private Label labelResponsible;
    private Label labelProject;
    private List<ComboBox<Integer>> answerCombos;
    private Label labelTotalScore;
    private final int columns = 5;
    private final int rows = 10;
    private Button buttonPrint;
    private Button buttonBack;

    public GUIGenerateSelfEvaluation(Student student) {
        this.student = student;
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("Autoevaluación de Prácticas Profesionales");
        stage.setResizable(false);
        controller = new ControllerGenerateSelfEvaluation(this, stage, student);
        BorderPane mainPane = new BorderPane();
        mainPane.setPadding(new Insets(20));
        mainPane.setStyle("-fx-background-color: white;");
        VBox content = new VBox(15);
        content.setPadding(new Insets(10));
        Label title = new Label("EVALUACIÓN DEL ALUMNO - PRÁCTICAS DE INGENIERÍA DE SOFTWARE");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 16));
        content.getChildren().add(title);
        content.getChildren().add(createDataTable());
        content.getChildren().add(createInstructionsSection());
        content.getChildren().add(createQuestionsTable());
        content.getChildren().add(createFinalSection());
        HBox buttonRow = createButtonRow();
        content.getChildren().add(buttonRow);
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        mainPane.setCenter(scroll);
        Scene scene = new Scene(mainPane, 900, 750);
        GUIStyle.apply(scene);
        stage.setScene(scene);
        stage.show();
        controller.loadData();
        updateTotalScore();
    }

    private VBox createDataTable() {
        VBox vbox = new VBox(8);
        vbox.setStyle("-fx-border-color: #ccc; -fx-padding: 10;");
        GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(8);
        gridPane.add(new Label("Nombre del alumno:"), 0, 0);
        labelStudentName = new Label();
        gridPane.add(labelStudentName, 1, 0);
        gridPane.add(new Label("Matrícula:"), 0, 1);
        labelEnrollment = new Label();
        gridPane.add(labelEnrollment, 1, 1);
        gridPane.add(new Label("Organización vinculada:"), 0, 2);
        labelOrganization = new Label();
        gridPane.add(labelOrganization, 1, 2);
        gridPane.add(new Label("Responsable del proyecto:"), 0, 3);
        labelResponsible = new Label();
        gridPane.add(labelResponsible, 1, 3);
        gridPane.add(new Label("Nombre del proyecto:"), 0, 4);
        labelProject = new Label();
        gridPane.add(labelProject, 1, 4);
        vbox.getChildren().add(gridPane);
        return vbox;
    }

    private VBox createInstructionsSection() {
        VBox vbox = new VBox(5);
        vbox.setStyle("-fx-border-color: #ccc; -fx-padding: 10;");
        Label labelInstructionsTitle = new Label("INSTRUCCIONES:");
        labelInstructionsTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 12));
        Label labelInstructions = new Label("Responde a cada una de las afirmaciones presentadas, seleccionando el número del 1 al 5 según el siguiente criterio:");
        Label labelCriteria = new Label("1: Totalmente en desacuerdo | 2: En desacuerdo | 3: Indeciso | 4: De acuerdo | 5: Totalmente de acuerdo");
        labelCriteria.setWrapText(true);
        vbox.getChildren().addAll(labelInstructionsTitle, labelInstructions, labelCriteria);
        return vbox;
    }

    private VBox createQuestionsTable() {
        VBox vbox = new VBox(5);
        vbox.setStyle("-fx-border-color: #ccc; -fx-padding: 10;");
        Label labelTitleAfirmations = new Label("AFIRMACIONES");
        labelTitleAfirmations.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        GridPane header = new GridPane();
        header.setHgap(5);
        header.add(new Label("N°"), 0, 0);
        header.add(new Label("Afirmación"), 1, 0);
        for (int i = 1; i <= columns; i++) {
            header.add(new Label(String.valueOf(i)), i+1, 0);
        }
        header.setStyle("-fx-border-color: black; -fx-padding: 5;");
        String[] questions = {
                "Mi participación en la Organización Vinculada fue productiva.",
                "Logré la aplicación de los conocimientos teórico-prácticos adquiridos en la Licenciatura en Ingeniería de Software.",
                "Me sentí seguro al realizar las actividades encomendadas.",
                "Las actividades encomendadas despertaron mi interés.",
                "La Organización Vinculada me proporcionó la información y facilidades adecuadas durante el desarrollo de las prácticas.",
                "La Organización Vinculada me dio a conocer las reglas internas que debía seguir al conducirme durante el desarrollo de las prácticas.",
                "El Responsable del Proyecto me orientó correctamente para el desarrollo de mis actividades.",
                "El Responsable del Proyecto realizó un seguimiento efectivo de mis actividades.",
                "El proyecto es congruente con la formación de mi carrera.",
                "Considero que las prácticas son importantes para mi formación profesional."
        };
        VBox gridContainer = new VBox(2);
        answerCombos = new ArrayList<>();
        for (int i = 0; i < questions.length; i++) {
            GridPane rowGrid = new GridPane();
            rowGrid.setHgap(columns);
            rowGrid.add(new Label(String.valueOf(i+1)), 0, 0);
            Label labelQuestion = new Label(questions[i]);
            labelQuestion.setWrapText(true);
            labelQuestion.setPrefWidth(500);
            rowGrid.add(labelQuestion, 1, 0);
            ComboBox<Integer> combo = new ComboBox<>(FXCollections.observableArrayList(1,2,3,4,5));
            combo.setValue(1);
            combo.setPrefWidth(60);
            answerCombos.add(combo);
            rowGrid.add(combo, 2, 0);
            rowGrid.setStyle("-fx-border-color: lightgray; -fx-padding: 3;");
            gridContainer.getChildren().add(rowGrid);
        }
        for (ComboBox<Integer> answer : answerCombos) {
            answer.valueProperty().addListener((obs, old, newVal) -> updateTotalScore());
        }
        vbox.getChildren().addAll(labelTitleAfirmations, header, gridContainer);
        return vbox;
    }

    private void updateTotalScore() {
        int sum = 0;
        for (ComboBox<Integer> answer : answerCombos) {
            Integer value = answer.getValue();
            if (value != null) {
                sum += value;
            }
        }
        labelTotalScore.setText("PUNTUACIÓN FINAL: " + sum + " / 50");
    }

    private VBox createFinalSection() {
        VBox vbox = new VBox(10);
        vbox.setStyle("-fx-border-color: #ccc; -fx-padding: 10;");
        labelTotalScore = new Label("PUNTUACIÓN FINAL: 0 / 50");
        labelTotalScore.setFont(Font.font("SansSerif", FontWeight.BOLD, 12));
        vbox.getChildren().addAll(labelTotalScore);
        return vbox;
    }

    private HBox createButtonRow() {
        buttonPrint = new Button("Imprimir");
        buttonPrint.setPrefWidth(120);
        buttonBack = new Button("Regresar");
        buttonBack.setPrefWidth(120);
        buttonPrint.setOnAction(controller::handleSelfEvaluationButtons);
        buttonBack.setOnAction(controller::handleSelfEvaluationButtons);
        HBox box = new HBox(12, buttonPrint, buttonBack);
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setPadding(new Insets(18, 0, 0, 0));
        return box;
    }

    public Student getStudent() {
        return student;
    }

    public Label getLabelStudentName() {
        return labelStudentName;
    }

    public Label getLabelEnrollment() {
        return labelEnrollment;
    }

    public Label getLabelOrganization() {
        return labelOrganization;
    }

    public Label getLabelResponsible() {
        return labelResponsible;
    }

    public Label getLabelProject() {
        return labelProject;
    }

    public List<ComboBox<Integer>> getAnswerCombos() {
        return answerCombos;
    }

    public Label getLabelTotalScore() {
        return labelTotalScore;
    }

    public void showError(String msg) {
        GUIUtils.showError(msg);
    }

    public void showSuccess(String msg) {
        GUIUtils.showSuccess(msg);
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public void closeWindow() {
        stage.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}