package com.ctx.course_service.service.contract;

import com.ctx.course_service.dto.CourseResponseDTO;
import com.ctx.course_service.dto.ModuleResponseDTO;
import com.ctx.course_service.exceptions.custom_exceptions.UserIdDonotMatchException;
import com.ctx.course_service.model.Course;
import com.ctx.course_service.model.CourseModule;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.EncoderException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface CourseModuleInterface {

    ModuleResponseDTO uploadModule(MultipartFile file, String title, UUID courseId, UUID teacherId)
            throws IOException, EncoderException, UserIdDonotMatchException;

    String getModuleUrl(UUID id);

    Resource loadModuleAsResource(UUID moduleId) throws IOException;

    CourseModule updateModule(MultipartFile file, String title, UUID moduleId, UUID courseId, UUID userId)
            throws IOException;

    String deleteModule(Course course, CourseModule module) throws IOException;

    String deleteModuleById(UUID moduleId, UUID courseId, UUID userId)
            throws IOException, UserIdDonotMatchException;


}