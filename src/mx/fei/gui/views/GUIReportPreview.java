package mx.fei.gui.views;

import mx.fei.gui.controllers.ControllerReportPreview;
import mx.fei.gui.utils.GUIUtils;
import mx.fei.logic.dto.Document;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

public class GUIReportPreview extends Application {

    private static final int RENDER_DPI = 110;
    private final Document report;
    private final GUIEvaluateStudent parent;
    private Label labelStatus;
    private Button buttonAccept;
    private Button buttonClose;
    private Stage stage;
    private static final Logger logger = Logger.getLogger(GUIReportPreview.class.getName());

    public GUIReportPreview(Document report, GUIEvaluateStudent parent) {
        this.report = report;
        this.parent = parent;
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        Label title = new Label(report.getDocumentType().getDocumentType() + " - " + report.getName());
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        labelStatus = new Label(report.isAccepted() ? "Estado: Aceptado" : "Estado: Pendiente");
        labelStatus.setFont(Font.font("SansSerif", 12));

        buttonAccept = new Button("Aceptar reporte");
        buttonClose = new Button("Cerrar");
        String buttonStyle = "-fx-background-color: #1e1e23; -fx-text-fill: white; -fx-font-size: 13px; -fx-cursor: hand; -fx-background-radius: 10;";
        buttonAccept.setStyle(buttonStyle);
        buttonClose.setStyle(buttonStyle);
        buttonAccept.setPrefWidth(150);
        buttonClose.setPrefWidth(120);
        buttonAccept.setDisable(report.isAccepted());

        ControllerReportPreview controllerReportPreview = new ControllerReportPreview(this);
        buttonAccept.setOnAction(controllerReportPreview::handleAcceptCloseButtons);
        buttonClose.setOnAction(controllerReportPreview::handleAcceptCloseButtons);

        HBox topBar = new HBox(15, buttonAccept, buttonClose, labelStatus);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0, 0, 12, 0));
        VBox topBox = new VBox(8, title, topBar);

        VBox pagesBox = new VBox(10);
        pagesBox.setAlignment(Pos.TOP_CENTER);
        pagesBox.setPadding(new Insets(10));
        for (Image pageImage : renderReport()) {
            ImageView imageView = new ImageView(pageImage);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(720);
            pagesBox.getChildren().add(imageView);
        }
        ScrollPane scrollPane = new ScrollPane(pagesBox);
        scrollPane.setFitToWidth(true);

        BorderPane mainPanel = new BorderPane();
        mainPanel.setPadding(new Insets(20, 24, 20, 24));
        mainPanel.setTop(topBox);
        mainPanel.setCenter(scrollPane);

        Scene scene = new Scene(mainPanel, 800, 720);
        stage.setTitle("Vista previa del reporte");
        stage.setScene(scene);
        stage.show();
    }

    private List<Image> renderReport() {
        List<Image> pages = new ArrayList<>();
        File file = new File(report.getDirectory());
        try (PDDocument pdfDocument = Loader.loadPDF(file)) {
            PDFRenderer renderer = new PDFRenderer(pdfDocument);
            for (int page = 0; page < pdfDocument.getNumberOfPages(); page++) {
                BufferedImage bufferedImage = renderer.renderImageWithDPI(page, RENDER_DPI);
                pages.add(toFXImage(bufferedImage));
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al renderizar el reporte PDF", e);
            showError("No se pudo abrir la vista previa del reporte.");
        }
        return pages;
    }

    private Image toFXImage(BufferedImage bufferedImage) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", outputStream);
        return new Image(new ByteArrayInputStream(outputStream.toByteArray()));
    }

    public void markAsAccepted() {
        labelStatus.setText("Estado: Aceptado");
        labelStatus.setTextFill(Color.web("#2e7d32"));
        buttonAccept.setDisable(true);
    }

    public Document getReport() {
        return report;
    }

    public GUIEvaluateStudent getParent() {
        return parent;
    }

    public Button getButtonAccept() {
        return buttonAccept;
    }

    public Button getButtonClose() {
        return buttonClose;
    }

    public Stage getStage() {
        return stage;
    }

    public void showError(String message) {
        GUIUtils.showError(message);
    }

    public void showSuccess(String message) {
        GUIUtils.showSuccess(message);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
