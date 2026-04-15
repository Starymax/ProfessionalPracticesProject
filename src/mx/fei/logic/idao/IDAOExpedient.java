package mx.fei.logic.idao;

import mx.fei.logic.exceptions.DataBaseConnectionException;

import java.io.File;
import java.io.IOException;

public interface IDAOExpedient {
    boolean loadDocument(String enrollment, String documentType, boolean loadState) throws DataBaseConnectionException;

    boolean isLoaded(String enrollment, String documentType) throws DataBaseConnectionException;

    boolean uploadDocument(String enrollment, String documentType, File sourceFile) throws IOException;
}
