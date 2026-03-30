package com.ctx.assessment_service.service.implementation.attachment;


import com.ctx.assessment_service.dto.attachment.AttachmentStreamDTO;
import com.ctx.assessment_service.exception.custom_exceptions.DocumentExceptions;
import com.ctx.assessment_service.exception.custom_exceptions.ResourceNotFoundException;
import com.ctx.assessment_service.model.Submission;
import com.ctx.assessment_service.model.attachment.assignment.AssignmentAttachment;
import com.ctx.assessment_service.model.attachment.base_class.Attachment;
import com.ctx.assessment_service.dto.user.CurrentUser;
import com.ctx.assessment_service.repo.assessment.SubmissionRepo;
import com.ctx.assessment_service.repo.assessment.attachment.AssignmentAttachmentRepo;
import com.ctx.assessment_service.repo.attachment.AttachmentRepo;
import com.ctx.assessment_service.service.contract.attachment.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepo attachmentRepo;
    private final AssignmentAttachmentRepo assignmentAttachmentRepo;
    private final SubmissionRepo submissionRepo;

    @Value("${gateway.base-url}")
    private String gatewayBaseUrl;

    @Override
    public AttachmentStreamDTO getAttachment(UUID attachmentId, CurrentUser user)
            throws DocumentExceptions {

        Attachment attachment = attachmentRepo
                .findById(attachmentId)
                .orElseThrow(() -> new DocumentExceptions("Attachment not found"));

        // Only enforce ownership check if user is authenticated as STUDENT
        if (user != null && user.getRole().equals("STUDENT")
                && !((AssignmentAttachment) attachment)
                .getSubmission().getStudentId().equals(user.getUserId())) {
            throw new DocumentExceptions("Not authorized to access this attachment");
        }

        byte[] fileData = attachment.getFileData();

        if (fileData == null) {
            throw new DocumentExceptions("Document has no data");
        }

        return new AttachmentStreamDTO(attachment, new ByteArrayInputStream(fileData));
    }

    @Override
    public List<String> getAllAttachmentUrisByStudentAndAssessment(
            UUID studentId, UUID assessmentId, CurrentUser user) throws BadRequestException {

        if (user.getRole().equals("STUDENT")
                && !user.getUserId().equals(studentId)) {
            throw new BadRequestException("Not authorized to access these attachments");
        }

        Submission submission = submissionRepo
                .findByStudentIdAndAssessmentAssessmentId(studentId, assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        return assignmentAttachmentRepo
                .findAllBySubmissionSubmissionId(submission.getSubmissionId())
                .stream()
                .map(a -> gatewayBaseUrl + "/attachment/view/"
                        + a.getAttachmentId())
                .toList();
    }
}