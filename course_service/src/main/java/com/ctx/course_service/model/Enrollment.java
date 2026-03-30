package com.ctx.course_service.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "course_id"})
})
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false,updatable = false)
    private UUID enrollmentId;

    private UUID studentId;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;


    private boolean isActive = true;

    private Double remainingDuration;

    private Double progress;

    private Double finalGrade;
}