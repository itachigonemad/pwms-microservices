package com.pwms.report.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.pwms.report")
public class ReportExceptionHandler {

    @ExceptionHandler(ReportNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(ReportNotFoundException ex) {
        return Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status",    404,
                "error",     "Not Found",
                "message",   ex.getMessage()
        );
    }
}