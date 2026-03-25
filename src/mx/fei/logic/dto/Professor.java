package mx.fei.logic.dto;

public class Professor extends User{
    private int UserId;
    private String name;
    private int personalNumber;
    private boolean idCoordinator;
    private boolean idAdmin;
    private String shift;

    public Professor(int userId, String name, String lastName, String email, String password, String gender, boolean active_status, int userId1, String name1, int personalNumber, boolean idCoordinator, boolean idAdmin, String shift) {
        super(userId, name, lastName, email, password, gender, active_status);
        UserId = userId1;
        this.name = name1;
        this.personalNumber = personalNumber;
        this.idCoordinator = idCoordinator;
        this.idAdmin = idAdmin;
        this.shift = shift;
    }

    public int getPersonalNumber() {
        return personalNumber;
    }

    public void setPersonalNumber(int personalNumber) {
        this.personalNumber = personalNumber;
    }

    public boolean isIdCoordinator() {
        return idCoordinator;
    }

    public void setIdCoordinator(boolean idCoordinator) {
        this.idCoordinator = idCoordinator;
    }

    public boolean isIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(boolean idAdmin) {
        this.idAdmin = idAdmin;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }
}