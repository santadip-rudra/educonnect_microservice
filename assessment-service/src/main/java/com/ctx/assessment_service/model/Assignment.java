package com.ctx.assessment_service.model;

import com.ctx.assessment_service.model.attachment.assignment.AssignmentAttachment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID assignmentId;

    @OneToOne
    @JoinColumn(name = "assessment_id")
    private Assessment assessment ;

    private LocalDate dueDate;

    private Integer noOfDocumentsToBeUploaded ;

    private String instruction;

    @OneToMany(mappedBy = "assignment")
    List<AssignmentAttachment> assignmentAttachmentList;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
