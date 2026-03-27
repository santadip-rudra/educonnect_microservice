package com.ctx.course_service.controller;

import com.ctx.course_service.clientCall.TeacherClient;
import com.ctx.course_service.dto.CourseRequestDTO;
import com.ctx.course_service.dto.CourseResponseDTO;
import com.ctx.course_service.dto.ModuleRequestDTO;
import com.ctx.course_service.dto.ModuleResponseDTO;
import com.ctx.course_service.dto.teacher.TeacherResponse;
import com.ctx.course_service.exceptions.custom_exceptions.UserIdDonotMatchException;
import com.ctx.course_service.model.CourseModule;
import com.ctx.course_service.service.contract.CourseModuleInterface;
import com.ctx.course_service.service.contract.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.codec.EncoderException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
@RequiredArgsConstructor
@RestController
@RequestMapping("course")
public class CourseController {
    private final TeacherClient client;
    private final CourseService courseService;
    private  final CourseModuleInterface courseVideoInterface;


    @GetMapping
    public String test(){
        return  "working";
    }

    @PostMapping("add-course")
    public ResponseEntity<CourseResponseDTO> addCourse(@RequestBody CourseRequestDTO course , @RequestHeader("X-User-Id") UUID teacherId) throws Exception {
        System.out.println(teacherId);
      TeacherResponse t =client.getTeacherById(teacherId)
                .orElseThrow(()-> new Exception("Teacher not found"));
        return  ResponseEntity.ok(
                courseService.addCourse(course,t.getTeacherId()));

    }

    @PostMapping("/add-module")
    public ResponseEntity<ModuleResponseDTO> addVideo(
            @RequestParam MultipartFile file,
            @RequestParam String title,
            @RequestParam UUID courseId,
            @RequestHeader("X-User-Id") UUID userId) throws IOException, EncoderException, ws.schild.jave.EncoderException, UserIdDonotMatchException {
        return ResponseEntity.ok(courseVideoInterface.uploadModule(file,title,courseId,userId));
    }


        @PostMapping("/{courseId}/video/{videoId}/update-video")
        public ResponseEntity<CourseModule> updateVideo(
                @RequestParam MultipartFile file,
                @PathVariable UUID courseId,
                @RequestParam String title,
                @PathVariable UUID videoId,
              @RequestHeader("X-User-Id") UUID userId)
                throws IOException, EncoderException {

            return ResponseEntity.ok(
                   courseVideoInterface.updateVideoResource(file,title,videoId,courseId,userId)
            );
        }

        /**
         * handles video deletion
         * @param courseId The unique identifier of the course whose {@link CourseModule}(video) is to be deleted
         * @param videoId The unique identifier of the video(module) that is to be deleted
         * @return ResponseEntity of {@link CourseModule}
         * @return Success message
         * @throws IOException
         * @throws ws.schild.jave.EncoderException
         */

        @DeleteMapping("/{courseId}/video/{videoId}/delete-video")
        public ResponseEntity<String> deleteVideo(
                @PathVariable UUID courseId,
                @PathVariable UUID videoId,
                @RequestHeader("X-User-Id") UUID userId
        ) throws UserIdDonotMatchException, IOException {


            return ResponseEntity.ok(
                   courseVideoInterface.deleteVideoResourceWithids(courseId, videoId,userId)
            );
        }


        @GetMapping("/get-module/{id}")
        public ResponseEntity<String> getVideo(@PathVariable UUID id) throws IOException {
            String url=courseVideoInterface.getVideoUrl(id);
            return ResponseEntity.ok(url);
        }


        @GetMapping("/stream/{moduleId}")
        public ResponseEntity<Resource> streamContent(@PathVariable UUID moduleId) throws IOException {
            Resource resource = courseVideoInterface.LoadVideoAsResource(moduleId);
            String filename = resource.getFilename();
            String contentType = "application/octet-stream";

            if (filename != null) {
                if (filename.endsWith(".pdf")) {
                    contentType = "application/pdf";
                } else if (filename.endsWith(".mp3")) {
                    contentType = "audio/mpeg";
                } else if (filename.endsWith(".mp4")) {
                    contentType = "video/mp4";
                }
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
        }


        @GetMapping("/get-course/{courseId}")
        public CourseResponseDTO getcourse( @PathVariable UUID courseId ) throws Exception {
            return courseService.getByIdCourse(courseId);
        }



        @GetMapping("modules")
        public ResponseEntity<List<ModuleResponseDTO>> getAllModulesOfaCourse(@RequestBody ModuleRequestDTO requestDTO)
        {
            return new ResponseEntity<>(courseService.getAllModulesOfACourse(requestDTO.courseId()), HttpStatus.OK);
        }

    }
