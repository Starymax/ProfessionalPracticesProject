package mx.fei.logic.idao;

import mx.fei.logic.dto.Enterprise;

import java.util.List;

public interface IDAOEnterprise {
    Enterprise getEnterpriseById(int idEnterprise);

    int registerEnterprise(Enterprise enterprise);

    List<Enterprise> getActiveEnterprises();
}
