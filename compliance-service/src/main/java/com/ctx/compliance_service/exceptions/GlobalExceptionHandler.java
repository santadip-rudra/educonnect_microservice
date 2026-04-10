package com.ctx.compliance_service.exceptions;

import com.ctx.compliance_service.exceptions.custom.ComplianceRecordNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleJsonErrors(HttpMessageNotReadableException ex) {
        String errorDetails = ex.getMessage();
        if (errorDetails.contains("com.ctx.compliance_service.models.ComplianceType")) {
            return new ResponseEntity<>(
                    "Invalid Compliance Type. Accepted values are: BACKGROUND_CHECK, HEALTH_SCREENING, etc.",
                    HttpStatus.BAD_REQUEST
            );
        }
        return new ResponseEntity<>("Malformed JSON request", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ComplianceRecordNotFoundException.class)
    public ResponseEntity<String> handleJsonErrors(ComplianceRecordNotFoundException ex) {
        String errorDetails = ex.getMessage();
        if (errorDetails.contains("com.ctx.compliance_service.models.ComplianceType")) {
            return new ResponseEntity<>(
                    "Record not found",
                    HttpStatus.BAD_REQUEST
            );
        }
        return new ResponseEntity<>("Record Not Found", HttpStatus.BAD_REQUEST);
    }
}
