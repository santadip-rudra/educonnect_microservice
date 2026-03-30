package com.ctx.assessment_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_student_quiz_question_response",
                        columnNames = {"submission_id","question_id"}
                )
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentQuizQuestionResponse { //Respose for each question
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID studentQuizQuestionResponseId;

    private Boolean isCorrectOptionChosen;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "question_option_id")
    private QuestionOption questionOption;  // We might need to create another table!!! IFF we accept multiple ans

    @ManyToOne
    @JoinColumn(name = "submission_id")
    private Submission submission;

}
