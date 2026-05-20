package mx.fei.logic.dto;

public class Expedient {
    private int id;
    private boolean liberationLetter;
    private boolean acceptationOffice;
    private boolean workPlan;
    private boolean schelude;
    private boolean competenceEvaluation;
    private String period;
    private Student student;

    public Expedient(int expedientId, boolean liberationLetter, boolean acceptationOffice, boolean workPlan, boolean schelude, boolean competenceEvaluation, String period, Student student) {
        this.id = expedientId;
        this.liberationLetter = liberationLetter;
        this.acceptationOffice = acceptationOffice;
        this.workPlan = workPlan;
        this.schelude = schelude;
        this.competenceEvaluation = competenceEvaluation;
        this.period = period;
        this.student = student;
    }

    public int getExpedientId() {
        return id;
    }

    public void setExpedientId(int expedientId) {
        this.id = expedientId;
    }

    public boolean isLiberationLetter() {
        return liberationLetter;
    }

    public void setLiberationLetter(boolean liberationLetter) {
        this.liberationLetter = liberationLetter;
    }

    public boolean isAcceptationOffice() {
        return acceptationOffice;
    }

    public void setAcceptationOffice(boolean acceptationOffice) {
        this.acceptationOffice = acceptationOffice;
    }

    public boolean isWorkPlan() {
        return workPlan;
    }

    public void setWorkPlan(boolean workPlan) {
        this.workPlan = workPlan;
    }

    public boolean isSchelude() {
        return schelude;
    }

    public void setSchelude(boolean schelude) {
        this.schelude = schelude;
    }

    public boolean isCompetenceEvaluation() {
        return competenceEvaluation;
    }

    public void setCompetenceEvaluation(boolean competenceEvaluation) {
        this.competenceEvaluation = competenceEvaluation;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
