package com.ctx.assessment_service.repo.assessment.quiz;

import com.ctx.assessment_service.model.QuizDraftAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuizDraftAnswerRepo extends JpaRepository<QuizDraftAnswer, UUID> {

    List<QuizDraftAnswer> findAllBySubmissionSubmissionId(UUID submissionId);

    Optional<QuizDraftAnswer> findBySubmissionSubmissionIdAndQuestionId(
            UUID submissionId, UUID questionId);

    @Modifying
    @Query("DELETE FROM QuizDraftAnswer d WHERE d.submission.submissionId = :submissionId")
    void deleteAllBySubmissionId(UUID submissionId);
}