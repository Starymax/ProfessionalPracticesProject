import mx.fei.logic.dao.*;
import mx.fei.logic.dto.*;
import mx.fei.logic.exceptions.DataOperationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class StudentDAOTest {

    private StudentDAO studentDAO;
    private static final String EXISTING_ENROLLMENT = "zS20013001";
    private static final String NON_EXISTING_ENROLLMENT = "zS99999999";

    @BeforeEach
    void setUp() {
        studentDAO = new StudentDAO();
    }

    @Test
    @DisplayName("Debe regresar un Student cuando la matricula existe")
    void getStudentByEnrollment_existingEnrollment_returnsStudent() throws DataOperationException {
        Student student = studentDAO.getStudentByEnrollment(EXISTING_ENROLLMENT);
        assertNotNull(student);
        assertEquals(EXISTING_ENROLLMENT, student.getEnrollment());
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando la matricula no existe")
    void getStudentByEnrollment_nonExistingEnrollment_throwsException() {
        assertThrows(NoSuchElementException.class, () ->
                studentDAO.getStudentByEnrollment(NON_EXISTING_ENROLLMENT)
        );
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException con matricula nula")
    void getStudentByEnrollment_nullEnrollment_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                studentDAO.getStudentByEnrollment(null)
        );
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException con matricula vacia")
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
                "S24017414", "2026-1", false, 0.0f, null, null
        );
        boolean result = studentDAO.registerStudent(newStudent);
        assertTrue(result);
    }

    @Test
    @DisplayName("Debe lanzar IllegalStateException si la matricula ya existe")
    void registerStudent_duplicateEnrollment_throwsIllegalState() {
        Student duplicate = new Student(
                1, "Duplicado", "Test", "dup@uv.mx",
                "pass123", "Femenino", true,
                EXISTING_ENROLLMENT, "2026-1", false, 0.0f, null, null
        );
        assertThrows(IllegalStateException.class, () ->
                studentDAO.registerStudent(duplicate)
        );
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException con estudiante nulo")
    void registerStudent_nullStudent_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                studentDAO.registerStudent(null)
        );
    }

    @Test
    @DisplayName("Debe modificar un alumno existente y regresar true")
    void modifyStudent_existingStudent_returnsTrue() throws DataOperationException {
        Student student = studentDAO.getStudentByEnrollment(EXISTING_ENROLLMENT);
        student.setPeriod("2026-2");
        boolean result = studentDAO.modifyStudent(student);
        assertTrue(result);
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException con estudiante nulo")
    void modifyStudent_nullStudent_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                studentDAO.modifyStudent(null)
        );
    }

    @Test
    @DisplayName("Debe regresar una lista no vacia de estudiantes")
    void getStudents_returnsNonEmptyList() throws DataOperationException {
        List<Student> students = studentDAO.getStudents();
        assertNotNull(students);
        assertFalse(students.isEmpty());
    }

    @Test
    @DisplayName("Debe regresar una lista (puede estar vacia) de estudiantes sin proyecto")
    void getStudentsWithoutProject_returnsList() throws DataOperationException {
        List<Student> students = studentDAO.getStudentsWithoutProject();
        assertNotNull(students);
    }

    @Test
    @DisplayName("Todos los estudiantes regresados deben tener estado_activo = true")
    void getActiveStudents_allHaveActiveStatus() throws DataOperationException {
        List<Student> students = studentDAO.getActiveStudents();
        assertNotNull(students);
        students.forEach(s -> assertTrue(s.isActive()));
    }

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
    @DisplayName("Debe regresar lista no vacia de proyectos activos")
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
    @DisplayName("Debe lanzar IllegalArgumentException con organizacion nula")
    void registerEnterprise_nullEnterprise_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                enterpriseDAO.registerEnterprise(null)
        );
    }
}

class EducationalExperienceDAOTest {
    private EducationalExperienceDAO educationalExperienceDAO;
    private static final String EXISTING_NRC = "NRC-001";
    private static final String NON_EXISTING_NRC = "XX-999";

    @BeforeEach
    void setUp() {
        educationalExperienceDAO = new EducationalExperienceDAO();
    }

