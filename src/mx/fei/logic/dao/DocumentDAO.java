package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.Document;
import mx.fei.logic.dto.DocumentType;
import mx.fei.logic.dto.Practice;
import mx.fei.logic.dto.RegistrationStatus;
import mx.fei.logic.dto.Student;
import mx.fei.logic.dto.StudentValidationSummary;
import mx.fei.logic.dto.ValidationStatus;
import mx.fei.logic.exceptions.DataOperationException;
import mx.fei.logic.idao.IDAODocument;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DocumentDAO implements IDAODocument {
    private static final Logger logger = Logger.getLogger(DocumentDAO.class.getName());

    @Override
    public boolean createExpedient(int studentId, String period) throws DataOperationException {
        if (period == null || period.isBlank()) {
            logger.log(Level.WARNING, "El periodo esta vacio");
            throw new IllegalArgumentException("El periodo no puede estar vacio");
        }
        boolean result = false;
        String query = "INSERT INTO expediente_practicas (carta_liberacion, oficio_aceptacion, plan_trabajo, horario, evaluacion_competencias, id_alumno, periodo) VALUES (FALSE, FALSE, FALSE, FALSE, FALSE, ?, ?)";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
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
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, enrollment);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    period = resultSet.getString("periodo");
                }
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
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, document.getName());
            preparedStatement.setString(2, document.getDirectory());
            preparedStatement.setString(3, document.getDocumentType().name());
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
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1,enrollment);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    isLoaded = resultSet.getBoolean(documentType);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al comprobar los documentos");
            throw new DataOperationException("Error al corroborar si esta cargado el documento");
        }
        return isLoaded;
    }

    @Override
    public String uploadDocument(String enrollment, Document document) throws IOException{
    String targetPath = null;
    if (document.getDirectory() == null || document.getDirectory().isEmpty()) {
        logger.log(Level.WARNING, "La ruta del documento esta vacia");
        throw new IllegalArgumentException("La ruta del documento no puede estar vacia");
    }
        try {
            Path expedientDirectory = Paths.get(System.getProperty("user.home"), "practices/expedients", enrollment);
            Files.createDirectories(expedientDirectory);
            Path targetFilePath = expedientDirectory.resolve(document.getDocumentType().name() + ".pdf");
            Path sourceFilePath = Paths.get(document.getDirectory());
            Files.copy(sourceFilePath, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
            targetPath = targetFilePath.toString();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al subir el documento", e);
            throw new IOException("Error al subir el documento");
        }
    return targetPath;
    }

    @Override
    public List<Document> getDocumentsByPractice(Practice practice) throws DataOperationException {
        if (practice == null) {
            logger.log(Level.WARNING, "La practica es nula");
            throw new IllegalArgumentException("La practica no puede ser nula");
        }
        List<Document> documents = new ArrayList<>();
        String query = "SELECT id_documento, nombre, ruta, tipoDocumento FROM documentos WHERE id_practica = ?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, practice.getId());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int idDocument = resultSet.getInt("id_documento");
                    String name = resultSet.getString("nombre");
                    String path = resultSet.getString("ruta");
                    String type = resultSet.getString("tipoDocumento");
                    DocumentType documentType = DocumentType.valueOf(type);
                    Document document = new Document(name, path, documentType, practice);
                    document.setId(idDocument);
                    documents.add(document);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener los documentos de la practica", e);
            throw new DataOperationException("Error al obtener los documentos");
        }
        return documents;
    }

    public List<Document> getUploadedReportsByPractice(Practice practice) throws DataOperationException {
        if (practice == null) {
            logger.log(Level.WARNING, "La practica es nula");
            throw new IllegalArgumentException("La practica no puede ser nula");
        }
        List<Document> reports = new ArrayList<>();
        String query = "SELECT id_documento, nombre, ruta, tipoDocumento, estado_validacion FROM documentos WHERE id_practica = ? AND tipoDocumento IN ('PARTIAL_REPORT', 'MONTHLY_REPORT', 'FINAL_REPORT')";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, practice.getId());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int idDocument = resultSet.getInt("id_documento");
                    String name = resultSet.getString("nombre");
                    String path = resultSet.getString("ruta");
                    String type = resultSet.getString("tipoDocumento");
                    ValidationStatus validationStatus = ValidationStatus.fromValidationValue(resultSet.getString("estado_validacion"));
                    DocumentType documentType = DocumentType.valueOf(type);
                    Document document = new Document(name, path, documentType, practice);
                    document.setId(idDocument);
                    document.setValidationStatus(validationStatus);
                    document.setAccepted(validationStatus == ValidationStatus.VALIDATED);
                    reports.add(document);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener los reportes subidos de la practica", e);
            throw new DataOperationException("Error al obtener los reportes subidos");
        }
        return reports;
    }

    public boolean acceptReport(int documentId) throws DataOperationException {
        boolean accepted = false;
        String query = "UPDATE documentos SET estado_validacion = 'VALIDADO' WHERE id_documento = ?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, documentId);
            accepted = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al aceptar el reporte", e);
            throw new DataOperationException("Error al aceptar el reporte");
        }
        return accepted;
    }

    public List<StudentValidationSummary> getStudentsWithUploadedDocuments() throws DataOperationException {
        List<int[]> rawSummaries = new ArrayList<>();
        String query = "SELECT a.id_usuario AS id_alumno, " +
                "COUNT(d.id_documento) AS total_documentos, " +
                "SUM(CASE WHEN d.estado_validacion = 'PENDIENTE' THEN 1 ELSE 0 END) AS documentos_pendientes " +
                "FROM alumno a " +
                "INNER JOIN practicas p ON p.id_alumno = a.id_usuario " +
                "INNER JOIN documentos d ON d.id_practica = p.id_practica " +
                "AND d.tipoDocumento NOT IN ('PARTIAL_REPORT', 'MONTHLY_REPORT', 'FINAL_REPORT') " +
                "WHERE a.proyecto_asignado IS NOT NULL " +
                "GROUP BY a.id_usuario";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int studentId = resultSet.getInt("id_alumno");
                int totalDocuments = resultSet.getInt("total_documentos");
                int pendingDocuments = resultSet.getInt("documentos_pendientes");
                rawSummaries.add(new int[]{studentId, pendingDocuments, totalDocuments});
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener los alumnos con documentos subidos", e);
            throw new DataOperationException("Error al obtener los alumnos con documentos subidos");
        }
        List<StudentValidationSummary> summaries = new ArrayList<>();
        StudentDAO studentDAO = new StudentDAO();
        for (int[] rawSummary : rawSummaries) {
            int studentId = rawSummary[0];
            try {
                Student student = studentDAO.getStudentById(studentId);
                summaries.add(new StudentValidationSummary(student, rawSummary[1], rawSummary[2]));
            } catch (NoSuchElementException e) {
                logger.log(Level.WARNING, "No se encontró el alumno con id: " + studentId);
            }
        }
        return summaries;
    }

    public List<Document> getDocumentsForValidation(Practice practice) throws DataOperationException {
        if (practice == null) {
            logger.log(Level.WARNING, "La practica es nula");
            throw new IllegalArgumentException("La practica no puede ser nula");
        }
        List<Document> documents = new ArrayList<>();
        String query = "SELECT id_documento, nombre, ruta, tipoDocumento, estado_validacion FROM documentos WHERE id_practica = ?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, practice.getId());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int idDocument = resultSet.getInt("id_documento");
                    String name = resultSet.getString("nombre");
                    String path = resultSet.getString("ruta");
                    String type = resultSet.getString("tipoDocumento");
                    DocumentType documentType = DocumentType.valueOf(type);
                    Document document = new Document(name, path, documentType, practice);
                    document.setId(idDocument);
                    document.setValidationStatus(ValidationStatus.fromValidationValue(resultSet.getString("estado_validacion")));
                    documents.add(document);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener los documentos para validar", e);
            throw new DataOperationException("Error al obtener los documentos para validar");
        }
        return documents;
    }

    public boolean validateDocument(int documentId) throws DataOperationException {
        return updateValidationStatus(documentId, ValidationStatus.VALIDATED);
    }

    public boolean areInitialDocumentsUploaded(Practice practice) throws DataOperationException {
        if (practice == null) {
            logger.log(Level.WARNING, "La practica es nula");
            throw new IllegalArgumentException("La practica no puede ser nula");
        }
        boolean uploaded = false;
        String query = "SELECT COUNT(DISTINCT tipoDocumento) AS total FROM documentos " +
                "WHERE id_practica = ? AND tipoDocumento IN ('ACCEPTANCE_LETTER', 'STUDENT_SCHEDULE') " +
                "AND estado_validacion = 'VALIDADO'";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, practice.getId());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    uploaded = resultSet.getInt("total") >= 2;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al verificar los prerrequisitos de los reportes", e);
            throw new DataOperationException("Error al verificar los prerrequisitos de los reportes");
        }
        return uploaded;
    }

    public boolean deleteDocument(Document document) throws DataOperationException {
        if (document == null) {
            logger.log(Level.WARNING, "El documento es nulo");
            throw new IllegalArgumentException("El documento no puede ser nulo");
        }
        boolean deleted = false;
        String query = "DELETE FROM documentos WHERE id_documento = ?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, document.getId());
            deleted = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al eliminar el documento", e);
            throw new DataOperationException("Error al eliminar el documento");
        }
        if (deleted) {
            deletePhysicalFile(document.getDirectory());
        }
        return deleted;
    }

    private void deletePhysicalFile(String path) {
        if (path != null && !path.isBlank()) {
            try {
                Files.deleteIfExists(Paths.get(path));
            } catch (IOException e) {
                logger.log(Level.WARNING, "No se pudo eliminar el archivo físico del documento: " + path, e);
            }
        }
    }

    private boolean updateValidationStatus(int documentId, ValidationStatus status) throws DataOperationException {
        boolean updated = false;
        String query = "UPDATE documentos SET estado_validacion = ? WHERE id_documento = ?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, status.getValidationValue());
            preparedStatement.setInt(2, documentId);
            updated = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al actualizar el estado de validación del documento", e);
            throw new DataOperationException("Error al actualizar el estado de validación del documento");
        }
        return updated;
    }

}
