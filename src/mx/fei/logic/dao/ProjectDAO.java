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

/**
 * Data Access Object for Project.
 * Provides persistence and retrieval operations on the proyecto table,
 * joining the related organizacion_vinculada and responsable_proyecto tables.
 */
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

    /**
     * Builds a Project from the current row of the given result set,
     * including its associated enterprise and project manager when present.
     *
     * @param resultSet a result set positioned on a valid row
     * @return the Project represented by the current row
     * @throws SQLException if a column cannot be read from the result set
     */
    private Project buildProjectFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id_proyecto");
        String projectName = resultSet.getString("nombre_proyecto");
        String description = resultSet.getString("descripcion_proyecto");
        String generalObjective = resultSet.getString("objetivo_general");
        String immediateObjectives = resultSet.getString("objetivos_inmediatos");
        String mediateObjectives = resultSet.getString("objetivos_mediatos");
        String methodology = resultSet.getString("metodologia");
        String responsibilities = resultSet.getString("responsabilidades");
        String resources = resultSet.getString("recursos");
        Date startDate = resultSet.getDate("fecha_inicio");
        Date endDate = resultSet.getDate("fecha_final");
        boolean activeStatus = resultSet.getBoolean("estado_activo");
        int availablePlaces = resultSet.getInt("lugares_disponibles");
        Enterprise enterprise = null;
        if (resultSet.getObject("id_empresa") != null) {
            enterprise = new Enterprise(
                    resultSet.getInt("id_empresa"), resultSet.getString("nombre_empresa"), resultSet.getString("sector"),
                    resultSet.getString("tel_empresa"), resultSet.getString("correo_empresa"), resultSet.getString("ciudad"),
                    resultSet.getLong("usuarios_directos"), resultSet.getLong("usuarios_indirectos"),
                    resultSet.getBoolean("activo_empresa"), resultSet.getString("pais"));
        }
        ProjectManager projectManager = null;
        if (resultSet.getObject("id_responsable") != null) {
            projectManager = new ProjectManager(
                    resultSet.getInt("id_responsable"), resultSet.getString("nombre_responsable"),
                    resultSet.getString("correo_responsable"), resultSet.getString("telefono_responsable"),
                    resultSet.getString("cargo"), resultSet.getInt("id_empresa"));
        }
        return new Project(id, projectName, description, generalObjective, immediateObjectives, mediateObjectives, methodology, responsibilities, resources, startDate, endDate, activeStatus, availablePlaces, enterprise, projectManager);
    }

    /**
     * Retrieves a project by its identifier, including its enterprise and project manager.
     *
     * @param idProject the identifier of the project to retrieve
     * @return the matching Project
     * @throws NoSuchElementException if no project exists with the given id
     * @throws DataOperationException if a database error occurs
     */
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
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener el proyecto");
        }
    }

    /**
     * Registers a new project.
     *
     * @param project the project to register
     * @return the generated identifier of the new project, or -1 if none was generated
     * @throws DataOperationException if a database error occurs
     */
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
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al registrar el proyecto");
        }
        return generatedID;
    }

    /**
     * Retrieves all projects whose status is active.
     *
     * @return a list of active projects, empty if there are none
     * @throws DataOperationException if a database error occurs
     */
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
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener los proyectos activos");
        }
        return projects;
    }

    /**
     * Retrieves all projects regardless of their status.
     *
     * @return a list of all projects, empty if there are none
     * @throws DataOperationException if a database error occurs
     */
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
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al obtener los proyectos");
        }
        return projects;
    }

    /**
     * Retrieves the active projects that still have available places and at least one activity,
     * meaning they can take new students.
     *
     * @return a list of available projects, empty if there are none
     * @throws DataOperationException if a database error occurs
     */
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

    /**
     * Updates the data of an existing project.
     *
     * @param project the project carrying the updated data
     * @return true if at least one row was updated
     * @throws DataOperationException if a database error occurs
     */
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
            if (DAOUtils.isConnectionError(e)) {
                throw new DataOperationException("Error de conexión. Intente más tarde.");
            }
            throw new DataOperationException("Error al modificar los datos del proyecto");
        }
        return updated;
    }
}
