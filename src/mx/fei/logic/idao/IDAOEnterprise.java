package mx.fei.logic.idao;

import mx.fei.logic.dto.Enterprise;

public interface IDAOEnterprise {
    public Enterprise getEnterpriseById(int idEnterprise);

     public int registerEnterprise(Enterprise enterprise);
}
