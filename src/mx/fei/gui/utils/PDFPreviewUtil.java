package mx.fei.gui.utils;


import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDFPreviewUtil {

    private static final float RENDER_DPI = 110f;

    private PDFPreviewUtil() {
    }

    public static List<Image> renderPages(String pdfPath) throws IOException {
        if (pdfPath == null || pdfPath.isBlank()) {
            throw new IOException("La ruta del documento está vacía");
        }
        File file = new File(pdfPath);
        if (!file.exists()) {
            throw new IOException("El archivo no existe: " + pdfPath);
        }
        List<Image> images = new ArrayList<>();
        try (PDDocument document = Loader.loadPDF(file)) {
            PDFRenderer renderer = new PDFRenderer(document);
            int pageCount = document.getNumberOfPages();
            for (int page = 0; page < pageCount; page++) {
                BufferedImage bufferedImage = renderer.renderImageWithDPI(page, RENDER_DPI);
                images.add(toFXImage(bufferedImage));
            }
        }
        return images;
    }

    private static Image toFXImage(BufferedImage bufferedImage) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, "png", outputStream);
            return new Image(new ByteArrayInputStream(outputStream.toByteArray()));
        }
    }
}
