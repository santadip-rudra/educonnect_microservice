package com.ctx.student_registry_service.controller;

import com.ctx.student_registry_service.dto.document.DocStreamDto;
import com.ctx.student_registry_service.exceptions.custom.StudentNotFoundException;
import com.ctx.student_registry_service.models.StudentDocument;
import com.ctx.student_registry_service.models.enums.DocTypeEnum;
import com.ctx.student_registry_service.services.StudentDoctypeService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v2/api/docs")
@RequiredArgsConstructor
public class DocumentController {


    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping
    public String test(){
        return "test";
    }

    private final StudentDoctypeService studentDocumentService;
    /**
     * Uploads and saves a student document to the database.
     * <p>
     * This method accepts a file and stores it as a BLOB in the DB.
     * </p>
     *
     * @param userId The student id whose document will be uploaded.
     * @param file The document file
     * @param docType The type of the document(ADHAAR, PAN...)
     * @since 1.0
     */

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping(path = "/upload", consumes = "multipart/form-data")
    ResponseEntity<String> saveDocument(
            @RequestParam MultipartFile file,
            @RequestParam DocTypeEnum docType,
           @RequestHeader("X-User-Id") UUID userId
    ) throws IOException, StudentNotFoundException {

        return ResponseEntity.ok(
                studentDocumentService.saveStudentDocument(userId, file,docType)
        ) ;

    }


    /**
     *
     * <p>
     * This method accepts a file and stores it as a BLOB in the DB.
     * </p>
     *
     * @param documentUuid The unique identifier to fetch the document (BLOB).
     * @param response The {@link HttpServletResponse} used to set the Content-Type and stream the file data.
     * @since 1.0
     *
     */
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping(path = "view/{documentUuid}")
    public void viewDoc(
            @PathVariable("documentUuid") UUID documentUuid,
            HttpServletResponse response
    ) throws IOException {
        DocStreamDto fileData = studentDocumentService.getDocument(documentUuid);
        StudentDocument document = fileData.getStudentDocument();
        InputStream inputStream = fileData.getInputStream();

        response.setHeader("Content-Disposition",
                "inline; filename=\""  + document.getFileName() + "\"");
        response.setHeader("Cache-Control", "public, max-age=86400"); // optional

        String contentType = MediaTypeFactory.getMediaType(document.getFileName())
                .map(MediaType::toString)
                .orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        response.setContentType(contentType);
        StreamUtils.copy(inputStream,response.getOutputStream());
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Map<String, String>>> getStudentDocuments(
            @PathVariable UUID studentId) {
        return ResponseEntity.ok(
                studentDocumentService.getAllDocumentsByStudent(studentId)
        );
    }
}
