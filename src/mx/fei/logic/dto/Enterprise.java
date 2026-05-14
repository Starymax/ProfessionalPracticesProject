package mx.fei.logic.dto;

public class Enterprise {
    private int id;
    private String name;
    private String sector;
    private String phoneNumber;
    private String contactEmail;
    private String city;
    private int directUsers;
    private int indirectUsers;
    private boolean activeStatus;
    private String country;

    public Enterprise(int enterpriseId, String name, String sector, String phoneNumber, String contactEmail, String city, int directUsers, int indirectUsers, boolean activeStatus, String country) {
        this.id = enterpriseId;
        this.name = name;
        this.sector = sector;
        this.phoneNumber = phoneNumber;
        this.contactEmail = contactEmail;
        this.city = city;
        this.directUsers = directUsers;
        this.indirectUsers = indirectUsers;
        this.activeStatus = activeStatus;
        this.country = country;
    }

    public int getEnterpriseId() {
        return id;
    }

    public void setEnterpriseId(int enterpriseId) {
        this.id = enterpriseId;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
