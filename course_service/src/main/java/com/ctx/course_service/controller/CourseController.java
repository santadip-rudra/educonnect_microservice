package com.ctx.course_service.controller;

import com.ctx.course_service.clientCall.TeacherClient;
import com.ctx.course_service.dto.CourseRequestDTO;
import com.ctx.course_service.dto.CourseResponseDTO;
import com.ctx.course_service.dto.ModuleRequestDTO;
import com.ctx.course_service.dto.ModuleResponseDTO;
import com.ctx.course_service.dto.common.GenericResponse;
import com.ctx.course_service.dto.teacher.TeacherResponse;
import com.ctx.course_service.dto.user.CurrentUser;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.EncoderException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/course")
public class CourseController {

    private final TeacherClient client;
    private final CourseService courseService;
    private final CourseModuleInterface courseModuleInterface;

    @PostMapping("/add-course")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<CourseResponseDTO> addCourse(
            @RequestBody CourseRequestDTO course,
            @AuthenticationPrincipal CurrentUser user) throws Exception {

        TeacherResponse t = client.getTeacherById(user.getUserId())
                .orElseThrow(() -> new Exception("Teacher not found"));

        return ResponseEntity.ok(courseService.addCourse(course, t.getTeacherId()));
    }

    @PostMapping("/add-module")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ModuleResponseDTO> uploadModule(
            @RequestParam MultipartFile file,
            @RequestParam String title,
            @RequestParam UUID courseId,
            @AuthenticationPrincipal CurrentUser user)
            throws IOException, EncoderException, UserIdDonotMatchException {

        return ResponseEntity.ok(
                courseModuleInterface.uploadModule(file, title, courseId, user.getUserId())
        );
    }

    @PostMapping("/{courseId}/module/{moduleId}/update-module")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<CourseModule> updateModule(
            @RequestParam MultipartFile file,
            @RequestParam String title,
            @PathVariable UUID courseId,
            @PathVariable UUID moduleId,
            @AuthenticationPrincipal CurrentUser user) throws IOException {

        return ResponseEntity.ok(
                courseModuleInterface.updateModule(file, title, moduleId, courseId, user.getUserId())
        );
    }

    @DeleteMapping("/{courseId}/module/{moduleId}/delete-module")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> deleteModule(
            @PathVariable UUID courseId,
            @PathVariable UUID moduleId,
            @AuthenticationPrincipal CurrentUser user)
            throws UserIdDonotMatchException, IOException {

        return ResponseEntity.ok(
                new HashMap<>().put(
                        "message",
                        courseModuleInterface.deleteModuleById(moduleId, courseId, user.getUserId())
                )
        );
    }

    @GetMapping("/get-module/{id}")
    public ResponseEntity<String> getModuleUrl(@PathVariable UUID id) {
        return ResponseEntity.ok(courseModuleInterface.getModuleUrl(id));
    }

    @GetMapping("/stream/{moduleId}")
    public ResponseEntity<Resource> streamContent(@PathVariable UUID moduleId) throws IOException {
        Resource resource = courseModuleInterface.loadModuleAsResource(moduleId);
        String filename = resource.getFilename();
        String contentType = "application/octet-stream";

        if (filename != null) {
            if (filename.endsWith(".pdf")) {
                contentType = "application/pdf";
            } else if (filename.endsWith(".mp3")) {
                contentType = "audio/mpeg";
            } else if (filename.endsWith(".mp4")) {
                contentType = "video/mp4";
            } else if (filename.endsWith(".mov")) {
                contentType = "video/quicktime";
            } else if (filename.endsWith(".mkv")) {
                contentType = "video/x-matroska";
            } else if (filename.endsWith(".wav")) {
                contentType = "audio/wav";
            } else if (filename.endsWith(".aac")) {
                contentType = "audio/aac";
            }
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);
    }

    @GetMapping("/get-course/{courseId}")
    public ResponseEntity<CourseResponseDTO> getCourse(@PathVariable UUID courseId) throws Exception {
        return ResponseEntity.ok(courseService.getByIdCourse(courseId));
    }

    @GetMapping("/modules")
    public ResponseEntity<List<ModuleResponseDTO>> getAllModulesOfCourse(
            @RequestBody ModuleRequestDTO requestDTO) {
        return new ResponseEntity<>(
                courseService.getAllModulesOfACourse(requestDTO.courseId()),
                HttpStatus.OK
        );
    }

    @GetMapping("/get-all-courses/{teacherId}")
    @PreAuthorize("hasRole('TEACHER') OR hasRole('STUDENT') OR hasRole('ADMIN')")
    public ResponseEntity<?> getCoursesByTeacher(@PathVariable("teacherId") UUID teacherId){
        return  ResponseEntity.ok(
                new GenericResponse<>(
                        courseService.getCoursesByTeacherId(teacherId),
                        "Retrieved courses successfully",
                        HttpStatus.OK.value(),
                        LocalDateTime.now()
                )
        );
    }
}