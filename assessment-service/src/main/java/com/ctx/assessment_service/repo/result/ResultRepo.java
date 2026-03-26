package com.ctx.assessment_service.repo.result;

import com.ctx.assessment_service.model.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResultRepo extends JpaRepository<Result, UUID> {
    Optional<Result> findByAssessmentAssessmentId(UUID assessmentId);
    Optional<Result> findBySubmissionSubmissionId(UUID submissionId);
    boolean existsByAssessmentAssessmentId(UUID assessmentId);
}
