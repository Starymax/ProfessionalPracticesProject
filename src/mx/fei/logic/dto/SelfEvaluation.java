package mx.fei.logic.dto;

public class SelfEvaluation {
    private int selfEvaluationId;
    private String results;
    private Expedient expedient;

    public SelfEvaluation(int selfEvaluationId, String results, Expedient expedient) {
        this.selfEvaluationId = selfEvaluationId;
        this.results = results;
        this.expedient = expedient;
    }

    public int getSelfEvaluationId() {
        return selfEvaluationId;
    }

    public void setSelfEvaluationId(int selfEvaluationId) {
        this.selfEvaluationId = selfEvaluationId;
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