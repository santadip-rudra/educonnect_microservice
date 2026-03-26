package com.ctx.user_management_service.utils;

import com.ctx.user_management_service.dto.student.StudentResponse;
import  com.ctx.user_management_service.models.Student;
import org.springframework.stereotype.Component;

import java.util.Objects;
import  java.util.List;

@Component
public class StudentMapper {
    public StudentResponse toResponseDTO(Student s) {
        Objects.requireNonNull(s);
        StudentResponse studentResponse = new StudentResponse();
        UpdateUtil.setIfPresent(s.getStudentId(), studentResponse::setStudentId);
        UpdateUtil.setIfPresent(s.getFullName(),studentResponse::setFullName);
        UpdateUtil.setIfPresent(s.getEmail(),studentResponse::setEmail);
        UpdateUtil.setIfPresent(s.getIsActive(),studentResponse::setIsActive);
        UpdateUtil.setIfPresent(s.getDateOfBirth(),studentResponse::setDateOfBirth);
        UpdateUtil.setIfPresent(s.getEnrollmentNumber(),studentResponse::setEnrollmentNumber);
        return studentResponse;
    }

    public  List<StudentResponse> toResponseDtos(List<Student> students){
        return  students.stream()
                .map(this::toResponseDTO)
                .toList();
    }
}
