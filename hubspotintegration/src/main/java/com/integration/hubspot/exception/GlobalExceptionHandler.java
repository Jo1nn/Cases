package com.integration.hubspot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Exceção específica para AuthException
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Object> handleAuthException(AuthException ex, WebRequest request) {
        return new ResponseEntity<>("Erro de autenticação: " + ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    // Exceção genérica para outras exceções (RuntimeException ou outras)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleKnownExceptions(RuntimeException ex, WebRequest request) {
        return new ResponseEntity<>("Erro desconhecido: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        ex.printStackTrace(); // Mostra o erro real no console
        return new ResponseEntity<>("Erro interno: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
