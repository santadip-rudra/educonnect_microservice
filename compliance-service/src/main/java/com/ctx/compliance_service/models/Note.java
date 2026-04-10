package com.ctx.compliance_service.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.UUID;

@Data
@Entity
@Table(name = "compliance_notes")
public class Note {

    // primary key
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false,updatable = false)
    private UUID NoteId;

    private String note;

    @ManyToOne
    @JoinColumn(name = "compliance_record")
    @ToString.Exclude // Prevents infinite loop back to ComplianceRecord
    @EqualsAndHashCode.Exclude
    private ComplianceRecord complianceRecord;
}