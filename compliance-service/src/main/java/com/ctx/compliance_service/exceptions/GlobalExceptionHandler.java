package com.ctx.compliance_service.exceptions;

import com.ctx.compliance_service.exceptions.custom.ComplianceRecordNotFoundException;
import com.ctx.compliance_service.exceptions.custom.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleJsonErrors(HttpMessageNotReadableException ex) {
        String errorDetails = ex.getMessage() == null ? "" : ex.getMessage();
        String message;

        if (errorDetails.contains("ComplianceType")) {
            message = "Invalid Compliance Type. Accepted values: " +
                    "POLICY_CHECK, SECURITY_AUDIT, DATA_REVIEW, ACCESS_CONTROL, TRAINING_VERIFICATION.";
        } else if (errorDetails.contains("ComplianceResult")) {
            message = "Invalid Compliance Result. Accepted values: " +
                    "COMPLIANT, NON_COMPLIANT, PENDING, IN_PROGRESS.";
        } else {
            message = "Malformed JSON request";
        }
        return new ResponseEntity<>(Map.of("message", message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ComplianceRecordNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleRecordNotFound(ComplianceRecordNotFoundException ex) {
        return new ResponseEntity<>(Map.of("message", ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound(UserNotFoundException ex) {
        return new ResponseEntity<>(Map.of("message", ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception ex) {
        return new ResponseEntity<>(
                Map.of("message", "An unexpected error occurred: " + ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}