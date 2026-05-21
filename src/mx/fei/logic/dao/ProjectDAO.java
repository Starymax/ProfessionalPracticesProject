package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
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
    private Logger logger = Logger.getLogger(ProjectDAO.class.getName());
    @Override
    public Project getProjectById(Integer idProject) throws DataOperationException {
        Project project = null;
        String query = "SELECT * FROM proyecto WHERE id_proyecto = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, idProject);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String projectName = resultSet.getString("nombre_proyecto");
                String description = resultSet.getString("descripcion_proyecto");
                String generalObjective = resultSet.getString("objetivo_general");
                String immediateObjectives = resultSet.getString("objetivos_inmediatos");
                String mediateObjectives = resultSet.getString("objetivos_mediatos");
                String methodology = resultSet.getString("metodologia");
                String responsabilities = resultSet.getString("responsabilidades");
                String resources = resultSet.getString("recursos");
                Date startDate = resultSet.getDate("fecha_inicio");
                Date endDate = resultSet.getDate("fecha_final");
                boolean activeStatus = resultSet.getBoolean("estado_activo");
                int available_places = resultSet.getInt("lugares_disponibles");
                int idCompany = resultSet.getInt("id_empresa");
                int idProjectManager = resultSet.getInt("id_responsable");
                EnterpriseDAO enterpriseDAO = new EnterpriseDAO();
                Enterprise enterprise = enterpriseDAO.getEnterpriseById(idCompany);
                ProjectManagerDAO projectManagerDAO = new ProjectManagerDAO();
                ProjectManager projectManager = projectManagerDAO.getProjectManagerById(idProjectManager);
                project = new Project(idProject, projectName, description, generalObjective,
                        immediateObjectives, mediateObjectives, methodology, responsabilities, resources,
                        startDate, endDate, activeStatus, available_places, enterprise, projectManager);
            }
            if (project == null) {
                logger.log(Level.WARNING, "No se encontro el proyecto con el id: " + idProject);
                throw new NoSuchElementException("No se encontro el proyecto");
            }
            return project;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error obteniendo el proyecto",e);
            throw  new DataOperationException("Error al obtener el proyecto");
        }
    }

    @Override
    public int registerProject(Project project) throws DataOperationException {
        int generatedID = -1;
        String query = "INSERT INTO proyecto (nombre_proyecto, descripcion_proyecto, objetivo_general, objetivos_inmediatos, objetivos_mediatos, metodologia, responsabilidades, recursos, fecha_inicio, fecha_final, estado_activo, lugares_disponibles, id_empresa, id_responsable) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnectionManager.getConnection();
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
            ResultSet keys = preparedStatement.getGeneratedKeys();
            if (keys.next()) {
                generatedID = keys.getInt(1);
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
        String query = "SELECT id_proyecto FROM proyecto WHERE estado_activo = true";
        try (Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Integer> projectIDs = new ArrayList<>();
            while (resultSet.next()) {
                projectIDs.add((resultSet.getInt("id_proyecto")));
            }
            resultSet.close();
            for (Integer projectID : projectIDs) {
                projects.add(getProjectById(projectID));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error obteniendo todos los proyectos activos",e);
            throw new DataOperationException("Error al obtener los proyectos activos");
        }
        return projects;
    }

    @Override
    public List<Project> getAllProjects() throws DataOperationException {
        List<Project> projects = new ArrayList<>();
        String query = "SELECT id_proyecto FROM proyecto";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Integer> projectIDs = new ArrayList<>();
            while (resultSet.next()) {
                projectIDs.add((resultSet.getInt("id_proyecto")));
            }
            resultSet.close();
            for (Integer projectID : projectIDs) {
                projects.add(getProjectById(projectID));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error obteniendo todos los proyectos",e);
            throw new DataOperationException("Error al obtener los proyectos");
        }
        return projects;
    }

    @Override
    public List<Project> getAvailableProjects() throws DataOperationException {
        List<Project> availableProjects = new ArrayList<>();
        List<Project> activeProjects = getActiveProjects();
        for (Project project : activeProjects) {
            if (project.getAvailablePlaces() > 0) {
                availableProjects.add(project);
            }
        }
        return availableProjects;
    }

    @Override
    public boolean modifyProject(Project project) throws DataOperationException {
        boolean updated = false;
        String query = "UPDATE proyecto SET nombre_proyecto = ?, descripcion_proyecto = ?, objetivo_general = ?, objetivos_inmediatos = ?, objetivos_mediatos = ?, metodologia = ?, recursos = ?, fecha_inicio = ?, fecha_final = ?, estado_activo = ?, lugares_disponibles = ?, id_empresa = ?, id_responsable = ?, responsabilidades = ? WHERE id_proyecto = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
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
