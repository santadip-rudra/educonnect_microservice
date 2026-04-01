package com.ctx.assessment_service.exception.custom_exceptions;

/**
 * Thrown when attempting to fetch demographics for a student who hasn't completed their profile.
 */
public class DemographicsNotFoundException extends RuntimeException {
    public DemographicsNotFoundException(String message) {
        super(message);
    }
}