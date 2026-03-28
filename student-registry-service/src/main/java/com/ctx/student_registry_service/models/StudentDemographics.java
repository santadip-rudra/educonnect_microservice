package com.ctx.student_registry_service.models;

import com.ctx.student_registry_service.models.enums.BloodGroup;
import com.ctx.student_registry_service.models.enums.Gender;
import com.ctx.student_registry_service.models.enums.VisaType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentDemographics {
    @Id
    private UUID studentId;

    private String legalFullName; // In case it differs from preferred name
    private String nationality;
    private String religion;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private BloodGroup bloodGroup;

    private String passportNumber;

    @Enumerated(EnumType.STRING)
    private VisaType visaType;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "perm_street")),
            @AttributeOverride(name = "city", column = @Column(name = "perm_city")),
            @AttributeOverride(name = "state", column = @Column(name = "perm_state")),
            @AttributeOverride(name = "zipCode", column = @Column(name = "perm_zip")),
            @AttributeOverride(name = "country", column = @Column(name = "perm_country"))
    })
    private Address permanentAddress;

    private String parentEmail;
    private String parentPhone;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "contactName", column = @Column(name = "emerg_contact_name")),
            @AttributeOverride(name = "relationship", column = @Column(name = "emerg_relationship")),
            @AttributeOverride(name = "contactPhone", column = @Column(name = "emerg_phone"))
    })
    private EmergencyContact emergencyContact;
}
