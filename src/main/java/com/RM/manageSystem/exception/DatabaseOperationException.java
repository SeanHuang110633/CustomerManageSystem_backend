package com.RM.manageSystem.exception;

public class DatabaseOperationException extends RuntimeException{
    public DatabaseOperationException(String message, Exception e) {
        super(message);
    }

    public DatabaseOperationException(String message) {
        super(message);
    }
}
