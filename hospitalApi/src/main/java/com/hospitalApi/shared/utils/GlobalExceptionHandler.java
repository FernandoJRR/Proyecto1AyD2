package com.hospitalApi.shared.utils;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import com.hospitalApi.shared.exceptions.BadCredentialsException;
import com.hospitalApi.shared.exceptions.BadRequestException;
import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.shared.exceptions.NotFoundException;

/**
 *
 * @author Luis Monterroso
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicatedEntryException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(DuplicatedEntryException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(""
                + "Error inesperado en la Base de Datos.");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleResourceNotFound(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                "Autenticación fallida: El correo electrónico o la contraseña son incorrectos."
                        + " Por favor, verifica tus credenciales e intenta de nuevo.");
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequestException(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Cubre las excepciones que se lanzan cuando @Valid no es cumplido y las
     * validaciones no
     * pasan.
     * 
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String menssage = "";
        // recorre los errores de validación y agregarlos al mensaje de respuesta
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            menssage = menssage + String.format("- %s \n", error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(menssage.trim());
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<?> handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
        StringBuilder mensaje = new StringBuilder();

        // recorre los errores de validación y agregarlos al mensaje de respuesta
        ex.getAllValidationResults().forEach(result -> {
            result.getResolvableErrors().forEach(error -> {
                mensaje.append("- ").append(error.getDefaultMessage()).append("\n");
            });
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensaje.toString().trim());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ocurrió un error inesperado: " + ex.getMessage());
    }

}
