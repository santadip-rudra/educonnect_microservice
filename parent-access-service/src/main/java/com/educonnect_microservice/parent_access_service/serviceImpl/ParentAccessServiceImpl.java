package com.educonnect_microservice.parent_access_service.serviceImpl;

import com.educonnect_microservice.parent_access_service.entity.ParentStudentMapping;
import com.educonnect_microservice.parent_access_service.entity.ParentVerificationToken;
import com.educonnect_microservice.parent_access_service.repo.ParentStudentRepo;
import com.educonnect_microservice.parent_access_service.repo.ParentVerificationTokenRepo;
import com.educonnect_microservice.parent_access_service.service.ParentAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParentAccessServiceImpl implements ParentAccessService {
    private final ParentStudentRepo mappingRepo;
    private final ParentVerificationTokenRepo tokenRepo;
    @Override
    public void linkParent(UUID parentId, UUID studentId){
        mappingRepo.findByParentIdAndStudentId(parentId,studentId)
                .ifPresent(m->{throw new RuntimeException("Already linked");
                });
        //TO CHECK
        ParentStudentMapping mapping=new ParentStudentMapping();
        mapping.setParentId(parentId);
        mapping.setStudentId(studentId);
        mappingRepo.save(mapping);
    }
    @Override
    public String sendVerification(UUID parentId){
        tokenRepo.findByParentId(parentId)
                .ifPresent(tokenRepo::delete);
        String token=UUID.randomUUID().toString();
        ParentVerificationToken verificationToken=ParentVerificationToken.builder().
                parentId(parentId).
                token(token).
                expiryDate(LocalDateTime.now().plusHours(24)).
                build();
        tokenRepo.save(verificationToken);
        return "http://localhost:8081/parent/verify?token="+token;
    }
    @Override
    public void verifyParent(String token){
        ParentVerificationToken verificationToken=tokenRepo.findByToken(token).
                orElseThrow(()->new RuntimeException("Invalid token"));
        tokenRepo.delete(verificationToken);
    }
    @Override
    public boolean getAccess(UUID parentId,UUID studentId){
        return mappingRepo.findByParentIdAndStudentId(parentId,studentId).isPresent();
    }

    @Override
    public void grantAccess(UUID parentId,UUID studentId){
        mappingRepo.findByParentIdAndStudentId(parentId, studentId).
                ifPresent(m->{
                    throw new RuntimeException("Access already granted");
                });
        ParentStudentMapping parentStudentMapping=new ParentStudentMapping();
        parentStudentMapping.setParentId(parentId);
        parentStudentMapping.setStudentId(studentId);
        mappingRepo.save(parentStudentMapping);
    }
}
