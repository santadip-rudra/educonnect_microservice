package com.ctx.assessment_service.strategy.contract;


import com.ctx.assessment_service.dto.assessment.create.CreateAssessmentRequestDTO;
import com.ctx.assessment_service.dto.assessment.report.AssessmentReportDTO;
import com.ctx.assessment_service.dto.assessment.serve.AssessmentServeDTO;
import com.ctx.assessment_service.dto.assessment.submit.AssessmentRequestDTO;
import com.ctx.assessment_service.dto.user.CurrentUser;
import com.ctx.assessment_service.exception.custom_exceptions.DocumentProcessingException;
import com.ctx.assessment_service.model.enums.AssessmentType;
import org.apache.coyote.BadRequestException;

import java.util.Map;
import java.util.UUID;

/**
 * Defines the contract for assessment operations
 * @author SudipSarkar
 * @version 1.0
 * @since 1.0
 */
public interface AssessmentStrategy {

    /**
     * Check if the strategy implementation supports the given assessment type
     * @param type The type of the assessment ({@link AssessmentType})
     * @return true if supported , false if not
     */
    boolean supports(AssessmentType type);

    /**
     * Handles assessment submission
     * @param student The student submitting the assessment
     * @param assessmentRequestDTO The payload
     * @return A success message (Might change in future)
     */
    Map<String,String> submitAssessment(CurrentUser student , AssessmentRequestDTO assessmentRequestDTO) throws BadRequestException, DocumentProcessingException;

    /**
     * Handles assessment creation
     * @param teacher The teacher creating the assessment
     * @param assessmentRequestDTO The payload
     * @return A success message (Might change in future)
     */
    Map<String,String> createAssessment(CurrentUser teacher, CreateAssessmentRequestDTO assessmentRequestDTO) throws BadRequestException;


    default boolean canCreateAssessment(UUID teacherId, UUID courseTeacherId){
        return teacherId.equals(courseTeacherId);
    }

    AssessmentServeDTO serveAssessment(UUID assessmentId, CurrentUser student) throws BadRequestException;

    AssessmentReportDTO getReport(UUID submissionId, CurrentUser user) throws BadRequestException;

}
