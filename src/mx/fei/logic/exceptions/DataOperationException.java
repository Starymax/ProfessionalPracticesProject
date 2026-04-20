package mx.fei.logic.exceptions;

import java.sql.SQLException;

public class DataOperationException extends SQLException {
    public DataOperationException(String message) {
        super(message);
    }
}