package mx.fei.logic.dto;

import java.sql.Date;

public class Project {
    private int id;
    private String nameProject;
    private String descriptionProject;
    private String generalObjective;
    private String mediatesObjectives;
    private String immediateObjectives;
    private String methodology;
    private String responsibilities;
    private String resources;
    private Date startDate;
    private Date finalDate;
    private boolean activeStatus;
    private int availablePlaces;
    private Enterprise enterprise;
    private ProjectManager projectManager;

    public Project(int projectId, String nameProject, String descriptionProject, String generalObjective, String mediatesObjectives, String immediateObjectives, String methodology, String responsibilities, String resources, Date startDate, Date finalDate, boolean activeStatus, int availablePlaces, Enterprise enterprise, ProjectManager projectManager) {
        this.id = projectId;
        this.nameProject = nameProject;
        this.descriptionProject = descriptionProject;
        this.generalObjective = generalObjective;
        this.mediatesObjectives = mediatesObjectives;
        this.immediateObjectives = immediateObjectives;
        this.methodology = methodology;
        this.responsibilities = responsibilities;
        this.resources = resources;
        this.startDate = startDate;
        this.finalDate = finalDate;
        this.activeStatus = activeStatus;
        this.availablePlaces = availablePlaces;
        this.enterprise = enterprise;
        this.projectManager = projectManager;
    }

    public Project(int id, String nameProject) {
        this.id = id;
        this.nameProject = nameProject;
    }

    public Project(int id, String nameProject, int availablePlaces) {
        this.id = id;
        this.nameProject = nameProject;
        this.availablePlaces = availablePlaces;
    }

    public int getProjectId() {
        return id;
    }

    public void setProjectId(int projectId) {
        this.id = projectId;
    }

    public String getNameProject() {
        return nameProject;
    }

    public void setNameProject(String nameProject) {
        this.nameProject = nameProject;
    }

    public String getDescriptionProject() {
        return descriptionProject;
    }

    public void setDescriptionProject(String descriptionProject) {
        this.descriptionProject = descriptionProject;
    }

    public String getGeneralObjective() {
        return generalObjective;
    }

    public void setGeneralObjective(String generalObjective) {
        this.generalObjective = generalObjective;
    }

    public String getMediatesObjectives() {
        return mediatesObjectives;
    }

    public void setMediatesObjectives(String mediatesObjectives) {
        this.mediatesObjectives = mediatesObjectives;
    }

    public String getImmediateObjectives() {
        return immediateObjectives;
    }

    public void setImmediateObjectives(String immediateObjectives) {
        this.immediateObjectives = immediateObjectives;
    }

    public String getMethodology() {
        return methodology;
    }

    public void setMethodology(String methodology) {
        this.methodology = methodology;
    }

    public String getResponsibilities() {
        return responsibilities;
    }

    public void setResponsibilities(String responsibilities) {
        this.responsibilities = responsibilities;
    }

    public String getResources() {
        return resources;
    }

    public void setResources(String resources) {
        this.resources = resources;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getFinalDate() {
        return finalDate;
    }

    public void setFinalDate(Date finalDate) {
        this.finalDate = finalDate;
    }

    public int getAvailablePlaces() {
        return availablePlaces;
    }

    public void setAvailablePlaces(int availablePlaces) {
        this.availablePlaces = availablePlaces;
    }

    public boolean getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(boolean activeStatus) {
        this.activeStatus = activeStatus;
    }

    public Enterprise getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }

    public ProjectManager getProjectManager() {
        return projectManager;
    }

    public void setProjectManager(ProjectManager projectManager) {
        this.projectManager = projectManager;
    }
}
