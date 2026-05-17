package mx.fei.logic.idao;

import mx.fei.logic.dto.Period;
import mx.fei.logic.exceptions.DataOperationException;

import java.util.List;

public interface IDAOPeriod {
    public boolean activatePeriod(int year, int number) throws DataOperationException;

    public Period getActivePeriod() throws DataOperationException;

    public List<Period> getAllPeriods() throws DataOperationException;
}
