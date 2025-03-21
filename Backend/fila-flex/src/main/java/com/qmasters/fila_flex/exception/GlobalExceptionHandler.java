package com.qmasters.fila_flex.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class) //(por enquanto não é usado)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );

        logger.warn("Erro de validação: " + errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    
    @ExceptionHandler(InvalidRatingException.class) //erro de avaliacao invalida
    public ResponseEntity<String> handleInvalidRatingException(InvalidRatingException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class) //erro de argumento invalido
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("Erro de argumento inválido: ", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class) //erro generico
    public ResponseEntity<String> handleGenericException(Exception ex) {
        logger.error("Erro inesperado no sistema: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro interno no servidor.");
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException ex) {
        logger.warn("Recurso não encontrado: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    // =============================== Erros do JPA ==============================
    @ExceptionHandler(ConstraintViolationException.class) //erro de validação que é jogado pelo Model da classe
    public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException ex) {
        logger.warn("Violação de restrição: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: " + ex.getMessage());
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class) //erro de integridade do banco de dados
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        logger.warn("Erro de integridade do banco de dados: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: Violação de integridade do banco de dados.");
    }
}
