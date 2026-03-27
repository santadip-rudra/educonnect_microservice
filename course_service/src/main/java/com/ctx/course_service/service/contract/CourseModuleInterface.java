package com.ctx.course_service.service.contract;



import com.ctx.course_service.dto.ModuleResponseDTO;
import com.ctx.course_service.exceptions.custom_exceptions.UserIdDonotMatchException;
import com.ctx.course_service.model.Course;
import com.ctx.course_service.model.CourseModule;
import org.apache.commons.codec.EncoderException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface CourseModuleInterface  {
    ModuleResponseDTO uploadModule(MultipartFile file, String title, UUID courseId , UUID teacherId) throws IOException, EncoderException, ws.schild.jave.EncoderException, UserIdDonotMatchException;
    String getVideoUrl(UUID id) throws IOException;
  Resource LoadVideoAsResource(UUID id) throws IOException;
      String deleteVideoResource(Course course, CourseModule module) throws IOException,UserIdDonotMatchException;
  CourseModule updateVideoResource(MultipartFile file, String title, UUID videoId, UUID courseId,UUID userId) throws IOException, EncoderException;
  String deleteVideoResourceWithids(UUID videoId, UUID courseId, UUID id) throws IOException, UserIdDonotMatchException;
}
