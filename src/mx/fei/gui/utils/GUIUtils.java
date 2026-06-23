package mx.fei.gui.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.regex.Pattern;

public class GUIUtils {
    public static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}\\s]{3,50}$");
    public static final Pattern LONG_TEXT_PATTERN = Pattern.compile("^[\\p{L}\\d\\s.,¡!¿?()%\\-]{1,65535}$");
    public static final Pattern REPORT_OBSERVATION_PATTERN = Pattern.compile("^[\\p{L}\\d\\s.,¡!¿?()%\\-]{1,195}$");
    public static final Pattern SHORT_TEXT_PATTERN = Pattern.compile("^[\\p{L}\\d\\s.,¡!¿?()%\\-]{1,100}$");
    public static final Pattern REPETITION_PATTERN = Pattern.compile("(\\p{L})\\1{3,}", Pattern.CASE_INSENSITIVE);
    public static final Pattern CONTAINS_NUMBERS_PATTERN = Pattern.compile("\\d");
    public static final Pattern CONTAINS_VOWELS_PATTERN = Pattern.compile("[aeiouáéíóúüAEIOUÁÉÍÓÚÜ]", Pattern.CASE_INSENSITIVE);
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    public static final Pattern PERIOD_PATTERN = Pattern.compile("^(19|20)\\d{2}-(0[1-9]|1[0-2])$");
    public static final Pattern NUMERIC_PATTERN = Pattern.compile("^\\d+$");
    public static final Pattern ENROLLMENT_PATTERN = Pattern.compile("^\\d{6,}$");
    public static final Pattern SEARCH_FORBIDDEN_PATTERN = Pattern.compile("[^\\p{L}\\d\\s-]");
    private static final int limitNumberPhone = 10;
    private static final int limitMail = 100;
    private static final int limitNrc = 5;
    private static final float minimumGrade = 0.0f;
    private static final float maxGrade = 10.0f;
    private static final int minimumPersonalNumber = 3;
    private static final int maxPersonalNumber = 10;
    private static final int limitSingleDigitNumber = 9;
    private static final int singleDigitNumber = 2;
    private static final int longDigitNumber = 15;

    public static void validateNames(String value, String fieldName, List<String> errors) {
        if (value == null || value.isEmpty()) {
            errors.add("El campo de " + fieldName + " es obligatorio.");
        } else if (value.trim().isEmpty()) {
            errors.add(fieldName + " no puede contener solo espacios.");
        } else if (!value.equals(value.trim())) {
            errors.add(fieldName + " no puede empezar ni terminar con espacios.");
        } else if (CONTAINS_NUMBERS_PATTERN.matcher(value).find()) {
            errors.add(fieldName + " no puede contener números.");
        } else if (!CONTAINS_VOWELS_PATTERN.matcher(value).find()) {
            errors.add(fieldName + " debe contener al menos una vocal.");
        } else if (!NAME_PATTERN.matcher(value).matches()) {
            errors.add(fieldName + " solo debe contener letras, espacios y un mínimo de 3 caracteres y máximo de 50.");
        } else if (REPETITION_PATTERN.matcher(value).find()) {
            errors.add(fieldName + " no puede tener 3 o más veces consecutivas la misma letra.");
        }
    }

    public static void validateLongText(String value, String fieldName, List<String> errors) {
        if (value == null || value.isEmpty()) {
            errors.add("El campo de " + fieldName + " es obligatorio.");
        } else if (value.trim().isEmpty()) {
            errors.add(fieldName + " no puede contener solo espacios.");
        } else if (!value.equals(value.trim())) {
            errors.add(fieldName + " no puede empezar ni terminar con espacios.");
        } else if (!LONG_TEXT_PATTERN.matcher(value).matches()) {
            errors.add(fieldName + " no debe contener caracteres inválidos o contener más de 1000 caracteres");
        } else if (REPETITION_PATTERN.matcher(value).find()) {
            errors.add(fieldName + " no puede tener 3 o más veces consecutivas la misma letra.");
        }
    }

    public static void validateShortText(String value, String fieldName, List<String> errors) {
        if (value == null || value.isEmpty()) {
            errors.add("El campo de " + fieldName + " es obligatorio.");
        } else if (value.trim().isEmpty()) {
            errors.add(fieldName + " no puede contener solo espacios.");
        } else if (!value.equals(value.trim())) {
            errors.add(fieldName + " no puede empezar ni terminar con espacios.");
        } else if (!SHORT_TEXT_PATTERN.matcher(value).matches()) {
            errors.add(fieldName + " no debe contener caracteres inválidos o contener más de 100 letras");
        } else if (REPETITION_PATTERN.matcher(value).find()) {
            errors.add(fieldName + " no puede tener 3 o más veces consecutivas la misma letra.");
        }
    }

    public static void validateEmail(String email, List<String> errors) {
        if (email == null || email.isEmpty()) {
            errors.add("El campo de correo es obligatorio.");
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            errors.add("El correo electrónico no tiene un formato válido (ejemplo: usuario@dominio.com).");
        } else if (email.length() > limitMail) {
            errors.add("El correo electrónico no puede tener más de 100 caracteres.");
        } else if (email.contains("..") || email.startsWith(".") || email.endsWith(".")) {
            errors.add("El correo electrónico tiene puntos consecutivos o puntos al inicio/final.");
        }
    }

    public static void validatePeriod(String period, List<String> errors) {
        if (period.isEmpty()) {
            errors.add("El campo de periodo es obligatorio.");
        } else if (!PERIOD_PATTERN.matcher(period).matches()) {
            errors.add("El periodo debe tener formato YYYY-MM (ejemplo: 2026-01).");
        }
    }

    public static void validateEnrollment(String enrollment, String fieldName, List<String> errors) {
        if (enrollment.isEmpty()) {
            errors.add("El campo de " + fieldName + " es obligatorio.");
        } else if (!ENROLLMENT_PATTERN.matcher(enrollment).matches()) {
            errors.add(fieldName + " debe ser un número de al menos 6 dígitos.");
        }
    }

    public static void validateGrade(String value, String fieldName, List<String> errors) {
        if (value == null || value.isEmpty()) {
            errors.add("El campo de " + fieldName + " es obligatorio.");
        } else {
            try {
                float grade = Float.parseFloat(value);
                if (grade < minimumGrade || grade > maxGrade) {
                    errors.add(fieldName + " debe estar entre 0.0 y 10.0.");
                }
            } catch (NumberFormatException e) {
                errors.add(fieldName + " debe ser un número válido (uso de punto decimal permitido). ");
            }
        }
    }

    public static void validatePersonalNumber(String value, String fieldName, List<String> errors) {
        if (value.isEmpty()) {
            errors.add("El campo de " + fieldName + " es obligatorio.");
        } else if (!NUMERIC_PATTERN.matcher(value).matches()) {
            errors.add(fieldName + " debe contener solo números.");
        } else if (value.length() < minimumPersonalNumber) {
            errors.add(fieldName + " debe tener al menos 3 dígitos.");
        } else if (value.length() > maxPersonalNumber) {
            errors.add(fieldName + " no puede tener más de 10 dígitos.");
        } else if (value.startsWith("0")) {
            errors.add(fieldName + " no puede empezar con cero.");
        }
    }

    public static void validatePhone(String value, String fieldName, List<String> errors) {
        if (value.isEmpty()) {
            errors.add("El campo de " + fieldName + " es obligatorio.");
        } else if (!NUMERIC_PATTERN.matcher(value).matches()) {
            errors.add(fieldName + " debe contener solo números.");
        } else if (value.length() != limitNumberPhone) {
            errors.add(fieldName + " debe tener 10 dígitos.");
        }
    }

    public static void validateLong(String value, String fieldName, List<String> errors) {
        if (value.isEmpty()) {
            errors.add("El campo de " + fieldName + " es obligatorio.");
        } else if (!NUMERIC_PATTERN.matcher(value).matches()) {
            errors.add(fieldName + " debe contener solo números.");
        } else if (value.length() > longDigitNumber) {
            errors.add(fieldName + " no puede ser un valor tan grande.");
        }
    }

    public static void validateShortInt(String value, String fieldName, List<String> errors) {
        if (value.isEmpty()) {
            errors.add("El campo de " + fieldName + " es obligatorio.");
        } else if (!NUMERIC_PATTERN.matcher(value).matches()) {
            errors.add(fieldName + " debe contener solo números.");
        } else if (value.length() > singleDigitNumber) {
            errors.add(fieldName + " no puede ser un valor tan grande.");
        }
    }

    public static void validateInt(String value, String fieldName, List<String> errors) {
        if (value.isEmpty()) {
            errors.add("El campo de " + fieldName + " es obligatorio.");
        } else if (!NUMERIC_PATTERN.matcher(value).matches()) {
            errors.add(fieldName + " debe contener solo números.");
        } else if (value.length() > limitSingleDigitNumber) {
            errors.add(fieldName + " no puede ser un valor tan grande.");
        }
    }

    public static void validateNRC(String value, String fieldName, List<String> errors) {
        if (value.isEmpty()) {
            errors.add("El campo de " + fieldName + " es obligatorio.");
        } else if (!NUMERIC_PATTERN.matcher(value).matches()) {
            errors.add(fieldName + " debe contener solo números.");
        } else if (value.length() != limitNrc) {
            errors.add(fieldName + " debe tener 5 dígitos");
        } else if (value.charAt(0) == '0') {
            errors.add(fieldName + " no puede comenzar con el número 0.");
        }
    }

    public static void validateStrongPassword(String password, List<String> errors) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.,]).{8,}$";
        if (password.isEmpty()) {
            errors.add("El campo de contraseña es obligatorio.");
        } else if (!password.matches(regex)) {
            errors.add("La contraseña debe tener mínimo 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial.");
        }
    }

    public static void validateObsesrvationsText(String value, String fieldName, List<String> errors) {
        if (value == null || value.isEmpty()) {
            errors.add("El campo de " + fieldName + " es obligatorio.");
        } else if (value.trim().isEmpty()) {
            errors.add(fieldName + " no puede contener solo espacios.");
        } else if (!value.equals(value.trim())) {
            errors.add(fieldName + " no puede empezar ni terminar con espacios.");
        } else if (!REPORT_OBSERVATION_PATTERN.matcher(value).matches()) {
            errors.add(fieldName + " no debe contener caracteres inválidos o contener más de 195 caracteres");
        } else if (REPETITION_PATTERN.matcher(value).find()) {
            errors.add(fieldName + " no puede tener 3 o más veces consecutivas la misma letra.");
        }
    }

    public static void validateComboBoxSelection(String selectedValue, String fieldName, List<String> errors) {
        if (selectedValue == null || selectedValue.trim().isEmpty()) {
            errors.add("Debe seleccionar un " + fieldName + ".");
        }
    }

    public static void validateRadioSelection(boolean isSelected, String fieldName, List<String> errors) {
        if (!isSelected) {
            errors.add("Selecciona " + fieldName + ".");
        }
    }

    public static void validateNotEmpty(String value, String fieldName, List<String> errors) {
        if (value.trim().isEmpty()) {
            errors.add("El campo de " + fieldName + " es obligatorio.");
        }
    }

    public static void bindDatePickersToPeriodSelection(ComboBox<SchoolPeriod> periodComboBox, DatePicker startPicker, DatePicker endPicker) {
        startPicker.setDisable(true);
        endPicker.setDisable(true);
        bindStartDateToPeriod(periodComboBox, startPicker);
        bindEndDateToStartDate(periodComboBox, startPicker, endPicker);
    }

    private static void bindStartDateToPeriod(ComboBox<SchoolPeriod> periodComboBox, DatePicker startPicker) {
        periodComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                startPicker.setDayCellFactory(null);
                startPicker.setValue(null);
                startPicker.setDisable(true);
            } else {
                YearMonth firstMonth = newValue.firstMonth();
                startPicker.setDayCellFactory(singleMonthCellFactory(firstMonth));
                startPicker.setDisable(false);
                startPicker.setValue(null);
                startPicker.setValue(firstMonth.atDay(1));
            }
        });
    }

    private static void bindEndDateToStartDate(ComboBox<SchoolPeriod> periodComboBox, DatePicker startPicker, DatePicker endPicker) {
        startPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            SchoolPeriod schoolPeriod = periodComboBox.getValue();
            if (newValue == null || schoolPeriod == null) {
                endPicker.setValue(null);
                endPicker.setDisable(true);
            } else {
                YearMonth lastMonth = schoolPeriod.lastMonth();
                endPicker.setDayCellFactory(singleMonthCellFactory(lastMonth));
                endPicker.setDisable(false);
                if (endPicker.getValue() == null) {
                    endPicker.setValue(lastMonth.atDay(1));
                }
            }
        });
    }

    private static Callback<DatePicker, DateCell> singleMonthCellFactory(YearMonth allowedMonth) {
        return datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setDisable(true);
                    return;
                }
                setDisable(!YearMonth.from(date).equals(allowedMonth));
            }
        };
    }

    public static void showError(String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showSuccess(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showErrors(List<String> errors) {
        if (!errors.isEmpty()) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText("Corrija los siguientes errores:");
            TextArea textArea = new TextArea(String.join("\n", errors));
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setPrefSize(420, 160);
            alert.getDialogPane().setContent(textArea);
            alert.showAndWait();
        }
    }

    public static String sanitizeSearch(String value) {
        String sanitized = "";
        if (value != null) {
            sanitized = SEARCH_FORBIDDEN_PATTERN.matcher(value).replaceAll("").trim().toLowerCase();
        }
        return sanitized;
    }

    public static boolean matchesSearch(String itemText, String sanitizedQuery) {
        boolean matches = false;
        if (itemText != null) {
            matches = itemText.toLowerCase().contains(sanitizedQuery);
        }
        return matches;
    }

    public static void closeWindow(Stage stage) {
        if (stage != null) {
            stage.close();
        }
    }
}
