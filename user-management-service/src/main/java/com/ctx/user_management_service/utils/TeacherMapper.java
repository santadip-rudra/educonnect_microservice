package com.ctx.user_management_service.utils;

import com.ctx.user_management_service.dto.teacher.TeacherResponse;
import com.ctx.user_management_service.models.Teacher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TeacherMapper {

    /**
     * Converts a Teacher entity to a TeacherResponse DTO.
     * * @param teacher The source entity.
     * @return The populated TeacherResponse DTO.
     */
    public TeacherResponse toResponseDTO(Teacher teacher) {
        if (teacher == null) {
            return null;
        }

        return TeacherResponse.builder()
                .teacherId(teacher.getTeacherId())
                .department(teacher.getDepartment())
                .qualification(teacher.getQualification())
                .build();
    }

    /**
     * Converts a list of Teacher entities to a list of TeacherResponse DTOs.
     * Used for bulk retrieval operations.
     * * @param teachers List of entities from the repository.
     * @return List of TeacherResponse DTOs.
     */
    public List<TeacherResponse> toResponseDtos(List<Teacher> teachers) {
        if (teachers == null) {
            return List.of();
        }

        return teachers.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}