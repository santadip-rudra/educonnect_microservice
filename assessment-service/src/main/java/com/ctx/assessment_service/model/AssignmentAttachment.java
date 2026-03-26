package com.ctx.assessment_service.model;

import com.ctx.assessment_service.model.enums.FileTypeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Data
@ToString(onlyExplicitlyIncluded = true)
@PrimaryKeyJoinColumn(name = "assignment_attachment_id")

public class AssignmentAttachment {

    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID attachmentId;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false, columnDefinition = "LONGBLOB")
    private byte[] fileData;

    private String description;

    private String fileName ;

    private FileTypeEnum fileTypeEnum;

    private String uri;
    @ManyToOne
    @JoinColumn(name = "submission_id")
    private Submission submission;

    @ManyToOne
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

}
