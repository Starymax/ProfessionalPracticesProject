package mx.fei.logic.idao;

public interface IDAOExpedient {
    boolean loadDocument(String enrollment, String documentType, boolean loadState);

    boolean isLoaded(String enrollment, String documentType);
}
