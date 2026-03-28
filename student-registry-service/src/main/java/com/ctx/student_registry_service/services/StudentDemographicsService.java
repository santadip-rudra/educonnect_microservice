package com.ctx.student_registry_service.services;

import com.ctx.student_registry_service.client.StudentClient;
import com.ctx.student_registry_service.dto.demographics.StudentDemographicsDTO;
import com.ctx.student_registry_service.dto.student.StudentResponse;
import com.ctx.student_registry_service.models.StudentDemographics;
import com.ctx.student_registry_service.repos.StudentDemographicsRepo;
import com.ctx.student_registry_service.utils.mapper.DemographicsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentDemographicsService {
    private final StudentDemographicsRepo studentDemographicsRepo;
    private final StudentClient studentClient;
    private final DemographicsMapper demographicsMapper;

    public StudentResponse findStudentById(UUID studentId){
        System.out.println(studentClient.existsBystudentId(studentId));
        return studentClient.findByStudentId(studentId);
    }

    public StudentDemographics createDemoGraphics(UUID studentId,
                                             StudentDemographicsDTO demographicsDTO) throws Exception {
       Map<String , Object> response = studentClient.existsBystudentId(studentId);
       if(response.containsKey("exists") && response.get("exists").equals(Boolean.TRUE)){
         return  studentDemographicsRepo.save(demographicsMapper.toEntity(demographicsDTO));
       }
       throw new Exception("Student Does not exist");
    }

    public  StudentDemographics findDemographicsById(UUID studentId) throws Exception {
        return  studentDemographicsRepo.findById(studentId)
                .orElseThrow(()-> new Exception("Demographics does not exist"));
    }
}
