package mx.fei.logic.dto;

import java.util.Objects;

public abstract class User {
    private int id;
    private String name;
    private String lastName;
    private String email;
    private String password;
    private String gender;
    private boolean activeStatus;

    public User(int userId, String name, String lastName, String email, String password, String gender, boolean activeStatus) {
        this.id = userId;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.activeStatus = activeStatus;
    }

    public int getUserId() {
        return id;
    }

    public void setUserId(int userId) {
        this.id = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isActive() {
        return activeStatus;
    }

    public void setActiveStatus(boolean activeStatus) {
        this.activeStatus = activeStatus;
    }

    @Override
    public boolean equals(Object object) {
        boolean isEqual = false;
        if (this == object) {
            isEqual = true;
        } else if (object != null && getClass() == object.getClass()) {
            User that = (User) object;
            isEqual = id == that.id
                    && activeStatus == that.activeStatus
                    && Objects.equals(name, that.name)
                    && Objects.equals(lastName, that.lastName)
                    && Objects.equals(email, that.email)
                    && Objects.equals(password, that.password)
                    && Objects.equals(gender, that.gender);
        }
        return isEqual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, lastName, email, password, gender, activeStatus);
    }
}
