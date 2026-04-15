package mx.fei.logic.idao;

import mx.fei.logic.dto.Enterprise;
import mx.fei.logic.exceptions.DataBaseConnectionException;

import java.util.List;

public interface IDAOEnterprise {
    Enterprise getEnterpriseById(int idEnterprise) throws DataBaseConnectionException;

    int registerEnterprise(Enterprise enterprise) throws DataBaseConnectionException;

    List<Enterprise> getActiveEnterprises() throws DataBaseConnectionException;
}
