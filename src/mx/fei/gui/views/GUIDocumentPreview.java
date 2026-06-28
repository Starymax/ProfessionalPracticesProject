package mx.fei.gui.views;

import mx.fei.gui.utils.GUIStyle;
import mx.fei.gui.controllers.ControllerDocumentPreview;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.gui.utils.PDFPreviewUtil;
import mx.fei.logic.dto.Document;
import mx.fei.logic.dto.ValidationStatus;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class GUIDocumentPreview extends Application {

    private final Document document;
    private Button buttonValidate;
    private Button buttonReject;
    private Button buttonClose;
    private Stage stage;

    public GUIDocumentPreview(Document document) {
        this.document = document;
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        Label labelTitle = new Label("Documento: " + document.getDocumentType().getDocumentType());
        labelTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 15));

        VBox pagesBox = new VBox(12);
        pagesBox.setAlignment(Pos.TOP_CENTER);
        pagesBox.setPadding(new Insets(10));

        boolean previewLoaded = loadPreview(pagesBox);

        ScrollPane scrollPane = new ScrollPane(pagesBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(560);

        buttonValidate = createButton("Validar");
        buttonReject = createButton("Rechazar");
        buttonClose = createButton("Cerrar");

        boolean reviewable = previewLoaded && document.getValidationStatus() == ValidationStatus.PENDING;
        buttonValidate.setDisable(!reviewable);
        buttonReject.setDisable(!reviewable);

        ControllerDocumentPreview controllerDocumentPreview = new ControllerDocumentPreview(this);
        buttonValidate.setOnAction(controllerDocumentPreview::handleValidateRejectCloseButtons);
        buttonReject.setOnAction(controllerDocumentPreview::handleValidateRejectCloseButtons);
        buttonClose.setOnAction(controllerDocumentPreview::handleValidateRejectCloseButtons);

        Label labelStatus = new Label("Estado actual: " + statusLabel(document.getValidationStatus()));
        labelStatus.setFont(Font.font("SansSerif", 13));

        HBox buttonRow = new HBox(15, buttonValidate, buttonReject, buttonClose);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);

        VBox bottomPanel = new VBox(10, labelStatus, buttonRow);
        bottomPanel.setPadding(new Insets(12, 0, 0, 0));

        VBox mainPanel = new VBox(14, labelTitle, scrollPane, bottomPanel);
        mainPanel.setPadding(new Insets(20, 24, 20, 24));

        Scene scene = new Scene(mainPanel, 700, 720);
        GUIStyle.apply(scene);
        stage.setTitle("Vista previa del documento");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
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

    public Document getDocument() {
        return document;
    }

    public Button getButtonValidate() {
        return buttonValidate;
    }

    public Button getButtonReject() {
        return buttonReject;
    }

    public Button getButtonClose() {
        return buttonClose;
    }

    public Stage getStage() {
        return stage;
    }

    private boolean loadPreview(VBox pagesBox) {
        boolean previewLoaded = false;
        try {
            List<Image> pages = PDFPreviewUtil.renderPages(document.getDirectory());
            if (pages.isEmpty()) {
                pagesBox.getChildren().add(new Label("El documento no contiene páginas."));
            } else {
                for (Image page : pages) {
                    ImageView imageView = new ImageView(page);
                    imageView.setPreserveRatio(true);
                    imageView.setFitWidth(620);
                    pagesBox.getChildren().add(imageView);
                }
                previewLoaded = true;
            }
        } catch (IOException e) {
            pagesBox.getChildren().add(new Label("No se pudo cargar la vista previa: " + e.getMessage()));
        }
        return previewLoaded;
    }

    private String statusLabel(ValidationStatus status) {
        return switch (status) {
            case VALIDATED -> "Validado";
            case REJECTED -> "Rechazado";
            case NOT_UPLOADED -> "No subido";
            default -> "Subido (sin revisión)";
        };
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(140);
        return button;
    }
}