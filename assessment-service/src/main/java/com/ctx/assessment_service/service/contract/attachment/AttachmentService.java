package com.ctx.assessment_service.service.contract.attachment;

import com.ctx.assessment_service.dto.attachment.AttachmentStreamDTO;
import com.ctx.assessment_service.dto.user.CurrentUser;
import com.ctx.assessment_service.exception.custom_exceptions.DocumentExceptions;
import com.ctx.assessment_service.exception.custom_exceptions.ResourceNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
public interface AttachmentService {
    /**
     * Returns a stream of the requested attachment's file data.
     *
     * <p>Students may only access attachments that belong to their own submissions.
     * Teachers and admins may access any attachment.
     *
     * <p><strong>The caller is responsible for closing the returned {@link InputStream}
     * after writing the HTTP response.</strong> Example:
     * <pre>{@code
     * try (InputStream stream = dto.getInputStream()) {
     *     StreamUtils.copy(stream, response.getOutputStream());
     * }
     * }</pre>
     *
     * @param attachmentId the {@link UUID} of the attachment to stream
     * @param user         the currently authenticated user
     * @return an {@link AttachmentStreamDTO} containing the attachment metadata and a readable stream
     * @throws DocumentExceptions  if the attachment is not found, has no file data,
     *                             or the student is not authorized to access it
     */
    AttachmentStreamDTO getAttachment(UUID attachmentId,CurrentUser user) throws DocumentExceptions;


    /**
     * Returns all attachment URIs for a given student's submission on a specific assessment.
     *
     * <p>Students may only retrieve their own attachment URIs. Teachers and admins
     * may retrieve URIs for any student.
     *
     * @param studentId    the {@link UUID} of the student whose attachments are requested
     * @param assessmentId the {@link UUID} of the assessment
     * @param user         the currently authenticated user
     * @return a {@link List} of URI strings pointing to the student's submitted attachments
     * @throws ResourceNotFoundException if no submission is found for the given student and assessment
     * @throws BadRequestException       if a student attempts to access another student's attachments
     */
    List<String> getAllAttachmentUrisByStudentAndAssessment(UUID studentId, UUID assessmentId, CurrentUser user) throws BadRequestException;
}
