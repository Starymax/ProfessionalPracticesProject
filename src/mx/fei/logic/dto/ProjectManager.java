package mx.fei.logic.dto;

import java.util.Objects;

public class ProjectManager {
    private int id;
    private String name;
    private String emailProjectManager;
    private String phoneNumberProjectManager;
    private String rol;
    private int enterpriseId;

    public ProjectManager(int projectManagerId, String name, String emailProjectManager, String phoneNumberProjectManager, String rol, int enterpriseId) {
        this.id = projectManagerId;
        this.name = name;
        this.emailProjectManager = emailProjectManager;
        this.phoneNumberProjectManager = phoneNumberProjectManager;
        this.rol = rol;
        this.enterpriseId = enterpriseId;
    }

    public ProjectManager() {
    }

    public int getProjectManagerId() {
        return id;
    }

    public void setProjectManagerId(int projectManagerId) {
        this.id = projectManagerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailProjectManager() {
        return emailProjectManager;
    }

    public void setEmailProjectManager(String emailProjectManager) {
        this.emailProjectManager = emailProjectManager;
    }

    public String getPhoneNumberProjectManager() {
        return phoneNumberProjectManager;
    }

    public void setPhoneNumberProjectManager(String phoneNumberProjectManager) {
        this.phoneNumberProjectManager = phoneNumberProjectManager;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public int getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(int enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object object) {
        boolean isEqual = false;
        if (this == object) {
            isEqual = true;
        } else if (object != null && getClass() == object.getClass()) {
            ProjectManager that = (ProjectManager) object;
            isEqual = id == that.id
                    && enterpriseId == that.enterpriseId
                    && Objects.equals(name, that.name)
                    && Objects.equals(emailProjectManager, that.emailProjectManager)
                    && Objects.equals(phoneNumberProjectManager, that.phoneNumberProjectManager)
                    && Objects.equals(rol, that.rol);
        }
        return isEqual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, emailProjectManager, phoneNumberProjectManager, rol, enterpriseId);
    }
}
