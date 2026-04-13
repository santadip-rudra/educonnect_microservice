package com.ctx.student_registry_service.repos;

import com.ctx.student_registry_service.dto.report.AttendanceStatsDTO;
import com.ctx.student_registry_service.models.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AttendanceRepo extends JpaRepository<Attendance, UUID> {
    /**
     * Checks if an attendance record already exists for a specific student, course, and date.
     * Useful for preventing duplicate attendance entries for the same day.
     *
     * @param s    The  entity unique id.
     * @param c    The  entity unique id.
     * @param date The {@link LocalDate} of the class session.
     * @return true if a record exists, false otherwise.
     */
    Boolean existsByStudentIdAndCourseIdAndDate(UUID s, UUID c, LocalDateTime date);

    /**
     * Retrieves all attendance records associated with a specific student.
     *
     * @param studentId The unique {@link UUID} of the student user.
     * @return A list of {@link Attendance} records for the given student.
     */
    List<Attendance> findByStudentId(UUID studentId);

    /**
     * Retrieves all attendance records for a specific course.
     *
     * @param courseId The unique {@link UUID} of the course.
     * @return A list of all {@link Attendance} entries for the given course.
     */
    List<Attendance> findByCourseId(UUID courseId);

    /**
     * Retrieves a filtered list of attendance records for a specific student within a specific course.
     *
     * @param studentId The {@link UUID} of the student.
     * @param courseId  The {@link UUID} of the course.
     * @return A list of {@link Attendance} records matching both criteria.
     */
    @Query("Select a from Attendance a where a.studentId = :studentId AND a.courseId = :courseId")
    List<Attendance> findByCourseAndAttendance(@Param("studentId") UUID studentId, @Param("courseId") UUID courseId);


    // --- Added for Reporting ---
    @Query("SELECT new com.ctx.student_registry_service.dto.report.AttendanceStatsDTO(" +
            "COUNT(CASE WHEN a.attendance_status = 'PRESENT' THEN 1 END), " +
            "COUNT(CASE WHEN a.attendance_status = 'ABSENT' THEN 1 END), " +
            "COALESCE(AVG(CASE WHEN a.attendance_status = 'PRESENT' THEN 100.0 ELSE 0.0 END), 0.0)) " + // Added COALESCE
            "FROM Attendance a")
    AttendanceStatsDTO getGlobalAttendanceStats();
}
