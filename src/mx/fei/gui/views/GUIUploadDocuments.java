package mx.fei.gui.views;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import mx.fei.gui.controllers.ControllerUploadDocument;
import mx.fei.logic.dto.Document;
import mx.fei.logic.dto.DocumentType;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUIUploadDocuments extends Application {
    private String studentEnrollment;
    private Map<DocumentType, Document> selectedDocuments;
    private Map<DocumentType, Label> documentLabels;
    private Label labelCompetenceEvaluation;
    private Label labelAcceptanceLetter;
    private Label labelWorkPlan;
    private Label labelSchedule;
    private Label labelLetterOfRelease;
    private Button buttonSelect;
    private Button buttonUpload;
    private Button buttonCancel;
    private static final Map<String, DocumentType> FILE_NAME_MAP = Map.of(
            "evaluacion_competencias", DocumentType.COMPETENCE_EVALUATION,
            "carta_asignacion", DocumentType.ACCEPTANCE_LETTER,
            "plan_trabajo", DocumentType.WORK_PLAN,
            "horario", DocumentType.STUDENT_SCHEDULE,
            "carta_liberacion", DocumentType.LETTER_OF_RELEASE
    );

    public GUIUploadDocuments(String studentEnrollment) {
        this.studentEnrollment = studentEnrollment;
        this.selectedDocuments = new HashMap<>();
        this.documentLabels = new HashMap<>();
    }

    public GUIUploadDocuments() {
        this.selectedDocuments = new HashMap<>();
        this.documentLabels = new HashMap<>();
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("SubirDocumento");
        stage.setResizable(false);
        VBox formPanel = new VBox(15);
        formPanel.setPadding(new Insets(20, 30, 20, 30));
        formPanel.setAlignment(Pos.TOP_CENTER);
        formPanel.setBackground(new Background(new BackgroundFill(Color.rgb(220, 220, 220), CornerRadii.EMPTY, Insets.EMPTY)));
        formPanel.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        Label labelTitle = new Label("Subir documentos");
        labelTitle.setFont(Font.font("SansSerif", FontWeight.NORMAL, 15));

        labelCompetenceEvaluation = createDocumentLabel("Evaluación de competencias");
        labelAcceptanceLetter = createDocumentLabel("Carta de asignación");
        labelWorkPlan = createDocumentLabel("Plan de trabajo");
        labelSchedule = createDocumentLabel("Horario");
        labelLetterOfRelease = createDocumentLabel("Carta de liberación");

        documentLabels.put(DocumentType.COMPETENCE_EVALUATION, labelCompetenceEvaluation);
        documentLabels.put(DocumentType.ACCEPTANCE_LETTER, labelAcceptanceLetter);
        documentLabels.put(DocumentType.WORK_PLAN, labelWorkPlan);
        documentLabels.put(DocumentType.STUDENT_SCHEDULE, labelSchedule);
        documentLabels.put(DocumentType.LETTER_OF_RELEASE, labelLetterOfRelease);
        VBox documentsBox = new VBox(10, labelCompetenceEvaluation, labelAcceptanceLetter, labelWorkPlan, labelSchedule, labelLetterOfRelease);
        documentsBox.setPadding(new Insets(10, 0, 10, 0));

        buttonSelect = createActionButton("Seleccionar");
        buttonUpload = createActionButton("Subir");
        buttonCancel = createActionButton("Cancelar");

        HBox buttonsBox = new HBox(20, buttonSelect, buttonUpload, buttonCancel);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(15, 0, 5, 0));
        formPanel.getChildren().addAll(labelTitle, documentsBox, buttonsBox);
        StackPane mainPanel = new StackPane(formPanel);
        mainPanel.setPadding(new Insets(20));
        mainPanel.setBackground(new Background(new BackgroundFill(Color.rgb(200, 200, 200), CornerRadii.EMPTY, Insets.EMPTY)));

        ControllerUploadDocument controllerUploadDocument = new ControllerUploadDocument(this, stage);
        buttonSelect.setOnAction(event -> controllerUploadDocument.handleSelectUploadCancel(event));
        buttonUpload.setOnAction(event -> controllerUploadDocument.handleSelectUploadCancel(event));
        buttonCancel.setOnAction(event -> controllerUploadDocument.handleSelectUploadCancel(event));
        Scene scene = new Scene(mainPanel, 560, 460);
        stage.setScene(scene);
        stage.show();
    }

    private Label createDocumentLabel(String documentName) {
        Label labelDocuments = new Label(documentName + ": pendiente");
        labelDocuments.setFont(new Font("SansSerif", 13));
        return labelDocuments;
    }

    private Button createActionButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(130);
        button.setPrefHeight(45);
        button.setStyle("-fx-background-color: #323232; -fx-text-fill: white; " + "-fx-background-radius: 10; -fx-font-size: 13px;");
        return button;
    }
    public void updateDocumentLabel(DocumentType type, String fileName) {
        Label labelDocuments = documentLabels.get(type);
        if (labelDocuments != null) {
            labelDocuments.setText(labelDocuments.getText().split(":")[0] + ": " + fileName);
            labelDocuments.setStyle("-fx-text-fill: #2e7d32;");
        }
    }

    public void processSelectedFiles(List<File> files) {
        boolean anyMatched = false;
        StringBuilder unmatched = new StringBuilder();
        for (File file : files) {
            String fileNameWithoutExtension = file.getName().toLowerCase().replace(".pdf", "");
            DocumentType type = FILE_NAME_MAP.get(fileNameWithoutExtension);
            if (type != null) {
                Document document = new Document(file.getName(), file.getAbsolutePath(), type);
                selectedDocuments.put(type, document);
                updateDocumentLabel(type, file.getName());
                anyMatched = true;
            } else {
                unmatched.append("- ").append(file.getName()).append("\n");
            }
        }
        if (!anyMatched) {
            showError("Ningún archivo coincide con los nombres esperados.\n\n" +
                    "Nombres válidos:\n" +
                    "evaluacion_competencias.pdf\n" +
                    "carta_asignacion.pdf\n" +
                    "plan_trabajo.pdf\n" +
                    "horario.pdf\n" +
                    "carta_liberacion.pdf");
        } else if (unmatched.length() > 0) {
            showError("Los siguientes archivos no coinciden con ningún documento:\n" + unmatched);
        }
    }

    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Exito");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public boolean showConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }

    public void closeWindow() {
        ((Stage) buttonCancel.getScene().getWindow()).close();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public String getStudentEnrollment() { return studentEnrollment; }
    public Map<DocumentType, Document> getSelectedDocuments() { return selectedDocuments; }
    public Button getButtonSelect() { return buttonSelect; }
    public Button getButtonUpload() { return buttonUpload; }
    public Button getButtonCancel() { return buttonCancel; }
}