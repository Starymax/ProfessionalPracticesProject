package mx.fei.beans;

public class ProjectManager {
    private int projectManagerId;
    private String nameProjectManager;
    private String emailProjectManager;
    private String phoneNumberProjectManager;
    private String rol;
    private Project project;

    public ProjectManager(int projectManagerId, String nameProjectManager, String emailProjectManager, String phoneNumberProjectManager, String rol, Project project) {
        this.projectManagerId = projectManagerId;
        this.nameProjectManager = nameProjectManager;
        this.emailProjectManager = emailProjectManager;
        this.phoneNumberProjectManager = phoneNumberProjectManager;
        this.rol = rol;
        this.project = project;
    }

    public int getProjectManagerId() {
        return projectManagerId;
    }

    public void setProjectManagerId(int projectManagerId) {
        this.projectManagerId = projectManagerId;
    }

    public String getNameProjectManager() {
        return nameProjectManager;
    }

    public void setNameProjectManager(String nameProjectManager) {
        this.nameProjectManager = nameProjectManager;
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
