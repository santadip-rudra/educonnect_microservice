package com.ctx.student_registry_service.dto.demographics;

import lombok.Data;
import java.util.UUID;

@Data
public class StudentDemographicsDTO {
    private UUID studentId;
    private String legalFullName;
    private String nationality;
    private String religion;
    private String gender;
    private String bloodGroup;
    private String passportNumber;
    private String visaType;

    private AddressDto permanentAddress;

    private String parentEmail;
    private String parentPhone;

    private EmergencyContactDto emergencyContact;
}