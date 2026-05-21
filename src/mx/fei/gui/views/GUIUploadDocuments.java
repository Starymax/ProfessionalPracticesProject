package mx.fei.gui.views;

import mx.fei.gui.utils.GUIUtils;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
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
import java.util.Arrays;

public class GUIUploadDocuments extends Application {
    private String studentEnrollment;
    private Map<DocumentType, Document> selectedDocuments;
    private ComboBox<String> comboBoxCategory;
    private ComboBox<String> comboBoxType;
    private VBox selectedFilesBox;
    private Button buttonSelect;
    private Button buttonUpload;
    private Button buttonCancel;
    private static final Map<String, DocumentType> TYPE_MAP = Map.of(
            "evaluacion_competencias", DocumentType.COMPETENCE_EVALUATION,
            "carta_asignacion", DocumentType.ACCEPTANCE_LETTER,
            "plan_trabajo", DocumentType.WORK_PLAN,
            "horario", DocumentType.STUDENT_SCHEDULE,
            "carta_liberacion", DocumentType.LETTER_OF_RELEASE,
            "Parcial", DocumentType.PARTIAL_REPORT,
            "Mensual", DocumentType.MONTHLY_REPORT,
            "Final", DocumentType.FINAL_REPORT
    );
    private final List<String> documentOptions = Arrays.asList("evaluacion_competencias", "carta_asignacion", "plan_trabajo", "horario", "carta_liberacion");
    private final List<String> reportOptions = Arrays.asList("Parcial", "Mensual", "Final");

    public GUIUploadDocuments(String studentEnrollment) {
        this.studentEnrollment = studentEnrollment;
        this.selectedDocuments = new HashMap<>();
    }

    public GUIUploadDocuments() {
        this.selectedDocuments = new HashMap<>();
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Subir Documentos y Reportes");
        stage.setResizable(false);
        VBox formPanel = new VBox(15);
        formPanel.setPadding(new Insets(20, 30, 20, 30));
        formPanel.setAlignment(Pos.TOP_CENTER);
        formPanel.setBackground(new Background(new BackgroundFill(Color.rgb(220, 220, 220), CornerRadii.EMPTY, Insets.EMPTY)));
        formPanel.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        Label labelTitle = new Label("Subir Documentos y Reportes");
        labelTitle.setFont(Font.font("SansSerif", FontWeight.NORMAL, 15));
        comboBoxCategory = new ComboBox<>(FXCollections.observableArrayList("Documento", "Reporte"));
        comboBoxCategory.setPromptText("Seleccione categoría");
        comboBoxCategory.setPrefWidth(250);
        comboBoxType = new ComboBox<>();
        comboBoxType.setPromptText("Seleccione tipo");
        comboBoxType.setPrefWidth(250);
        comboBoxCategory.setOnAction(e -> {
            if ("Documento".equals(comboBoxCategory.getValue())) {
                comboBoxType.setItems(FXCollections.observableArrayList(documentOptions));
            } else if ("Reporte".equals(comboBoxCategory.getValue())) {
                comboBoxType.setItems(FXCollections.observableArrayList(reportOptions));
            }
        });
        HBox combosBox = new HBox(15, comboBoxCategory, comboBoxType);
        combosBox.setAlignment(Pos.CENTER);
        selectedFilesBox = new VBox(5);
        selectedFilesBox.setAlignment(Pos.CENTER_LEFT);
        selectedFilesBox.setPadding(new Insets(10, 0, 10, 0));
        buttonSelect = createActionButton("Seleccionar");
        buttonUpload = createActionButton("Subir");
        buttonCancel = createActionButton("Cancelar");
        HBox buttonsBox = new HBox(20, buttonSelect, buttonUpload, buttonCancel);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(15, 0, 5, 0));
        formPanel.getChildren().addAll(labelTitle, combosBox, selectedFilesBox, buttonsBox);
        StackPane mainPanel = new StackPane(formPanel);
        mainPanel.setPadding(new Insets(20));
        mainPanel.setBackground(new Background(new BackgroundFill(Color.rgb(200, 200, 200), CornerRadii.EMPTY, Insets.EMPTY)));
        ControllerUploadDocument controllerUploadDocument = new ControllerUploadDocument(this, stage);
        buttonSelect.setOnAction(controllerUploadDocument::handleSelectUploadCancelButtons);
        buttonUpload.setOnAction(controllerUploadDocument::handleSelectUploadCancelButtons);
        buttonCancel.setOnAction(controllerUploadDocument::handleSelectUploadCancelButtons);
        Scene scene = new Scene(mainPanel, 560, 460);
        stage.setScene(scene);
        stage.show();
    }

    private Button createActionButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(130);
        button.setPrefHeight(45);
        button.setStyle("-fx-background-color: #323232; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-size: 13px;");
        return button;
    }

    public String getSelectedType() {
        return comboBoxType.getValue();
    }

    public void processSingleFile(File file, String typeStr) {
        DocumentType type = TYPE_MAP.get(typeStr);
        if (type != null) {
            Document document = new Document(file.getName(), file.getAbsolutePath(), type);
            selectedDocuments.put(type, document); // Si elige el mismo tipo, se reemplaza
            updateSelectedFilesView();
        } else {
            showError("Tipo de archivo no reconocido.");
        }
    }

    private void updateSelectedFilesView() {
        selectedFilesBox.getChildren().clear();
        Label title = new Label("Archivos listos para subir:");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 12));
        selectedFilesBox.getChildren().add(title);
        for (Map.Entry<DocumentType, Document> entry : selectedDocuments.entrySet()) {
            Label label = new Label("- " + entry.getKey().name() + ": " + entry.getValue().getName());
            label.setStyle("-fx-text-fill: #2e7d32;");
            selectedFilesBox.getChildren().add(label);
        }
    }

    public void showError(String message) {
        GUIUtils.showError(message);
    }

    public void showSuccess(String message) {
        GUIUtils.showSuccess(message);
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

    public String getStudentEnrollment() {
        return studentEnrollment;
    }

    public Map<DocumentType, Document> getSelectedDocuments() {
        return selectedDocuments;
    }
}