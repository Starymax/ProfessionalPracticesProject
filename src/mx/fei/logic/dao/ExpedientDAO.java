package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.Document;
import mx.fei.logic.exceptions.DataOperationException;
import mx.fei.logic.idao.IDAOExpedient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExpedientDAO implements IDAOExpedient {
    private Logger logger = Logger.getLogger(ExpedientDAO.class.getName());
    @Override
    public boolean loadDocument(String enrollment, String documentType, boolean loadState) throws DataOperationException {
        boolean loaded = false;
        String queryLoad = "UPDATE expediente_practicas set" + documentType + "= ? where enrollment=?;";
        try (Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(queryLoad)) {
            preparedStatement.setBoolean(1,loadState);
            preparedStatement.setString(2,enrollment);
            loaded = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Error al cargar los documentos");
            throw new DataOperationException("Error al cargar los documentos");
        }
        return loaded;
    }

    @Override
    public boolean isLoaded(String enrollment, String documentType) throws DataOperationException {
        boolean isLoaded = false;
        String queryIsLoaded = "SELECT " + documentType + " FROM expediente_practicas WHERE matricula = ?;";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryIsLoaded)) {
            preparedStatement.setString(1,enrollment);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                isLoaded = resultSet.getBoolean(documentType);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al comprobar los documentos");
            throw new DataOperationException("Error al corroborar si esta cargado el documento");
        }
        return isLoaded;
    }

    @Override
    public boolean uploadDocument(String enrollment, Document document) throws IOException{
    boolean uploaded = false;
    if (!document.getDirectory().isEmpty()) {
        try {
            Path expedientDirectory = Paths.get(System.getProperty("user.home"), "practices/expedients", enrollment);
            Files.createDirectories(expedientDirectory);
            Path targetFilePath = expedientDirectory.resolve(document.getDocumentType().name() + ".pdf");
            Path sourceFilePath = Paths.get(document.getDirectory());
            Files.copy(sourceFilePath, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
            uploaded = true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al subir el documento", e);
            throw new IOException("Error al subir el documento");
        }
    }
    return uploaded;
    }
}
