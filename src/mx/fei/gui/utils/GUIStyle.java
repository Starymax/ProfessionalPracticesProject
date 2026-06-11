package mx.fei.gui.utils;

import javafx.scene.Scene;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class GUIStyle {

    public static final String STYLESHEET = "/styles/style.css";
    private static final Logger LOGGER = Logger.getLogger(GUIStyle.class.getName());
    private static String cachedStylesheet;

    private GUIStyle() {
    }

    public static void apply(Scene scene) {
        if (scene != null) {
            String stylesheet = resolveStylesheet();
            if (stylesheet != null && !scene.getStylesheets().contains(stylesheet)) {
                scene.getStylesheets().add(stylesheet);
            }
        }
    }

    public static void addStyleClass(Region node, String styleClass) {
        if (node != null && styleClass != null && !styleClass.isBlank()) {
            if (!node.getStyleClass().contains(styleClass)) {
                node.getStyleClass().add(styleClass);
            }
        }
    }

    private static String resolveStylesheet() {
        if (cachedStylesheet == null) {
            URL resource = GUIStyle.class.getResource(STYLESHEET);
            if (resource != null) {
                cachedStylesheet = resource.toExternalForm();
            } else {
                LOGGER.log(Level.WARNING, "No se encontró la hoja de estilos ", STYLESHEET);
            }
        }
        return cachedStylesheet;
    }
}
