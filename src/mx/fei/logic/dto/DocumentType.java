package mx.fei.logic.dto;

public enum DocumentType {
    SELF_EVALUATION ("autoevaluacion"),
    LETTER_OF_RELEASE ("cartaDeLiberacion"),
    ACCEPTANCE_LETTER ("cartaDeceptacion"),
    WORK_PLAN ("planDeTrabajo"),
    STUDENT_SCHEDULE ("horario"),
    COMPETENCE_EVALUATION ("evaluacionDeCompetencias"),
    PARTIAL_REPORT ("reporteParcial"),
    MONTHLY_REPORT ("reporteMensual"),
    FINAL_REPORT ("reporteFinal");
    private String documentType;
    private DocumentType(String documentName) {
        this.documentType = documentName;
    }

    public String getDocumentType() {
        return documentType;
    }
}
