package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.Enterprise;
import mx.fei.logic.exceptions.DataBaseConnectionException;
import mx.fei.logic.idao.IDAOEnterprise;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EnterpriseDAO implements IDAOEnterprise {
    private Logger logger = Logger.getLogger(EnterpriseDAO.class.getName());
    @Override
    public Enterprise getEnterpriseById(int idEnterprise) throws DataBaseConnectionException {
        Enterprise enterprise = null;
        String query = "SELECT * FROM organizacion_vinculada WHERE id_empresa = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setInt(1, idEnterprise);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("nombre_empresa");
                String sector = resultSet.getString("sector");
                String phone = resultSet.getString("telefono");
                String mail = resultSet.getString("correo");
                String address = resultSet.getString("direccion");
                int directUsers = resultSet.getInt("usuarios_directos");
                int indirectUsers = resultSet.getInt("usuarios_indirectos");
                boolean activeStatus = resultSet.getBoolean("estado_activo");
                enterprise = new Enterprise(idEnterprise, name, sector, phone, mail,
                        address, directUsers, indirectUsers, activeStatus);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error obteniendo la organizacion vinculada");
            throw new DataBaseConnectionException("Error al obtener la organizacion vinculada");
        }
        return enterprise;
    }

    @Override
    public int registerEnterprise(Enterprise enterprise) throws DataBaseConnectionException {
        int generatedId = -1;
        String queryRegisterEnterprise = "INSERT INTO organizacion_vinculada (nombre_empresa, sector, telefono, correo, direccion, usuarios_directos, usuarios_indirectos, estado_activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryRegisterEnterprise, Statement.RETURN_GENERATED_KEYS);) {
            preparedStatement.setString(1,enterprise.getName());
            preparedStatement.setString(2,enterprise.getSector());
            preparedStatement.setString(3,enterprise.getPhoneNumber());
            preparedStatement.setString(4,enterprise.getContactEmail());
            preparedStatement.setString(5,enterprise.getAddress());
            preparedStatement.setInt(6,enterprise.getDirectUsers());
            preparedStatement.setInt(7,enterprise.getIndirectUsers());
            preparedStatement.setBoolean(8,enterprise.isActiveStatus());
            preparedStatement.executeUpdate();
            ResultSet keys = preparedStatement.getGeneratedKeys();
            if (keys.next()) {
                generatedId = keys.getInt(1);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error registrando la organizacion vinculada");
            throw new DataBaseConnectionException("Error al registrar la organizacion vinculada");
        }
        return generatedId;
    }

    @Override
    public List<Enterprise> getActiveEnterprises() throws DataBaseConnectionException {
        List<Enterprise> enterprises = new ArrayList<>();
        String queryActiveEnterprises = "SELECT id_empresa from organizacion_vinculada WHERE estado_activo = true;";
        try (Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(queryActiveEnterprises)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Integer> enterprisesIds = new ArrayList<>();
            while (resultSet.next()){
                enterprisesIds.add(resultSet.getInt("id_empresa"));
            }
            resultSet.close();
            for (Integer id : enterprisesIds) {
                enterprises.add(getEnterpriseById(id));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error obteniendo las organizaciones vinculadas activas");
            throw new DataBaseConnectionException("Error al obtener todas las organizaciones activas");
        }
        return enterprises;
    }
}
