package com.ctx.assessment_service.exception.custom_exceptions;

public class NoChildFoundException extends RuntimeException {
    public NoChildFoundException(String message) {
        super(message);
    }
}
