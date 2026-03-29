package com.ctx.assessment_service.controller;

import com.ctx.assessment_service.dto.attachment.AttachmentStreamDTO;
import com.ctx.assessment_service.dto.user.CurrentUser;
import com.ctx.assessment_service.exception.custom_exceptions.DocumentExceptions;
import com.ctx.assessment_service.model.attachment.base_class.Attachment;
import com.ctx.assessment_service.service.contract.attachment.AttachmentService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/attachment")
@RequiredArgsConstructor
@Slf4j
//@Tag(name = "11 AttachmentController")
public class AttachmentController {

    private final AttachmentService attachmentService;

    @GetMapping("/view/{id}")
    public void viewAttachment(
            @PathVariable("id") UUID id,
            @AuthenticationPrincipal CurrentUser currentUser,
            HttpServletResponse response) throws IOException, DocumentExceptions {

        InputStream inputStream = null;
        try {
            AttachmentStreamDTO data = attachmentService.getAttachment(id, currentUser);
            Attachment attachment = data.getAttachment();

            String contentType = MediaTypeFactory.getMediaType(attachment.getFileName())
                    .map(MediaType::toString)
                    .orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);

            response.setContentType(contentType);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename=\"" + attachment.getFileName() + "\"");
            response.setHeader(HttpHeaders.CACHE_CONTROL, "public, max-age=86400");

            inputStream = data.getInputStream();
            StreamUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();

        } catch (DocumentExceptions e) {
            log.error("Attachment error: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error serving attachment: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            if (inputStream != null) inputStream.close();
        }
    }

    @GetMapping("assessment/{assessmentId}/student/{studentId}")
    public ResponseEntity<List<String>> getAssignmentAttachmentUris(
            @PathVariable("assessmentId") UUID assessmentId,
            @PathVariable("studentId") UUID studentId,
            @AuthenticationPrincipal CurrentUser user
    ) throws BadRequestException {
        return new ResponseEntity<>(
                attachmentService.getAllAttachmentUrisByStudentAndAssessment(studentId,assessmentId,user),
                HttpStatus.OK
        );
    }

}
