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

/**
 * Data Access Object for Document and the student practice expedient.
 * Provides persistence and retrieval operations on the documentos and
 * expediente_practicas tables, as well as physical file handling for uploaded documents.
 */
public class DocumentDAO implements IDAODocument {
    private static final Logger LOGGER = Logger.getLogger(DocumentDAO.class.getName());

    /**
     * Creates an empty practice expedient for a student in the given period.
     *
     * @param studentId the student's identifier
     * @param period the period for the expedient, must not be null or blank
     * @return true if the expedient was created successfully
     * @throws IllegalArgumentException if period is null or blank
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public boolean createExpedient(int studentId, String period) throws DataOperationException {
        if (period == null || period.isBlank()) {
            LOGGER.log(Level.WARNING, "El periodo esta vacio");
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
            LOGGER.log(Level.SEVERE, "Error al crear el expediente", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al crear el expediente");
        }
        return result;
    }

    /**
     * Retrieves the expedient period for a student identified by enrollment.
     *
     * @param enrollment the student's enrollment, must not be null or blank
     * @return the expedient period, or null if none was found
     * @throws IllegalArgumentException if enrollment is null or blank
     * @throws DataOperationException if a database error occurs
     */
    public String getPeriodByStudentEnrollment(String enrollment) throws DataOperationException {
        if (enrollment == null || enrollment.isBlank()) {
            LOGGER.log(Level.WARNING, "La matricula esta vacia");
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
            LOGGER.log(Level.SEVERE, "Error al obtener el periodo del expediente", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener el periodo del expediente");
        }
        return period;
    }

    /**
     * Returns the current period formatted as yyyy-MM.
     *
     * @return the current period based on the system date
     */
    public String getCurrentPeriod() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        return LocalDate.now().format(formatter);
    }

