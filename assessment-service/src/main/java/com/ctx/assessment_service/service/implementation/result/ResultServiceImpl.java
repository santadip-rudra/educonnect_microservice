package com.ctx.assessment_service.service.implementation.result;

import com.ctx.assessment_service.client.CourseServiceClient;
import com.ctx.assessment_service.client.UserManagementServiceClient;
import com.ctx.assessment_service.dto.assessment.report.AssessmentReportDTO;
import com.ctx.assessment_service.dto.assessment.result.StudentResultDTO;
import org.springframework.transaction.annotation.Transactional;
import com.ctx.assessment_service.dto.external_response.CourseResponse;
import com.ctx.assessment_service.dto.external_response.StudentResponse;
import com.ctx.assessment_service.dto.user.CurrentUser;
import com.ctx.assessment_service.exception.custom_exceptions.ResourceNotFoundException;
import com.ctx.assessment_service.model.*;
import com.ctx.assessment_service.repo.assessment.AssessmentRepo;
import com.ctx.assessment_service.repo.assessment.SubmissionRepo;
import com.ctx.assessment_service.repo.assessment.quiz.StudentQuizQuestionResponseRepo;
import com.ctx.assessment_service.repo.result.ResultRepo;
import com.ctx.assessment_service.service.contract.result.ResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResultServiceImpl implements ResultService {

    private final SubmissionRepo submissionRepo;
    private final AssessmentRepo assessmentRepo;
    //    private final StudentRepo studentRepo;
    private final ResultRepo resultRepo;
    private final StudentQuizQuestionResponseRepo studentQuizQuestionResponseRepo;

    private final UserManagementServiceClient userManagementServiceClient;
    private final CourseServiceClient courseServiceClient;

    @Override
    public String computeQuizResult(UUID assessmentId, UUID studentId) {
        Assessment assessment = assessmentRepo.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));

        Submission submission = submissionRepo.findByStudentIdAndAssessmentAssessmentId(studentId, assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        List<StudentQuizQuestionResponse> studentQuizQuestionResponseList
                = studentQuizQuestionResponseRepo.findAllBySubmission(submission);

        long totalResponse = studentQuizQuestionResponseList.size();
        long correctResponse = studentQuizQuestionResponseList.stream()
                .filter(resp -> resp.getIsCorrectOptionChosen())
                .count();

        double score = (totalResponse == 0) ? 0.0
                : (double) correctResponse / totalResponse;

        Result result = new Result();
        result.setAssessment(assessment);
        result.setStudentId(studentId);
        result.setSubmission(submission);
        result.setPercentageScore(score);
        result.setStatus(score >= 0.4 ? ResultStatus.PASSED : ResultStatus.FAILED);

        resultRepo.save(result);

        return "result computed successfully";
    }

    @Override
    public String evaluateStudent(UUID assessmentId, UUID studentId, CurrentUser teacher, double givenScore) throws BadRequestException {

        Assessment assessment = assessmentRepo.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));

        CourseResponse course = courseServiceClient.getcourse(assessment.getCourseId());

        if (course == null) {
            throw new ResourceNotFoundException("Course not found");
        }

        if (!course.getTeacherId().equals(teacher.getUserId())) {
            throw new BadRequestException("Teacher `" + teacher.getUsername() + "` does not have permission to evaluate");
        }

        if (resultRepo.existsByAssessmentAssessmentIdAndStudentId(assessmentId, studentId)) {
            log.info("Overwriting the assignment score...");

            Result result = resultRepo
                    .findByAssessmentAssessmentIdAndStudentId(assessmentId, studentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Result not found"));

            double score = (assessment.getMaxScore() == 0) ? 0.0
                    : (double) givenScore / assessment.getMaxScore();

            result.setPercentageScore(score);
            result.setStatus(score >= 0.4 ? ResultStatus.PASSED : ResultStatus.FAILED);

            resultRepo.save(result);

            return teacher.getUsername() + " updated the score of the assignment";
        }

        StudentResponse student = userManagementServiceClient.findByStudentId(studentId);

        Submission submission = submissionRepo.findByStudentIdAndAssessmentAssessmentId(studentId, assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student [name: " + student.getFullName()
                        + "] has not submitted the assignment yet"));

        if (!submission.getSubmissionStatus().equals(SubmissionStatus.SUBMITTED)) {
            throw new BadRequestException("Student [name: " + student.getFullName() + "] has not submitted the assignment yet");
        }

        double score = (assessment.getMaxScore() == 0) ? 0.0
                : (double) givenScore / assessment.getMaxScore();

        Result result = new Result();
        result.setAssessment(assessment);
        result.setStudentId(studentId);
        result.setSubmission(submission);
        result.setPercentageScore(score);
        result.setStatus(score >= 0.4 ? ResultStatus.PASSED : ResultStatus.FAILED);

        resultRepo.save(result);

        return "Result of `" + student.getFullName() + "` [evaluated by `" + teacher.getUsername() + "`] saved successfully";
    }

    @Override
    public Result getResultWithId(UUID submissionId, CurrentUser user) throws BadRequestException {

        Submission submission = submissionRepo.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        if (user.getRole().equals("STUDENT") &&
                !submission.getStudentId().equals(user.getUserId())) {
            throw new BadRequestException("Student [" + user.getUsername() + "] is not authorized to access this result");
        }

        return resultRepo.findBySubmissionSubmissionId(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Result not found"));
    }


    @Override
    @Transactional(readOnly = true)
    public List<StudentResultDTO> getAllResultsByStudentId(UUID studentId, CurrentUser user) throws BadRequestException {
        if (user.getRole().equals("STUDENT") && !user.getUserId().equals(studentId)) {
            throw new BadRequestException("Not authorized to access these results");
        }

        List<Result> results = resultRepo.findAllByStudentId(studentId);

        return results.stream().map(r -> StudentResultDTO.builder()
                .resultId(r.getResultId())
                .studentId(r.getStudentId())
                .percentageScore(r.getPercentageScore())
                .status(r.getStatus() != null ? r.getStatus().name() : null)
                .assessmentId(r.getAssessment() != null ? r.getAssessment().getAssessmentId() : null)
                .assessmentTitle(r.getAssessment() != null ? r.getAssessment().getTitle() : null)
                .maxScore(r.getAssessment() != null ? r.getAssessment().getMaxScore() : null)
                .assessmentType(r.getAssessment() != null ? r.getAssessment().getType().name() : null)
                .courseId(r.getAssessment() != null ? r.getAssessment().getCourseId() : null)
                .submissionId(r.getSubmission() != null ? r.getSubmission().getSubmissionId() : null)
                .build()
        ).toList();
    }

}