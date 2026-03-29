package com.ctx.assessment_service.model;

import com.ctx.assessment_service.model.enums.AssessmentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false,updatable = false)
    private UUID assessmentId;

    private Double maxScore;
    private String title;

    @Enumerated(EnumType.STRING)
    private AssessmentType type;


    @Column(nullable = false)
    private UUID courseId;

    @OneToOne(mappedBy = "assessment")
    private Assignment assignment;

    @OneToOne(mappedBy = "assessment")
    private  Quiz quiz;

}
