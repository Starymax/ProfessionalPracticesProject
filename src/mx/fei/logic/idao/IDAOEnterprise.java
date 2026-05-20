package mx.fei.logic.idao;

import mx.fei.logic.dto.Enterprise;
import mx.fei.logic.exceptions.DataOperationException;

import java.util.List;

public interface IDAOEnterprise {
    Enterprise getEnterpriseById(int idEnterprise) throws DataOperationException;

    int registerEnterprise(Enterprise enterprise) throws DataOperationException;

    List<Enterprise> getActiveEnterprises() throws DataOperationException;

    List<Enterprise> getEnterprises() throws DataOperationException;

    boolean modifyEnterprise(Enterprise enterprise) throws DataOperationException;
}
