package com.ctx.course_service.service.contract;



import com.ctx.course_service.dto.ModuleResponseDTO;
import com.ctx.course_service.exceptions.custom_exceptions.UserIdDonotMatchException;
import com.ctx.course_service.model.CourseModule;
import org.apache.commons.codec.EncoderException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface CourseVideoInerface  {
    ModuleResponseDTO uploadVideo(MultipartFile file, String title, UUID courseId , UUID teacherId) throws IOException, EncoderException, ws.schild.jave.EncoderException, UserIdDonotMatchException;
}
