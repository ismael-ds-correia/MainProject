package com.qmasters.fila_flex.exception;

public class CannotInsertOnQueueException extends RuntimeException {
    public CannotInsertOnQueueException(String message) {
        super(message);
    }
}
