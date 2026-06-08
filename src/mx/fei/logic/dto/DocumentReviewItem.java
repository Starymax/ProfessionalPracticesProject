package mx.fei.logic.dto;

public class DocumentReviewItem {
    private final DocumentType documentType;
    private final Document document;

    public DocumentReviewItem(DocumentType documentType, Document document) {
        this.documentType = documentType;
        this.document = document;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public Document getDocument() {
        return document;
    }

    public ValidationStatus getStatus() {
        return document == null ? ValidationStatus.NOT_UPLOADED : document.getValidationStatus();
    }

    public boolean isUploaded() {
        return document != null;
    }
}
