package com.ctx.user_management_service.strategy.impl;

import com.ctx.user_management_service.dto.base_useer_response.UserResponse;
import com.ctx.user_management_service.dto.register.AuthRegisterRequest;
import com.ctx.user_management_service.dto.register.UpdateTeacherDTO;
import com.ctx.user_management_service.dto.register.base_user.UpdateUserDTO;
import com.ctx.user_management_service.dto.teacher.TeacherResponse;
import com.ctx.user_management_service.exceptions.custom.UserNotFoundException;
import com.ctx.user_management_service.models.Teacher;
import com.ctx.user_management_service.repo.TeacherRepo;
import com.ctx.user_management_service.strategy.RegisterAndUpdateStrategy;
import com.ctx.user_management_service.utils.UpdateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TeacherRegisterAndUpdateStrategy implements RegisterAndUpdateStrategy {
    private final TeacherRepo teacherRepo;

    @Override
    public boolean supports(String role) {
        return role.equals("TEACHER");
    }

    @Override
    public UserResponse updateUserDetails(UpdateUserDTO dto) {
        Teacher teacher = teacherRepo.findById(dto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Teacher profile does not exist"));

        UpdateTeacherDTO teacherDTO = (UpdateTeacherDTO) dto;

        UpdateUtil.setIfPresent(teacherDTO.getDepartment(), teacher::setDepartment);
        UpdateUtil.setIfPresent(teacherDTO.getQualification(), teacher::setQualification);
        UpdateUtil.setIfPresent(teacherDTO.getFullName(),teacher::setFullName);
        UpdateUtil.setIfPresent(teacherDTO.getIsActive(), teacher::setIsActive);

        return mapToResponse(teacherRepo.save(teacher));
    }

    @Override
    public Map<String, String> register(AuthRegisterRequest dto) {
        Teacher teacher = new Teacher();
        teacher.setFullName(dto.getUsername());
        teacher.setTeacherId(dto.getId());
        teacherRepo.save(teacher);
        return Map.of("message","User[Role: " + dto.getRole() + "] registered successfully");
    }

    private UserResponse mapToResponse(Teacher teacher) {
        TeacherResponse response = new TeacherResponse();
        response.setTeacherId(teacher.getTeacherId());
        response.setDepartment(teacher.getDepartment());
        response.setQualification(teacher.getQualification());
        return response;
    }


}
