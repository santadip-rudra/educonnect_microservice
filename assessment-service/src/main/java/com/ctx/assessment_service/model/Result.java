package com.ctx.assessment_service.model;

import com.educonnect.model.user.Student;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

//ResultID, AssessmentID, StudentID, Score, Status

@Entity
@Data
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false,updatable = false)
    private UUID resultId;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "assessment_id")
    private Assessment assessment;

    @OneToOne
    @JoinColumn(name ="submission_id" )
    private Submission submission;

    private Double percentageScore ;

    @Enumerated(EnumType.STRING)
    private ResultStatus status;

}
