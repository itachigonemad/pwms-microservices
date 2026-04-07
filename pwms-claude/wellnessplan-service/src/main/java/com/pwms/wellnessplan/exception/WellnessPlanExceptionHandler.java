package com.pwms.wellnessplan.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.pwms.wellnessplan")
public class WellnessPlanExceptionHandler {

    @ExceptionHandler(PlanNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handlePlanNotFound(PlanNotFoundException ex) {
        return Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status",    404,
                "error",     "Not Found",
                "message",   ex.getMessage()
        );
    }

    @ExceptionHandler(PlanAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handlePlanAlreadyExists(PlanAlreadyExistsException ex) {
        return Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status",    409,
                "error",     "Conflict",
                "message",   ex.getMessage()
        );
    }

    @ExceptionHandler(PatientAlreadyAssignedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleAlreadyAssigned(PatientAlreadyAssignedException ex) {
        return Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status",    409,
                "error",     "Conflict",
                "message",   ex.getMessage()
        );
    }
}