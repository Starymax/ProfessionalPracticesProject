package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.Enterprise;
import mx.fei.logic.dto.Project;
import mx.fei.logic.idao.IDAOProject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProjectDAO implements IDAOProject {
    private Logger logger = Logger.getLogger(ProjectDAO.class.getName());
    @Override
    public Project getProjectById(int idProject) {
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
                String resources = resultSet.getString("recursos");
                Date startDate = resultSet.getDate("fecha_inicio");
                Date endDate = resultSet.getDate("fecha_final");
                boolean activeStatus = resultSet.getBoolean("estado_activo");
                int available_places = resultSet.getInt("lugares_disponibles");
                int idCompany = resultSet.getInt("id_empresa");
                EnterpriseDAO enterpriseDAO = new EnterpriseDAO();
                Enterprise enterprise = enterpriseDAO.getEnterpriseById(idCompany);
                project = new Project(idProject, projectName, description, generalObjective,
                        immediateObjectives, mediateObjectives, methodology, resources,
                        startDate, endDate, activeStatus, available_places, enterprise);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return project;
    }

    @Override
    public int registerProject(Project project) {
        int generatedID = -1;
        String queryRegisterProject = "INSERT INTO proyecto (nombre_proyecto, descripcion_proyecto, objetivo_general, objetivos_inmediatos, objetivos_mediatos, metodologia, recursos, fecha_inicio, fecha_final, estado_activo, lugares_disponibles, id_empresa) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryRegisterProject, Statement.RETURN_GENERATED_KEYS);) {
            preparedStatement.setString(1,project.getNameProject());
            preparedStatement.setString(2,project.getDescriptionProject());
            preparedStatement.setString(3,project.getGeneralObjective());
            preparedStatement.setString(4,project.getImmediateObjectives());
            preparedStatement.setString(5,project.getMediatesObjectives());
            preparedStatement.setString(6,project.getMethodology());
            preparedStatement.setString(7,project.getResources());
            preparedStatement.setDate(8, project.getStartDate());
            preparedStatement.setDate(9,project.getFinalDate());
            preparedStatement.setBoolean(10,project.getActiveStatus());
            preparedStatement.setInt(11,project.getAvailablePlaces());
            preparedStatement.setInt(12,project.getEnterprise().getEnterpriseId());
            preparedStatement.executeUpdate();
            ResultSet keys = preparedStatement.getGeneratedKeys();
            if (keys.next()) {
                generatedID = keys.getInt(1);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return generatedID;
    }

    @Override
    public List<Project> getActiveProjects() {
        List<Project> projects = new ArrayList<>();
        String queryActiveProjects = "SELECT id_proyecto FROM proyecto WHERE estado_activo = true";
        try (Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(queryActiveProjects)) {
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
            logger.log(Level.SEVERE, e.getMessage());
        }
        return projects;
    }

    @Override
    public List<Project> getAvailableProjects() {
        List<Project> availableProjects = new ArrayList<>();
        List<Project> activeProjects = getActiveProjects();
        for (Project project : activeProjects) {
            if (project.getAvailablePlaces() != 0) {
                modifyProject(project);
                availableProjects.add(project);
            }
        }
        return availableProjects;
    }

    @Override
    public boolean modifyProject(Project project) {
        boolean updated = false;
        String queryModifyProject = "UPDATE proyecto SET nombre_proyecto = ?, descripcion_proyecto = ?, objetivo_general = ?, objetivos_inmediatos = ?, objetivos_mediatos = ?, metodologia = ?, recursos = ?, fecha_inicio = ?, fecha_final = ?, estado_activo = ?, lugares_disponibles = ?, id_empresa = ? WHERE id_proyecto = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryModifyProject)) {
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
            preparedStatement.setInt(13, project.getProjectId());
            updated = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return updated;
    }
}
