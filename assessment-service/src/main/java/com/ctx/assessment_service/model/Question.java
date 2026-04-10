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
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID questionId;

    private String questionText;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @OneToMany(mappedBy = "question")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<QuestionOption> questionOptionList;

    @OneToMany(mappedBy = "question")
    private List<StudentQuizQuestionResponse> studentQuizQuestionResponseList;

    @Lob
    @Column(nullable = true, columnDefinition = "LONGBLOB")
    @Basic(fetch = FetchType.LAZY)
    private byte[] imageBinData;

    private Boolean hasImage = false;

    @Column(nullable = true)
    private String imageContentType;

    @Column(nullable = true)
    private String imageFileName;

}
