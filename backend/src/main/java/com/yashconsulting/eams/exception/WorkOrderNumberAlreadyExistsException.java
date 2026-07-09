package com.yashconsulting.eams.exception;

public class WorkOrderNumberAlreadyExistsException extends RuntimeException {
    public WorkOrderNumberAlreadyExistsException(String message) {
        super(message);
    }
}
