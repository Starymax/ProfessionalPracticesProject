package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.Enterprise;
import mx.fei.logic.dto.Project;
import mx.fei.logic.idao.IDAOProject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ProjectDAO implements IDAOProject {
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
                int idCompany = resultSet.getInt("id_empresa");
                EnterpriseDAO enterpriseDAO = new EnterpriseDAO();
                Enterprise enterprise = enterpriseDAO.getEnterpriseById(idCompany);
                project = new Project(idProject, projectName, description, generalObjective,
                        immediateObjectives, mediateObjectives, methodology, resources,
                        startDate, endDate, activeStatus, enterprise);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return project;
    }
}
