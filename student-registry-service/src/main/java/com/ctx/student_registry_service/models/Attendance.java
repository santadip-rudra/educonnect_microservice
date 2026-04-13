package com.ctx.student_registry_service.models;

import com.ctx.student_registry_service.models.enums.AttendanceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID attendanceId;
    private UUID studentId;
    private UUID courseId;

    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    private AttendanceStatus attendance_status;
}
