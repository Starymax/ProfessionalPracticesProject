package mx.fei.logic.idao;

import mx.fei.logic.dto.Document;
import mx.fei.logic.dto.Practice;
import mx.fei.logic.exceptions.DataOperationException;

import javax.print.Doc;
import java.io.IOException;

public interface IDAODocument {
    boolean createExpedient(int studentId, String period) throws DataOperationException;

    int loadDocument(Practice practice, Document document) throws DataOperationException;

    boolean isLoaded(String enrollment, String documentType) throws DataOperationException;

    boolean uploadDocument(String enrollment, Document document) throws IOException;
}
