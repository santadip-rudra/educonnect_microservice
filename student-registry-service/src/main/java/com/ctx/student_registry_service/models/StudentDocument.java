package com.ctx.student_registry_service.models;

import com.ctx.student_registry_service.models.enums.FileTypeEnum;
import com.ctx.student_registry_service.models.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class StudentDocument {
    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false,updatable = false)
    private UUID studentDocumentId;

    @Column(nullable = false)
    private String fileName ;

    @Column(nullable = false,updatable = false)
    private String fileUri;

    private UUID studentId;

    @ManyToOne
    @JoinColumn(name = "doctype_id" , nullable = false)
    private DocType docType;

    @Enumerated(EnumType.STRING)
    private FileTypeEnum fileType;


    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false, columnDefinition = "LONGBLOB")
    private byte[] fileData;


    @CreationTimestamp
    private LocalDateTime UploadedDate;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus = VerificationStatus.UNVERIFIED;

}
