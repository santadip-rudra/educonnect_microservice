package com.ctx.report_service.dto.external.course;



import java.util.List;
import java.util.UUID;

public record CourseResponseDTO (

        UUID courseId,
     String title,
     String description,
     String courseCode,
        Double duration,
     UUID teacherId,
     List<ModuleResponseDTO> moduleResponseDTOList,
     List<AssessmentResponseDTO> assessmentResponseDTOList,
     List<EnrollmentResponseDTOServe> enrollmentResponseDTOList
){
}