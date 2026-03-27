package com.ctx.course_service.exceptions.custom_exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super("RESOURCE not found : " + message);
    }
}
