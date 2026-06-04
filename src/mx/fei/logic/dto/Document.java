package mx.fei.logic.dto;

public class Document {
    private int id;
    private String name;
    private String directory;
    private DocumentType documentType;
    private Practice practice;
    private boolean accepted;

    public Document(String name, String directory, DocumentType documentType, Practice practice) {
        this.name = name;
        this.directory = directory;
        this.documentType = documentType;
        this.practice = practice;
    }

    public Document(String name, String directory, DocumentType documentType) {
        this.name = name;
        this.directory = directory;
        this.documentType = documentType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Practice getPractice() {
        return practice;
    }

    public void setPractice(Practice practice) {
        this.practice = practice;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}