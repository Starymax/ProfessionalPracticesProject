import java.sql.Date;
import mx.fei.logic.dao.*;
import mx.fei.logic.dto.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n========== MENÚ DE PRUEBA ==========");
            System.out.println("1.  Registrar estudiante");
            System.out.println("2.  Buscar estudiante por matrícula");
            System.out.println("3.  Listar todos los estudiantes");
            System.out.println("4.  Modificar estudiante");
            System.out.println("5.  Registrar profesor");
            System.out.println("6.  Buscar profesor por número de personal");
            System.out.println("7.  Listar todos los profesores");
            System.out.println("8.  Registrar empresa");
            System.out.println("9.  Buscar empresa por ID");
            System.out.println("10. Registrar proyecto");
            System.out.println("11. Buscar proyecto por ID");
            System.out.println("12. Listar proyectos activos");
            System.out.println("13. Listar proyectos disponibles");
            System.out.println("14. Asignar proyecto a estudiante");
            System.out.println("15. Registrar experiencia educativa");
            System.out.println("16. Buscar experiencia educativa por NRC");
            System.out.println("17. Asignar EE a estudiante");
            System.out.println("18. Registrar responsable de proyecto");
            System.out.println("19. Buscar responsable por ID");
            System.out.println("20. Crear reporte");
            System.out.println("21. Ver reportes de estudiante");
            System.out.println("22. Insertar actividad con registros semanales");
            System.out.println("23. Buscar actividad por ID");
            System.out.println("0.  Salir");
            System.out.print("Opción: ");
            opcion = Integer.parseInt(scanner.nextLine());

            switch (opcion) {
                case 1  -> probarRegistrarEstudiante();
                case 2  -> probarBuscarEstudiante();
                case 3  -> probarListarEstudiantes();
                case 4  -> probarModificarEstudiante();
                case 5  -> probarRegistrarProfesor();
                case 6  -> probarBuscarProfesor();
                case 7  -> probarListarProfesores();
                case 8  -> probarRegistrarEmpresa();
                case 9  -> probarBuscarEmpresa();
                case 10 -> probarRegistrarProyecto();
                case 11 -> probarBuscarProyecto();
                case 12 -> probarListarProyectosActivos();
                case 13 -> probarListarProyectosDisponibles();
                case 14 -> probarAsignarProyecto();
                case 15 -> probarRegistrarEE();
                case 16 -> probarBuscarEE();
                case 17 -> probarAsignarEE();
                case 18 -> probarRegistrarResponsable();
                case 19 -> probarBuscarResponsable();
                case 20 -> probarCrearReporte();
                case 21 -> probarReportesPorEstudiante();
                case 22 -> probarInsertarActividad();
                case 23 -> probarBuscarActividad();
                case 0  -> System.out.println("Saliendo...");
                default -> System.out.println("Opción no válida.");
            }
        }
    }

    static void probarRegistrarEstudiante() {
        StudentDAO dao = new StudentDAO();
        Student student = new Student(
                0, "Diego", "Leon", "diegoLeon@uv.mx", "pass456",
                "Hombre", true, "s24014150", "2025-01",
                false, 0.0f, null, null
        );
        boolean result = dao.registerStudent(student);
        System.out.println("Registrar estudiante: " + result);
    }

    static void probarBuscarEstudiante() {
        System.out.print("Matrícula: ");
        String enrollment = scanner.nextLine();
        StudentDAO dao = new StudentDAO();
        Student student = dao.getStudentByEnrollment(enrollment);
        System.out.println("Estudiante encontrado: " + student.getName());
    }

    static void probarListarEstudiantes() {
        StudentDAO dao = new StudentDAO();
        List<Student> students = dao.getStudents();
        System.out.println("Total estudiantes: " + students.size());
        for (Student student : students) {
            System.out.println(student.getName());
        }
    }

    static void probarModificarEstudiante() {
        System.out.print("Matrícula del estudiante a modificar: ");
        String enrollment = scanner.nextLine();
        StudentDAO dao = new StudentDAO();
        Student student = dao.getStudentByEnrollment(enrollment);
        if (student == null) { System.out.println("No encontrado."); return; }
        student.setGrade(9.5f);
        student.setPeriod("2025-02");
        boolean result = dao.modifyStudent(student);
        System.out.println("Modificar estudiante: " + result);
    }

    static void probarRegistrarProfesor() {
        ProfessorDAO dao = new ProfessorDAO();
        Professor professor = new Professor(
                0, "Juan Carlos", "Arriaga", "juanca@uv.mx", "pass789",
                "Hombre", true, 32514, true, false, "Vespertino"
        );
        boolean result = dao.registerProfessor(professor);
        System.out.println("Registrar profesor: " + result);
    }

    static void probarBuscarProfesor() {
        System.out.print("Número de personal: ");
        int num = Integer.parseInt(scanner.nextLine());
        ProfessorDAO dao = new ProfessorDAO();
        Professor professor = dao.getProfessorByPersonalNumber(num);
        System.out.println("Profesor: " + professor.getName());
    }

    static void probarListarProfesores() {
        ProfessorDAO dao = new ProfessorDAO();
        List<Professor> list = dao.getProfessors();
        System.out.println("Total profesores: " + list.size());
        for (Professor professor : list) {
            System.out.println(professor.getName());
        }
    }

    static void probarRegistrarEmpresa() {
        EnterpriseDAO dao = new EnterpriseDAO();
        Enterprise enterprise = new Enterprise(
                0, "Tech MX", "Tecnología", "2281234567",
                "contacto@techmx.com", "Xalapa, Ver.", 50, 100, true
        );
        int id = dao.registerEnterprise(enterprise);
        System.out.println("Empresa registrada con ID: " + id);
    }

    static void probarBuscarEmpresa() {
        System.out.print("ID empresa: ");
        int id = Integer.parseInt(scanner.nextLine());
        EnterpriseDAO dao = new EnterpriseDAO();
        Enterprise enterprise = dao.getEnterpriseById(id);
        System.out.println("Empresa: " + enterprise.getName());
    }

    static void probarRegistrarProyecto() {
        EnterpriseDAO eDao = new EnterpriseDAO();
        System.out.print("ID empresa para el proyecto: ");
        int idEmpresa = Integer.parseInt(scanner.nextLine());
        Enterprise enterprise = eDao.getEnterpriseById(idEmpresa);
        if (enterprise == null) { System.out.println("Empresa no encontrada."); return; }

        ProjectDAO dao = new ProjectDAO();
        Project project = new Project(
                0, "Sistema de inventario", "Descripción del proyecto",
                "Optimizar inventario", "Módulo de reportes", "Módulo de alertas",
                "SCRUM", "Computadoras, servidor",
                Date.valueOf("2025-01-15"), Date.valueOf("2025-07-15"),
                true, 5, enterprise
        );
        int id = dao.registerProject(project);
        System.out.println("Proyecto registrado con ID: " + id);
    }

    static void probarBuscarProyecto() {
        System.out.print("ID proyecto: ");
        int id = Integer.parseInt(scanner.nextLine());
        ProjectDAO dao = new ProjectDAO();
        Project project = dao.getProjectById(id);
        System.out.println("Proyecto: " + project.getNameProject());
    }

    static void probarListarProyectosActivos() {
        ProjectDAO dao = new ProjectDAO();
        List<Project> list = dao.getActiveProjects();
        System.out.println("Proyectos activos: " + list.size());
        for (Project project : list) {
            System.out.println(project.getNameProject());
        }
    }

    static void probarListarProyectosDisponibles() {
        ProjectDAO dao = new ProjectDAO();
        List<Project> list = dao.getAvailableProjects();
        System.out.println("Proyectos disponibles: " + list.size());
        for (Project project : list) {
            System.out.println(project.getNameProject());
        }
    }

    static void probarAsignarProyecto() {
        System.out.print("Matrícula del estudiante: ");
        String matricula = scanner.nextLine();
        System.out.print("ID del proyecto: ");
        int idProyecto = Integer.parseInt(scanner.nextLine());

        StudentDAO sDao = new StudentDAO();
        ProjectDAO pDao = new ProjectDAO();
        Student student = sDao.getStudentByEnrollment(matricula);
        Project project = pDao.getProjectById(idProyecto);

        if (student == null || project == null) { System.out.println("Datos no encontrados."); return; }
        boolean result = sDao.assignProject(student, project);
        System.out.println("Asignar proyecto: " + result);
    }

    static void probarRegistrarEE() {
        EducationalExperienceDAO dao = new EducationalExperienceDAO();
        EducationalExperience ee = new EducationalExperience(
                "NRC-001", "Prácticas Profesionales", "ISIC", "2025-02", null
        );
        boolean result = dao.registerEducationalExperience(ee);
        System.out.println("Registrar EE: " + result);
    }

    static void probarBuscarEE() {
        System.out.print("NRC: ");
        String nrc = scanner.nextLine();
        EducationalExperienceDAO dao = new EducationalExperienceDAO();
        EducationalExperience ee = dao.getEducationalExperienceByNrc(nrc);
        System.out.println("EE: " + ee.getName());
    }

    static void probarAsignarEE() {
        System.out.print("Matrícula del estudiante: ");
        String matricula = scanner.nextLine();
        System.out.print("NRC de la EE: ");
        String nrc = scanner.nextLine();

        StudentDAO sDao = new StudentDAO();
        EducationalExperienceDAO eDao = new EducationalExperienceDAO();
        Student student = sDao.getStudentByEnrollment(matricula);
        EducationalExperience ee = eDao.getEducationalExperienceByNrc(nrc);

        if (student == null || ee == null) { System.out.println("Datos no encontrados."); return; }
        boolean result = sDao.assignEducationalExperience(student, ee);
        System.out.println("Asignar EE: " + result);
    }

    static void probarRegistrarResponsable() {
        System.out.print("ID del proyecto: ");
        int idProyecto = Integer.parseInt(scanner.nextLine());
        ProjectDAO pDao = new ProjectDAO();
        Project project = pDao.getProjectById(idProyecto);
        if (project == null) { System.out.println("Proyecto no encontrado."); return; }

        ProjectManagerDAO dao = new ProjectManagerDAO();
        ProjectManager pm = new ProjectManager(
                0, "Luis Mendoza", "luis@empresa.com", "2289876543", "Gerente TI", project
        );
        boolean result = dao.registerProjectManager(pm);
        System.out.println("Registrar responsable: " + result);
    }

    static void probarBuscarResponsable() {
        System.out.print("ID responsable: ");
        int id = Integer.parseInt(scanner.nextLine());
        ProjectManagerDAO dao = new ProjectManagerDAO();
        ProjectManager pm = dao.getProjectManagerById(id);
        System.out.println("Responsable: " + pm.getName());
    }

    static void probarCrearReporte() {
        System.out.print("Matrícula del estudiante: ");
        String matricula = scanner.nextLine();
        StudentDAO sDao = new StudentDAO();
        Student student = sDao.getStudentByEnrollment(matricula);
        if (student == null) { System.out.println("Estudiante no encontrado."); return; }

        ReportDAO dao = new ReportDAO();
        Report report = new Report(
                0, 40.0f, "Parcial", Date.valueOf("2025-04-01"),
                "Sin observaciones", student
        );
        boolean result = dao.createReport(report);
        System.out.println("Crear reporte: " + result);
    }

    static void probarReportesPorEstudiante() {
        System.out.print("Matrícula: ");
        String matricula = scanner.nextLine();
        ReportDAO dao = new ReportDAO();
        List<Report> reports = dao.getReportsByStudentEnrollment(matricula);
        System.out.println("Reportes encontrados: " + reports.size());
        for (Report report : reports) {
            System.out.println(report.getReportType());
        }
    }

    static void probarInsertarActividad() {
        System.out.print("ID del proyecto: ");
        int idProyecto = Integer.parseInt(scanner.nextLine());
        ProjectDAO pDao = new ProjectDAO();
        Project project = pDao.getProjectById(idProyecto);
        if (project == null) { System.out.println("Proyecto no encontrado."); return; }

        Activity activity = new Activity(0, "Análisis de requerimientos", "Sin observaciones", project);

        ArrayList<WeeklyLog> logs = new ArrayList<>();
        logs.add(new WeeklyLog(0,  1, 8.0f, 10.0f, activity));
        logs.add(new WeeklyLog(0,  2, 9.0f, 10.0f, activity));

        ActivityDAO dao = new ActivityDAO();
        boolean result = dao.insertActivity(activity, idProyecto, logs);
        System.out.println("Insertar actividad: " + result);
    }

    static void probarBuscarActividad() {
        System.out.print("ID actividad: ");
        int id = Integer.parseInt(scanner.nextLine());
        ActivityDAO dao = new ActivityDAO();
        Activity activity = dao.getActivityById(id);
        System.out.println("Actividad: " + activity.getName());
        if (activity != null) {
            List<WeeklyLog> logs = dao.getWeeklyLogsByActivityId(id);
            System.out.println("Registros semanales: " + logs.size());
            logs.forEach(System.out::println);
        }
    }

    /*
    evitar varios return en un mismo método
    validacion de registerUser en registerProfessor y Student creo non (cambiar lo del numero magico -1)
     */
}