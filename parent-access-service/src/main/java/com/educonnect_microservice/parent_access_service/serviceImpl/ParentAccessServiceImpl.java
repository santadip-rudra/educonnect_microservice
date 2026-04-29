package com.educonnect_microservice.parent_access_service.serviceImpl;

import com.educonnect_microservice.parent_access_service.client.CourseServiceClient;
import com.educonnect_microservice.parent_access_service.client.StudentRegistryClient;
import com.educonnect_microservice.parent_access_service.client.UserManagementClient;
import com.educonnect_microservice.parent_access_service.dto.*;
import com.educonnect_microservice.parent_access_service.entity.ParentStudentMapping;
import com.educonnect_microservice.parent_access_service.entity.ParentVerificationToken;
import com.educonnect_microservice.parent_access_service.repo.ParentStudentRepo;
import com.educonnect_microservice.parent_access_service.repo.ParentVerificationTokenRepo;
import com.educonnect_microservice.parent_access_service.service.ParentAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParentAccessServiceImpl implements ParentAccessService {
    private final ParentStudentRepo mappingRepo;
    private final ParentVerificationTokenRepo tokenRepo;
    private final UserManagementClient userManagementClient;
    private final StudentRegistryClient studentRegistryClient;
    private final CourseServiceClient courseServiceClient;
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

    @Override
    public boolean isVerified(UUID parentId) {
        return tokenRepo.findByParentIdAndVerifiedTrue(parentId).isPresent();
    }

    @Override
    public List<ChildSummaryDto> getChildren(UUID parentId) {
        List<ParentStudentMapping> mappings = mappingRepo.findByParentId(parentId);
        return mappings.stream()
                .map(mapping -> buildChildSummary(mapping.getStudentId()))
                .toList();
    }

    private ChildSummaryDto buildChildSummary(UUID studentId) {
        // Fetch student profile
        StudentProfileDto profile = null;
        try {
            profile = userManagementClient.getStudentProfile(studentId);
        } catch (Exception ignored) {}

        // Fetch attendance and compute percentage
        Integer attendancePct = null;
        try {
            List<AttendanceRecordDto> records = studentRegistryClient.getAttendanceByStudent(studentId);
            if (records != null && !records.isEmpty()) {
                long present = records.stream()
                        .filter(r -> "PRESENT".equalsIgnoreCase(r.status()))
                        .count();
                attendancePct = (int) Math.round((present * 100.0) / records.size());
            }
        } catch (Exception ignored) {}

        // Fetch enrolled courses
        List<EnrolledCourseDto> enrolledCourses = Collections.emptyList();
        try {
            CourseEnrollmentResponse enrollmentResponse = courseServiceClient.getStudentEnrollments(studentId);
            if (enrollmentResponse != null && enrollmentResponse.getData() != null) {
                enrolledCourses = enrollmentResponse.getData().stream()
                        .map(e -> new EnrolledCourseDto(e.getCourseId(), e.getCourseName(), null))
                        .toList();
            }
        } catch (Exception ignored) {}

        return ChildSummaryDto.builder()
                .studentId(studentId)
                .name(profile != null ? profile.getFullName() : null)
                .gradeLevel(null)
                .dateOfBirth(profile != null ? profile.getDateOfBirth() : null)
                .email(profile != null ? profile.getEmail() : null)
                .gpa(null)
                .attendance(attendancePct)
                .enrolledCourses(enrolledCourses)
                .build();
    }
}
