package com.ctx.user_management_service.services;

import com.ctx.user_management_service.dto.student.StudentResponse;
import com.ctx.user_management_service.dto.student.StudentUpdateRequest;
import com.ctx.user_management_service.exceptions.custom.UserNotFoundException;
import com.ctx.user_management_service.models.Student;
import com.ctx.user_management_service.repo.StudentRepo;
import com.ctx.user_management_service.utils.StudentMapper;
import com.ctx.user_management_service.utils.UpdateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepo studentRepo;
    private final StudentMapper mapper;

    /**
     * Updates an existing student's information based on the provided request data.
     * Only fields present in the request will be updated (partial update).
     *
     * @param id      The {@link UUID} of the student to update.
     * @param request The {@link StudentUpdateRequest} containing the new values.
     * @return The updated {@link StudentResponse}.
     * @throws UserNotFoundException if the student does not exist in the repository.
     */
    public StudentResponse update(UUID id, StudentUpdateRequest request) throws UserNotFoundException {
        Student student = studentRepo.findByStudentId(id)
                .orElseGet(()-> new Student(id));

        UpdateUtil.setIfPresent(request.getFullName(), student::setFullName);
        UpdateUtil.setIfPresent(request.getEmail(), student::setEmail);
        UpdateUtil.setIfPresent(request.getDateOfBirth(), student::setDateOfBirth);
        UpdateUtil.setIfPresent(request.getIsActive(), student::setIsActive);
        UpdateUtil.setIfPresent(request.getEnrollmentNumber(), student::setEnrollmentNumber);
        UpdateUtil.setIfPresent(request.getParentEmail(),student::setParentEmail);
        return mapper.toResponseDTO(studentRepo.save(student));
    }

    public List<StudentResponse> findAllStudents(){
        return mapper.toResponseDtos(studentRepo.findAll());
    }
    
    public  StudentResponse findByStudentId(UUID studentId) throws UserNotFoundException{
        return studentRepo.findByStudentId(studentId)
                .map(mapper::toResponseDTO).orElseThrow(()-> new UserNotFoundException("Student does not Exist"));
    }

    public  Boolean exists(UUID studentId){
        return studentRepo.existsById(studentId);
    }
}
