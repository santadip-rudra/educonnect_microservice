package com.ctx.course_service.service.implementation;
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
import org.springframework.beans.factory.annotation.Value; // Fixed Import
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
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseModuleImpl implements CourseModuleInterface{


    @Value("${file.upload-dir-one}")
    private String uploadDir;

    @Value("${file.tempUpload-dir-one}")
    private String tempUploadDir;

    private final CourseModuleRepo courseModuleRepo;
    private final CourseRepo courseRepo;
    @Override
    public ModuleResponseDTO uploadModule(MultipartFile file, String title, UUID courseId, UUID teacherId) throws IOException, EncoderException, UserIdDonotMatchException {
        File tempFile = null;
        Path tempPath=null;
        Path finalFilePath=null;

        try{
            if(file==null || file.isEmpty())
            {
                throw new FileNotFoundException("file not found");
            }
            Course course = courseRepo.findById(courseId)
                    .orElseThrow(() -> new IOException("Course does not exist"));
            if (!course.getTeacherId().equals(teacherId)) {
                throw new UserIdDonotMatchException("Unauthorized: Only the course creator can upload modules.");
            }
            String extension = file.getOriginalFilename()
                    .substring(file.getOriginalFilename()
                            .lastIndexOf("."));
            MediaType mediaType=MediaType.parseMediaType(file.getContentType());
            String folder;
            ModuleType m;
            if(mediaType.getType().equals("audio"))
            {
                folder="audio/";
                m=ModuleType.AUDIO;

            }
            else if(mediaType.getType().equals("video"))
            {
                folder="video/";
                m=ModuleType.VIDEO;
            } else if (mediaType.equalsTypeAndSubtype(MediaType.APPLICATION_PDF)) {
                folder="pdf/";
                m=ModuleType.PDF;
            }
            else {
                throw new FileException("Unsupported file type : ");
            }

            Path targetFolderPath = Paths.get(this.uploadDir).resolve(folder);
            Path targetTempFolderPath=Paths.get(this.tempUploadDir).resolve(folder);
            if (!Files.exists(targetFolderPath))
            {
                Files.createDirectories(targetFolderPath);
                System.out.println("created target folder ");
            }
            if(!Files.exists(targetTempFolderPath))
            {
                Files.createDirectories(targetTempFolderPath);
                System.out.println("created temp folder: ");
            }
            UUID uuid = UUID.randomUUID();

            String filename = uuid.toString()  + extension;
            finalFilePath=targetFolderPath.resolve(filename);
            Files.copy(file.getInputStream(),finalFilePath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("files have been copied to original file :");
            double duration = 0.0;
            tempPath = targetTempFolderPath.resolve("temp-" + uuid + extension);
            Files.copy(finalFilePath, tempPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("files have been copied to temp file :");
            if (m != ModuleType.PDF) {
                duration = VideoUtil.getVideoDuration(tempPath.toFile());
                System.out.println("duration calculated");
            }

            var resp = courseModuleRepo.save(CourseModule.builder()
                    .moduleId(uuid)
                    .contentUrl(filename)
                    .course(course)
                    .title(title)
                    .moduleType(m)
                    .duration(duration)
                    .build());


            log.info("course duration 01={}",course.getDuration());

            course.setDuration(
                    (course.getDuration() == null ? 0 : course.getDuration() )
                            + duration);

            courseRepo.save(course);
            log.info("course duration 02={}",course.getDuration());
    ModuleResponseDTO moduleResponseDTO=new ModuleResponseDTO(resp.getContentUrl(),resp.getModuleId(),resp.getTitle(),resp.getDuration(),resp.getSequenceOrder());
            return moduleResponseDTO;

        }catch (Exception e){
            log.error(e.getMessage());
            if(finalFilePath!=null)
            {
                Files.deleteIfExists(finalFilePath);
                log.info("was issue in saving the file in db so deleted from file system too : ");
            }
            throw e;
        }finally {
            System.out.println("finally block triggered");
            if (tempPath != null) {
                Files.deleteIfExists(tempPath);
                log.info("Temp file deleted successfully: {}", tempPath);
            }
        }
    }

    @Override
    public String getVideoUrl(UUID id) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/api/course/stream/")
                .path(id.toString())
                .toUriString();
    }

    @Override
    public Resource LoadVideoAsResource(UUID moduleId) throws IOException {
        CourseModule video = courseModuleRepo.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Video record not found"));

        Path path = Paths.get(uploadDir)
                .resolve(video.getModuleType().toString().toLowerCase())
                .resolve(video.getContentUrl()).normalize();
        System.out.println(path.toUri());
        if (!Files.exists(path)) throw new RuntimeException("File not found on disk");

        return new UrlResource(path.toUri());
    }

    @Override
    public CourseModule updateVideoResource(MultipartFile file, String title, UUID videoId, UUID courseId, UUID userId) throws IOException{

        Path tempFilePath = null;
        try{

            CourseModule video = courseModuleRepo.findById(videoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Video record not found"));

            Course course = courseRepo.findById(courseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
            if(userId!=course.getTeacherId())
            {
                throw new UserIdDonotMatchException("author of this course  do not match with the logged in User: ");
            }
            CourseModule module=courseModuleRepo.findById(videoId).orElseThrow(()->new ModuleNotFoundException("Module do not exists: "));

            Path path = Paths.get(uploadDir).resolve(module.getModuleType().toString().toLowerCase()).resolve(video.getContentUrl()).normalize();

            Files.deleteIfExists(path);

            File directory = new File(uploadDir);
            File tempDirectory = new File(tempUploadDir);

            if(!directory.exists()) {
                directory.mkdirs();
            }
            if(!tempDirectory.exists()) {
                tempDirectory.mkdirs();
            }

            String extension = file.getOriginalFilename()
                    .substring(file.getOriginalFilename()
                            .lastIndexOf("."));

//            String filename = video.getModuleId() + extension;
//
//            Path filePath = Paths.get(uploadDirOne)
//                    .resolve(module.getModuleType()
//                            .toString()
//                            .toLowerCase())
//                    .resolve(filename);

            tempFilePath =
                    Files.createTempFile(
                            tempDirectory.toPath().resolve(module.getModuleType().toString().toLowerCase()),

                            "temp-video-" + video.getModuleId() ,
                            extension);

            Files.copy(file.getInputStream(), tempFilePath , StandardCopyOption.REPLACE_EXISTING);
            File tempFile = tempFilePath.toFile();
            if (!tempFile.exists() || tempFile.length() == 0) {
                throw new IOException("File was not written correctly to disk!");
            }

            var duration = VideoUtil.getVideoDuration(tempFile);

            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);



            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println("duration "+ duration);
            System.out.println("video.getDuration() "+ video.getDuration());
            System.out.println("course.getDuration() "+ course.getDuration());
            System.out.println();
            System.out.println();;

            course.setDuration(
                    course.getDuration() != null ?
                            course.getDuration() - video.getDuration() + duration : 0
            );

            course.getModules().remove(video);

            video.setDuration(duration);
            video.setTitle(title);
            courseModuleRepo.save(video);

            courseRepo.save(course);

            log.info("video deleted successfully course table");
            System.out.println("video deleted successfully from course table");
            return video;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            if(tempFilePath != null)
                Files.deleteIfExists(tempFilePath);
        }
    }


    @Override
    public String deleteVideoResource(Course course, CourseModule module) throws IOException {
        Path path = Paths.get(uploadDir).resolve(module.getModuleType().toString().toLowerCase()).resolve(module.getContentUrl()).normalize();
        System.out.println("this is the path to be deleted " +path);
        if(!Files.exists(path)){
            throw new ResourceNotFoundException("Video not found [on disk]");
        }

        module.setCourse(null);
        course.getModules().remove(module);
        course.setDuration(
                course.getDuration() != null? course.getDuration() - module.getDuration() : 0
        );
        courseRepo.save(course);
        log.info("video deleted successfully course table");
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error(e.getMessage());
            System.out.println(e.getMessage());
            throw new IOException(e);
        }
        courseModuleRepo.deleteById(module.getModuleId());
        return "Successfully deleted the video with title " + module.getTitle() + " of course with title " + course.getTitle();
    }

    @Override
    public String deleteVideoResourceWithids(UUID videoId, UUID courseId , UUID userId) throws IOException, UserIdDonotMatchException {
        CourseModule video = courseModuleRepo.findById(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Video record not found"));

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        if(!course.getTeacherId().equals(userId))
        {
            System.out.println("teacher id "+course.getTeacherId());
            System.out.println("login user id "+userId);
            throw new UserIdDonotMatchException("TeacherId who uploaded the course do not match with loginId");
        }
        courseModuleRepo.deleteById(videoId);
        return deleteVideoResource(course,video);

    }
}


