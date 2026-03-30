package com.ctx.assessment_service.exception.custom_exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super("RESOURCE not found : " + message);
    }
}
