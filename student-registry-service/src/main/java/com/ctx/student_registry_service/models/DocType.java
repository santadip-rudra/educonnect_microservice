package com.ctx.student_registry_service.models;

import com.ctx.student_registry_service.models.enums.DocTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false,updatable = false)
    private UUID docTypeId;
    @Enumerated(EnumType.STRING)
    private DocTypeEnum docTypeName;
    private String description;
    @OneToMany(mappedBy = "docType")
    List<StudentDocument> studentDocumentList;

}