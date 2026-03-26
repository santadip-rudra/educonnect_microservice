package com.ctx.assessment_service.repo.assessment;


import com.ctx.assessment_service.model.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AssessmentRepo extends JpaRepository<Assessment, UUID> {
}
