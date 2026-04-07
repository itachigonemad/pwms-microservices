package com.pwms.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> handleAuthError(RuntimeException ex) {
        return Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status",    401,
                "error",     "Unauthorized",
                "message",   ex.getMessage()
        );
    }
}