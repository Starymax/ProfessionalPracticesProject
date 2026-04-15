package mx.fei.logic.idao;

import java.io.File;
import java.io.IOException;

public interface IDAOExpedient {
    boolean loadDocument(String enrollment, String documentType, boolean loadState);

    boolean isLoaded(String enrollment, String documentType);

    boolean uploadDocument(String enrollment, String documentType, File sourceFile) throws IOException;
}
