package com.ctx.assessment_service.model.attachment.assignment;

import com.ctx.assessment_service.model.Assignment;
import com.ctx.assessment_service.model.Submission;
import com.ctx.assessment_service.model.attachment.base_class.Attachment;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "assignment_attachment_id")

public class AssignmentAttachment extends Attachment {

    @ManyToOne
    @JoinColumn(name = "submission_id")
    private Submission submission;

    @ManyToOne
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

}

