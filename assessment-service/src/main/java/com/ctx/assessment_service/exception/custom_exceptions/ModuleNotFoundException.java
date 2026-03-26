package com.ctx.assessment_service.exception.custom_exceptions;

public class ModuleNotFoundException extends RuntimeException {
    public ModuleNotFoundException(String message) {
        super(message);
    }
}
