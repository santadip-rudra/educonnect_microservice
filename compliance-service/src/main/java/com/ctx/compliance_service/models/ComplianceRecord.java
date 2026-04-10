package com.ctx.compliance_service.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
public class ComplianceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false,updatable = false)
    private UUID complianceRecordId;

    private UUID userId;

    @Enumerated(EnumType.STRING)
    private ComplianceType type;

    private String result;
    private LocalDate date;

    @OneToMany(mappedBy = "complianceRecord")
    @ToString.Exclude // Prevents infinite loop in logging/debugging
    @EqualsAndHashCode.Exclude // Prevents infinite loop in collections
    private List<Note> notes = new ArrayList<>();

}