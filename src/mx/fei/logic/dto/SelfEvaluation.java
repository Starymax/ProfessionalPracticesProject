package mx.fei.logic.dto;

public class SelfEvaluation {
    private int id;
    private String results;
    private Expedient expedient;

    public SelfEvaluation(int selfEvaluationId, String results, Expedient expedient) {
        this.id = selfEvaluationId;
        this.results = results;
        this.expedient = expedient;
    }

    public int getSelfEvaluationId() {
        return id;
    }

    public void setSelfEvaluationId(int selfEvaluationId) {
        this.id = selfEvaluationId;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public Expedient getExpedient() {
        return expedient;
    }

    public void setExpedient(Expedient expedient) {
        this.expedient = expedient;
    }
}