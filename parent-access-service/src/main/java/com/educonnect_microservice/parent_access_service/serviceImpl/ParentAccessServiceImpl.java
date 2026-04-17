package com.educonnect_microservice.parent_access_service.serviceImpl;

import com.educonnect_microservice.parent_access_service.entity.ParentStudentMapping;
import com.educonnect_microservice.parent_access_service.entity.ParentVerificationToken;
import com.educonnect_microservice.parent_access_service.repo.ParentStudentRepo;
import com.educonnect_microservice.parent_access_service.repo.ParentVerificationTokenRepo;
import com.educonnect_microservice.parent_access_service.service.ParentAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParentAccessServiceImpl implements ParentAccessService {
    private final ParentStudentRepo mappingRepo;
    private final ParentVerificationTokenRepo tokenRepo;
    @Override
    public void linkParent(UUID parentId, UUID studentId){
        tokenRepo.findByParentIdAndVerifiedTrue(parentId)
                .orElseThrow(() -> new RuntimeException("Parent is not verified. Please complete email verification before linking."));
        mappingRepo.findByParentIdAndStudentId(parentId,studentId)
                .ifPresent(m->{throw new RuntimeException("Already linked");
                });
        ParentStudentMapping mapping=new ParentStudentMapping();
        mapping.setParentId(parentId);
        mapping.setStudentId(studentId);
        mappingRepo.save(mapping);
    }
    @Override
    public String sendVerification(UUID parentId){
        tokenRepo.findByParentIdAndVerifiedTrue(parentId)
                .ifPresent(existing -> {
                    throw new RuntimeException("Parent is already verified.");
                });
        tokenRepo.findByParentId(parentId)
                .ifPresent(tokenRepo::delete);
        String token=UUID.randomUUID().toString();
        ParentVerificationToken verificationToken=ParentVerificationToken.builder().
                parentId(parentId).
                token(token).
                expiryDate(LocalDateTime.now().plusHours(24)).
                build();
        tokenRepo.save(verificationToken);
        return "http://localhost:8081/parent-access/verify?token="+token;
    }
    @Override
    public void verifyParent(String token){
        if (token == null || token.isBlank()) {
            throw new RuntimeException("Invalid or already used token");
        }
        ParentVerificationToken verificationToken=tokenRepo.findByToken(token).
                orElseThrow(()->new RuntimeException("Invalid or already used token"));
        if(verificationToken.isVerified()){
            throw new RuntimeException("Parent is already verified.");
        }
        if(verificationToken.getExpiryDate().isBefore(LocalDateTime.now())){
            tokenRepo.delete(verificationToken);
            throw new RuntimeException("Verification token has expired. Please request a new one.");
        }
        verificationToken.setVerified(true);
        verificationToken.setToken(null);
        verificationToken.setExpiryDate(null);
        tokenRepo.save(verificationToken);
    }
    @Override
    public boolean getAccess(UUID parentId,UUID studentId){
        return mappingRepo.findByParentIdAndStudentId(parentId,studentId).isPresent();
    }

    @Override
    public void grantAccess(UUID parentId,UUID studentId){
        tokenRepo.findByParentIdAndVerifiedTrue(parentId)
                .orElseThrow(() -> new RuntimeException("Parent is not verified. Please complete email verification before granting access."));
        if(mappingRepo.findByParentIdAndStudentId(parentId, studentId).isPresent()){
            return;
        }
        ParentStudentMapping parentStudentMapping=new ParentStudentMapping();
        parentStudentMapping.setParentId(parentId);
        parentStudentMapping.setStudentId(studentId);
        mappingRepo.save(parentStudentMapping);
    }
}
