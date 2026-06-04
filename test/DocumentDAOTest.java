import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dao.DocumentDAO;
import mx.fei.logic.dto.Document;
import mx.fei.logic.dto.DocumentType;
import mx.fei.logic.dto.Practice;
import mx.fei.logic.dto.RegistrationStatus;
import mx.fei.logic.exceptions.DataOperationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class DocumentDAOTest {

    private DocumentDAO documentDAO;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private MockedStatic<DatabaseConnectionManager> databaseConnectionManager;

    @BeforeEach
    void setUp() throws SQLException {
        documentDAO = new DocumentDAO();
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);
        databaseConnectionManager = Mockito.mockStatic(DatabaseConnectionManager.class);
        databaseConnectionManager.when(DatabaseConnectionManager::getConnection).thenReturn(connection);
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
    void createExpedient_PeriodNull_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> documentDAO.createExpedient(1, null));
    }

    @Test
    void createExpedient_PeriodBlank_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> documentDAO.createExpedient(1, "   "));
    }

    @Test
    void createExpedient_Successful_ReturnsTrue() throws SQLException, DataOperationException {
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = documentDAO.createExpedient(1, "2025-01");
        assertTrue(result);
    }

    @Test
    void createExpedient_SQLException_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de insercion"));
        assertThrows(DataOperationException.class, () -> documentDAO.createExpedient(1, "2025-01"));
    }

    @Test
    void getPeriodByStudentEnrollment_EnrollmentNull_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> documentDAO.getPeriodByStudentEnrollment(null));
    }

    @Test
    void getPeriodByStudentEnrollment_EnrollmentBlank_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> documentDAO.getPeriodByStudentEnrollment("  "));
    }

    @Test
    void getPeriodByStudentEnrollment_Found_ReturnsPeriod() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("periodo")).thenReturn("2025-01");
        String result = documentDAO.getPeriodByStudentEnrollment("zS22013456");
        assertEquals("2025-01", result);
    }

    @Test
    void getPeriodByStudentEnrollment_NotFound_ReturnsNull() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        String result = documentDAO.getPeriodByStudentEnrollment("zS99999999");
        assertNull(result);
    }

    @Test
    void getPeriodByStudentEnrollment_SQLException_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de lectura"));
        assertThrows(DataOperationException.class, () -> documentDAO.getPeriodByStudentEnrollment("zS22013456"));
    }

    @Test
    void getPeriodByStudentEnrollment_QueriesCorrectEnrollment_VerifiesParameter() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        documentDAO.getPeriodByStudentEnrollment("zS22013456");
        verify(preparedStatement).setString(1, "zS22013456");
    }

    @Test
    void getCurrentPeriod_ReturnsNonNull() {
        String result = documentDAO.getCurrentPeriod();
        assertNotNull(result);
    }

    @Test
    void loadDocument_Successful_ReturnsGeneratedId() throws SQLException, DataOperationException {
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
    void loadDocument_NoGeneratedKeys_ReturnsFailureValue() throws SQLException, DataOperationException {
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
    void loadDocument_SQLException_ThrowsDataOperationException() throws SQLException {
        Practice practice = mock(Practice.class);
        when(practice.getId()).thenReturn(1);
        Document document = new Document("doc.pdf", "/ruta/doc.pdf", DocumentType.WORK_PLAN);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de insercion"));
        assertThrows(DataOperationException.class, () -> documentDAO.loadDocument(practice, document));
    }

    @Test
    void isLoaded_DocumentLoaded_ReturnsTrue() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getBoolean("carta_liberacion")).thenReturn(true);
        boolean result = documentDAO.isLoaded("zS22013456", "carta_liberacion");
        assertTrue(result);
    }

    @Test
    void isLoaded_DocumentNotLoaded_ReturnsFalse() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getBoolean("carta_liberacion")).thenReturn(false);
        boolean result = documentDAO.isLoaded("zS22013456", "carta_liberacion");
        assertFalse(result);
    }

    @Test
    void isLoaded_StudentNotFound_ReturnsFalse() throws SQLException, DataOperationException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        boolean result = documentDAO.isLoaded("zS99999999", "carta_liberacion");
        assertFalse(result);
    }

    @Test
    void isLoaded_SQLException_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de lectura"));
        assertThrows(DataOperationException.class, () -> documentDAO.isLoaded("zS22013456", "carta_liberacion"));
    }

    @Test
    void uploadDocument_DirectoryNull_ThrowsIllegalArgumentException() {
        Document document = new Document("doc.pdf", null, DocumentType.WORK_PLAN);
        assertThrows(IllegalArgumentException.class, () -> documentDAO.uploadDocument("zS22013456", document));
    }

    @Test
    void uploadDocument_DirectoryEmpty_ThrowsIllegalArgumentException() {
        Document document = new Document("doc.pdf", "", DocumentType.WORK_PLAN);
        assertThrows(IllegalArgumentException.class, () -> documentDAO.uploadDocument("zS22013456", document));
    }

    @Test
    void uploadDocument_Successful_ReturnsTargetPath() throws IOException {
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
    void uploadDocument_NonExistentSourceFile_ThrowsIOException() {
        Document document = new Document("doc.pdf", "/ruta/inexistente/archivo.pdf", DocumentType.WORK_PLAN);
        assertThrows(IOException.class, () -> documentDAO.uploadDocument("zS22013456", document));
    }

    @Test
    void getDocumentsByPractice_PracticeNull_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> documentDAO.getDocumentsByPractice(null));
    }

    @Test
    void getDocumentsByPractice_WithDocuments_ReturnsList() throws SQLException, DataOperationException {
        Practice practice = mock(Practice.class);
        when(practice.getId()).thenReturn(1);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("id_documento")).thenReturn(10);
        when(resultSet.getString("nombre")).thenReturn("plan_trabajo.pdf");
        when(resultSet.getString("ruta")).thenReturn("/ruta/plan_trabajo.pdf");
        when(resultSet.getString("tipoDocumento")).thenReturn("WORK_PLAN");
        List<Document> result = documentDAO.getDocumentsByPractice(practice);
        assertEquals(1, result.size());
    }

    @Test
    void getDocumentsByPractice_EmptyResult_ReturnsEmptyList() throws SQLException, DataOperationException {
        Practice practice = mock(Practice.class);
        when(practice.getId()).thenReturn(1);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<Document> result = documentDAO.getDocumentsByPractice(practice);
        assertTrue(result.isEmpty());
    }

    @Test
    void getDocumentsByPractice_SQLException_ThrowsDataOperationException() throws SQLException {
        Practice practice = mock(Practice.class);
        when(practice.getId()).thenReturn(1);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de lectura"));
        assertThrows(DataOperationException.class, () -> documentDAO.getDocumentsByPractice(practice));
    }

    @Test
    void getDocumentsByPractice_DocumentNameIsCorrect_ReturnsExpectedName() throws SQLException, DataOperationException {
        Practice practice = mock(Practice.class);
        when(practice.getId()).thenReturn(1);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("id_documento")).thenReturn(5);
        when(resultSet.getString("nombre")).thenReturn("horario.pdf");
        when(resultSet.getString("ruta")).thenReturn("/ruta/horario.pdf");
        when(resultSet.getString("tipoDocumento")).thenReturn("STUDENT_SCHEDULE");
        List<Document> result = documentDAO.getDocumentsByPractice(practice);
        assertEquals("horario.pdf", result.get(0).getName());
    }

    @Test
    void getDocumentsByPractice_QueriesCorrectPractice_VerifiesParameter() throws SQLException, DataOperationException {
        Practice practice = mock(Practice.class);
        when(practice.getId()).thenReturn(3);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        documentDAO.getDocumentsByPractice(practice);
        verify(preparedStatement).setInt(1, 3);
    }

    @Test
    void getDocumentsByPractice_ReturnsNotNull_WhenResultSetEmpty() throws SQLException, DataOperationException {
        Practice practice = mock(Practice.class);
        when(practice.getId()).thenReturn(1);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<Document> result = documentDAO.getDocumentsByPractice(practice);
        assertNotNull(result);
    }

    @Test
    void getUploadedReportsByPractice_PracticeNull_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> documentDAO.getUploadedReportsByPractice(null));
    }

    @Test
    void getUploadedReportsByPractice_WithReports_ReturnsList() throws SQLException, DataOperationException {
        Practice practice = mock(Practice.class);
        when(practice.getId()).thenReturn(1);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("id_documento")).thenReturn(10);
        when(resultSet.getString("nombre")).thenReturn("reporte_parcial.pdf");
        when(resultSet.getString("ruta")).thenReturn("/ruta/reporte_parcial.pdf");
        when(resultSet.getString("tipoDocumento")).thenReturn("PARTIAL_REPORT");
        when(resultSet.getBoolean("aceptado")).thenReturn(false);
        List<Document> result = documentDAO.getUploadedReportsByPractice(practice);
        assertEquals(1, result.size());
    }

    @Test
    void getUploadedReportsByPractice_EmptyResult_ReturnsEmptyList() throws SQLException, DataOperationException {
        Practice practice = mock(Practice.class);
        when(practice.getId()).thenReturn(1);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        List<Document> result = documentDAO.getUploadedReportsByPractice(practice);
        assertTrue(result.isEmpty());
    }

    @Test
    void getUploadedReportsByPractice_SQLException_ThrowsDataOperationException() throws SQLException {
        Practice practice = mock(Practice.class);
        when(practice.getId()).thenReturn(1);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Error de lectura"));
        assertThrows(DataOperationException.class, () -> documentDAO.getUploadedReportsByPractice(practice));
    }

    @Test
    void getUploadedReportsByPractice_ReportAcceptedStatusIsCorrect_ReturnsExpectedStatus() throws SQLException, DataOperationException {
        Practice practice = mock(Practice.class);
        when(practice.getId()).thenReturn(1);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("id_documento")).thenReturn(5);
        when(resultSet.getString("nombre")).thenReturn("reporte_final.pdf");
        when(resultSet.getString("ruta")).thenReturn("/ruta/reporte_final.pdf");
        when(resultSet.getString("tipoDocumento")).thenReturn("FINAL_REPORT");
        when(resultSet.getBoolean("aceptado")).thenReturn(true);
        List<Document> result = documentDAO.getUploadedReportsByPractice(practice);
        assertTrue(result.get(0).isAccepted());
    }

    @Test
    void getUploadedReportsByPractice_QueriesCorrectPractice_VerifiesParameter() throws SQLException, DataOperationException {
        Practice practice = mock(Practice.class);
        when(practice.getId()).thenReturn(4);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        documentDAO.getUploadedReportsByPractice(practice);
        verify(preparedStatement).setInt(1, 4);
    }

    @Test
    void acceptReport_Successful_ReturnsTrue() throws SQLException, DataOperationException {
        when(preparedStatement.executeUpdate()).thenReturn(1);
        boolean result = documentDAO.acceptReport(10);
        assertTrue(result);
    }

    @Test
    void acceptReport_ReportNotFound_ReturnsFalse() throws SQLException, DataOperationException {
        when(preparedStatement.executeUpdate()).thenReturn(0);
        boolean result = documentDAO.acceptReport(999);
        assertFalse(result);
    }

    @Test
    void acceptReport_SQLException_ThrowsDataOperationException() throws SQLException {
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Error de escritura"));
        assertThrows(DataOperationException.class, () -> documentDAO.acceptReport(10));
    }

    @Test
    void acceptReport_SetsCorrectDocumentId_VerifiesParameter() throws SQLException, DataOperationException {
        when(preparedStatement.executeUpdate()).thenReturn(1);
        documentDAO.acceptReport(15);
        verify(preparedStatement).setInt(1, 15);
    }
}
