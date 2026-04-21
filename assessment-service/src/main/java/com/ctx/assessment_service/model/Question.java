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

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
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

    // nullable to stay compatible with existing rows
    @Column(nullable = true)
    private Integer marks;

    // [ADDED] true if teacher marked more than one option as correct.
    // Computed at creation time in QuizStrategy — not set by the teacher directly.
    // Tells the frontend to render checkboxes instead of radio buttons.
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private Boolean isMultiOption = false;

    // [ADDED] only relevant when isMultiOption = true.
    // false → student must select ALL correct options or gets 0 for the question.
    // true  → student gets (correctSelected / totalCorrect) × marks (partial credit).
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private Boolean isPartMarkingAllowed = false;
}