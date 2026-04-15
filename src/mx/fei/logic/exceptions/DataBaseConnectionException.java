package mx.fei.logic.exceptions;

import java.sql.SQLException;

public class DataBaseConnectionException extends SQLException {
    public DataBaseConnectionException(String message) {
        super(message);
    }
}