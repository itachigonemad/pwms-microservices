package com.pwms.wellnessplan.exception;

public class PatientAlreadyAssignedException extends Exception {
    public PatientAlreadyAssignedException(String message) {
        super(message);
    }
}