package com.ctx.assessment_service.service.contract.assessment;

import com.ctx.assessment_service.dto.assessment.general.AssessmentResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface AssessmentService {
    List<AssessmentResponseDTO> getAllAssessmentUsingCourseId(UUID courseId);
    List<AssessmentResponseDTO> getAllAssessmentUsingListOfCourseIds(List<UUID> courseIds);
}
