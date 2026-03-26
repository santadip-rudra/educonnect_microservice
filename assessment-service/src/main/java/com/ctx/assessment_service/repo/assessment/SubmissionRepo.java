package com.ctx.assessment_service.repo.assessment;


import com.ctx.assessment_service.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubmissionRepo extends JpaRepository<Submission, UUID> {

    Optional<Submission> findByStudentIdAndAssessmentId(UUID studentId, UUID assessmentId);

    boolean existsByStudentIdAndAssessmentId(UUID studentId, UUID assessmentId);

    @Query(
            """
                   SELECT sub FROM Submission sub
                   JOIN FETCH sub.assignmentAttachmentList aal
                   JOIN FETCH sub.assessment asm
                   JOIN FETCH asm.assignment asn
                   WHERE sub.submissionId = :submissionId
            """
    )
    Optional<Submission> findAssignmentAndAssessmentAndAttachments(UUID submissionId);
}
