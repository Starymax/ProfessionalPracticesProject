package mx.fei.logic.dto;

public enum RegistrationStatus {
    SUCCES(1),
    FAILURE(-1);
    private int value;

    public int getValue() {
        return value;
    }

    private RegistrationStatus(int value) {
        this.value = value;
    }
}
