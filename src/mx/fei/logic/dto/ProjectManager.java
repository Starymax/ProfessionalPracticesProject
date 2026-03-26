package mx.fei.logic.dto;

public class ProjectManager {
    private int id;
    private String name;
    private String emailProjectManager;
    private String phoneNumberProjectManager;
    private String rol;
    private Project project;

    public ProjectManager(int projectManagerId, String name, String emailProjectManager, String phoneNumberProjectManager, String rol, Project project) {
        this.id = projectManagerId;
        this.name = name;
        this.emailProjectManager = emailProjectManager;
        this.phoneNumberProjectManager = phoneNumberProjectManager;
        this.rol = rol;
        this.project = project;
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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
