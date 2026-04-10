package com.ctx.compliance_service.exceptions.custom;

public class ComplianceRecordNotFoundException extends RuntimeException {
    public ComplianceRecordNotFoundException(String message) {
        super(message);
    }
}
