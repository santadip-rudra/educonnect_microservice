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

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID noteId;

    private String note;

    @ManyToOne
    @JoinColumn(name = "compliance_record")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ComplianceRecord complianceRecord;
}