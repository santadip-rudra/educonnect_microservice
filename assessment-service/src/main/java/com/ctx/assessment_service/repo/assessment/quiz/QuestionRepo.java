package com.ctx.assessment_service.repo.assessment.quiz;

import com.ctx.assessment_service.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionRepo extends JpaRepository<Question, UUID> {
    List<Question> findAllByQuizQuizId(UUID quizId);
}
