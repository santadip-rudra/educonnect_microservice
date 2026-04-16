package com.ctx.course_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Data
@ToString(exclude = "course")
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_course_sequence",
                        columnNames = {"course_id","sequence_order"}
                )
        }
)
public class CourseModule {
    @Id
    @Column(nullable = false,updatable = false)
    private UUID moduleId;

    private String title;
    private ModuleType moduleType;
    private String contentUrl; // S3 Link or File Path

    @Column(name = "sequence_order")
    private Integer sequenceOrder; // 1, 2, 3...

    private Double duration ;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonIgnore
    private Course course;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL)
    private List<ModuleCompletion> moduleCompletions = new ArrayList<>();

    @PrePersist
    public void assignSequenceOrder() {
        if (this.sequenceOrder == null && this.course != null) {
            if (this.course.getModules() == null || this.course.getModules().isEmpty()) {
                this.sequenceOrder = 1;
            } else {
                this.sequenceOrder= course.getModules().size()+1;


            }
        }
    }
}