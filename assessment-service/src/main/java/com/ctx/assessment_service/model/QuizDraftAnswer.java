package com.ctx.assessment_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_draft_answer",
                        columnNames = {"submission_id", "question_id"}
                )
        }
)
public class QuizDraftAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID draftAnswerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private Submission submission;

    @Column(nullable = false)
    private UUID questionId;

    // Comma-separated option IDs — simple, no extra join table needed
    // e.g. "uuid1,uuid2" for multi-select, "uuid1" for single
    @Column(nullable = false)
    private String selectedOptionIds;
}