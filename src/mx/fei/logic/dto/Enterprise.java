package mx.fei.logic.dto;

public class Enterprise {
    private int enterpriseId;
    private String name;
    private String sector;
    private String phoneNumber;
    private String contactEmail;
    private String address;
    private int directUsers;
    private int indirectUsers;
    private boolean activeStatus;

    public Enterprise(int enterpriseId, String name, String sector, String phoneNumber, String contactEmail, String address, int directUsers, int indirectUsers, boolean activeStatus) {
        this.enterpriseId = enterpriseId;
        this.name = name;
        this.sector = sector;
        this.phoneNumber = phoneNumber;
        this.contactEmail = contactEmail;
        this.address = address;
        this.directUsers = directUsers;
        this.indirectUsers = indirectUsers;
        this.activeStatus = activeStatus;
    }

    public int getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(int enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getDirectUsers() {
        return directUsers;
    }

    public void setDirectUsers(int directUsers) {
        this.directUsers = directUsers;
    }

    public int getIndirectUsers() {
        return indirectUsers;
    }

    public void setIndirectUsers(int indirectUsers) {
        this.indirectUsers = indirectUsers;
    }

    public boolean isActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(boolean activeStatus) {
        this.activeStatus = activeStatus;
    }
}
