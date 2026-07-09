package com.yashconsulting.eams.exception;

public class PurchaseOrderNumberAlreadyExistsException extends RuntimeException {
    public PurchaseOrderNumberAlreadyExistsException(String message) {
        super(message);
    }
}
