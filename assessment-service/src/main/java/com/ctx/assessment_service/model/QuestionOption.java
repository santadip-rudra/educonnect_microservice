package com.ctx.assessment_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionOption {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID questionOptionId;

    @ManyToOne
    @JoinColumn(name = "question_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Question question;

    private String optionText;

    private Boolean isCorrectOption ;

    @OneToMany(mappedBy = "questionOption")
    private List<StudentQuizQuestionResponse> studentQuizQuestionResponseList;
}
