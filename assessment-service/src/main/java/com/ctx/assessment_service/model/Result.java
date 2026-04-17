package com.ctx.assessment_service.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

//ResultID, AssessmentID, StudentID, Score, Status

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false,updatable = false)
    private UUID resultId;

    @Column(nullable = false,updatable = false)
    private UUID studentId;

    @ManyToOne
    @JoinColumn(name = "assessment_id")
    private Assessment assessment;

    @OneToOne
    @JoinColumn(name ="submission_id" )
    private Submission submission;

    private Double percentageScore ;

    @Enumerated(EnumType.STRING)
    private ResultStatus status;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
