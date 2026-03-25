package mx.fei.logic.dto;

public class Project {
    private int projectId;
    private String nameProject;
    private String descriptionProject;
    private String generalObjective;
    private String mediatesObjectives;
    private String immediateObjectives;
    private String methodology;
    private String resources;
    private String startDate;
    private String finalDate;
    private String activeStatus;
    private Enterprise enterprise;

    public Project(int projectId, String nameProject, String descriptionProject, String generalObjective, String mediatesObjectives, String immediateObjectives, String methodology, String resources, String startDate, String finalDate, String activeStatus, Enterprise enterprise) {
        this.projectId = projectId;
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
        this.enterprise = enterprise;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getFinalDate() {
        return finalDate;
    }

    public void setFinalDate(String finalDate) {
        this.finalDate = finalDate;
    }

    public String getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(String activeStatus) {
        this.activeStatus = activeStatus;
    }

    public Enterprise getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }
}
