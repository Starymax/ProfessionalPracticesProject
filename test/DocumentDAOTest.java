import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dao.DocumentDAO;
import mx.fei.logic.dao.StudentDAO;
import mx.fei.logic.dto.Document;
import mx.fei.logic.dto.DocumentType;
import mx.fei.logic.dto.Practice;
import mx.fei.logic.dto.RegistrationStatus;
import mx.fei.logic.dto.Student;
import mx.fei.logic.dto.StudentValidationSummary;
import mx.fei.logic.dto.ValidationStatus;
import mx.fei.logic.exceptions.DataOperationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import static mx.fei.logic.dto.DocumentType.STUDENT_SCHEDULE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

public class DocumentDAOTest {

    private DocumentDAO documentDAO;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private MockedStatic<DatabaseConnectionManager> databaseConnectionManager;
    private DatabaseConnectionManager mockManager;

    @BeforeEach
    void setUp() throws SQLException {
        documentDAO = new DocumentDAO();
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);
        mockManager = mock(DatabaseConnectionManager.class);
        databaseConnectionManager = Mockito.mockStatic(DatabaseConnectionManager.class);
        databaseConnectionManager.when(DatabaseConnectionManager::getInstance).thenReturn(mockManager);
        when(mockManager.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);
    }

    @AfterEach
    void tearDown() {
        if (databaseConnectionManager != null) {
            databaseConnectionManager.close();
        }
    }

    @Test
    void createExpedient_PeriodIsNull_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> documentDAO.createExpedient(1, null));
    }

    @Test
    void createExpedient_PeriodIsBlank_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> documentDAO.createExpedient(1, "   "));
    }

    @Test
    void createExpedient_InsertSucceeds_ReturnsTrue() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = documentDAO.createExpedient(1, "2025-01");
        assertTrue(result);
    }

    @Test
    void createExpedient_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de insercion"));
        assertThrows(DataOperationException.class, () -> documentDAO.createExpedient(1, "2025-01"));
    }

    @Test
    void getPeriodByStudentEnrollment_EnrollmentIsNull_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> documentDAO.getPeriodByStudentEnrollment(null));
    }

    @Test
    void getPeriodByStudentEnrollment_EnrollmentIsBlank_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> documentDAO.getPeriodByStudentEnrollment("  "));
    }

    @Test
    void getPeriodByStudentEnrollment_ExpedientExists_ReturnsPeriod() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("periodo")).thenReturn("2025-01");
        String result = documentDAO.getPeriodByStudentEnrollment("zS22013456");
        assertEquals("2025-01", result);
    }

    @Test
    void getPeriodByStudentEnrollment_ExpedientDoesNotExist_ReturnsNull() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        String result = documentDAO.getPeriodByStudentEnrollment("zS99999999");
        assertNull(result);
    }

    @Test
    void getPeriodByStudentEnrollment_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de lectura"));
        assertThrows(DataOperationException.class, () -> documentDAO.getPeriodByStudentEnrollment("zS22013456"));
    }

    @Test
    void getCurrentPeriod_Always_ReturnsNonNullPeriod() {
        String result = documentDAO.getCurrentPeriod();
        assertNotNull(result);
    }

    @Test
    void loadDocument_InsertReturnsGeneratedKey_ReturnsGeneratedId() throws SQLException {
        Practice practice = mock(Practice.class);
        when(practice.getId()).thenReturn(1);
        Document document = new Document("doc.pdf", "/ruta/doc.pdf", DocumentType.WORK_PLAN);
        ResultSet generatedKeys = mock(ResultSet.class);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(42);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        int result = documentDAO.loadDocument(practice, document);
        assertEquals(42, result);
    }

    @Test
    void loadDocument_InsertReturnsNoGeneratedKey_ReturnsFailureValue() throws SQLException {
        Practice practice = mock(Practice.class);
        when(practice.getId()).thenReturn(1);
        Document document = new Document("doc.pdf", "/ruta/doc.pdf", DocumentType.WORK_PLAN);
        ResultSet generatedKeys = mock(ResultSet.class);
        when(generatedKeys.next()).thenReturn(false);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        int result = documentDAO.loadDocument(practice, document);
        assertEquals(RegistrationStatus.FAILURE.getValue(), result);
    }

    @Test
    void loadDocument_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        Practice practice = mock(Practice.class);
        when(practice.getId()).thenReturn(1);
        Document document = new Document("doc.pdf", "/ruta/doc.pdf", DocumentType.WORK_PLAN);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de insercion"));
        assertThrows(DataOperationException.class, () -> documentDAO.loadDocument(practice, document));
    }

    @Test
    void isLoaded_DocumentColumnIsTrue_ReturnsTrue() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getBoolean("carta_liberacion")).thenReturn(true);
        boolean result = documentDAO.isLoaded("zS22013456", "carta_liberacion");
        assertTrue(result);
    }

    @Test
    void isLoaded_DocumentColumnIsFalse_ReturnsFalse() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getBoolean("carta_liberacion")).thenReturn(false);
        boolean result = documentDAO.isLoaded("zS22013456", "carta_liberacion");
        assertFalse(result);
    }

    @Test
    void isLoaded_StudentExpedientNotFound_ReturnsFalse() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        boolean result = documentDAO.isLoaded("zS99999999", "carta_liberacion");
        assertFalse(result);
    }

    @Test
    void isLoaded_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de lectura"));
        assertThrows(DataOperationException.class, () -> documentDAO.isLoaded("zS22013456", "carta_liberacion"));
    }

    @Test
    void uploadDocument_DirectoryIsNull_ThrowsIllegalArgumentException() {
        Document document = new Document("doc.pdf", null, DocumentType.WORK_PLAN);
        assertThrows(IllegalArgumentException.class, () -> documentDAO.uploadDocument("zS22013456", document));
    }

    @Test
    void uploadDocument_DirectoryIsEmpty_ThrowsIllegalArgumentException() {
        Document document = new Document("doc.pdf", "", DocumentType.WORK_PLAN);
        assertThrows(IllegalArgumentException.class, () -> documentDAO.uploadDocument("zS22013456", document));
    }

    @Test
    void uploadDocument_SourceFileExists_ReturnsTargetPath() throws IOException {
        Path tempFile = Files.createTempFile("testDoc", ".pdf");
        try {
            Document document = new Document("doc.pdf", tempFile.toString(), DocumentType.WORK_PLAN);
            String result = documentDAO.uploadDocument("zS22013456", document);
            assertNotNull(result);
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    void uploadDocument_SourceFileDoesNotExist_ThrowsIOException() {
        Document document = new Document("doc.pdf", "/ruta/inexistente/archivo.pdf", DocumentType.WORK_PLAN);
        assertThrows(IOException.class, () -> documentDAO.uploadDocument("zS22013456", document));
    }

    @Test
    void getDocumentsByPractice_PracticeIsNull_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> documentDAO.getDocumentsByPractice(null));
    }

    @Test
    void getDocumentsByPractice_PracticeHasOneDocument_ReturnsListWithOneDocument() throws SQLException {
        Practice practice = mock(Practice.class);
        when(practice.getId()).thenReturn(1);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("id_documento")).thenReturn(10);
        when(resultSet.getString("nombre")).thenReturn("plan_trabajo.pdf");
        when(resultSet.getString("ruta")).thenReturn("/ruta/plan_trabajo.pdf");
        when(resultSet.getString("tipoDocumento")).thenReturn("WORK_PLAN");
        Document expectedDocument = new Document("plan_trabajo.pdf", "/ruta/plan_trabajo.pdf", DocumentType.WORK_PLAN, practice);
        expectedDocument.setId(10);
        List<Document> result = documentDAO.getDocumentsByPractice(practice);
        assertEquals(List.of(expectedDocument), result);
    }

    @Test
    void getDocumentsByPractice_PracticeHasOneDocument_ReturnsExpectedDocument() throws SQLException {
        Practice practice = mock(Practice.class);
        when(practice.getId()).thenReturn(1);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("id_documento")).thenReturn(5);
        when(resultSet.getString("nombre")).thenReturn("horario.pdf");
        when(resultSet.getString("ruta")).thenReturn("/ruta/horario.pdf");
        when(resultSet.getString("tipoDocumento")).thenReturn("STUDENT_SCHEDULE");
        Document expectedDocument = new Document("horario.pdf", "/ruta/horario.pdf", DocumentType.STUDENT_SCHEDULE, practice);
        expectedDocument.setId(5);
        List<Document> result = documentDAO.getDocumentsByPractice(practice);
        assertEquals(List.of(expectedDocument), result);
    }

    @Test
    void getDocumentsByPractice_PracticeHasNoDocuments_ReturnsEmptyList() throws SQLException {
        Practice practice = mock(Practice.class);
        when(practice.getId()).thenReturn(1);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<Document> result = documentDAO.getDocumentsByPractice(practice);
        assertTrue(result.isEmpty());
    }

    @Test
    void getDocumentsByPractice_PracticeHasNoDocuments_ReturnsNonNullList() throws SQLException {
        Practice practice = mock(Practice.class);
        when(practice.getId()).thenReturn(1);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<Document> result = documentDAO.getDocumentsByPractice(practice);
        assertNotNull(result);
    }

    @Test
    void getDocumentsByPractice_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        Practice practice = mock(Practice.class);
        when(practice.getId()).thenReturn(1);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de lectura"));
        assertThrows(DataOperationException.class, () -> documentDAO.getDocumentsByPractice(practice));
    }

    @Test
    void getUploadedReportsByPractice_PracticeIsNull_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> documentDAO.getUploadedReportsByPractice(null));
    }

    @Test
    void getUploadedReportsByPractice_PracticeHasOneReport_ReturnsListWithOneReport() throws SQLException {
        Practice practice = mock(Practice.class);
        when(practice.getId()).thenReturn(1);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("id_documento")).thenReturn(10);
        when(resultSet.getString("nombre")).thenReturn("reporte_parcial.pdf");
        when(resultSet.getString("ruta")).thenReturn("/ruta/reporte_parcial.pdf");
        when(resultSet.getString("tipoDocumento")).thenReturn("PARTIAL_REPORT");
        when(resultSet.getString("estado_validacion")).thenReturn("PENDIENTE");
        Document expectedDocument = new Document("reporte_parcial.pdf", "/ruta/reporte_parcial.pdf", DocumentType.PARTIAL_REPORT, practice);
        expectedDocument.setId(10);
        List<Document> result = documentDAO.getUploadedReportsByPractice(practice);
        assertEquals(List.of(expectedDocument), result);
    }

    @Test
    void getUploadedReportsByPractice_ReportColumnAcceptedIsTrue_ReturnsReportMarkedAsAccepted() throws SQLException {
        Practice practice = mock(Practice.class);
        when(practice.getId()).thenReturn(1);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("id_documento")).thenReturn(5);
        when(resultSet.getString("nombre")).thenReturn("reporte_final.pdf");
        when(resultSet.getString("ruta")).thenReturn("/ruta/reporte_final.pdf");
        when(resultSet.getString("tipoDocumento")).thenReturn("FINAL_REPORT");
        when(resultSet.getString("estado_validacion")).thenReturn("VALIDADO");
        Document expectedDocument = new Document("reporte_final.pdf", "/ruta/reporte_final.pdf", DocumentType.FINAL_REPORT, practice);
        expectedDocument.setId(5);
        expectedDocument.setAccepted(true);
        List<Document> result = documentDAO.getUploadedReportsByPractice(practice);
        assertEquals(expectedDocument, result.get(0));
    }

    @Test
    void getUploadedReportsByPractice_PracticeHasNoReports_ReturnsEmptyList() throws SQLException {
        Practice practice = mock(Practice.class);
        when(practice.getId()).thenReturn(1);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<Document> result = documentDAO.getUploadedReportsByPractice(practice);
        assertTrue(result.isEmpty());
    }

    @Test
    void getUploadedReportsByPractice_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        Practice practice = mock(Practice.class);
        when(practice.getId()).thenReturn(1);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de lectura"));
        assertThrows(DataOperationException.class, () -> documentDAO.getUploadedReportsByPractice(practice));
    }

    @Test
    void acceptReport_UpdateAffectsOneRow_ReturnsTrue() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = documentDAO.acceptReport(10);
        assertTrue(result);
    }

    @Test
    void acceptReport_UpdateAffectsZeroRows_ReturnsFalse() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(0);
        boolean result = documentDAO.acceptReport(999);
        assertFalse(result);
    }

    @Test
    void acceptReport_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de escritura"));
        assertThrows(DataOperationException.class, () -> documentDAO.acceptReport(10));
    }

    @Test
    void getStudentsWithUploadedDocuments_NoStudentsWithUploadedDocuments_ReturnsEmptyList() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<StudentValidationSummary> result = documentDAO.getStudentsWithUploadedDocuments();
        assertTrue(result.isEmpty());
    }

    @Test
    void getStudentsWithUploadedDocuments_OneStudentWithUploadedDocuments_ReturnsListWithExpectedSummary() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("id_alumno")).thenReturn(5);
        when(resultSet.getInt("total_documentos")).thenReturn(4);
        when(resultSet.getInt("documentos_pendientes")).thenReturn(2);
        Student student = mock(Student.class);
        StudentValidationSummary expectedSummary = new StudentValidationSummary(student, 2, 4);
        try (MockedConstruction<StudentDAO> mockedStudentDAO = mockConstruction(StudentDAO.class,
                (mock, context) -> when(mock.getStudentById(5)).thenReturn(student))) {
            List<StudentValidationSummary> result = documentDAO.getStudentsWithUploadedDocuments();
            assertEquals(List.of(expectedSummary), result);
        }
    }

    @Test
    void getStudentsWithUploadedDocuments_StudentCannotBeLoaded_ReturnsListWithoutThatStudent() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("id_alumno")).thenReturn(9);
        try (MockedConstruction<StudentDAO> mockedStudentDAO = mockConstruction(StudentDAO.class,
                (mock, context) -> when(mock.getStudentById(9)).thenThrow(new NoSuchElementException()))) {
            List<StudentValidationSummary> result = documentDAO.getStudentsWithUploadedDocuments();
            assertTrue(result.isEmpty());
        }
    }

    @Test
    void getStudentsWithUploadedDocuments_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de red"));
        assertThrows(DataOperationException.class, () -> documentDAO.getStudentsWithUploadedDocuments());
    }

    @Test
    void getDocumentsForValidation_PracticeIsNull_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> documentDAO.getDocumentsForValidation(null));
    }

    @Test
    void getDocumentsForValidation_PracticeHasOneDocument_ReturnsListWithExpectedDocument() throws SQLException {
        Practice practice = mock(Practice.class);
        when(practice.getId()).thenReturn(1);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("id_documento")).thenReturn(7);
        when(resultSet.getString("nombre")).thenReturn("plan_trabajo.pdf");
        when(resultSet.getString("ruta")).thenReturn("/ruta/plan_trabajo.pdf");
        when(resultSet.getString("tipoDocumento")).thenReturn("WORK_PLAN");
        when(resultSet.getString("estado_validacion")).thenReturn("VALIDADO");
        Document expectedDocument = new Document("plan_trabajo.pdf", "/ruta/plan_trabajo.pdf", DocumentType.WORK_PLAN, practice);
        expectedDocument.setId(7);
        expectedDocument.setValidationStatus(ValidationStatus.VALIDATED);
        List<Document> result = documentDAO.getDocumentsForValidation(practice);
        assertEquals(List.of(expectedDocument), result);
    }

    @Test
    void getDocumentsForValidation_PracticeHasNoDocuments_ReturnsEmptyList() throws SQLException {
        Practice practice = mock(Practice.class);
        when(practice.getId()).thenReturn(1);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<Document> result = documentDAO.getDocumentsForValidation(practice);
        assertTrue(result.isEmpty());
    }

    @Test
    void getDocumentsForValidation_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        Practice practice = mock(Practice.class);
        when(practice.getId()).thenReturn(1);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de lectura"));
        assertThrows(DataOperationException.class, () -> documentDAO.getDocumentsForValidation(practice));
    }

    @Test
    void validateDocument_UpdateAffectsOneRow_ReturnsTrue() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = documentDAO.validateDocument(10);
        assertTrue(result);
    }

    @Test
    void validateDocument_UpdateAffectsZeroRows_ReturnsFalse() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(0);
        boolean result = documentDAO.validateDocument(999);
        assertFalse(result);
    }

    @Test
    void validateDocument_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de escritura"));
        assertThrows(DataOperationException.class, () -> documentDAO.validateDocument(10));
    }

    @Test
    void deleteDocument_DocumentIsNull_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> documentDAO.deleteDocument(null));
    }

    @Test
    void deleteDocument_DeleteAffectsOneRow_ReturnsTrue() throws SQLException {
        Document document = mock(Document.class);
        when(document.getId()).thenReturn(10);
        when(document.getDirectory()).thenReturn(null);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = documentDAO.deleteDocument(document);
        assertTrue(result);
    }

    @Test
    void deleteDocument_DeleteAffectsZeroRows_ReturnsFalse() throws SQLException {
        Document document = mock(Document.class);
        when(document.getId()).thenReturn(999);
        when(preparedStatement.executeUpdate()).thenReturn(0);
        boolean result = documentDAO.deleteDocument(document);
        assertFalse(result);
    }

    @Test
    void deleteDocument_SQLExceptionThrown_ThrowsDataOperationException() throws SQLException {
        Document document = mock(Document.class);
        when(document.getId()).thenReturn(10);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de escritura"));
        assertThrows(DataOperationException.class, () -> documentDAO.deleteDocument(document));
    }
}
