package com.ctx.course_service.service.contract;

import com.ctx.course_service.model.Course;
import com.ctx.course_service.model.CourseModule;
import org.apache.commons.codec.EncoderException;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public interface CourseVideoService {
    CourseModule uploadVideo(MultipartFile file, String title, Integer sequenceOrder, UUID courseId) throws IOException, EncoderException;
//    String getVideoUrl(UUID id) throws IOException;
//    Resource LoadVideoAsResource(UUID id) throws IOException;
//    String deleteVideoResource(CourseModule video, Course course) throws IOException;
//    CourseModule updateVideoResource(MultipartFile file,String title,UUID videoId, UUID courseId , UUID userId) throws IOException, EncoderException;
//    String deleteVideoResourceWithids(UUID videoId, UUID courseId, UUID id) throws IOException;
}
