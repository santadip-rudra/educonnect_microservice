package com.ctx.assessment_service.service.implementation.result;

import com.ctx.assessment_service.client.CourseServiceClient;
import com.ctx.assessment_service.client.UserManagementServiceClient;
import com.ctx.assessment_service.dto.assessment.report.AssessmentReportDTO;
import com.ctx.assessment_service.dto.assessment.result.StudentResultDTO;
import com.ctx.assessment_service.dto.result.CoursePassFailStatsDTO;
import org.springframework.transaction.annotation.Transactional;
import com.ctx.assessment_service.dto.external_response.CourseResponse;
import com.ctx.assessment_service.dto.external_response.StudentResponse;
import com.ctx.assessment_service.dto.result.ExamStatsDTO;
import com.ctx.assessment_service.dto.result.MonthlyAssessmentStatsDTO;
import com.ctx.assessment_service.dto.result.MonthlyExamStatsDTO;
import com.ctx.assessment_service.dto.user.CurrentUser;
import com.ctx.assessment_service.exception.custom_exceptions.ResourceNotFoundException;
import com.ctx.assessment_service.model.*;
import com.ctx.assessment_service.repo.EntityManagerRepo;
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
import java.util.Map;
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

    private final EntityManagerRepo entityManagerRepo;

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

        // Push updated average to course-service so Enrollment.finalGrade stays current.
        pushAverageToCourseService(studentId, assessment.getCourseId());

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

            // Push updated average to course-service.
            pushAverageToCourseService(studentId, assessment.getCourseId());

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

        // Push updated average to course-service.
        pushAverageToCourseService(studentId, assessment.getCourseId());

        return "Result of `" + student.getFullName() + "` [evaluated by `" + teacher.getUsername() + "`] saved successfully";
    }

    /**
     * Computes the student's weighted average percentageScore across all Results they
     * have for the given course, then calls course-service to persist it on
     * Enrollment.finalGrade.
     *
     * Formula: sum(score x weight) / sum(weight)
     *   - Only assessments the student has a Result for contribute to the sum.
     *   - Ungraded assessments are excluded entirely (not treated as 0),
     *     so the grade always reflects only what has actually been evaluated.
     *
     * Failure handling: if the Feign call fails (course-service down), we log and
     * swallow -- the Result is already saved, so data is safe. Kafka is the
     * production-grade upgrade path.
     */
    private void pushAverageToCourseService(UUID studentId, UUID courseId) {
        try {
            List<Assessment> courseAssessments = assessmentRepo.findByCourseId(courseId);
            List<UUID> assessmentIds = courseAssessments.stream()
                    .map(Assessment::getAssessmentId)
                    .toList();

            // Only include assessments the student has a Result for.
            // Missing result = not yet graded, not a zero.
            List<Result> studentResults = resultRepo.findAllByStudentId(studentId).stream()
                    .filter(r -> r.getAssessment() != null
                            && assessmentIds.contains(r.getAssessment().getAssessmentId()))
                    .toList();

            if (studentResults.isEmpty()) {
                log.warn("No results found for studentId={} courseId={} - skipping finalGrade push", studentId, courseId);
                return;
            }

            // Weighted average => sum(score x weight) / sum(weight)
            double weightedScoreSum = studentResults.stream()
                    .mapToDouble(r -> r.getPercentageScore() * r.getAssessment().getWeight())
                    .sum();

            double totalWeight = studentResults.stream()
                    .mapToDouble(r -> r.getAssessment().getWeight())
                    .sum();

            double weightedAverage = (totalWeight == 0) ? 0.0 : weightedScoreSum / totalWeight;

            courseServiceClient.updateFinalGrade(studentId, courseId, Map.of("finalGrade", weightedAverage));

            log.info("Pushed weighted finalGrade={} for studentId={} courseId={} ({}/{} assessments graded)",
                    weightedAverage, studentId, courseId, studentResults.size(), courseAssessments.size());

        } catch (Exception e) {
            log.error("Failed to push finalGrade to course-service for studentId={} courseId={}: {}",
                    studentId, courseId, e.getMessage());
        }
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

    @Override
    public List<MonthlyExamStatsDTO> getMonthlyExamStats(){
        return entityManagerRepo.getMonthlyExamStats();
    }

    @Override
    public List<MonthlyAssessmentStatsDTO> getMonthlyAssessmentAndSubmissionStats(){
        return entityManagerRepo.getMonthlyAssessmentAndSubmissionStats();
    }

    @Override
    public List<CoursePassFailStatsDTO> getCoursePassFailStats() {
        return entityManagerRepo.getCoursePassFailStats();
    }

}