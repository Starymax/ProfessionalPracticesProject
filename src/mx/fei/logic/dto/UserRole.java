package mx.fei.logic.dto;

public enum UserRole {
    ADMINISTRATOR("admin"),
    DEFAULT("default"),
    COORDINATOR("coordinator"),
    PROFESSOR("professor"),
    STUDENT("student");

    private final String propertiesKey;

    UserRole(String propertiesKey) {
        this.propertiesKey = propertiesKey;
    }

    public String getPropertiesKey() {
        return propertiesKey;
    }
}