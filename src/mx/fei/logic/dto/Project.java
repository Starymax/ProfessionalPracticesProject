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
    private String resources;
    private Date startDate;
    private Date finalDate;
    private boolean activeStatus;
    private int availablePlaces;
    private Enterprise enterprise;

    public Project(int projectId, String nameProject, String descriptionProject, String generalObjective, String mediatesObjectives, String immediateObjectives, String methodology, String resources, Date startDate, Date finalDate, boolean activeStatus, int available_places, Enterprise enterprise) {
        this.id = projectId;
        this.nameProject = nameProject;
        this.descriptionProject = descriptionProject;
        this.generalObjective = generalObjective;
        this.mediatesObjectives = mediatesObjectives;
        this.immediateObjectives = immediateObjectives;
        this.methodology = methodology;
        this.resources = resources;
        this.startDate = startDate;
        this.finalDate = finalDate;
        this.activeStatus = activeStatus;
        this.availablePlaces = available_places;
        this.enterprise = enterprise;
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

    public void setAvailablePlaces(int available_places) {
        this.availablePlaces = available_places;
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
}
