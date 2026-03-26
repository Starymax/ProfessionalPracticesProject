package mx.fei.logic.dao;

import mx.fei.dataaccess.DatabaseConnectionManager;
import mx.fei.logic.dto.Enterprise;
import mx.fei.logic.idao.IDAOEnterprise;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EnterpriseDAO implements IDAOEnterprise {
    @Override
    public Enterprise getEnterpriseById(int idEnterprise) {
        Enterprise enterprise = null;
        try {
            DatabaseConnectionManager connection = DatabaseConnectionManager.buildConnection();
            String query = "SELECT * FROM organizacion_vinculada WHERE id_empresa = ?";
            PreparedStatement preparedStatement = connection.preparedStatement(query);
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
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return enterprise;
    }
}
