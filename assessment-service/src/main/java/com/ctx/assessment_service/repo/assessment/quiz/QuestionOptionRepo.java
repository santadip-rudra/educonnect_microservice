package com.ctx.assessment_service.repo.assessment.quiz;


import com.ctx.assessment_service.model.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionOptionRepo extends JpaRepository<QuestionOption, UUID> {
    List<QuestionOption> findAllByQuestionQuestionId(UUID questionId);
}
