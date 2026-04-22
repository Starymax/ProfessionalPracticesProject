package mx.fei.logic.idao;

import mx.fei.logic.dto.Document;
import mx.fei.logic.exceptions.DataOperationException;
import mx.fei.logic.exceptions.DataOperationException;

import java.io.IOException;

public interface IDAOExpedient {
    boolean loadDocument(String enrollment, String documentType, boolean loadState) throws DataOperationException, DataOperationException;

    boolean isLoaded(String enrollment, String documentType) throws DataOperationException, DataOperationException;

    boolean uploadDocument(String enrollment, Document document) throws IOException;
}
