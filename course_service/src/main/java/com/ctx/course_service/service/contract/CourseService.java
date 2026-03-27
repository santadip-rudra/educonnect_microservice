package com.ctx.course_service.service.contract;



import com.ctx.course_service.dto.CourseRequestDTO;
import com.ctx.course_service.dto.CourseResponseDTO;
import com.ctx.course_service.dto.ModuleResponseDTO;

import java.util.List;
import java.util.UUID;

public interface CourseService{
    CourseResponseDTO addCourse(CourseRequestDTO request, UUID teacherId);
    public List<CourseResponseDTO> getAllCourse();
    public CourseResponseDTO getByIdCourse(UUID id) throws Exception;
    public String deleteById(UUID id);
List<ModuleResponseDTO> getAllModulesOfACourse(UUID courseId );
}
