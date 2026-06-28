package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.gui.controllers.ControllerUploadDocument;
import mx.fei.logic.dto.Document;
import mx.fei.logic.dto.DocumentType;
import mx.fei.logic.dto.Practice;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class GUIUploadDocuments extends Application {
    private Practice practice;
    private String studentEnrollment;
    private Map<DocumentType, Document> selectedDocuments;
    private Set<DocumentType> uploadedDocuments;
    private ComboBox<String> comboBoxCategory;
    private ComboBox<String> comboBoxType;
    private VBox statusBox;
    private VBox selectedFilesBox;
    private Button buttonSelect;
    private Button buttonUpload;
    private Button buttonCancel;

    private static final Map<String, DocumentType> TYPE_MAP = Map.ofEntries(
            Map.entry("Evaluacion de competencias", DocumentType.COMPETENCE_EVALUATION),
            Map.entry("Carta de asignacion", DocumentType.ACCEPTANCE_LETTER),
            Map.entry("Plan de trabajo", DocumentType.WORK_PLAN),
            Map.entry("Horario", DocumentType.STUDENT_SCHEDULE),
            Map.entry("Carta de liberacion", DocumentType.LETTER_OF_RELEASE),
            Map.entry("Parcial", DocumentType.PARTIAL_REPORT),
            Map.entry("Mensual", DocumentType.MONTHLY_REPORT),
            Map.entry("Final", DocumentType.FINAL_REPORT),
            Map.entry("Acta de nacimiento", DocumentType.BIRTH_CERTIFICATE),
            Map.entry("Curp", DocumentType.CURP),
            Map.entry("Constancia de estudios", DocumentType.CERTIFICATE_OF_ENROLLMENT)
    );

    private final List<String> documentOptions = Arrays.asList(
            "Evaluacion de competencias", "Carta de asignacion",
            "Plan de trabajo", "Horario", "Carta de liberacion", "Acta de nacimiento", "Curp", "Constancia de estudios"
    );
    private final List<String> reportOptions = Arrays.asList("Parcial", "Mensual", "Final");

    public GUIUploadDocuments(Practice practice) {
        this.practice = practice;
        this.studentEnrollment = practice.getStudent().getEnrollment();
        this.selectedDocuments = new HashMap<>();
        this.uploadedDocuments = new HashSet<>();
    }

    public GUIUploadDocuments() {
        this.selectedDocuments = new HashMap<>();
        this.uploadedDocuments = new HashSet<>();
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Subir Documentos y Reportes");
        stage.setResizable(false);
        VBox formPanel = new VBox(15);
        formPanel.setPadding(new Insets(20, 30, 20, 30));
        formPanel.setAlignment(Pos.TOP_CENTER);
        formPanel.getStyleClass().add("form-panel");
        Label labelTitle = new Label("Subir Documentos y Reportes");
        labelTitle.setFont(Font.font("SansSerif", FontWeight.NORMAL, 15));
        Label labelStatus = new Label("Estado de documentos:");
        labelStatus.setFont(Font.font("SansSerif", FontWeight.BOLD, 13));
        statusBox = new VBox(5);
        statusBox.setPadding(new Insets(5, 0, 10, 10));
        refreshStatusBox();
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
        selectedFilesBox.setPadding(new Insets(5, 0, 5, 0));
        buttonSelect = createActionButton("Seleccionar");
        buttonUpload = createActionButton("Subir");
        buttonCancel = createActionButton("Cancelar");
        HBox buttonsBox = new HBox(20, buttonSelect, buttonUpload, buttonCancel);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(15, 0, 5, 0));
        formPanel.getChildren().addAll(labelTitle, labelStatus, statusBox, combosBox, selectedFilesBox, buttonsBox);
        StackPane mainPanel = new StackPane(formPanel);
        mainPanel.setPadding(new Insets(20));
        ControllerUploadDocument controllerUploadDocument = new ControllerUploadDocument(this, stage);
        buttonSelect.setOnAction(controllerUploadDocument::handleSelectUploadCancelButtons);
        buttonUpload.setOnAction(controllerUploadDocument::handleSelectUploadCancelButtons);
        buttonCancel.setOnAction(controllerUploadDocument::handleSelectUploadCancelButtons);
        Scene scene = new Scene(mainPanel, 560, 560);
        GUIStyle.apply(scene);
        stage.setScene(scene);
        stage.show();
    }

    public void setUploadedDocuments(List<Document> documents) {
        uploadedDocuments.clear();
        for (Document document : documents) {
            uploadedDocuments.add(document.getDocumentType());
        }
        refreshStatusBox();
    }

    public String getSelectedType() {
        return comboBoxType.getValue();
    }

    public void processSingleFile(File file, String documentTypeString) {
        DocumentType documentType = TYPE_MAP.get(documentTypeString);
        if (documentType != null) {
            Document document = new Document(file.getName(), file.getAbsolutePath(), documentType);
            selectedDocuments.put(documentType, document);
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
            Label label = new Label("- " + entry.getKey().getDocumentType() + ": " + entry.getValue().getName());
            label.getStyleClass().add("label-link");
            selectedFilesBox.getChildren().add(label);
        }
    }

    public void markAsUploaded(DocumentType documentType) {
        uploadedDocuments.add(documentType);
        selectedDocuments.remove(documentType);
        refreshStatusBox();
        updateSelectedFilesView();
    }

    public void showError(String message) {
        GUIUtils.showError(message);
    }

    public void showSuccess(String message) {
        GUIUtils.showSuccess(message);
    }

    public boolean showConfirmation(String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
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

    public Set<DocumentType> getUploadedDocuments() {
        return uploadedDocuments;
    }

    public ComboBox<String> getComboBoxCategory() {
        return comboBoxCategory;
    }

    public ComboBox<String> getComboBoxType() {
        return comboBoxType;
    }

    public Button getButtonSelect() {
        return buttonSelect;
    }

    public Button getButtonUpload() {
        return buttonUpload;
    }

    public Button getButtonCancel() {
        return buttonCancel;
    }

    public Practice getPractice() {
        return practice;
    }

    private void refreshStatusBox() {
        if (statusBox != null) {
            statusBox.getChildren().clear();
            for (Map.Entry<String, DocumentType> entry : TYPE_MAP.entrySet()) {
                boolean uploaded = uploadedDocuments.contains(entry.getValue());
                String statusText = uploaded ? " ✔ Subido" : " ✘ Pendiente";
                String color = uploaded ? "#2e7d32" : "#b71c1c";
                Label label = new Label(entry.getValue().getDocumentType() + ":" + statusText);
                label.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px;");
                statusBox.getChildren().add(label);
            }
        }
    }

    private Button createActionButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(130);
        button.setPrefHeight(45);
        return button;
    }
}