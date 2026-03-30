package com.ctx.user_management_service.services;

import com.ctx.user_management_service.dto.teacher.TeacherResponse;
import com.ctx.user_management_service.dto.teacher.TeacherUpdateRequest;
import com.ctx.user_management_service.exceptions.custom.UserNotFoundException;
import com.ctx.user_management_service.models.Teacher;
import com.ctx.user_management_service.repo.TeacherRepo;
import com.ctx.user_management_service.utils.TeacherMapper;
import com.ctx.user_management_service.utils.UpdateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepo teacherRepo;
    private final TeacherMapper mapper;

    /**
     * Updates an existing teacher's information based on the provided request data.
     * Only fields present in the request will be updated (partial update).
     *
     * @param id      The {@link UUID} of the teacher to update.
     * @param request The {@link TeacherUpdateRequest} containing the new values.
     * @return The updated {@link TeacherResponse}.
     * @throws UserNotFoundException if the teacher does not exist in the repository.
     */
    public TeacherResponse update(UUID id, TeacherUpdateRequest request) throws UserNotFoundException {
        // Logic follows the student service: find or initialize a new instance for partial updates
        Teacher teacher = teacherRepo.findByTeacherId(id)
                .orElseGet(() -> new Teacher(id, null, null,null,null));

        UpdateUtil.setIfPresent(request.getDepartment(), teacher::setDepartment);
        UpdateUtil.setIfPresent(request.getQualification(), teacher::setQualification);

        // Add additional common profile fields if they exist in your Teacher entity
        // e.g., UpdateUtil.setIfPresent(request.getFullName(), teacher::setFullName);

        return mapper.toResponseDTO(teacherRepo.save(teacher));
    }

    /**
     * Retrieves all teachers and maps them to response DTOs.
     *
     * @return A list of {@link TeacherResponse}.
     */
    public List<TeacherResponse> findAllTeachers() {
        return mapper.toResponseDtos(teacherRepo.findAll());
    }

    /**
     * Finds a specific teacher by their unique ID.
     *
     * @param teacherId The UUID of the teacher.
     * @return The {@link TeacherResponse}.
     * @throws java.util.NoSuchElementException if the teacher is not found.
     */
    public TeacherResponse findByTeacherId(UUID teacherId) {
        return teacherRepo.findByTeacherId(teacherId)
                .map(mapper::toResponseDTO)
                .orElseThrow();
    }
}