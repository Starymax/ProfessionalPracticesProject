
import mx.fei.logic.dao.*;
import mx.fei.logic.dto.*;
import mx.fei.logic.exceptions.DataOperationException;
import org.junit.jupiter.api.*;
import java.io.File;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;

class StudentDAOTest {

    private StudentDAO studentDAO;
    private static final String EXISTING_ENROLLMENT = "zS20013001";
    private static final String NON_EXISTING_ENROLLMENT = "zS99999999";

    @BeforeEach
    void setUp() {
        studentDAO = new StudentDAO();
    }

    // --- getStudentByEnrollment ---

    @Test
    @DisplayName("Debe regresar un Student cuando la matrícula existe")
    void getStudentByEnrollment_existingEnrollment_returnsStudent() throws DataOperationException {
        Student student = studentDAO.getStudentByEnrollment(EXISTING_ENROLLMENT);
        assertNotNull(student);
        assertEquals(EXISTING_ENROLLMENT, student.getEnrollment());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la matrícula no existe")
    void getStudentByEnrollment_nonExistingEnrollment_throwsException() {
        assertThrows(NoSuchElementException.class, () ->
                studentDAO.getStudentByEnrollment(NON_EXISTING_ENROLLMENT)
        );
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException con matrícula nula")
    void getStudentByEnrollment_nullEnrollment_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                studentDAO.getStudentByEnrollment(null)
        );
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException con matrícula vacía")
    void getStudentByEnrollment_emptyEnrollment_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                studentDAO.getStudentByEnrollment("")
        );
    }

    @Test
    @DisplayName("Debe registrar un estudiante valido y regresar true")
    void registerStudent_validStudent_returnsTrue() throws DataOperationException {
        Student newStudent = new Student(
                0, "Prueba", "Unitaria", "prueba@uv.mx",
                "pass123", "Masculino", true,
                "zS20019984", "2026-1", false, 0.0f, null, null
        );
        boolean result = studentDAO.registerStudent(newStudent);
        assertTrue(result);
    }

    @Test
    @DisplayName("Debe lanzar IllegalStateException si la matrícula ya existe")
    void registerStudent_duplicateEnrollment_throwsIllegalState() {
        Student duplicate = new Student(
                0, "Duplicado", "Test", "dup@uv.mx",
                "pass123", "Femenino", true,
                EXISTING_ENROLLMENT, "2026-1", false, 0.0f, null, null
        );
        assertThrows(IllegalStateException.class, () ->
                studentDAO.registerStudent(duplicate)
        );
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException con Student nulo")
    void registerStudent_nullStudent_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                studentDAO.registerStudent(null)
        );
    }

    @Test
    @DisplayName("Debe modificar un Student existente y regresar true")
    void modifyStudent_existingStudent_returnsTrue() throws DataOperationException {
        Student student = studentDAO.getStudentByEnrollment(EXISTING_ENROLLMENT);
        student.setPeriod("2026-2");
        boolean result = studentDAO.modifyStudent(student);
        assertTrue(result);
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException con Student nulo")
    void modifyStudent_nullStudent_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                studentDAO.modifyStudent(null)
        );
    }

    // --- getStudents ---

    @Test
    @DisplayName("Debe regresar una lista no vacía de estudiantes")
    void getStudents_returnsNonEmptyList() throws DataOperationException {
        List<Student> students = studentDAO.getStudents();
        assertNotNull(students);
        assertFalse(students.isEmpty());
    }

    // --- getStudentsWithoutProject ---

    @Test
    @DisplayName("Debe regresar una lista (puede estar vacía) de estudiantes sin proyecto")
    void getStudentsWithoutProject_returnsList() throws DataOperationException {
        List<Student> students = studentDAO.getStudentsWithoutProject();
        assertNotNull(students);
    }

    // --- getActiveStudents ---

    @Test
    @DisplayName("Todos los estudiantes regresados deben tener estado_activo = true")
    void getActiveStudents_allHaveActiveStatus() throws DataOperationException {
        List<Student> students = studentDAO.getActiveStudents();
        assertNotNull(students);
        students.forEach(s -> assertTrue(s.isActive()));
    }

    // --- saveSelectedProjects y getSelectedProjects ---

    @Test
    @DisplayName("Los proyectos guardados deben poderse recuperar")
    void saveAndGetSelectedProjects_persistsData() throws DataOperationException {
        Student student = studentDAO.getStudentByEnrollment(EXISTING_ENROLLMENT);
        ProjectDAO projectDAO = new ProjectDAO();
        List<Project> projects = List.of(projectDAO.getProjectById(1));

        studentDAO.saveSelectedProjects(projects, student);
        List<Project> retrieved = studentDAO.getSelectedProjects(student);

        assertNotNull(retrieved);
        assertFalse(retrieved.isEmpty());
    }
}

