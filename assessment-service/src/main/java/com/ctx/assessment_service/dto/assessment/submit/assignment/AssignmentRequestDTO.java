package com.ctx.assessment_service.dto.assessment.submit.assignment;


import com.ctx.assessment_service.dto.assessment.submit.AssessmentRequestDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class AssignmentRequestDTO extends AssessmentRequestDTO {
    private UUID assignmentId;
    private List<MultipartFile> files;
}
