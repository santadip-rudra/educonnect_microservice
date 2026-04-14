package com.ctx.user_management_service.strategy.impl;

import com.ctx.user_management_service.dto.base_useer_response.UserResponse;
import com.ctx.user_management_service.dto.register.AuthRegisterRequest;
import com.ctx.user_management_service.dto.register.UpdateStudentDTO;
import com.ctx.user_management_service.dto.register.base_user.UpdateUserDTO;
import com.ctx.user_management_service.dto.student.StudentResponse;
import com.ctx.user_management_service.exceptions.custom.UserNotFoundException;
import com.ctx.user_management_service.models.Student;
import com.ctx.user_management_service.repo.StudentRepo;
import com.ctx.user_management_service.strategy.UserStrategy;
import com.ctx.user_management_service.utils.UpdateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentUserStrategy implements UserStrategy {
    private final StudentRepo studentRepo;

    @Override
    public boolean supports(String role) {
        return role.equals("STUDENT");
    }

    @Override
    public UserResponse updateUserDetails(UpdateUserDTO dto) {
        Student student = studentRepo.findById(dto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Student profile does not exist"));

        UpdateStudentDTO studentDTO = (UpdateStudentDTO) dto;

        UpdateUtil.setIfPresent(studentDTO.getFullName(), student::setFullName);
        UpdateUtil.setIfPresent(studentDTO.getEmail(), student::setEmail);
        UpdateUtil.setIfPresent(studentDTO.getDateOfBirth(), student::setDateOfBirth);
        UpdateUtil.setIfPresent(studentDTO.getEnrollmentNumber(), student::setEnrollmentNumber);
        UpdateUtil.setIfPresent(studentDTO.getParentEmail(), student::setParentEmail);
        UpdateUtil.setIfPresent(studentDTO.getIsActive(), student::setIsActive);
        UpdateUtil.setIfPresent(studentDTO.getIsVerified(),student::setIsVerified);

        return mapToResponse(studentRepo.save(student));
    }
    private StudentResponse mapToResponse(Student student) {
        StudentResponse response = new StudentResponse();
        response.setStudentId(student.getStudentId());
        response.setFullName(student.getFullName());
        response.setEmail(student.getEmail());
        response.setDateOfBirth(student.getDateOfBirth());
        response.setEnrollmentNumber(student.getEnrollmentNumber());
        response.setParentEmail(student.getParentEmail());
        response.setIsActive(student.getIsActive());
        return response;
    }

    @Override
    public Map<String, String> register(AuthRegisterRequest dto) {
        Student student = new Student();
        student.setFullName(dto.getUsername());
        student.setStudentId(dto.getId());
        studentRepo.save(student);
        return Map.of("message","User[Role: " + dto.getRole() + "] registered successfully");
    }

    @Override
    public UserResponse getUserDetails(UUID userId) {
        Student student = studentRepo.findByStudentId(userId)
                .orElseThrow(()->new UserNotFoundException("Student not found"));
        return StudentResponse.builder()
                .studentId(userId)
                .role("STUDENT")
                .email(student.getEmail())
                .fullName(student.getFullName())
                .dateOfBirth(student.getDateOfBirth())
                .enrollmentNumber(student.getEnrollmentNumber())
                .isActive(student.getIsActive())
                .parentEmail(student.getParentEmail())
                .build();
    }
}