class ProjectDAOTest {
    private ProjectDAO projectDAO;
    private static final int EXISTING_ID = 1;
    private static final int NON_EXISTING_ID = 99999;

    @BeforeEach
    void setUp() {
        projectDAO = new ProjectDAO();
    }

    @Test
    @DisplayName("Debe regresar un Project cuando el id existe")
    void getProjectById_existingId_returnsProject() throws DataOperationException {
        Project project = projectDAO.getProjectById(EXISTING_ID);
        assertNotNull(project);
        assertEquals(EXISTING_ID, project.getProjectId());
    }

    @Test
    @DisplayName("Debe lanzar NoSuchElementException cuando el id no existe")
    void getProjectById_nonExistingId_throwsException() {
        assertThrows(NoSuchElementException.class, () ->
                projectDAO.getProjectById(NON_EXISTING_ID)
        );
    }

    @Test
    @DisplayName("Debe regresar lista no vacía de proyectos activos")
    void getActiveProjects_returnsNonEmptyList() throws DataOperationException {
        List<Project> projects = projectDAO.getActiveProjects();
        assertNotNull(projects);
        assertFalse(projects.isEmpty());
    }
}

class EnterpriseDAOTest {
    private EnterpriseDAO enterpriseDAO;
    private static final int EXISTING_ID = 1;
    private static final int NON_EXISTING_ID = 99999;

    @BeforeEach
    void setUp() {
        enterpriseDAO = new EnterpriseDAO();
    }

    @Test
    @DisplayName("Debe regresar una Enterprise cuando el id existe")
    void getEnterpriseById_existingId_returnsEnterprise() throws DataOperationException {
        Enterprise enterprise = enterpriseDAO.getEnterpriseById(EXISTING_ID);
        assertNotNull(enterprise);
        assertEquals(EXISTING_ID, enterprise.getEnterpriseId());
    }

    @Test
    @DisplayName("Debe lanzar NoSuchElementException cuando el id no existe")
    void getEnterpriseById_nonExistingId_throwsException() {
        assertThrows(NoSuchElementException.class, () ->
                enterpriseDAO.getEnterpriseById(NON_EXISTING_ID)
        );
    }

    @Test
    @DisplayName("registerUser debe regresar un id mayor a 0")
    void registerUser_validStudent_returnsPositiveId() throws DataOperationException {
        UserDAO userDAO = new UserDAO();
        Student student = new Student(
                0, "Test", "Usuario", "testuser@uv.mx",
                "pass123", "Masculino", true,
                "zS20018888", "2026-1", false, 0.0f, null, null
        );
        int id = userDAO.registerUser(student);
        assertTrue(id > 0);
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException con Enterprise nula")
    void registerEnterprise_nullEnterprise_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                enterpriseDAO.registerEnterprise(null)
        );
    }
}


// ============================================================
//  EducationalExperienceDAO
// ============================================================
class EducationalExperienceDAOTest {

