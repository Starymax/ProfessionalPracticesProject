package mx.fei.logic.idao;

import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataOperationException;

import java.io.File;
import java.io.IOException;

public interface IDAOExpedient {
    boolean loadDocument(String enrollment, String documentType, boolean loadState) throws DataOperationException;

    boolean isLoaded(String enrollment, String documentType) throws DataOperationException;

    boolean uploadDocument(Student student, String documentType, File sourceFile) throws IOException;
}
