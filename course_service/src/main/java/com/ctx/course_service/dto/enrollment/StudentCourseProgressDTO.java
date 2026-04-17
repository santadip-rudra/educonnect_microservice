package com.ctx.course_service.dto.enrollment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentCourseProgressDTO {

    private UUID   courseId;
    private String courseTitle;
    private String courseCode;

    private Double progress;

    private Double finalGrade;

    private Double    remainingDuration;
    private boolean   isActive;
    private LocalDate enrolledDate;

    private LocalDate completedDate;

    private List<ModuleProgressDTO> moduleProgress;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ModuleProgressDTO {
        private UUID    moduleId;
        private String  title;
        private Integer sequenceOrder;
        private Double  duration;
        private boolean completed;

        private LocalDate completedAt;
    }
}