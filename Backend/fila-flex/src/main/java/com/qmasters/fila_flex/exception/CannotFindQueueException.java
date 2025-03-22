package com.qmasters.fila_flex.exception;

public class CannotFindQueueException extends RuntimeException {
    public CannotFindQueueException(String message) {
        super(message);
    }
}
