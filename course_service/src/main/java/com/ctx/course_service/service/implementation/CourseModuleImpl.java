package com.ctx.course_service.service.implementation;
import com.ctx.course_service.dto.CourseResponseDTO;
import com.ctx.course_service.dto.ModuleResponseDTO;
import com.ctx.course_service.exceptions.custom_exceptions.FileException;
import com.ctx.course_service.exceptions.custom_exceptions.ModuleNotFoundException;
import com.ctx.course_service.exceptions.custom_exceptions.ResourceNotFoundException;
import com.ctx.course_service.exceptions.custom_exceptions.UserIdDonotMatchException;
import com.ctx.course_service.model.Course;
import com.ctx.course_service.model.CourseModule;
import com.ctx.course_service.model.ModuleType;
import com.ctx.course_service.repo.CourseModuleRepo;
import com.ctx.course_service.repo.CourseRepo;
import com.ctx.course_service.service.contract.CourseModuleInterface;
import com.ctx.course_service.utils.video.VideoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ws.schild.jave.EncoderException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseModuleImpl implements CourseModuleInterface {

    @Value("${gateway.url}")
    private String gatewayUrl;

    @Value("${file.upload-dir-one}")
    private String uploadDir;

    @Value("${file.tempUpload-dir-one}")
    private String tempUploadDir;

    private final CourseModuleRepo courseModuleRepo;
    private final CourseRepo courseRepo;

    @Override
    public ModuleResponseDTO uploadModule(MultipartFile file, String title, UUID courseId, UUID teacherId)
            throws IOException, EncoderException, UserIdDonotMatchException {

        Path tempPath = null;
        Path finalFilePath = null;

        try {
            if (file == null || file.isEmpty()) {
                throw new FileNotFoundException("File not found!!");
            }

            if (file.getContentType() == null) {
                throw new FileException("Could not determine file type!");
            }

            Course course = courseRepo.findById(courseId)
                    .orElseThrow(() -> new IOException("Course does not exist"));

            if (!course.getTeacherId().equals(teacherId)) {
                throw new UserIdDonotMatchException("Unauthorized: Only the course creator can upload modules.");
            }

            String extension = file.getOriginalFilename()
                    .substring(file.getOriginalFilename().lastIndexOf("."));

            MediaType mediaType = MediaType.parseMediaType(file.getContentType());

            String folder;
            ModuleType moduleType;

            if (mediaType.getType().equals("audio")) {
                folder = "audio/";
                moduleType = ModuleType.AUDIO;
            } else if (mediaType.getType().equals("video")) {
                folder = "video/";
                moduleType = ModuleType.VIDEO;
            } else if (mediaType.equalsTypeAndSubtype(MediaType.APPLICATION_PDF)) {
                folder = "pdf/";
                moduleType = ModuleType.PDF;
            } else {
                throw new FileException("Unsupported file type: " + file.getContentType());
            }

            Path targetFolderPath = Paths.get(this.uploadDir).resolve(folder);
            Path targetTempFolderPath = Paths.get(this.tempUploadDir).resolve(folder);

            if (!Files.exists(targetFolderPath)) {
                Files.createDirectories(targetFolderPath);
                log.info("Created target folder: {}", targetFolderPath);
            }
            if (!Files.exists(targetTempFolderPath)) {
                Files.createDirectories(targetTempFolderPath);
                log.info("Created temp folder: {}", targetTempFolderPath);
            }

            UUID uuid = UUID.randomUUID();
            String filename = uuid.toString() + extension;

            finalFilePath = targetFolderPath.resolve(filename);
            Files.copy(file.getInputStream(), finalFilePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Module file saved to: {}", finalFilePath);

            double duration = 0.0;

            if (moduleType != ModuleType.PDF) {
                tempPath = targetTempFolderPath.resolve("temp-" + uuid + extension);
                Files.copy(finalFilePath, tempPath, StandardCopyOption.REPLACE_EXISTING);
                duration = VideoUtil.getVideoDuration(tempPath.toFile());
                log.info("Duration calculated: {}", duration);
            }

            int nextSequenceOrder = courseModuleRepo.countByCourse(course) + 1;
            log.info("Assigned sequence order: {}", nextSequenceOrder);

            CourseModule savedModule = courseModuleRepo.save(CourseModule.builder()
                    .moduleId(uuid)
                    .contentUrl(filename)
                    .course(course)
                    .title(title)
                    .moduleType(moduleType)
                    .duration(duration)
                    .sequenceOrder(nextSequenceOrder)
                    .build());

            log.info("Course duration before update: {}", course.getDuration());
            course.setDuration((course.getDuration() == null ? 0 : course.getDuration()) + duration);
            courseRepo.save(course);
            log.info("Course duration after update: {}", course.getDuration());

            return new ModuleResponseDTO(
                    savedModule.getContentUrl(),
                    savedModule.getModuleId(),
                    savedModule.getTitle(),
                    savedModule.getDuration(),
                    savedModule.getSequenceOrder()
            );

        } catch (Exception e) {
            log.error("Error uploading module: {}", e.getMessage());
            if (finalFilePath != null) {
                Files.deleteIfExists(finalFilePath);
                log.info("Rolled back: deleted file from disk due to error");
            }
            throw e;
        } finally {
            if (tempPath != null) {
                Files.deleteIfExists(tempPath);
                log.info("Temp file deleted: {}", tempPath);
            }
        }
    }

    public String getModuleUrl(UUID id) {
        return gatewayUrl + "/course/stream/" + id;
    }

    @Override
    public Resource loadModuleAsResource(UUID moduleId) throws IOException {
        CourseModule module = courseModuleRepo.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module record not found"));

        Path path = Paths.get(uploadDir)
                .resolve(module.getModuleType().toString().toLowerCase())
                .resolve(module.getContentUrl())
                .normalize();

        log.info("Loading module from path: {}", path.toUri());

        if (!Files.exists(path)) {
            throw new ResourceNotFoundException("File not found on disk");
        }

        return new UrlResource(path.toUri());
    }

    @Override
    public CourseModule updateModule(MultipartFile file, String title, UUID moduleId, UUID courseId, UUID userId)
            throws IOException {

        Path tempFilePath = null;

        try {
            CourseModule module = courseModuleRepo.findById(moduleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Module record not found"));

            Course course = courseRepo.findById(courseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

            if (!userId.equals(course.getTeacherId())) {
                throw new UserIdDonotMatchException("Author of this course does not match the logged in user");
            }

            if (file.getContentType() == null) {
                throw new FileException("Could not determine file type");
            }

            Path existingFilePath = Paths.get(uploadDir)
                    .resolve(module.getModuleType().toString().toLowerCase())
                    .resolve(module.getContentUrl())
                    .normalize();

            Files.deleteIfExists(existingFilePath);

            Path tempSubDir = Paths.get(tempUploadDir)
                    .resolve(module.getModuleType().toString().toLowerCase());

            if (!Files.exists(tempSubDir)) {
                Files.createDirectories(tempSubDir);
            }

            String extension = file.getOriginalFilename()
                    .substring(file.getOriginalFilename().lastIndexOf("."));

            tempFilePath = Files.createTempFile(
                    tempSubDir,
                    "temp-module-" + module.getModuleId(),
                    extension
            );

            Files.copy(file.getInputStream(), tempFilePath, StandardCopyOption.REPLACE_EXISTING);

            if (!tempFilePath.toFile().exists() || tempFilePath.toFile().length() == 0) {
                throw new IOException("File was not written correctly to disk");
            }


            double duration = 0.0;
            MediaType mediaType = MediaType.parseMediaType(file.getContentType());
            if (mediaType.getType().equals("video") || mediaType.getType().equals("audio")) {
                duration = VideoUtil.getVideoDuration(tempFilePath.toFile());
            }

            Files.copy(tempFilePath, existingFilePath, StandardCopyOption.REPLACE_EXISTING);

            course.setDuration(
                    course.getDuration() != null
                            ? course.getDuration() - module.getDuration() + duration
                            : 0
            );

            module.setDuration(duration);
            module.setTitle(title);
            courseModuleRepo.save(module);
            courseRepo.save(course);

            log.info("Module updated successfully: {}", moduleId);
            return module;

        } catch (Exception e) {
            log.error("Error updating module: {}", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            if (tempFilePath != null)
                Files.deleteIfExists(tempFilePath);
        }
    }

    @Override
    public String deleteModule(Course course, CourseModule module) throws IOException {
        Path path = Paths.get(uploadDir)
                .resolve(module.getModuleType().toString().toLowerCase())
                .resolve(module.getContentUrl())
                .normalize();

        log.info("Deleting module file at path: {}", path);

        if (!Files.exists(path)) {
            throw new ResourceNotFoundException("Module file not found on disk");
        }

        module.setCourse(null);
        course.getModules().remove(module);
        course.setDuration(
                course.getDuration() != null ? course.getDuration() - module.getDuration() : 0
        );
        courseRepo.save(course);

        try {
            Files.deleteIfExists(path);
            log.info("Successfully deleted the file from disk: {}", path.toUri());
        } catch (IOException e) {
            log.error("Failed to delete file from disk: {}", e.getMessage());
            throw new IOException(e);
        }

        courseModuleRepo.deleteById(module.getModuleId());
        log.info("Module deleted successfully: {}", module.getModuleId());

        return "Successfully deleted module '" + module.getTitle()
                + "' from course '" + course.getTitle() + "'";
    }

    @Override
    public String deleteModuleById(UUID moduleId, UUID courseId, UUID userId)
            throws IOException, UserIdDonotMatchException {

        CourseModule module = courseModuleRepo.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module record not found"));

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (!course.getTeacherId().equals(userId)) {
            throw new UserIdDonotMatchException("Teacher ID does not match the logged in user");
        }

        return deleteModule(course, module);
    }


}