package mx.fei.logic.dto;

import java.util.Objects;

public class StudentValidationSummary {
    private final Student student;
    private final int pendingDocumentCount;
    private final int totalDocumentCount;

    public StudentValidationSummary(Student student, int pendingDocumentCount, int totalDocumentCount) {
        this.student = student;
        this.pendingDocumentCount = pendingDocumentCount;
        this.totalDocumentCount = totalDocumentCount;
    }

    public Student getStudent() {
        return student;
    }

    public int getPendingDocumentCount() {
        return pendingDocumentCount;
    }

    public int getTotalDocumentCount() {
        return totalDocumentCount;
    }

    @Override
    public boolean equals(Object object) {
        boolean isEqual = false;
        if (this == object) {
            isEqual = true;
        } else if (object != null && getClass() == object.getClass()) {
            StudentValidationSummary that = (StudentValidationSummary) object;
            isEqual = pendingDocumentCount == that.pendingDocumentCount
                    && totalDocumentCount == that.totalDocumentCount
                    && Objects.equals(student, that.student);
        }
        return isEqual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(student, pendingDocumentCount, totalDocumentCount);
    }
}
