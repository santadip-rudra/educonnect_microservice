package com.ctx.student_registry_service.services;

import com.ctx.student_registry_service.client.CourseClient;
import com.ctx.student_registry_service.client.StudentClient;
import com.ctx.student_registry_service.dto.course.CourseResponseDto;
import com.ctx.student_registry_service.dto.student.StudentResponse;
import com.ctx.student_registry_service.exceptions.custom.CourseNotFoundException;
import com.ctx.student_registry_service.exceptions.custom.StudentNotFoundException;
import com.ctx.student_registry_service.models.Attendance;
import com.ctx.student_registry_service.models.enums.AttendanceStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import  com.ctx.student_registry_service.repos.AttendanceRepo;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepo attendanceRepo;
    private final StudentClient studentClient;
    private final CourseClient courseClient;

    /**
     * Records attendance for a student. If attendance for the current date
     * already exists, it returns true without creating a duplicate.
     *
     * @param studentId Unique identifier of the student.
     * @param courseId  Unique identifier of the course.
     * @return true if attendance is recorded or already exists.
     * @throws StudentNotFoundException   if the student does not exist.
     * @throws CourseNotFoundException if the course does not exist.
     */

    @Transactional
    public boolean addAttendance
    (
            UUID studentId,
            UUID courseId )
            throws CourseNotFoundException,StudentNotFoundException
    {
        StudentResponse s = studentClient.findByStudentId(studentId)
                .orElseThrow(()-> new StudentNotFoundException(studentId + " not found"));
        CourseResponseDto c = courseClient.findById(courseId).orElseThrow(()->new CourseNotFoundException("Course "+ courseId+ " not found")
        );
        if(attendanceRepo.existsByStudentIdAndCourseIdAndDate(studentId,courseId, LocalDateTime.now())){
            return  true;
        }
        Attendance a =  Attendance.builder()
                .studentId(s.getStudentId())
                .courseId(c.courseId())
                .date(LocalDateTime.now())
                .attendance_status(AttendanceStatus.PRESENT)
                .build();

        attendanceRepo.save(a);
        return true;
    }

    public List<Attendance> findByStudentId(UUID studentId) {
        return attendanceRepo.findByStudentId(studentId);
    }
    public List<Attendance> findByCourseId(UUID courseId) {
        return attendanceRepo.findByCourseId(courseId);
    }
    public List<Attendance> findByStudentIdAndCourseId(UUID studentId, UUID courseId) {
        return attendanceRepo.findByCourseAndAttendance(studentId,courseId);
    }

    // --- Added for Reporting ---
    public com.ctx.student_registry_service.dto.report.AttendanceStatsDTO getAttendanceStats() {
        return attendanceRepo.getGlobalAttendanceStats();
    }
}
