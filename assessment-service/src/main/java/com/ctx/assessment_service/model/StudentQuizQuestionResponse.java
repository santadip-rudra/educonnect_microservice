package com.ctx.assessment_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_student_quiz_question_response",
                        columnNames = {"submission_id", "question_id", "question_option_id"}
                )
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentQuizQuestionResponse { //Respose for each question (options actually)
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID studentQuizQuestionResponseId;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Quiz quiz;

    @ManyToOne
    @JoinColumn(name = "question_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Question question;

    @ManyToOne
    @JoinColumn(name = "question_option_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private QuestionOption questionOption;

    @ManyToOne
    @JoinColumn(name = "submission_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Submission submission;
}