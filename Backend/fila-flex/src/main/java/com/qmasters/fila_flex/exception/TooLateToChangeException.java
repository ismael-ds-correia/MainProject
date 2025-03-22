package com.qmasters.fila_flex.exception;

public class TooLateToChangeException extends RuntimeException {
    public TooLateToChangeException(String message) {
        super(message);
    }
}
