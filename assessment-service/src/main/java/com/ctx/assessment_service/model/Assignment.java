package com.ctx.assessment_service.model;

import com.ctx.assessment_service.model.attachment.assignment.AssignmentAttachment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

}
