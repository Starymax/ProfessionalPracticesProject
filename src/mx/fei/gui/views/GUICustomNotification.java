package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import mx.fei.gui.controllers.ControllerCustomNotification;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.logic.dto.Document;
import mx.fei.logic.dto.Student;

public class GUICustomNotification extends Application {

    private final Document document;
    private final GUIDocumentPreview parentPreview;
    private TextField textFieldTitle;
    private TextArea textAreaMessage;
    private Button buttonSend;
    private Button buttonCancel;
    private Stage stage;

    public GUICustomNotification(Document document, GUIDocumentPreview parentPreview) {
        this.document = document;
        this.parentPreview = parentPreview;
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        Student student = document.getPractice() != null ? document.getPractice().getStudent() : null;
        String studentName = student != null ? student.getName() + " " + student.getLastName() : "alumno";
        String documentName = document.getDocumentType().getDocumentType();

        Label labelTitle = new Label("Notificar rechazo de documento");
        labelTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 15));

        Label labelInfo = new Label("Documento: " + documentName + "\nAlumno: " + studentName);
        labelInfo.setFont(Font.font("SansSerif", 13));

        Label labelTitleField = new Label("Título de la notificación:");
        labelTitleField.setFont(Font.font("SansSerif", 13));
        textFieldTitle = new TextField("Documento rechazado");

        Label labelMessageField = new Label("Mensaje para el alumno:");
        labelMessageField.setFont(Font.font("SansSerif", 13));
        textAreaMessage = new TextArea("Tu documento '" + documentName + "' fue rechazado. Por favor súbelo nuevamente con las correcciones necesarias.");
        textAreaMessage.setWrapText(true);
        textAreaMessage.setPrefRowCount(5);

        Label labelWarning = new Label("Al enviar, el documento se eliminará y el alumno deberá subirlo nuevamente.");
        labelWarning.setStyle("-fx-text-fill: #b71c1c; -fx-font-size: 12px;");
        labelWarning.setWrapText(true);

        buttonSend = createButton("Enviar y eliminar");
        buttonCancel = createButton("Cancelar");

        ControllerCustomNotification controller = new ControllerCustomNotification(this);
        buttonSend.setOnAction(controller::handleSendCancelButtons);
        buttonCancel.setOnAction(controller::handleSendCancelButtons);

        HBox buttonRow = new HBox(12, buttonSend, buttonCancel);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);
        buttonRow.setPadding(new Insets(8, 0, 0, 0));

        VBox mainPanel = new VBox(10, labelTitle, labelInfo, labelTitleField, textFieldTitle, labelMessageField, textAreaMessage, labelWarning, buttonRow);
        mainPanel.setPadding(new Insets(20, 24, 20, 24));

        Scene scene = new Scene(mainPanel, 520, 470);
        GUIStyle.apply(scene);
        stage.setTitle("Enviar notificación");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(160);
        button.setStyle("-fx-background-color: #1e1e23; -fx-text-fill: white; -fx-font-size: 13px; -fx-cursor: hand; -fx-background-radius: 10;");
        return button;
    }

    public Document getDocument() {
        return document;
    }

    public String getNotificationTitle() {
        return textFieldTitle.getText();
    }

    public String getNotificationMessage() {
        return textAreaMessage.getText();
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

    public void closeParentPreview() {
        if (parentPreview != null) {
            parentPreview.closeWindow();
        }
    }

    public Button getButtonSend() {
        return buttonSend;
    }

    public Button getButtonCancel() {
        return buttonCancel;
    }

    public Stage getStage() {
        return stage;
    }
}
