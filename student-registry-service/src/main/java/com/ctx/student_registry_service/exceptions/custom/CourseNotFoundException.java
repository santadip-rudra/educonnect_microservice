package com.ctx.student_registry_service.exceptions.custom;

public class CourseNotFoundException extends RuntimeException {
    public CourseNotFoundException(String message) {
        super(message);
    }
}
