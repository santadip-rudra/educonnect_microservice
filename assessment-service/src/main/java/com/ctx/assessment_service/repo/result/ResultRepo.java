package com.ctx.assessment_service.repo.result;

import com.ctx.assessment_service.dto.report.ExamStatsDTO;
import com.ctx.assessment_service.dto.report.GraphDataPointDTO;
import com.ctx.assessment_service.model.Result;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResultRepo extends JpaRepository<Result, UUID> {
    Optional<Result> findByAssessmentAssessmentId(UUID assessmentId);
    Optional<Result> findBySubmissionSubmissionId(UUID submissionId);
    boolean existsByAssessmentAssessmentId(UUID assessmentId);
    boolean existsByAssessmentAssessmentIdAndStudentId(UUID assessmentId, UUID studentId);
    Optional<Result> findByAssessmentAssessmentIdAndStudentId(UUID assessmentId, UUID studentId);

    // --- Added for Reporting ---

    @Query("SELECT new com.ctx.assessment_service.dto.report.ExamStatsDTO(" +
            "CAST(AVG(r.percentageScore) AS double), " +
            "COUNT(r.resultId), " +
            "CAST(MAX(r.percentageScore) AS double)) " +
            "FROM Result r")
    ExamStatsDTO getGlobalExamStats();

    @Query("SELECT new com.ctx.assessment_service.dto.report.GraphDataPointDTO(r.dateCreated, r.percentageScore) " +
            "FROM Result r WHERE r.studentId = :studentId ORDER BY r.dateCreated ASC")
    List<GraphDataPointDTO> findTrendByStudentId(@Param("studentId") UUID studentId);

    // Trend for a specific Course (Aggregated)
    @Query("SELECT new com.ctx.assessment_service.dto.report.GraphDataPointDTO(r.dateCreated, AVG(r.percentageScore)) " +
            "FROM Result r WHERE r.assessment.courseId = :courseId " +
            "GROUP BY r.dateCreated " +
            "ORDER BY r.dateCreated ASC")
    List<GraphDataPointDTO> findTrendByCourseId(@Param("courseId") UUID courseId);

    @Query("SELECT new com.ctx.assessment_service.dto.report.GraphDataPointDTO(r.dateCreated, AVG(r.percentageScore)) " +
            "FROM Result r WHERE r.assessment.assessmentId = :assessmentId " +
            "GROUP BY r.dateCreated " +
            "ORDER BY r.dateCreated ASC")
    List<GraphDataPointDTO> findTrendByAssessmentId(@Param("assessmentId") UUID assessmentId);
}
