package com.ctx.assessment_service.controller;



import com.ctx.assessment_service.dto.assessment.create.CreateAssessmentRequestDTO;
import com.ctx.assessment_service.dto.assessment.report.AssessmentReportDTO;
import com.ctx.assessment_service.dto.assessment.serve.AssessmentServeDTO;
import com.ctx.assessment_service.dto.assessment.submit.AssessmentRequestDTO;
import com.ctx.assessment_service.dto.assessment.submit.assignment.AssignmentRequestDTO;
import com.ctx.assessment_service.dto.common.GenericResponse;
import com.ctx.assessment_service.dto.user.CurrentUser;
import com.ctx.assessment_service.exception.custom_exceptions.DocumentProcessingException;
import com.ctx.assessment_service.factory.AssessmentFactory;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/assessment")
//@Tag(name = "09 AssessmentController")
/**
 * REST controller for handling assessment related requests
 * Delegates business logic for assessment related operations
 */
public class AssessmentController {
    private final AssessmentFactory assessmentFactory;


    /**
     *
     * @param dto The payload
     * @param user The CurrentUser
     * @return Success message
     */

    @PostMapping("/create")
    public ResponseEntity<GenericResponse<Map<String,String>>> createAssignment(
            @RequestBody CreateAssessmentRequestDTO dto,
            @AuthenticationPrincipal CurrentUser user
            ) throws BadRequestException{


        return new ResponseEntity<>(
                new GenericResponse<>(
                        assessmentFactory.createAssessment(user, dto),
                        "Assessment created successfully",
                        HttpStatus.CREATED.value(),
                        LocalDateTime.now()
                ),
                HttpStatus.CREATED
        );
    }

    /**
     *
     * @param dto The payload
     * @param user AuthenticationPrincipal
     * @param files The file data (null or empty in case of Quiz submission)
     * @return Success message
     * @throws BadRequestException
     */
    @PostMapping("/submit")
    public ResponseEntity<GenericResponse<Map<String,String>>> submitAssessment(
            @RequestPart("request") AssessmentRequestDTO dto ,
            @AuthenticationPrincipal  CurrentUser user,
            @RequestPart("files") @Nullable  MultipartFile[] files
    ) throws BadRequestException, DocumentProcessingException {

        if(files != null && files.length != 0){
            if(dto != null){
                ((AssignmentRequestDTO) dto).setFiles(Arrays.asList(files));
            }
        }

        return new ResponseEntity<>(
                new GenericResponse<>(
                        assessmentFactory.submitAssessment(user, dto),
                        "Assessment created successfully",
                        HttpStatus.CREATED.value(),
                        LocalDateTime.now()
                ),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/get-assessment/{assessmentType}/{assessmentId}")
    public ResponseEntity<GenericResponse<AssessmentServeDTO>> serveAssessment(
            @PathVariable("assessmentId") UUID assessmentId,
            @PathVariable("assessmentType") String assessmentType,
            @AuthenticationPrincipal CurrentUser user
            ) throws BadRequestException {
        return ResponseEntity.ok(
                new GenericResponse<>(
                        assessmentFactory.serveAssessment(assessmentId,assessmentType,user),
                        "Assessment [" +assessmentType.toLowerCase()+ "] retrieved successfully",
                        HttpStatus.OK.value(),
                        LocalDateTime.now()
                )
        );
    }

    @GetMapping("/report/{assessmentType}/{submissionId}")
    private ResponseEntity<GenericResponse<AssessmentReportDTO>> getReport(
            @PathVariable("submissionId") UUID submissionId,
            @AuthenticationPrincipal CurrentUser user,
            @PathVariable("assessmentType") String assessmentType
    ) throws BadRequestException {
        return ResponseEntity.ok(
                new GenericResponse<>(
                        assessmentFactory.getReport(submissionId, user,assessmentType),
                        "Assessment [" +assessmentType.toLowerCase()+ "] report retrieved successfully",
                        HttpStatus.OK.value(),
                        LocalDateTime.now()
                )
        );
    }
}
