package mx.fei.logic.dto;

import java.util.Date;
import java.util.Objects;

public class Notification {

    private int notificationId;
    private String title;
    private String message;
    private Date emissionDate;
    private boolean read;
    private Student student;

    public Notification(int notificationId, String title, String message, Date emissionDate, boolean read, Student student) {
        this.notificationId = notificationId;
        this.title = title;
        this.message = message;
        this.emissionDate = emissionDate;
        this.read = read;
        this.student = student;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public Date getEmissionDate() {
        return emissionDate;
    }

    public boolean isRead() {
        return read;
    }

    public Student getStudent() {
        return student;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    @Override
    public boolean equals(Object object) {
        boolean isEqual = false;
        if (this == object) {
            isEqual = true;
        } else if (object != null && getClass() == object.getClass()) {
            Notification that = (Notification) object;
            isEqual = notificationId == that.notificationId
                    && read == that.read
                    && Objects.equals(title, that.title)
                    && Objects.equals(message, that.message)
                    && Objects.equals(emissionDate, that.emissionDate);
        }
        return isEqual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(notificationId, title, message, emissionDate, read);
    }
}