package com.ctx.assessment_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-management-service")
public interface UserManagementServiceClient {

    boolean isStudentEnrolled(@PathVariable UUID studentId,@PathVariable UUID courseId);

    boolean hasStudentSubmitted(UUID assessmentId,UUID userId);
}
