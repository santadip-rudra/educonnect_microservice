package com.ctx.student_registry_service.utils.mapper;


import com.ctx.student_registry_service.dto.attendance.AttendanceResponseDto;
import com.ctx.student_registry_service.models.Attendance;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AttendanceMapper{


    public AttendanceResponseDto toResponseDTO(Attendance entity) {
        return new AttendanceResponseDto(
                entity.getAttendanceId(),
                entity.getStudentId(),
                entity.getCourseId(),
                entity.getDate(),
                entity.getAttendance_status().name()
        );
    }

    public List<AttendanceResponseDto> toListResponseDTO(List<Attendance> attendanceList){
        return attendanceList.stream().map(this::toResponseDTO).toList();
    }
}
