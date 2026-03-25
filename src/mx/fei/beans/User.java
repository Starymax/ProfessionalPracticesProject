package mx.fei.beans;

public class User {
    private int UserId;
    private String name;
    private String lastName;
    private String email;
    private String password;
    private String gender;
    private boolean activeStatus;

    public User(int userId, String name, String lastName, String email, String password, String gender, boolean activeStatus) {
        UserId = userId;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.activeStatus = activeStatus;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
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
}
