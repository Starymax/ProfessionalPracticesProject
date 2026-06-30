package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;
import mx.fei.gui.controllers.ControllerValidateStudentDocuments;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.gui.utils.StudentStatusFilter;
import mx.fei.logic.dto.DocumentReviewItem;
import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.Student;
import mx.fei.logic.dto.StudentValidationSummary;
import mx.fei.logic.dto.ValidationStatus;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

public class GUIValidateStudentDocuments extends Application {

    private ListView<StudentValidationSummary> listViewStudents;
    private ComboBox<String> comboBoxStatusFilter;
    private ListView<DocumentReviewItem> listViewDocuments;
    private Label labelStudentName;
    private Label labelEnrollment;
    private Label labelEmail;
    private Label labelProject;
    private Label labelDetailTitle;
    private Button buttonReview;
    private Button buttonClose;
    private VBox detailPanel;
    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        Label labelTitle = new Label("Validación de documentos");
        labelTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 16));

        Label labelStudentsTitle = new Label("Alumnos con documentos subidos:");
        labelStudentsTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 13));

        listViewStudents = new ListView<>();
        listViewStudents.setPrefWidth(300);
        listViewStudents.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(StudentValidationSummary summary, boolean empty) {
                super.updateItem(summary, empty);
                if (empty || summary == null) {
                    setText(null);
                } else {
                    Student student = summary.getStudent();
                    setText(student.getName() + " " + student.getLastName() + "  —  " + summary.getPendingDocumentCount() + " sin revisión");
                }
            }
        });

        comboBoxStatusFilter = new ComboBox<>(FXCollections.observableArrayList(StudentStatusFilter.filterLabels()));
        comboBoxStatusFilter.setValue(StudentStatusFilter.ALL_LABEL);
        comboBoxStatusFilter.setPrefWidth(180);
        Label labelFilter = new Label("Estado:");
        HBox filterBox = new HBox(10, labelFilter, comboBoxStatusFilter);
        filterBox.setAlignment(Pos.CENTER_LEFT);

        VBox studentsPanel = new VBox(10, labelStudentsTitle, filterBox, listViewStudents);
        studentsPanel.setPadding(new Insets(0, 16, 0, 0));
        VBox.setVgrow(listViewStudents, Priority.ALWAYS);

        buildDetailPanel();

        ControllerValidateStudentDocuments controller = new ControllerValidateStudentDocuments(this);
        listViewStudents.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> controller.onStudentSelected());
        comboBoxStatusFilter.setOnAction(event -> controller.handleStatusFilter());
        buttonReview.setOnAction(event -> controller.openDocumentPreview());
        buttonClose.setOnAction(event -> closeWindow());

        BorderPane contentPane = new BorderPane();
        contentPane.setLeft(studentsPanel);
        contentPane.setCenter(detailPanel);

        BorderPane mainPanel = new BorderPane();
        mainPanel.setPadding(new Insets(20, 24, 20, 24));
        mainPanel.setTop(labelTitle);
        mainPanel.setCenter(contentPane);
        BorderPane.setMargin(labelTitle, new Insets(0, 0, 16, 0));

        Scene scene = new Scene(mainPanel, 860, 640);
        GUIStyle.apply(scene);
        stage.setTitle("Validar documentos de alumnos");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        controller.loadStudents();
    }

    public void loadStudents(List<StudentValidationSummary> summaries) {
        listViewStudents.getSelectionModel().clearSelection();
        listViewStudents.getItems().setAll(summaries);
    }

    public void showStudentDetail(Student student, Project project, List<DocumentReviewItem> items) {
        labelDetailTitle.setText("Documentos de " + student.getName() + " " + student.getLastName());
        labelStudentName.setText("Nombre: " + student.getName() + " " + student.getLastName());
        labelEnrollment.setText("Matrícula: " + student.getEnrollment());
        labelEmail.setText("Correo: " + student.getEmail());
        labelProject.setText("Proyecto asignado: " + (project != null ? project.getNameProject() : "Sin proyecto"));
        listViewDocuments.getItems().setAll(items);
    }

    public StudentValidationSummary getSelectedStudentSummary() {
        return listViewStudents.getSelectionModel().getSelectedItem();
    }

    public String getSelectedStatusLabel() {
        return comboBoxStatusFilter.getValue();
    }

    public DocumentReviewItem getSelectedDocumentItem() {
        return listViewDocuments.getSelectionModel().getSelectedItem();
    }

    public void showError(String message) {
        GUIUtils.showError(message);
    }

    public void showSuccess(String message) {
        GUIUtils.showSuccess(message);
    }

    public void closeWindow() {
        if (stage != null) {
            stage.close();
        }
    }

    public Button getButtonReview() {
        return buttonReview;
    }

    public Button getButtonClose() {
        return buttonClose;
    }

    public Stage getStage() {
        return stage;
    }

    private void buildDetailPanel() {
        labelDetailTitle = new Label("Seleccione un alumno para ver sus documentos");
        labelDetailTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        labelStudentName = new Label();
        labelEnrollment = new Label();
        labelEmail = new Label();
        labelProject = new Label();
        labelStudentName.setFont(Font.font("SansSerif", 13));
        labelEnrollment.setFont(Font.font("SansSerif", 13));
        labelEmail.setFont(Font.font("SansSerif", 13));
        labelProject.setFont(Font.font("SansSerif", 13));

        VBox infoBox = new VBox(4, labelStudentName, labelEnrollment, labelEmail, labelProject);
        infoBox.setPadding(new Insets(6, 0, 6, 0));

        Label labelDocsTitle = new Label("Documentos:");
        labelDocsTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 13));

        listViewDocuments = new ListView<>();
        listViewDocuments.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(DocumentReviewItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.getDocumentType().getDocumentType() + "  —  " + changeStatusLabel(item.getStatus()));
                    setStyle("-fx-text-fill: " + statusColor(item.getStatus()) + ";");
                }
            }
        });
        VBox.setVgrow(listViewDocuments, Priority.ALWAYS);

        buttonReview = createButton("Ver / Revisar documento");
        buttonClose = createButton("Cerrar");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox buttonRow = new HBox(12, spacer, buttonReview, buttonClose);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);
        buttonRow.setPadding(new Insets(12, 0, 0, 0));

        detailPanel = new VBox(10, labelDetailTitle, infoBox, labelDocsTitle, listViewDocuments, buttonRow);
        detailPanel.setPadding(new Insets(0, 0, 0, 16));
        VBox.setVgrow(listViewDocuments, Priority.ALWAYS);
    }

    private String changeStatusLabel(ValidationStatus status) {
        String statusLabel = "";
         switch (status) {
            case VALIDATED -> statusLabel = "Validado";
            case REJECTED -> statusLabel = "Rechazado";
            case NOT_UPLOADED -> statusLabel = "No subido";
            default -> statusLabel = "Subido (sin revisión)";
        };
         return statusLabel;
    }

    private String statusColor(ValidationStatus status) {
        String statusColor = "";
         switch (status) {
            case VALIDATED -> statusColor = "#2e7d32";
            case REJECTED -> statusColor = "#b71c1c";
            case NOT_UPLOADED -> statusColor = "#757575";
            default -> statusColor = "#e65100";
        };
         return statusColor;
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        return button;
    }
}
