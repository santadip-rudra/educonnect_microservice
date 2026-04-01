package com.ctx.assessment_service.exception.custom_exceptions;

public class ComplianceRecordNotFoundException extends RuntimeException {
    public ComplianceRecordNotFoundException(String message) {
        super(message);
    }
}
