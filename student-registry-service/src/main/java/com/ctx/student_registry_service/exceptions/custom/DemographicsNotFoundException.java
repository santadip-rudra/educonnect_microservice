package com.ctx.student_registry_service.exceptions.custom;

public class DemographicsNotFoundException extends RuntimeException {
    public DemographicsNotFoundException(String message) {
        super(message);
    }
}
