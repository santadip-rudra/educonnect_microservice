package com.ctx.course_service.exceptions.custom_exceptions;

public class FileException extends RuntimeException {
    public FileException(String message) {
        super(message);
    }
}
