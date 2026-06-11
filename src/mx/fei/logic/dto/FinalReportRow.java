package mx.fei.logic.dto;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FinalReportRow {

    private final StringProperty activity;
    private final StringProperty advance;
    private final StringProperty observation;
    private final StringProperty product;
    private final StringProperty advanceProduct;
    private final StringProperty observationProduct;

    public FinalReportRow(String activity, String advance, String observation, String product, String advanceProduct, String observationProduct) {
        this.activity = new SimpleStringProperty(activity);
        this.advance = new SimpleStringProperty(advance);
        this.observation = new SimpleStringProperty(observation);
        this.product = new SimpleStringProperty(product);
        this.advanceProduct = new SimpleStringProperty(advanceProduct);
        this.observationProduct = new SimpleStringProperty(observationProduct);
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

    public StringProperty advanceProductProperty() {
        return advanceProduct;
    }

    public String getAdvanceProduct() {
        return advanceProduct.get();
    }

    public void setAdvanceProduct(String advanceProduct) {
        this.advanceProduct.set(advanceProduct);
    }

    public StringProperty observationProductProperty() {
        return observationProduct;
    }

    public String getObservationProduct() {
        return observationProduct.get();
    }

    public void setObservationProduct(String observationProduct) {
        this.observationProduct.set(observationProduct);
    }
}
