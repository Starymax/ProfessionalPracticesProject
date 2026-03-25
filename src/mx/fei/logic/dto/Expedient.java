package mx.fei.logic.dto;

public class Expedient {
    private int expedientId;
    private boolean liberationLetter;
    private boolean acceptationOffice;
    private boolean workPlan;
    private boolean schelude;
    private boolean competenceEvaluation;
    private Student student;

    public Expedient(int expedientId, boolean liberationLetter, boolean acceptationOffice, boolean workPlan, boolean schelude, boolean competenceEvaluation, Student student) {
        this.expedientId = expedientId;
        this.liberationLetter = liberationLetter;
        this.acceptationOffice = acceptationOffice;
        this.workPlan = workPlan;
        this.schelude = schelude;
        this.competenceEvaluation = competenceEvaluation;
        this.student = student;
    }

    public int getExpedientId() {
        return expedientId;
    }

    public void setExpedientId(int expedientId) {
        this.expedientId = expedientId;
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

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