    private EducationalExperienceDAO eeDAO;
    private static final String EXISTING_NRC = "IS-001";
    private static final String NON_EXISTING_NRC = "XX-999";

    @BeforeEach
    void setUp() {
        eeDAO = new EducationalExperienceDAO();
    }

    @Test
    @DisplayName("Debe regresar una EducationalExperience cuando el NRC existe")
    void getEEByNrc_existingNrc_returnsExperience() throws DataOperationException {
        EducationalExperience ee = eeDAO.getEducationalExperienceByNrc(EXISTING_NRC);
        assertNotNull(ee);
        assertEquals(EXISTING_NRC, ee.getNrc());
    }

    @Test
    @DisplayName("Debe lanzar NoSuchElementException cuando el NRC no existe")
    void getEEByNrc_nonExistingNrc_throwsException() {
        assertThrows(NoSuchElementException.class, () ->
                eeDAO.getEducationalExperienceByNrc(NON_EXISTING_NRC)
        );
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException con NRC nulo")
    void getEEByNrc_nullNrc_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                eeDAO.getEducationalExperienceByNrc(null)
        );
    }

    @Test
    @DisplayName("Debe regresar lista no vacía de experiencias educativas")
    void getEducationalExperiences_returnsNonEmptyList() throws DataOperationException {
        List<EducationalExperience> list = eeDAO.getEducationalExperiences();
        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    @Test
    @DisplayName("Debe registrar una EducationalExperience válida y regresar true")
    void registerEE_validExperience_returnsTrue() throws DataOperationException {
        EducationalExperience ee = new EducationalExperience(
                "IS-TEST", "Prácticas Test", "Ingeniería de Software", "2026-1", null
        );
        boolean result = eeDAO.registerEducationalExperience(ee);
        assertTrue(result);
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException con EducationalExperience nula")
    void registerEE_nullExperience_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                eeDAO.registerEducationalExperience(null)
        );
    }
}


// ============================================================
//  ExpedientDAO
// ============================================================
class ExpedientDAOTest {

    private ExpedientDAO expedientDAO;

    @BeforeEach
    void setUp() {
        expedientDAO = new ExpedientDAO();
    }

   /* @Test
    @DisplayName("Debe regresar el expediente de un alumno existente")
    void getExpedientByEnrollment_existingStudent_returnsExpedient() {
        Expedient expedient = expedientDAO.getExpedientByEnrollment("zS20013001");
        assertNotNull(expedient);
    }*/

    @Test
    @DisplayName("isLoaded debe regresar false para documento no cargado")
    void isLoaded_documentNotLoaded_returnsFalse() throws DataOperationException {
        boolean loaded = expedientDAO.isLoaded("zS20013001", "evaluacion_competencias");
        assertFalse(loaded);
    }

    @Test
    @DisplayName("isLoaded debe lanzar IllegalArgumentException con tipo inválido")
    void isLoaded_invalidDocumentType_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                expedientDAO.isLoaded("zS20013001", "columna_inexistente")
        );
    }

    /*@Test
    @DisplayName("uploadDocument debe lanzar IllegalArgumentException con archivo nulo")
    void uploadDocument_nullFile_throwsIllegalArgument() throws DataOperationException {
        StudentDAO studentDAO = new StudentDAO();
        Student student = studentDAO.getStudentByEnrollment("zS20013001");
        Expedient expedient = expedientDAO.getExpedientByEnrollment("zS20013001");
        assertThrows(IllegalArgumentException.class, () ->
                expedientDAO.uploadDocument(expedient, "carta_liberacion", null)
        );
    }*/

   /* @Test
    @DisplayName("uploadDocument debe lanzar IllegalArgumentException con archivo inexistente")
    void uploadDocument_nonExistentFile_throwsIllegalArgument() {
        Expedient expedient = expedientDAO.getExpedientByEnrollment("zS20013001");
        File fakeFile = new File("ruta/que/no/existe.pdf");
        assertThrows(IllegalArgumentException.class, () ->
                expedientDAO.uploadDocument(expedient, "carta_liberacion", fakeFile)
        );
    }*/
}


