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

        logger.warn("Erro de validacao: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    
    @ExceptionHandler(InvalidRatingException.class) //erro de avaliação invalida
    public ResponseEntity<String> handleInvalidRatingException(InvalidRatingException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidPriceRangeException.class) //erro de intervalo de preço na busca de appointmentType
    public ResponseEntity<String> handleInvalidPriceRangeException(InvalidPriceRangeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidDateRangeException.class) //erro de datas invalidas na busca de appointments
    public ResponseEntity<String> handleInvalidTimeException(InvalidDateRangeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TooLateToChangeException.class) //erro de tentativa de alterar um agendamento muito tarde
    public ResponseEntity<String> handleTooLateToChangeException(TooLateToChangeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class) //erro de argumento invalido
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("Erro de argumento invalido: ", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class) //erro generico
    public ResponseEntity<String> handleGenericException(Exception ex) {
        logger.error("Erro inesperado no sistema: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro interno no servidor.");
    }

    @ExceptionHandler(NoSuchElementException.class) //erro do 404, elemento não encontrado
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException ex) {
        logger.warn("Recurso nao encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(CannotCreateTokenException.class) //erro de criação de token
    public ResponseEntity<String> handleCannotCreateTokenException(CannotCreateTokenException ex) {
        logger.error("Erro ao criar token JWT: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar token JWT.");
    }

    @ExceptionHandler(CannotFindQueueException.class) //erro de não encontrar a fila
    public ResponseEntity<String> handleCannotFindQueueException(CannotFindQueueException ex) {
        logger.warn("Erro ao buscar fila: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro ao buscar fila.");
    }

    @ExceptionHandler(CannotInsertOnQueueException.class) //erro de não conseguir inserir na fila
    public ResponseEntity<String> handleCannotInsertOnQueueException(CannotInsertOnQueueException ex) {
        logger.warn("Erro ao inserir na fila: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao inserir na fila.");
    }

    @ExceptionHandler(NotImplementedException.class) //erro de não implementado
    public ResponseEntity<String> handleNotImplementedException(NotImplementedException ex) {
        logger.warn("Funcao nao implementada: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Funcao nao implementada.");
    }

    // =============================== Erros do JPA ==============================
    @ExceptionHandler(ConstraintViolationException.class) //erro de validação que é jogado pelo Model da classe
    public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException ex) {
        logger.warn("Violação de restricao: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: " + ex.getMessage());
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class) //erro de integridade do banco de dados
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        logger.warn("Erro de integridade do banco de dados: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: Violação de integridade do banco de dados.");
    }
}
