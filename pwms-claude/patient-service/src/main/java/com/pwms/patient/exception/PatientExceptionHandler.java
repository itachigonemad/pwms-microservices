package com.pwms.patient.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class PatientExceptionHandler {

    @ExceptionHandler(PatientAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handlePatientAlreadyExists(
            PatientAlreadyExistsException ex) {
        return Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status",    409,
                "error",     "Conflict",
                "message",   ex.getMessage()
        );
    }

    @ExceptionHandler(PatientNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handlePatientNotFound(
            PatientNotFoundException ex) {
        return Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status",    404,
                "error",     "Not Found",
                "message",   ex.getMessage()
        );
    }
}