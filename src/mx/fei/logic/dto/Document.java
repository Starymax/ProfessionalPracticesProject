package mx.fei.logic.dto;

public class Document {
    private String fileName;
    private String directory;
    private DocumentType documentType;

    public Document(String fileName, String directory, DocumentType documentType) {
        this.fileName = fileName;
        this.directory = directory;
        this.documentType = documentType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }
}
