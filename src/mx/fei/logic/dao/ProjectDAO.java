package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.Activity;
import mx.fei.logic.dto.Enterprise;
import mx.fei.logic.dto.Project;
import mx.fei.logic.dto.ProjectManager;
import mx.fei.logic.exceptions.DataOperationException;
import mx.fei.logic.idao.IDAOProject;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProjectDAO implements IDAOProject {
    private static final Logger logger = Logger.getLogger(ProjectDAO.class.getName());

    private static final String BASE_JOIN_QUERY =
            "SELECT p.id_proyecto, p.nombre_proyecto, p.descripcion_proyecto, p.objetivo_general, " +
            "p.objetivos_inmediatos, p.objetivos_mediatos, p.metodologia, p.responsabilidades, " +
            "p.recursos, p.fecha_inicio, p.fecha_final, p.estado_activo, p.lugares_disponibles, " +
            "e.id_empresa, e.nombre_empresa, e.sector, e.telefono AS tel_empresa, " +
            "e.correo AS correo_empresa, e.ciudad, e.usuarios_directos, e.usuarios_indirectos, " +
            "e.estado_activo AS activo_empresa, e.pais, " +
            "r.id_responsable, r.nombre_responsable, r.correo_responsable, r.telefono_responsable, r.cargo " +
            "FROM proyecto p " +
            "LEFT JOIN organizacion_vinculada e ON p.id_empresa = e.id_empresa " +
            "LEFT JOIN responsable_proyecto r ON p.id_responsable = r.id_responsable";

    private Project buildProjectFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_proyecto");
        String projectName = rs.getString("nombre_proyecto");
        String description = rs.getString("descripcion_proyecto");
        String generalObjective = rs.getString("objetivo_general");
        String immediateObjectives = rs.getString("objetivos_inmediatos");
        String mediateObjectives = rs.getString("objetivos_mediatos");
        String methodology = rs.getString("metodologia");
        String responsibilities = rs.getString("responsabilidades");
        String resources = rs.getString("recursos");
        Date startDate = rs.getDate("fecha_inicio");
        Date endDate = rs.getDate("fecha_final");
        boolean activeStatus = rs.getBoolean("estado_activo");
        int availablePlaces = rs.getInt("lugares_disponibles");
        Enterprise enterprise = null;
        if (rs.getObject("id_empresa") != null) {
            enterprise = new Enterprise(
                    rs.getInt("id_empresa"), rs.getString("nombre_empresa"), rs.getString("sector"),
                    rs.getString("tel_empresa"), rs.getString("correo_empresa"), rs.getString("ciudad"),
                    rs.getLong("usuarios_directos"), rs.getLong("usuarios_indirectos"),
                    rs.getBoolean("activo_empresa"), rs.getString("pais"));
        }
        ProjectManager projectManager = null;
        if (rs.getObject("id_responsable") != null) {
            projectManager = new ProjectManager(
                    rs.getInt("id_responsable"), rs.getString("nombre_responsable"),
                    rs.getString("correo_responsable"), rs.getString("telefono_responsable"),
                    rs.getString("cargo"), rs.getInt("id_empresa"));
        }
        return new Project(id, projectName, description, generalObjective,
                immediateObjectives, mediateObjectives, methodology, responsibilities, resources,
                startDate, endDate, activeStatus, availablePlaces, enterprise, projectManager);
    }

    @Override
    public Project getProjectById(Integer idProject) throws DataOperationException {
        Project project = null;
        String query = BASE_JOIN_QUERY + " WHERE p.id_proyecto = ?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, idProject);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    project = buildProjectFromResultSet(resultSet);
                }
            }
            if (project == null) {
                logger.log(Level.WARNING, "No se encontro el proyecto con el id: " + idProject);
                throw new NoSuchElementException("No se encontro el proyecto");
            }
            return project;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error obteniendo el proyecto", e);
            throw new DataOperationException("Error al obtener el proyecto");
        }
    }

    @Override
    public int registerProject(Project project) throws DataOperationException {
        int generatedID = -1;
        String query = "INSERT INTO proyecto (nombre_proyecto, descripcion_proyecto, objetivo_general, objetivos_inmediatos, objetivos_mediatos, metodologia, responsabilidades, recursos, fecha_inicio, fecha_final, estado_activo, lugares_disponibles, id_empresa, id_responsable) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
            preparedStatement.setString(1,project.getNameProject());
            preparedStatement.setString(2,project.getDescriptionProject());
            preparedStatement.setString(3,project.getGeneralObjective());
            preparedStatement.setString(4,project.getImmediateObjectives());
            preparedStatement.setString(5,project.getMediatesObjectives());
            preparedStatement.setString(6,project.getMethodology());
            preparedStatement.setString(7,project.getResponsibilities());
            preparedStatement.setString(8,project.getResources());
            preparedStatement.setDate(9, project.getStartDate());
            preparedStatement.setDate(10,project.getFinalDate());
            preparedStatement.setBoolean(11,project.getActiveStatus());
            preparedStatement.setInt(12,project.getAvailablePlaces());
            preparedStatement.setInt(13,project.getEnterprise().getEnterpriseId());
            preparedStatement.setInt(14,project.getProjectManager().getProjectManagerId());
            preparedStatement.executeUpdate();
            try (ResultSet keys = preparedStatement.getGeneratedKeys()) {
                if (keys.next()) {
                    generatedID = keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error registrando el proyecto",e);
            throw new DataOperationException("Error al registrar el proyecto");
        }
        return generatedID;
    }

    @Override
    public List<Project> getActiveProjects() throws DataOperationException {
        List<Project> projects = new ArrayList<>();
        String query = BASE_JOIN_QUERY + " WHERE p.estado_activo = true";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                projects.add(buildProjectFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error obteniendo todos los proyectos activos", e);
            throw new DataOperationException("Error al obtener los proyectos activos");
        }
        return projects;
    }

    @Override
    public List<Project> getAllProjects() throws DataOperationException {
        List<Project> projects = new ArrayList<>();
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(BASE_JOIN_QUERY);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                projects.add(buildProjectFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error obteniendo todos los proyectos", e);
            throw new DataOperationException("Error al obtener los proyectos");
        }
        return projects;
    }

    @Override
    public List<Project> getAvailableProjects() throws DataOperationException {
        List<Project> availableProjects = new ArrayList<>();
        List<Project> activeProjects = getActiveProjects();
        ActivityDAO activityDAO = new ActivityDAO();
        for (Project project : activeProjects) {
            if (project.getAvailablePlaces() > 0) {
                List<Activity> activities = activityDAO.getActivitiesByProjectId(project.getProjectId());
                if (!activities.isEmpty()) {
                    availableProjects.add(project);
                }
            }
        }
        return availableProjects;
    }

    @Override
    public boolean modifyProject(Project project) throws DataOperationException {
        boolean updated = false;
        String query = "UPDATE proyecto SET nombre_proyecto = ?, descripcion_proyecto = ?, objetivo_general = ?, objetivos_inmediatos = ?, objetivos_mediatos = ?, metodologia = ?, recursos = ?, fecha_inicio = ?, fecha_final = ?, estado_activo = ?, lugares_disponibles = ?, id_empresa = ?, id_responsable = ?, responsabilidades = ? WHERE id_proyecto = ?";
        try (Connection connection = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, project.getNameProject());
            preparedStatement.setString(2, project.getDescriptionProject());
            preparedStatement.setString(3, project.getGeneralObjective());
            preparedStatement.setString(4, project.getImmediateObjectives());
            preparedStatement.setString(5, project.getMediatesObjectives());
            preparedStatement.setString(6, project.getMethodology());
            preparedStatement.setString(7, project.getResources());
            preparedStatement.setDate(8, project.getStartDate());
            preparedStatement.setDate(9, project.getFinalDate());
            preparedStatement.setBoolean(10, project.getActiveStatus());
            preparedStatement.setInt(11, project.getAvailablePlaces());
            preparedStatement.setInt(12, project.getEnterprise().getEnterpriseId());
            preparedStatement.setInt(13, project.getProjectManager().getProjectManagerId());
            preparedStatement.setInt(15, project.getProjectId());
            preparedStatement.setString(14, project.getResponsibilities());
            updated = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error modificando el proyecto",e);
            throw new DataOperationException("Error al modificar los datos del proyecto");
        }
        return updated;
    }
}
