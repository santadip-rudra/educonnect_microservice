package com.ctx.course_service.model;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;


@Data
@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = {"modules", "enrollments"})
@ToString(exclude = {"modules", "enrollments"})
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false,updatable = false)
    private UUID courseId;


    private String title;
    private String description;
    private String courseCode; // e.g., "CS101"


    private UUID teacherId;

    private Double duration;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private Set<CourseModule> modules = new HashSet<>();;

    @OneToMany(mappedBy = "course")

    private Set<Enrollment> enrollments = new HashSet<>();;

    @Column(nullable = false)
    private Long credit;

}