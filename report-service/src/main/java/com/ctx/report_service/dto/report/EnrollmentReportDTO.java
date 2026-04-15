package com.ctx.report_service.dto.report;


import com.ctx.report_service.dto.external.StudentResponse;
import com.ctx.report_service.dto.external.course.CourseResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class EnrollmentReportDTO {
    private List<CourseResponseDTO> courseResponse;
    private List<StudentResponse> studentResponse;
}
