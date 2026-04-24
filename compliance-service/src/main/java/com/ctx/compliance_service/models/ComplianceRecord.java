package com.ctx.compliance_service.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
public class ComplianceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID complianceRecordId;

    private UUID userId;

    @Enumerated(EnumType.STRING)
    private ComplianceType type;

    @Enumerated(EnumType.STRING)
    private ComplianceResult result;

    private LocalDate date;

    // cascade + orphanRemoval lets us manage notes by mutating this list;
    // clearing the list deletes the notes, and adding new ones persists them.
    @OneToMany(
            mappedBy = "complianceRecord",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Note> notes = new ArrayList<>();
}