package com.ctx.course_service;
import com.ctx.course_service.dto.ModuleResponseDTO;
import com.ctx.course_service.exceptions.custom_exceptions.FileException;
import com.ctx.course_service.exceptions.custom_exceptions.UserIdDonotMatchException;
import com.ctx.course_service.model.Course;
import com.ctx.course_service.model.CourseModule;
import com.ctx.course_service.model.ModuleType;
import com.ctx.course_service.repo.CourseModuleRepo;
import com.ctx.course_service.repo.CourseRepo;
import com.ctx.course_service.service.contract.CourseVideoInerface;
import com.ctx.course_service.utils.video.VideoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value; // Fixed Import
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.EncoderException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseVideoInterfaceImpl implements CourseVideoInerface {

    @Value("${file.upload-dir-one}")
    private String uploadDir;

    @Value("${file.tempUpload-dir-one}")
    private String tempUploadDir;

    private final CourseModuleRepo courseModuleRepo;
    private final CourseRepo courseRepo;

    @Override
    @Transactional(rollbackFor = Exception.class) // Ensures DB rolls back on error
    public ModuleResponseDTO uploadVideo(MultipartFile file, String title, UUID courseId, UUID teacherId)
            throws IOException, EncoderException, UserIdDonotMatchException {

        if (file == null || file.isEmpty()) {
            throw new FileNotFoundException("File is empty or not found");
        }

        // 1. Validate Course and Ownership
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new IOException("Course does not exist"));

        if (!course.getTeacherId().equals(teacherId)) {
            throw new UserIdDonotMatchException("Unauthorized: Only the course creator can upload modules.");
        }

        // 2. Determine File Type and Pathing
        String originalFilename = file.getOriginalFilename();
        String extension = (originalFilename != null && originalFilename.contains("."))
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";

        String contentType = file.getContentType();
        String folder;
        ModuleType moduleType;

        if (contentType != null && contentType.startsWith("audio")) {
            folder = "audio/";
            moduleType = ModuleType.AUDIO;
        } else if (contentType != null && contentType.startsWith("video")) {
            folder = "video/";
            moduleType = ModuleType.VIDEO;
        } else if (MediaType.APPLICATION_PDF_VALUE.equals(contentType)) {
            folder = "pdf/";
            moduleType = ModuleType.PDF;
        } else {
            throw new FileException("Unsupported file type: " + contentType);
        }

        Path targetFolderPath = Paths.get(this.uploadDir).resolve(folder);
        Path targetTempFolderPath = Paths.get(this.tempUploadDir).resolve(folder);

        Files.createDirectories(targetFolderPath);
        Files.createDirectories(targetTempFolderPath);

        UUID uuid = UUID.randomUUID();
        String filename = uuid + extension;
        Path finalFilePath = targetFolderPath.resolve(filename);
        Path tempPath = targetTempFolderPath.resolve("temp-" + filename);

        try {
            // 3. Save to Temp first for processing
            try (InputStream is = file.getInputStream()) {
                Files.copy(is, tempPath, StandardCopyOption.REPLACE_EXISTING);
            }

            // 4. Calculate Metadata
            double duration = 0.0;
            if (moduleType != ModuleType.PDF) {
                duration = VideoUtil.getVideoDuration(tempPath.toFile());
            }

            // 5. Move from Temp to Final destination
            Files.move(tempPath, finalFilePath, StandardCopyOption.REPLACE_EXISTING);

            // 6. Database Operations
            CourseModule module = CourseModule.builder()
                    .moduleId(uuid)
                    .contentUrl(filename)
                    .course(course)
                    .title(title)
                    .moduleType(moduleType)
                    .duration(duration)
                    .build();

            var resp = courseModuleRepo.save(module);

            // Update course aggregate duration
            double currentCourseDuration = (course.getDuration() == null) ? 0.0 : course.getDuration();
            course.setDuration(currentCourseDuration + duration);
            courseRepo.save(course);

            log.info("Successfully uploaded module {} to course {}", uuid, courseId);
            return resp;

        } catch (Exception e) {
            log.error("Upload failed for course {}: {}", courseId, e.getMessage());
            // Cleanup final file if it was moved before the DB failed
            Files.deleteIfExists(finalFilePath);
            throw e;
        } finally {
            // Always clean up temp file
            Files.deleteIfExists(tempPath);
        }
    }
}