package mx.fei.logic.dto;

public enum PracticeStatus {
    EN_CURSO("En curso"),
    CONCLUIDA("Concluida"),
    APROBADO("Aprobados"),
    REPROBADO("Reprobados");

    private final String label;

    PracticeStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static PracticeStatus fromLabel(String label) {
        PracticeStatus result = null;
        for (PracticeStatus status : values()) {
            if (status.label.equals(label)) {
                result = status;
                break;
            }
        }
        return result;
    }
}
