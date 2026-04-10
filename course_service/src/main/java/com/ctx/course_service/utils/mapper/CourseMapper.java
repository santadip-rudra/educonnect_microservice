package com.ctx.course_service.utils.mapper;


import com.ctx.course_service.clientCall.TeacherClient;
import com.ctx.course_service.dto.CourseRequestDTO;
import com.ctx.course_service.dto.CourseResponseDTO;
import com.ctx.course_service.dto.ModuleResponseDTO;
import com.ctx.course_service.dto.teacher.TeacherResponse;
import com.ctx.course_service.model.Course;
import com.ctx.course_service.model.CourseModule;
import com.ctx.course_service.service.contract.CourseModuleInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class CourseMapper implements Mapper<Course, CourseRequestDTO, CourseResponseDTO>{

    private final CourseModuleInterface courseModuleInterface;

    @Override
    public Course toEntity(CourseRequestDTO requestDTO) {
        Course course=new Course();
        course.setTitle(requestDTO.title());
        course.setDescription(requestDTO.description());
        course.setCourseCode(requestDTO.courseCode());
        course.setDuration(0.0);
        return course;
    }

    @Override
    public CourseResponseDTO toResponseDTO(Course entity) {
        List<ModuleResponseDTO> module=entity.getModules().stream().map(module1 -> getModule(module1)).toList();
        return new CourseResponseDTO(
                entity.getCourseId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getCourseCode(),
                entity.getDuration(),
                entity.getTeacherId(),
                module
        );
    }

    public ModuleResponseDTO getModule(CourseModule courseModule)
    {
        return new ModuleResponseDTO(
                courseModule.getContentUrl()
                ,courseModule.getModuleId(),
                courseModule.getTitle()
                ,courseModule.getDuration(),
                courseModule.getSequenceOrder(),
                courseModuleInterface.getModuleUrl(courseModule.getModuleId())
                );
    }
}
