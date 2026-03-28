package com.ctx.course_service.exceptions.custom_exceptions;

public class ModuleNotFoundException extends RuntimeException {
    public ModuleNotFoundException(String message) {
        super(message);
    }
}
