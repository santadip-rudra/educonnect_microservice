package com.ctx.assessment_service.service.contract.result;



import com.ctx.assessment_service.dto.result.CoursePassFailStatsDTO;
import com.ctx.assessment_service.dto.result.ExamStatsDTO;
import com.ctx.assessment_service.dto.result.MonthlyAssessmentStatsDTO;
import com.ctx.assessment_service.dto.result.MonthlyExamStatsDTO;
import com.ctx.assessment_service.dto.assessment.report.AssessmentReportDTO;
import com.ctx.assessment_service.dto.assessment.result.StudentResultDTO;
import com.ctx.assessment_service.dto.user.CurrentUser;
import com.ctx.assessment_service.exception.custom_exceptions.UserNotFoundException;
import com.ctx.assessment_service.model.Result;
import org.apache.coyote.BadRequestException;

import java.util.List;
import java.util.UUID;

/**
 * Contract for {@link Result} related operations
 */
public interface ResultService {
    /**
     * This method computes the result of an attempted {@link com.educonnect.model.assessment.Quiz}
     * (the teacher does not have to evaluate , the score of the Quiz shall be computed automatically)
     * @param assessmentId The unique identifier of the assessment(Quiz)
     * @param studentId The unique identifier of student who submitted the assessment (attempted the quiz)
     * @return A success message
     */
    String computeQuizResult(UUID assessmentId,UUID studentId);

    /**
     * This method enables teacher to evaluate an {@link com.educonnect.model.assessment.Assessment} of a student
     * (the teacher has to manually evaluate each assignment)
     * @param assessmentId The unique identifier of the assessment(Assignment)
     * @param studentId The unique identifier of student who submitted the assessment (Assignment)
     * @param teacher The Teacher who evaluates the assignment
     * @param givenScore The score given by the teacher
     * @return A success message
     */
    String evaluateStudent(UUID assessmentId, UUID studentId,
                           CurrentUser teacher, double givenScore)
            throws BadRequestException, UserNotFoundException;

    /**
     * This method returns the result
     * @param submissionId The unique identifier of the Submission
     * @return The {@link Result}
     */
    Result getResultWithId(UUID submissionId, CurrentUser user) throws BadRequestException;

    List<StudentResultDTO> getAllResultsByStudentId(UUID studentId, CurrentUser user) throws BadRequestException;

    List<MonthlyExamStatsDTO> getMonthlyExamStats();

    List<MonthlyAssessmentStatsDTO> getMonthlyAssessmentAndSubmissionStats();

    List<CoursePassFailStatsDTO> getCoursePassFailStats();
}

