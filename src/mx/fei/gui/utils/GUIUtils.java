package mx.fei.gui.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.regex.Pattern;

public class GUIUtils {
    public static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}\\s]{3,50}$");
    public static final Pattern LONG_TEXT_PATTERN = Pattern.compile("^[\\p{L}\\d\\s.,¡!¿?()%\\-]{1,65535}$");
    public static final Pattern SHORT_TEXT_PATTERN = Pattern.compile("^[\\p{L}\\d\\s.,¡!¿?()%\\-]{1,100}$");
    public static final Pattern REPETITION_PATTERN = Pattern.compile("(\\p{L})\\1{3,}", Pattern.CASE_INSENSITIVE);
    public static final Pattern CONTAINS_NUMBERS_PATTERN = Pattern.compile("\\d");
    public static final Pattern CONTAINS_VOWELS_PATTERN = Pattern.compile("[aeiouáéíóúüAEIOUÁÉÍÓÚÜ]", Pattern.CASE_INSENSITIVE);
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    public static final Pattern PERIOD_PATTERN = Pattern.compile("^(19|20)\\d{2}-(0[1-9]|1[0-2])$");
    public static final Pattern NUMERIC_PATTERN = Pattern.compile("^\\d+$");
    public static final Pattern ENROLLMENT_PATTERN = Pattern.compile("^\\d{6,}$");

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
        } else if (email.length() > 100) {
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
                if (grade < 0.0f || grade > 10.0f) {
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
        } else if (value.length() < 3) {
            errors.add(fieldName + " debe tener al menos 3 dígitos.");
        } else if (value.length() > 10) {
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
        } else if (value.length() != 10) {
            errors.add(fieldName + " debe tener 10 dígitos.");
        }
    }

    public static void validateLong(String value, String fieldName, List<String> errors) {
        if (value.isEmpty()) {
            errors.add("El campo de " + fieldName + " es obligatorio.");
        } else if (!NUMERIC_PATTERN.matcher(value).matches()) {
            errors.add(fieldName + " debe contener solo números.");
        } else if (value.length() > 15) {
            errors.add(fieldName + " no puede ser un valor tan grande.");
        }
    }

    public static void validateShortInt(String value, String fieldName, List<String> errors) {
        if (value.isEmpty()) {
            errors.add("El campo de " + fieldName + " es obligatorio.");
        } else if (!NUMERIC_PATTERN.matcher(value).matches()) {
            errors.add(fieldName + " debe contener solo números.");
        } else if (value.length() > 2) {
            errors.add(fieldName + " no puede ser un valor tan grande.");
        }
    }

    public static void validateInt(String value, String fieldName, List<String> errors) {
        if (value.isEmpty()) {
            errors.add("El campo de " + fieldName + " es obligatorio.");
        } else if (!NUMERIC_PATTERN.matcher(value).matches()) {
            errors.add(fieldName + " debe contener solo números.");
        } else if (value.length() > 9) {
            errors.add(fieldName + " no puede ser un valor tan grande.");
        }
    }

    public static void validateNRC(String value, String fieldName, List<String> errors) {
        if (value.isEmpty()) {
            errors.add("El campo de " + fieldName + " es obligatorio.");
        } else if (!NUMERIC_PATTERN.matcher(value).matches()) {
            errors.add(fieldName + " debe contener solo números.");
        } else if (value.length() != 5) {
            errors.add(fieldName + " debe tener 5 dígitos");
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

    public static void applyPeriodDateRestrictions(DatePicker startPicker, DatePicker endPicker) {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int year = today.getYear();
        YearMonth currentStartMonth, currentEndMonth;
        if (month >= 2 && month <= 7) {
            currentStartMonth = YearMonth.of(year, 2);
            currentEndMonth = YearMonth.of(year, 7);
        } else {
            int augustYear = (month >= 8) ? year : year - 1;
            currentStartMonth = YearMonth.of(augustYear, 8);
            currentEndMonth = YearMonth.of(augustYear + 1, 1);
        }
        final YearMonth nextStartMonth = currentEndMonth.plusMonths(1);
        final YearMonth nextEndMonth = nextStartMonth.plusMonths(5);
        final YearMonth allowedStart1 = currentStartMonth;
        final YearMonth allowedStart2 = nextStartMonth;
        final YearMonth allowedEnd1 = currentEndMonth;
        final YearMonth allowedEnd2 = nextEndMonth;
        startPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                YearMonth dateMonth = YearMonth.from(date);
                setDisable(!dateMonth.equals(allowedStart1) && !dateMonth.equals(allowedStart2));
            }
        });
        endPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                YearMonth dateMonth = YearMonth.from(date);
                setDisable(!dateMonth.equals(allowedEnd1) && !dateMonth.equals(allowedEnd2));
            }
        });
    }

    public static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showErrors(List<String> errors) {
        if (!errors.isEmpty()) {
            String combinedMessage = String.join("\n- ", errors);
            showError("- " + combinedMessage);
        }
    }

    public static void closeWindow(Stage stage) {
        if (stage != null) {
            stage.close();
        }
    }
}
