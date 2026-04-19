package com.ctx.course_service.service.contract;

import com.ctx.course_service.dto.enrollment.StudentCourseProgressDTO;

import java.util.List;
import java.util.UUID;

public interface ModuleCompletionService {
    void markModuleAsComplete(UUID studentId, UUID moduleId);
    public List<StudentCourseProgressDTO> getProgressPerCourse(UUID studentId);
}
