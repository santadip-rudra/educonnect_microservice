package com.ctx.course_service.model;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Data
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
    private List<CourseModule> modules;

    @OneToMany(mappedBy = "course")
    private List<Enrollment> enrollments;

//    @OneToMany(mappedBy = "course")
//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    private List<Assessment> assessments;
//
//    @OneToMany(mappedBy = "course")
//    private List<Attendance> attendanceList;
//
//    @OneToMany(mappedBy = "course")
//    private List<Engagement> engagements;
}