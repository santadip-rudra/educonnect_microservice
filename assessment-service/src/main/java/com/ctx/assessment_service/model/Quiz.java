package com.ctx.assessment_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID quizId;

    @OneToOne
    @JoinColumn(name = "assessment_id")
    @EqualsAndHashCode.Exclude
    private Assessment assessment ;

    @OneToMany(mappedBy = "quiz")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Question> questionList;


    @OneToMany(mappedBy = "quiz")
    private List<StudentQuizQuestionResponse> studentQuizResponseList;

    @JoinColumn(nullable = true)
    private Double durationMinutes;
}