    /**
     * Persists a document record associated with a practice.
     *
     * @param practice the practice the document belongs to
     * @param document the document metadata to store
     * @return the generated document identifier, or RegistrationStatus.FAILURE value if none was generated
     * @throws DataOperationException if a database error occurs
     */
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
            LOGGER.log(Level.SEVERE,"Error al cargar los documentos", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al cargar los documentos");
        }
        return generatedID;
    }

    /**
     * Checks whether a given document type has been loaded in a student's expedient.
     *
     * @param enrollment the student's enrollment
     * @param documentType the expedient column name representing the document type
     * @return true if the document type is marked as loaded
     * @throws DataOperationException if a database error occurs
     */
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
            LOGGER.log(Level.SEVERE, "Error al comprobar los documentos", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al corroborar si esta cargado el documento");
        }
        return isLoaded;
    }

    /**
     * Copies the document's source file into the student's expedient directory on disk.
     *
     * @param enrollment the student's enrollment, used as the target subdirectory
     * @param document the document whose source file is copied, its directory must not be empty
     * @return the absolute path of the stored file
     * @throws IllegalArgumentException if the document's directory is null or empty
     * @throws IOException if the file cannot be copied
     */
    @Override
    public String uploadDocument(String enrollment, Document document) throws IOException{
    String targetPath = null;
    if (document.getDirectory() == null || document.getDirectory().isEmpty()) {
        LOGGER.log(Level.WARNING, "La ruta del documento esta vacia");
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
            LOGGER.log(Level.SEVERE, "Error al subir el documento", e);
            throw new IOException("Error al subir el documento");
        }
    return targetPath;
    }

    /**
     * Retrieves all documents associated with a practice.
     *
     * @param practice the practice whose documents are requested, must not be null
     * @return a list of documents for the practice, empty if there are none
     * @throws IllegalArgumentException if practice is null
     * @throws DataOperationException if a database error occurs
     */
    @Override
    public List<Document> getDocumentsByPractice(Practice practice) throws DataOperationException {
        if (practice == null) {
            LOGGER.log(Level.WARNING, "La practica es nula");
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
            LOGGER.log(Level.SEVERE, "Error al obtener los documentos de la practica", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener los documentos");
        }
        return documents;
    }

    /**
     * Retrieves the report documents (partial, monthly and final) uploaded for a practice,
     * including their validation status.
     *
     * @param practice the practice whose uploaded reports are requested, must not be null
     * @return a list of uploaded report documents, empty if there are none
     * @throws IllegalArgumentException if practice is null
     * @throws DataOperationException if a database error occurs
     */
    public List<Document> getUploadedReportsByPractice(Practice practice) throws DataOperationException {
        if (practice == null) {
            LOGGER.log(Level.WARNING, "La practica es nula");
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
            LOGGER.log(Level.SEVERE, "Error al obtener los reportes subidos de la practica", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener los reportes subidos");
        }
        return reports;
    }

    /**
     * Marks a report document as validated (accepted).
     *
     * @param documentId the identifier of the document to accept
     * @return true if at least one row was updated
     * @throws DataOperationException if a database error occurs
     */
    public boolean acceptReport(int documentId) throws DataOperationException {
        boolean accepted = false;
        String query = "UPDATE documentos SET estado_validacion = 'VALIDADO' WHERE id_documento = ?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, documentId);
            accepted = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al aceptar el reporte", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al aceptar el reporte");
        }
        return accepted;
    }

    /**
     * Retrieves a validation summary for each student that has uploaded non-report documents
     * while having an assigned project.
     *
     * @return a list of student validation summaries, empty if there are none
     * @throws DataOperationException if a database error occurs
     */
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
            LOGGER.log(Level.SEVERE, "Error al obtener los alumnos con documentos subidos", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
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
                LOGGER.log(Level.WARNING, "No se encontró el alumno con id: " + studentId);
            }
        }
        return summaries;
    }

    /**
     * Retrieves all documents of a practice along with their validation status, for review.
     *
     * @param practice the practice whose documents are requested, must not be null
     * @return a list of documents pending or available for validation, empty if there are none
     * @throws IllegalArgumentException if practice is null
     * @throws DataOperationException if a database error occurs
     */
    public List<Document> getDocumentsForValidation(Practice practice) throws DataOperationException {
        if (practice == null) {
            LOGGER.log(Level.WARNING, "La practica es nula");
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
            LOGGER.log(Level.SEVERE, "Error al obtener los documentos para validar", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener los documentos para validar");
        }
        return documents;
    }

    /**
     * Sets a document's validation status to validated.
     *
     * @param documentId the identifier of the document to validate
     * @return true if at least one row was updated
     * @throws DataOperationException if a database error occurs
     */
    public boolean validateDocument(int documentId) throws DataOperationException {
        return updateValidationStatus(documentId, ValidationStatus.VALIDATED);
    }

    /**
     * Checks whether the required initial documents (acceptance letter and student schedule)
     * have been validated for a practice.
     *
     * @param practice the practice to check, must not be null
     * @return true if both required initial documents are validated
     * @throws IllegalArgumentException if practice is null
     * @throws DataOperationException if a database error occurs
     */
    public boolean areInitialDocumentsUploaded(Practice practice) throws DataOperationException {
        if (practice == null) {
            LOGGER.log(Level.WARNING, "La practica es nula");
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
            LOGGER.log(Level.SEVERE, "Error al verificar los prerrequisitos de los reportes", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al verificar los prerrequisitos de los reportes");
        }
        return uploaded;
    }

    /**
     * Deletes a document record and, on success, removes its physical file from disk.
     *
     * @param document the document to delete, must not be null
     * @return true if the document record was deleted
     * @throws IllegalArgumentException if document is null
     * @throws DataOperationException if a database error occurs
     */
    public boolean deleteDocument(Document document) throws DataOperationException {
        if (document == null) {
            LOGGER.log(Level.WARNING, "El documento es nulo");
            throw new IllegalArgumentException("El documento no puede ser nulo");
        }
        boolean deleted = false;
        String query = "DELETE FROM documentos WHERE id_documento = ?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, document.getId());
            deleted = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar el documento", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al eliminar el documento");
        }
        if (deleted) {
            deletePhysicalFile(document.getDirectory());
        }
        return deleted;
    }

    /**
     * Deletes the physical file at the given path if it exists. Failures are logged but not propagated.
     *
     * @param path the absolute path of the file to delete
     */
    private void deletePhysicalFile(String path) {
        if (path != null && !path.isBlank()) {
            try {
                Files.deleteIfExists(Paths.get(path));
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "No se pudo eliminar el archivo físico del documento: " + path, e);
            }
        }
    }

    /**
     * Updates the validation status of a document.
     *
     * @param documentId the identifier of the document to update
     * @param status the new validation status
     * @return true if at least one row was updated
     * @throws DataOperationException if a database error occurs
     */
    private boolean updateValidationStatus(int documentId, ValidationStatus status) throws DataOperationException {
        boolean updated = false;
        String query = "UPDATE documentos SET estado_validacion = ? WHERE id_documento = ?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, status.getValidationValue());
            preparedStatement.setInt(2, documentId);
            updated = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar el estado de validación del documento", e);
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al actualizar el estado de validación del documento");
        }
        return updated;
    }

}
