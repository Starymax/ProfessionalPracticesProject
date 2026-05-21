package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.Document;
import mx.fei.logic.dto.Practice;
import mx.fei.logic.dto.RegistrationStatus;
import mx.fei.logic.exceptions.DataOperationException;
import mx.fei.logic.idao.IDAODocument;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DocumentDAO implements IDAODocument {
    private Logger logger = Logger.getLogger(DocumentDAO.class.getName());

    @Override
    public boolean createExpedient(int studentId, String period) throws DataOperationException {
        if (period == null || period.isBlank()) {
            logger.log(Level.WARNING, "El periodo esta vacio");
            throw new IllegalArgumentException("El periodo no puede estar vacio");
        }
        boolean result = false;
        String query = "INSERT INTO expediente_practicas (carta_liberacion, oficio_aceptacion, plan_trabajo, horario, evaluacion_competencias, id_alumno, periodo) VALUES (FALSE, FALSE, FALSE, FALSE, FALSE, ?, ?)";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, studentId);
            preparedStatement.setString(2, period);
            preparedStatement.executeUpdate();
            result = true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al crear el expediente", e);
            throw new DataOperationException("Error al crear el expediente");
        }
        return result;
    }

    public String getPeriodByStudentEnrollment(String enrollment) throws DataOperationException {
        if (enrollment == null || enrollment.isBlank()) {
            logger.log(Level.WARNING, "La matricula esta vacia");
            throw new IllegalArgumentException("La matricula no puede estar vacia");
        }
        String period = null;
        String query = "SELECT periodo FROM vw_expediente_por_matricula WHERE matricula = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, enrollment);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                period = resultSet.getString("periodo");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener el periodo del expediente", e);
            throw new DataOperationException("Error al obtener el periodo del expediente");
        }
        return period;
    }

    public String getCurrentPeriod() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        return LocalDate.now().format(formatter);
    }

    @Override
    public int loadDocument(Practice practice, Document document) throws DataOperationException {
        int generatedID = RegistrationStatus.FAILURE.getValue();
        String query = "INSERT INTO documentos (nombre, ruta, tipoDocumento, id_practica) VALUES ( ?, ?, ?, ?);";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, document.getName());
            preparedStatement.setString(2, document.getDirectory());
            preparedStatement.setString(3, document.getDocumentType().getDocumentType());
            preparedStatement.setInt(4, practice.getId());
            preparedStatement.executeUpdate();
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    generatedID = resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Error al cargar los documentos", e);
            throw new DataOperationException("Error al cargar los documentos");
        }
        return generatedID;
    }

    @Override
    public boolean isLoaded(String enrollment, String documentType) throws DataOperationException {
        boolean isLoaded = false;
        String query = "SELECT " + documentType + " FROM vw_expediente_por_matricula WHERE matricula = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
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
