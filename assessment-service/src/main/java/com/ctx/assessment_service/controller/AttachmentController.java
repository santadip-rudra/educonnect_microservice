package com.ctx.assessment_service.controller;

import com.educonnect.dto.attachment.AttachmentStreamDTO;
import com.educonnect.exception.custom_exceptions.DocumentExceptions;
import com.educonnect.model.document.attachment.Attachment;
import com.educonnect.service.contract.attachment.AttachmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/api/attachment")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "11 AttachmentController")
public class AttachmentController {

    private final AttachmentService attachmentService;

    @GetMapping("/view/{id}")
    public void viewAttachment(@PathVariable("id") UUID id, HttpServletResponse response) throws IOException, DocumentExceptions {

        InputStream inputStream = null;
        try {
            AttachmentStreamDTO data = attachmentService.getAttachment(id);
            Attachment attachment = data.getAttachment();

            // MediaTypeFactory can auto-detect Content-Type from the filename !!!!
            String contentType = MediaTypeFactory.getMediaType(attachment.getFileName())
                    .map(MediaType::toString)
                    .orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);


            response.setContentType(contentType);

            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + attachment.getFileName() + "\"");
            response.setHeader(HttpHeaders.CACHE_CONTROL, "public, max-age=86400");


            inputStream = data.getInputStream();
            StreamUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();


        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            if(inputStream != null)
                inputStream.close();
        }

    }

    @GetMapping("assessment/{assessmentId}/student/{studentId}")
    public ResponseEntity<List<String>> getAssignmentAttachmentUris(
            @PathVariable("assessmentId") UUID assessmentId,
            @PathVariable("studentId") UUID studentId
    ){
        return new ResponseEntity<>(
                attachmentService.getAllAttachmentUrisByStudentAndAssessment(studentId,assessmentId),
                HttpStatus.OK
        );
    }

}
