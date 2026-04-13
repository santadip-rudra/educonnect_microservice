package com.ctx.student_registry_service.controller;


import com.ctx.student_registry_service.dto.attendance.AttendanceResponseDto;
import com.ctx.student_registry_service.dto.report.AttendanceStatsDTO;
import com.ctx.student_registry_service.exceptions.custom.StudentNotFoundException;
import com.ctx.student_registry_service.services.AttendanceService;
import com.ctx.student_registry_service.utils.mapper.AttendanceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v2/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final AttendanceMapper attendanceMapper;


    @GetMapping
    public  String test(){
        return "test";
    }

    @PostMapping
    public ResponseEntity<Boolean> addAttendance(@RequestParam UUID studentId, @RequestParam UUID courseId) throws  StudentNotFoundException {
        return ResponseEntity.ok(attendanceService.addAttendance(studentId,courseId));
    }

    @GetMapping("of")
    public  ResponseEntity<List<AttendanceResponseDto>> findBy(
            @RequestParam (required = false) UUID studentId, @RequestParam(required = false) UUID courseId
    ){
        if(studentId!=null && courseId==null){
            return  ResponseEntity.ok(attendanceMapper.toListResponseDTO(attendanceService.findByStudentId(studentId)));
        }
        if(courseId!=null && studentId==null){
            return  ResponseEntity.ok(attendanceMapper.toListResponseDTO(attendanceService.findByCourseId(courseId)));
        }
        if(courseId!=null && studentId!=null) {
            return ResponseEntity.ok(attendanceMapper.toListResponseDTO(attendanceService.findByStudentIdAndCourseId(studentId, courseId)));
        }
        return  ResponseEntity.badRequest().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<com.ctx.student_registry_service.dto.report.AttendanceStatsDTO> getAttendanceStats() {
        return ResponseEntity.ok(attendanceService.getAttendanceStats());
    }
}
