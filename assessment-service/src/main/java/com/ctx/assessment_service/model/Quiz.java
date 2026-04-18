package com.ctx.assessment_service.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID quizId;

    @OneToOne
    @JoinColumn(name = "assessment_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Assessment assessment ;

    @OneToMany(mappedBy = "quiz")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Question> questionList;


    @OneToMany(mappedBy = "quiz")
    private List<StudentQuizQuestionResponse> studentQuizResponseList;

    @JoinColumn(nullable = true)
    private Double durationMinutes;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