// ============================================================
//  ActivityDAO
// ============================================================
class ActivityDAOTest {

    private ActivityDAO activityDAO;

    @BeforeEach
    void setUp() {
        activityDAO = new ActivityDAO();
    }

    @Test
    @DisplayName("Debe regresar lista de actividades para un proyecto existente")
    void getActivitiesByProject_existingProject_returnsList() throws DataOperationException {
        List<Activity> activities = activityDAO.getActivitiesByProjectId(1);
        assertNotNull(activities);
        assertFalse(activities.isEmpty());
    }

    @Test
    @DisplayName("Debe regresar lista vacía para proyecto sin actividades")
    void getActivitiesByProject_projectWithNoActivities_returnsEmptyList() throws DataOperationException {
        List<Activity> activities = activityDAO.getActivitiesByProjectId(99999);
        assertNotNull(activities);
        assertTrue(activities.isEmpty());
    }

   /* @Test
    @DisplayName("Debe registrar una actividad válida y regresar true")
    void registerActivity_validActivity_returnsTrue() {
        Activity activity = new Activity(0, "Actividad de prueba", "Observación test", 1);
        boolean result = activityDAO.insertActivity(activity);
        assertTrue(result);
    }*/

   /* @Test
    @DisplayName("Debe lanzar IllegalArgumentException con Activity nula")
    void registerActivity_nullActivity_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                activityDAO.insertActivity(null)
        );
    }*/
}


// ============================================================
//  ReportDAO
// ============================================================
class ReportDAOTest {

    private ReportDAO reportDAO;

    @BeforeEach
    void setUp() {
        reportDAO = new ReportDAO();
    }

    @Test
    @DisplayName("Debe regresar lista de reportes para un alumno existente")
    void getReportsByStudent_existingStudent_returnsList() throws DataOperationException {
        List<Report> reports = reportDAO.getReportsByStudentEnrollment("zS20013001");
        assertNotNull(reports);
    }

    @Test
    @DisplayName("Debe registrar un reporte válido y regresar true")
    void registerReport_validReport_returnsTrue() throws DataOperationException {
        Student student = new Student(
                0, "Ian", "Diaz", "psd@uv.mx", "punt325",
                "Hombre", false, "s24736217", "2024-01",
                false, 0.0f, null, null
        );
        Report report = new Report(0, 40.0f, "Parcial", Date.valueOf("20/04/2026"), "Observación", student);
        boolean result = reportDAO.createReport(report);
        assertTrue(result);
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException con Report nulo")
    void registerReport_nullReport_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                reportDAO.createReport(null)
        );
    }
}


// ============================================================
//  UserDAO
// ============================================================
class UserDAOTest {

    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
    }

    @Test
    @DisplayName("registerUser debe regresar un id mayor a 0")
    void registerUser_validStudent_returnsPositiveId() throws DataOperationException {
        Student student = new Student(
                0, "Test", "Usuario", "testuser@uv.mx",
                "pass123", "Masculino", true,
                "zS20018888", "2026-1", false, 0.0f, null, null
        );
        int id = userDAO.registerUser(student);
        assertTrue(id > 0);
    }

    @Test
    @DisplayName("registerUser debe lanzar IllegalArgumentException con Student nulo")
    void registerUser_nullStudent_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                userDAO.registerUser(null)
        );
    }

    @Test
    @DisplayName("updateUser debe regresar true para usuario existente")
    void updateUser_existingStudent_returnsTrue() throws DataOperationException {
        StudentDAO studentDAO = new StudentDAO();
        Student student = studentDAO.getStudentByEnrollment("zS20013001");
        student.setEmail("nuevo@uv.mx");
        boolean result = userDAO.updateUser(student);
        assertTrue(result);
    }
}