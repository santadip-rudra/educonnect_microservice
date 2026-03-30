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
 * Defines the contract for all assessment operations across different assessment types.
 *
 * <p>This interface is part of the Strategy pattern. Each concrete implementation
 * handles a specific {@link AssessmentType} (e.g. {@code QUIZ}, {@code ASSIGNMENT})
 * and is selected at runtime based on the result of {@link #supports(AssessmentType)}.
 *
 * <p>Typical usage by a strategy resolver:
 * <pre>{@code
 * AssessmentStrategy strategy = strategies.stream()
 *     .filter(s -> s.supports(type))
 *     .findFirst()
 *     .orElseThrow(() -> new ResourceNotFoundException("No strategy found for type: " + type));
 * }</pre>
 *
 * @author SudipSarkar
 * @version 1.0
 * @since 1.0
 * @see AssessmentType
 */
public interface AssessmentStrategy {

    /**
     * Determines whether this strategy implementation supports the given assessment type.
     *
     * <p>Used by the strategy resolver to select the correct implementation at runtime.
     *
     * @param type the {@link AssessmentType} to check
     * @return {@code true} if this strategy handles the given type, {@code false} otherwise
     */
    boolean supports(AssessmentType type);

    /**
     * Handles the submission of an assessment by a student.
     *
     * <p>Implementations are expected to:
     * <ul>
     *   <li>Verify the student is enrolled in the course</li>
     *   <li>Validate the submission payload</li>
     *   <li>Prevent duplicate submissions</li>
     *   <li>Persist the submission and any associated data (e.g. files, answers)</li>
     * </ul>
     *
     * @param student              the currently authenticated student submitting the assessment;
     *                             must not be {@code null}
     * @param assessmentRequestDTO the submission payload; the concrete type is expected to match
     *                             the assessment type handled by this strategy
     *                             (e.g. {@code AssignmentRequestDTO} or {@code StudentQuizQuestionResponseDTO})
     * @return a {@link Map} containing at minimum a {@code "message"} key and a {@code "submissionId"} key
     * @throws BadRequestException         if the student is not enrolled, has already submitted,
     *                                     or the payload is otherwise invalid
     * @throws DocumentProcessingException if file processing fails (applicable to assignment submissions)
     */
    Map<String, String> submitAssessment(CurrentUser student, AssessmentRequestDTO assessmentRequestDTO)
            throws BadRequestException, DocumentProcessingException;

    /**
     * Handles the creation of a new assessment by a teacher.
     *
     * <p>Implementations are expected to:
     * <ul>
     *   <li>Verify the course exists</li>
     *   <li>Verify the teacher is authorized to add assessments to the course
     *       via {@link #canCreateAssessment(UUID, UUID)}</li>
     *   <li>Persist the assessment and all associated entities (e.g. quiz questions, assignment config)</li>
     * </ul>
     *
     * @param teacher              the currently authenticated teacher creating the assessment;
     *                             must not be {@code null}
     * @param assessmentRequestDTO the creation payload; the concrete type is expected to match
     *                             the assessment type handled by this strategy
     *                             (e.g. {@code CreateAssignmentRequestDTO} or {@code CreateQuizRequestDTO})
     * @return a {@link Map} containing at minimum a {@code "message"} key and an {@code "assessmentId"} key
     * @throws BadRequestException if the course is not found, the teacher is not authorized,
     *                             or the payload is invalid
     */
    Map<String, String> createAssessment(CurrentUser teacher, CreateAssessmentRequestDTO assessmentRequestDTO)
            throws BadRequestException;

    /**
     * Default authorization check that determines whether a teacher is allowed to create
     * an assessment for a given course.
     *
     * <p>The default implementation checks that the teacher's ID matches the course's
     * owner/teacher ID directly. Implementations may override this method to delegate
     * to an external service for more complex authorization rules.
     *
     * @param teacherId       the {@link UUID} of the teacher attempting to create the assessment
     * @param courseTeacherId the {@link UUID} of the teacher who owns the course
     * @return {@code true} if {@code teacherId} equals {@code courseTeacherId}, {@code false} otherwise
     */
    default boolean canCreateAssessment(UUID teacherId, UUID courseTeacherId) {
        return teacherId.equals(courseTeacherId);
    }

    /**
     * Serves the assessment content to the requesting user.
     *
     * <p>For students, implementations must verify enrollment before returning content.
     * The returned DTO is shaped to be safe for the consumer — for example, correct
     * answer flags must not be exposed to students in quiz serve responses.
     *
     * @param assessmentId the {@link UUID} of the assessment to serve
     * @param user         the currently authenticated user requesting the assessment;
     *                     may be a student, teacher, or admin
     * @return an {@link AssessmentServeDTO} containing the assessment content appropriate
     *         for the given user's role
     * @throws BadRequestException if the assessment is not found, or a student requests
     *                             an assessment for a course they are not enrolled in
     */
    AssessmentServeDTO serveAssessment(UUID assessmentId, CurrentUser user) throws BadRequestException;

    /**
     * Retrieves the assessment report for a given submission.
     *
     * <p>Access control rules:
     * <ul>
     *   <li>A {@code STUDENT} may only access their own submission report</li>
     *   <li>A {@code TEACHER} or {@code ADMIN} may access any submission report</li>
     * </ul>
     *
     * @param submissionId the {@link UUID} of the submission to report on
     * @param user         the currently authenticated user requesting the report
     * @return an {@link AssessmentReportDTO} containing the full submission report
     *         appropriate for the assessment type
     * @throws BadRequestException if the submission is not found, or a student attempts
     *                             to access another student's report
     */
    AssessmentReportDTO getReport(UUID submissionId, CurrentUser user) throws BadRequestException;

}