package com.ctx.assessment_service.factory;

import com.ctx.assessment_service.dto.assessment.create.CreateAssessmentRequestDTO;
import com.ctx.assessment_service.dto.assessment.report.AssessmentReportDTO;
import com.ctx.assessment_service.dto.assessment.serve.AssessmentServeDTO;
import com.ctx.assessment_service.dto.assessment.session.quiz.QuizSessionResponseDTO;
import com.ctx.assessment_service.dto.submission.StudentSubmissionSummaryDTO;
import com.ctx.assessment_service.dto.assessment.submit.AssessmentRequestDTO;
import com.ctx.assessment_service.dto.user.CurrentUser;
import com.ctx.assessment_service.exception.custom_exceptions.DocumentProcessingException;
import com.ctx.assessment_service.model.enums.AssessmentType;
import com.ctx.assessment_service.strategy.contract.AssessmentStrategy;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Factory method responsible for routing the assessment creation & submission request
 * <p> It dynamically selects & returns the correct {@link AssessmentStrategy} (either Assignment or Quiz) based on the {@link AssessmentType}</p>
 *
 * @author SudipSarkar
 * @version 2.0
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class AssessmentFactory {

    private final List<AssessmentStrategy> assessmentStrategyList;

    /**
     * Resolves the strategy that supports the given {@link AssessmentType}
     */
    private AssessmentStrategy resolve(AssessmentType type) {
        return assessmentStrategyList.stream()
                .filter(s -> s.supports(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No strategy registered for AssessmentType: " + type
                ));
    }

    /**
     * Parses a raw string into an {@link AssessmentType} with a clear error on invalid input
     */
    private AssessmentType parseType(String raw) {
        try {
            return AssessmentType.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Unknown assessment type: '" + raw + "'. Valid values: "
                            + java.util.Arrays.toString(AssessmentType.values())
            );
        }
    }

    /**
     * <p>Routes the assessment creation request to the appropriate strategy</p>
     * @param teacher              the Teacher creating the assessment
     * @param assessmentRequestDTO the payload
     * @return A success message
     */
    public Map<String, String> createAssessment(
            CurrentUser teacher,
            CreateAssessmentRequestDTO assessmentRequestDTO) throws BadRequestException {

        return resolve(assessmentRequestDTO.getAssessmentType())
                .createAssessment(teacher, assessmentRequestDTO);
    }

    /**
     * <p>Routes the assessment submission request to the appropriate strategy</p>
     * @param student              the student submitting the assessment
     * @param assessmentRequestDTO the payload
     * @return A success message
     */
    public Map<String, String> submitAssessment(
            CurrentUser student,
            AssessmentRequestDTO assessmentRequestDTO) throws BadRequestException, DocumentProcessingException {

        return resolve(assessmentRequestDTO.getAssessmentType())
                .submitAssessment(student, assessmentRequestDTO);
    }

    /**
     * <p>Routes the serve request to the appropriate strategy</p>
     * @param assessmentId   the assessment to serve
     * @param assessmentType raw type string from the path variable
     * @param user           the authenticated user
     * @return the assessment content DTO
     */
    public AssessmentServeDTO serveAssessment(
            UUID assessmentId,
            String assessmentType,
            CurrentUser user) throws BadRequestException {

        return resolve(parseType(assessmentType))
                .serveAssessment(assessmentId, user);
    }

    /**
     * <p>Routes the report retrieval request to the appropriate strategy</p>
     * @param submissionId   the submission to fetch the report for
     * @param user           the authenticated user
     * @param assessmentType raw type string from the path variable
     * @return the report DTO
     */
    public AssessmentReportDTO getReport(
            UUID submissionId,
            CurrentUser user,
            String assessmentType) throws BadRequestException {

        return resolve(parseType(assessmentType))
                .getReport(submissionId, user);
    }

    /**
     * <p>Starts a quiz session or resumes an existing one</p>
     * @param assessmentId   the quiz assessment ID
     * @param user           the authenticated student
     * @param assessmentType raw type string from the path variable
     * @return session DTO with submissionId, startedAt, duration, and any saved draft answers
     */
    public QuizSessionResponseDTO startSession(
            UUID assessmentId,
            CurrentUser user,
            String assessmentType) {

        return resolve(parseType(assessmentType))
                .startSession(assessmentId, user);
    }

    /**
     * <p>Saves a draft answer for a single question during an active quiz session</p>
     * @param assessmentType    raw type string from the path variable
     * @param submissionId      the active session ID
     * @param questionId        the question being answered
     * @param selectedOptionIds the option(s) selected by the student
     */
    public void saveAnswer(
            String assessmentType,
            UUID submissionId,
            UUID questionId,
            List<UUID> selectedOptionIds) throws BadRequestException {

        resolve(parseType(assessmentType))
                .saveAnswer(submissionId, questionId, selectedOptionIds);
    }

    /**
     * <p>Routes the resubmit request to the appropriate strategy</p>
     * @param student              the student resubmitting
     * @param assessmentRequestDTO the payload (same structure as submit)
     * @return A success message with updated attemptCount
     */
    public Map<String, String> resubmitAssessment(
            CurrentUser student,
            AssessmentRequestDTO assessmentRequestDTO) throws BadRequestException, DocumentProcessingException {

        return resolve(assessmentRequestDTO.getAssessmentType())
                .resubmitAssessment(student, assessmentRequestDTO);
    }

    /**
     * <p>Returns a summary of all student submissions for a given assignment (teacher only)</p>
     * @param assessmentId   the assignment's assessment ID
     * @param assessmentType raw type string from the path variable
     * @param teacher        the authenticated teacher
     * @return list of submission summaries
     */
    public List<StudentSubmissionSummaryDTO> getSubmissionSummaries(
            UUID assessmentId,
            String assessmentType,
            CurrentUser teacher) throws BadRequestException {

        return resolve(parseType(assessmentType))
                .getSubmissionSummaries(assessmentId, teacher);
    }

    public void deleteAssessment(UUID assessmentId, String assessmentType, CurrentUser teacher) throws BadRequestException {
        resolve(parseType(assessmentType)).deleteAssessment(assessmentId,teacher);
    }
}