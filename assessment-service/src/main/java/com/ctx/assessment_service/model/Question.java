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
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Quiz quiz;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OrderBy("position ASC")
    private Set<QuestionOption> questionOptionList;

    @OneToMany(mappedBy = "question")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<StudentQuizQuestionResponse> studentQuizQuestionResponseList;

    @Lob
    @Column(nullable = true, columnDefinition = "LONGBLOB")
    @Basic(fetch = FetchType.LAZY)
    private byte[] imageBinData;

    @Builder.Default
    private Boolean hasImage = false;

    @Column(nullable = true)
    private String imageContentType;

    @Column(nullable = true)
    private String imageFileName;

    @Column(nullable = true, columnDefinition = "INT DEFAULT 10")
    private Integer marks;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private Boolean isMultiOption = false;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private Boolean isPartMarkingAllowed = false;

    @Column(nullable = true)
    private Integer position;
}