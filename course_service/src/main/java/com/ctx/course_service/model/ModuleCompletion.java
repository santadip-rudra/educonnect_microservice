package com.ctx.course_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_module_completion",
                columnNames = {"enrollment_id", "module_id"}  // ← prevents duplicate completions
        )
})
public class ModuleCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID completionId;

    private UUID studentId;

    @ManyToOne
    @JoinColumn(name = "module_id")
    private CourseModule module;

    @ManyToOne
    @JoinColumn(name = "enrollment_id")
    private Enrollment enrollment;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDate completedAt;
}