    @Test
    @DisplayName("Debe regresar una experiencia cuando el NRC existe")
    void getEEByNrc_existingNrc_returnsExperience() throws DataOperationException {
        EducationalExperience educationalExperience = educationalExperienceDAO.getEducationalExperienceByNrc(EXISTING_NRC);
        assertNotNull(educationalExperience);
        assertEquals(EXISTING_NRC, educationalExperience.getNrc());
    }

    @Test
    @DisplayName("Debe lanzar NoSuchElementException cuando el NRC no existe")
    void getEEByNrc_nonExistingNrc_throwsException() {
        assertThrows(NoSuchElementException.class, () ->
                educationalExperienceDAO.getEducationalExperienceByNrc(NON_EXISTING_NRC)
        );
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException con nrc nulo")
    void getEEByNrc_nullNrc_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                educationalExperienceDAO.getEducationalExperienceByNrc(null)
        );
    }

    @Test
    @DisplayName("Debe regresar lista no vacia de experiencias educativas")
    void getEducationalExperiences_returnsNonEmptyList() throws DataOperationException {
        List<EducationalExperience> educationalExperiences = educationalExperienceDAO.getEducationalExperiences();
        assertNotNull(educationalExperiences);
        assertFalse(educationalExperiences.isEmpty());
    }

    @Test
    @DisplayName("Debe registrar una EducationalExperience valida y regresar true")
    void registerEE_validExperience_returnsTrue() throws DataOperationException {
        EducationalExperience educationalExperience = new EducationalExperience(
                "IS-TEST6", "Practicas Test", "Ingenieria de Software", "2026-1", null
        );
        boolean result = educationalExperienceDAO.registerEducationalExperience(educationalExperience);
        assertTrue(result);
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException con experiencia educativa nula")
    void registerEE_nullExperience_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                educationalExperienceDAO.registerEducationalExperience(null)
        );
    }
}

class ExpedientDAOTest {

    private ExpedientDAO expedientDAO;

    @BeforeEach
    void setUp() {
        expedientDAO = new ExpedientDAO();
    }

    @Test
    @DisplayName("isLoaded debe regresar false para documento no cargado")
    void isLoaded_documentNotLoaded_returnsFalse() throws DataOperationException {
        boolean loaded = expedientDAO.isLoaded("zS20013001", "evaluacion_competencias");
        assertFalse(loaded);
    }

    @Test
    @DisplayName("isLoaded debe lanzar IllegalArgumentException con tipo invalido")
    void isLoaded_invalidDocumentType_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                expedientDAO.isLoaded("zS20013001", "columna_inexistente")
        );
    }
}

class ActivityDAOTest {
    private ProjectDAO projectDAO;
    private ActivityDAO activityDAO;

    @BeforeEach
    void setUp() {
        activityDAO = new ActivityDAO();
        projectDAO = new ProjectDAO();
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

    @Test
    @DisplayName("Debe registrar una actividad valida y regresar true")
    void registerActivity_validActivity_returnsTrue() throws DataOperationException {
        Project project = projectDAO.getProjectById(1);
        ArrayList<WeeklyLog> weeklyLogs = new ArrayList<>();
        weeklyLogs.add(activityDAO.getWeeklyLogById(1));
        Activity activity = new Activity(0, "Actividad de prueba", "Observación test", project);
        boolean result = activityDAO.insertActivity(activity,project,weeklyLogs);
        assertTrue(result);
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException con actividad nula")
    void registerActivity_nullActivity_throwsIllegalArgument() throws DataOperationException {
        ProjectDAO projectDAO = new ProjectDAO();
        ArrayList<WeeklyLog> weeklyLogs = new ArrayList<>();
        weeklyLogs.add(activityDAO.getWeeklyLogById(1));
        Project project = projectDAO.getProjectById(1);
        assertThrows(IllegalArgumentException.class, () ->
                activityDAO.insertActivity(null, project,weeklyLogs)
        );
    }
}

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
    @DisplayName("Debe registrar un reporte valido y regresar verdadero")
    void registerReport_validReport_returnsTrue() throws DataOperationException {
        StudentDAO studentDAO = new StudentDAO();
        Student student = studentDAO.getStudentByEnrollment("s24014150");
        Report report = new Report(0, 40.0f, "Parcial", Date.valueOf(LocalDate.now()), "Observación", student);
        boolean result = reportDAO.createReport(report);
        assertTrue(result);
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException con reporte nulo")
    void registerReport_nullReport_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                reportDAO.createReport(null)
        );
    }
}

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