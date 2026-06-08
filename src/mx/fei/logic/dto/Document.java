package mx.fei.logic.dto;

import java.util.Objects;

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
    @Override
    public boolean equals(Object object) {
        boolean isEqual = false;
        if (this == object) {
            isEqual = true;
        } else if (object != null && getClass() == object.getClass()) {
            Document that = (Document) object;
            isEqual = id == that.id
                    && accepted == that.accepted
                    && Objects.equals(name, that.name)
                    && Objects.equals(directory, that.directory)
                    && documentType == that.documentType
                    && Objects.equals(practice, that.practice);
        }
        return isEqual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, directory, documentType, accepted, practice);
    }
}