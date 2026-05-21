package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.Enterprise;
import mx.fei.logic.exceptions.DataOperationException;
import mx.fei.logic.idao.IDAOEnterprise;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EnterpriseDAO implements IDAOEnterprise {
    private Logger logger = Logger.getLogger(EnterpriseDAO.class.getName());
    @Override
    public Enterprise getEnterpriseById(int idEnterprise) throws DataOperationException {
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
                String city = resultSet.getString("ciudad");
                long directUsers = resultSet.getLong("usuarios_directos");
                long indirectUsers = resultSet.getLong("usuarios_indirectos");
                boolean activeStatus = resultSet.getBoolean("estado_activo");
                String country = resultSet.getString("pais");
                enterprise = new Enterprise(idEnterprise, name, sector, phone, mail, city, directUsers, indirectUsers, activeStatus, country);
            }
            if (enterprise == null) {
                logger.log(Level.WARNING, "No se encontro a la empresa con el id: " + idEnterprise);
                throw new NoSuchElementException("No se encontro a la empresa");
            }
            return enterprise;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error obteniendo la organizacion vinculada",e);
            throw new DataOperationException("Error al obtener la organizacion vinculada");
        }
    }

    @Override
    public int registerEnterprise(Enterprise enterprise) throws DataOperationException {
        if (enterprise == null) {
            logger.log(Level.WARNING,"La empresa es nula");
            throw new IllegalArgumentException("La empresa no puede ser nula");
        }
        int generatedId = -1;
        String query = "INSERT INTO organizacion_vinculada (nombre_empresa, sector, telefono, correo, ciudad, usuarios_directos, usuarios_indirectos, estado_activo, pais) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
            preparedStatement.setString(1,enterprise.getName());
            preparedStatement.setString(2,enterprise.getSector());
            preparedStatement.setString(3,enterprise.getPhoneNumber());
            preparedStatement.setString(4,enterprise.getContactEmail());
            preparedStatement.setString(5,enterprise.getCity());
            preparedStatement.setLong(6,enterprise.getDirectUsers());
            preparedStatement.setLong(7,enterprise.getIndirectUsers());
            preparedStatement.setBoolean(8,enterprise.isActiveStatus());
            preparedStatement.setString(9,enterprise.getCountry());
            preparedStatement.executeUpdate();
            ResultSet keys = preparedStatement.getGeneratedKeys();
            if (keys.next()) {
                generatedId = keys.getInt(1);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error registrando la organizacion vinculada",e);
            throw new DataOperationException("Error al registrar la organizacion vinculada");
        }
        return generatedId;
    }

    @Override
    public List<Enterprise> getActiveEnterprises() throws DataOperationException {
        List<Enterprise> enterprises = new ArrayList<>();
        String query = "SELECT id_empresa from organizacion_vinculada WHERE estado_activo = true;";
        try (Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
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
            logger.log(Level.SEVERE, "Error obteniendo las organizaciones vinculadas activas",e);
            throw new DataOperationException("Error al obtener todas las organizaciones activas");
        }
        return enterprises;
    }

    public List<Enterprise> getEnterprises() throws DataOperationException {
        List<Enterprise> enterprises = new ArrayList<>();
        String query = "SELECT id_empresa from organizacion_vinculada;";
        try (Connection connection = DatabaseConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Integer> enterprisesIds = new ArrayList<>();
            while (resultSet.next()) {
                enterprisesIds.add(resultSet.getInt("id_empresa"));
            }
            resultSet.close();
            for (Integer enterpriseId : enterprisesIds) {
                enterprises.add(getEnterpriseById(enterpriseId));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error obteniendo las organizaciones activas",e);
            throw new DataOperationException("Error obteniendo todas las organizaciones");
        }
        return enterprises;
    }

    @Override
    public boolean modifyEnterprise(Enterprise enterprise) throws DataOperationException {
        if (enterprise == null) {
            logger.log(Level.WARNING, "La empresa es nula");
            throw new IllegalArgumentException("La empresa no puede ser nula");
        }
        boolean updated = false;
        String query = "UPDATE organizacion_vinculada SET nombre_empresa=?, sector=?, telefono=?, correo=?, ciudad=?, usuarios_directos=?, usuarios_indirectos=?, estado_activo=?, pais=? WHERE id_empresa=?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, enterprise.getName());
            preparedStatement.setString(2, enterprise.getSector());
            preparedStatement.setString(3, enterprise.getPhoneNumber());
            preparedStatement.setString(4, enterprise.getContactEmail());
            preparedStatement.setString(5, enterprise.getCity());
            preparedStatement.setLong(6, enterprise.getDirectUsers());
            preparedStatement.setLong(7, enterprise.getIndirectUsers());
            preparedStatement.setBoolean(8, enterprise.isActiveStatus());
            preparedStatement.setString(9, enterprise.getCountry());
            preparedStatement.setInt(10, enterprise.getEnterpriseId());
            updated = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al modificar la organizacion vinculada", e);
            throw new DataOperationException("Error al modificar la organizacion vinculada");
        }
        return updated;
    }
}
