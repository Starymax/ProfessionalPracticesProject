package mx.fei.logic.dto;

public enum DocumentType {
    SELF_EVALUATION ("autoevaluacion"),
    LETTER_OF_RELEASE ("cartaDeLiberacion"),
    ACCEPTANCE_LETTER ("cartaDeAceptacion"),
    WORK_PLAN ("planDeTrabajo"),
    STUDENT_SCHEDULE ("horario"),
    COMPETENCE_EVALUATION ("evaluacionDeCompetencias"),
    PARTIAL_REPORT ("reporteParcial"),
    MONTHLY_REPORT ("reporteMensual"),
    FINAL_REPORT ("reporteFinal");
    private final String documentType;

    public String getDocumentType() {
        return documentType;
    }

    public boolean isReport() {
        return this == PARTIAL_REPORT || this == MONTHLY_REPORT || this == FINAL_REPORT;
    }

    private DocumentType(String documentName) {
        this.documentType = documentName;
    }
}
