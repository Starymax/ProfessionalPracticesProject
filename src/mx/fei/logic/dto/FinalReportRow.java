package mx.fei.logic.dto;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FinalReportRow {

    private final StringProperty activity;
    private final StringProperty advance;
    private final StringProperty observation;
    private final StringProperty product;
    private final StringProperty advancep;
    private final StringProperty observationp;

    public FinalReportRow(String activity, String advance, String observation, String product, String advancep, String observationp) {
        this.activity = new SimpleStringProperty(activity);
        this.advance = new SimpleStringProperty(advance);
        this.observation = new SimpleStringProperty(observation);
        this.product = new SimpleStringProperty(product);
        this.advancep = new SimpleStringProperty(advancep);
        this.observationp = new SimpleStringProperty(observationp);
    }

    public StringProperty activityProperty() {
        return activity;
    }

    public String getActivity() {
        return activity.get();
    }

    public void setActivity(String activity) {
        this.activity.set(activity);
    }

    public StringProperty advanceProperty() {
        return advance;
    }

    public String getAdvance() {
        return advance.get();
    }

    public void setAdvance(String advance) {
        this.advance.set(advance);
    }

    public StringProperty observationProperty() {
        return observation;
    }

    public String getObservation() {
        return observation.get();
    }

    public void setObservation(String observation) {
        this.observation.set(observation);
    }

    public StringProperty productProperty() {
        return product;
    }

    public String getProduct() {
        return product.get();
    }

    public void setProduct(String product) {
        this.product.set(product);
    }

    public StringProperty advancepProperty() {
        return advancep;
    }

    public String getAdvancep() {
        return advancep.get();
    }

    public void setAdvancep(String advancep) {
        this.advancep.set(advancep);
    }

    public StringProperty observationpProperty() {
        return observationp;
    }

    public String getObservationp() {
        return observationp.get();
    }

    public void setObservationp(String observationp) {
        this.observationp.set(observationp);
    }
}
