package mx.fei.logic.dto;

public enum ValidationStatus {
    PENDING("PENDIENTE"),
    VALIDATED("VALIDADO"),
    REJECTED("RECHAZADO"),
    NOT_UPLOADED("NO_SUBIDO");

    private final String validationValue;

    ValidationStatus(String validationValue) {
        this.validationValue = validationValue;
    }

    public String getValidationValue() {
        return validationValue;
    }

    public static ValidationStatus fromValidationValue(String value) {
        ValidationStatus result = PENDING;
        if (value != null) {
            for (ValidationStatus status : values()) {
                if (status.validationValue.equalsIgnoreCase(value)) {
                    result = status;
                    break;
                }
            }
        }
        return result;
    }
}
