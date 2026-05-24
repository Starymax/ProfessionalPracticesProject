package mx.fei.logic.idao;

import mx.fei.logic.dto.Document;
import mx.fei.logic.dto.Practice;
import mx.fei.logic.exceptions.DataOperationException;

import javax.print.Doc;
import java.io.IOException;
import java.util.List;

public interface IDAODocument {
    boolean createExpedient(int studentId, String period) throws DataOperationException;

    int loadDocument(Practice practice, Document document) throws DataOperationException;

    boolean isLoaded(String enrollment, String documentType) throws DataOperationException;

    String uploadDocument(String enrollment, Document document) throws IOException;

    List<Document> getDocumentsByPractice(Practice practice) throws DataOperationException;
}
