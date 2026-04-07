package com.pwms.progress.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.pwms.progress")
public class ProgressExceptionHandler {

    @ExceptionHandler(ProgressNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(ProgressNotFoundException ex) {
        return Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status",    404,
                "error",     "Not Found",
                "message",   ex.getMessage()
        );
    }

    @ExceptionHandler(ProgressAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleAlreadyExists(ProgressAlreadyExistsException ex) {
        return Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status",    409,
                "error",     "Conflict",
                "message",   ex.getMessage()
        );
    }
}