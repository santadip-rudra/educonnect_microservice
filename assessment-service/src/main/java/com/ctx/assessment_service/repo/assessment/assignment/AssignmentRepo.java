package com.ctx.assessment_service.repo.assessment.assignment;


import com.ctx.assessment_service.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface AssignmentRepo extends JpaRepository<Assignment, UUID> {

    @Query(
            """
                   SELECT asn FROM Assignment asn
                   JOIN FETCH asn.assessment asm
                   WHERE asn.assessment.assessmentId = :assessmentId
            """
    )
    Optional<Assignment> findAssignmentAndAssessment(UUID assessmentId);

}
