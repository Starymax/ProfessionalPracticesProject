package mx.fei.logic.dto;

public class Professor extends User{
    private int personalNumber;
    private boolean isCoordinator;
    private boolean isAdmin;
    private String shift;

    public Professor(int userId, String name, String lastName, String email, String password, String gender, boolean active_status, int personalNumber, boolean isCoordinator, boolean isAdmin, String shift) {
        super(userId, name, lastName, email, password, gender, active_status);
        this.personalNumber = personalNumber;
        this.isCoordinator = isCoordinator;
        this.isAdmin = isAdmin;
        this.shift = shift;
    }

    public int getPersonalNumber() {
        return personalNumber;
    }

    public void setPersonalNumber(int personalNumber) {
        this.personalNumber = personalNumber;
    }

    public boolean isCoordinator() {
        return isCoordinator;
    }

    public void setCoordinator(boolean coordinator) {
        this.isCoordinator = coordinator;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        this.isAdmin = admin;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }
}