package com.ctx.assessment_service.controller;



import com.ctx.assessment_service.dto.assessment.create.CreateAssessmentRequestDTO;
import com.ctx.assessment_service.dto.assessment.general.AssessmentResponseDTO;
import com.ctx.assessment_service.dto.assessment.report.AssessmentReportDTO;
import com.ctx.assessment_service.dto.assessment.serve.AssessmentServeDTO;
import com.ctx.assessment_service.dto.assessment.submit.AssessmentRequestDTO;
import com.ctx.assessment_service.dto.assessment.submit.assignment.AssignmentRequestDTO;
import com.ctx.assessment_service.dto.common.GenericResponse;
import com.ctx.assessment_service.dto.user.CurrentUser;
import com.ctx.assessment_service.exception.custom_exceptions.DocumentProcessingException;
import com.ctx.assessment_service.factory.AssessmentFactory;
import com.ctx.assessment_service.model.Assessment;
import com.ctx.assessment_service.service.contract.assessment.AssessmentService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/assessment")
//@Tag(name = "09 AssessmentController")
/**
 * REST controller for handling assessment related requests
 * Delegates business logic for assessment related operations
 */
public class AssessmentController {
    private final AssessmentFactory assessmentFactory;
    private final AssessmentService assessmentService;
    /**
     *
     * @param dto The payload
     * @param user The CurrentUser
     * @return Success message
     */

    @PostMapping("/create")
    @PreAuthorize("hasRole('TEACHER')")
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
    @PreAuthorize("hasRole('STUDENT')")
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
    @PreAuthorize("hasRole('TEACHER') or hasRole('STUDENT') or hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<GenericResponse<AssessmentReportDTO>> getReport(
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

    @GetMapping("/{courseId}")
    public ResponseEntity<?> getAllAssessmentWithCourse(@PathVariable("courseId") UUID courseId){
        return ResponseEntity.ok(
                new GenericResponse<>(
                        assessmentService.getAllAssessmentUsingCourseId(courseId),
                        "Assessments retrieved successfully",
                        HttpStatus.OK.value(),
                        LocalDateTime.now()
                )
        );
    }

    @PostMapping("/all/by-courses")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<List<AssessmentResponseDTO>> getAllAssessmentWithCourseIds(@RequestBody List<String> courseIdList){

        List<UUID> idList = courseIdList.stream().map(UUID::fromString).toList();

        return ResponseEntity.ok(
                assessmentService.getAllAssessmentUsingListOfCourseIds(idList)
        );
    }
}
