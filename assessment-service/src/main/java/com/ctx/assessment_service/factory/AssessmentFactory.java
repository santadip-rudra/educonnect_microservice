package com.ctx.assessment_service.factory;

import com.ctx.assessment_service.dto.assessment.create.CreateAssessmentRequestDTO;
import com.ctx.assessment_service.dto.assessment.report.AssessmentReportDTO;
import com.ctx.assessment_service.dto.assessment.serve.AssessmentServeDTO;
import com.ctx.assessment_service.dto.assessment.submit.AssessmentRequestDTO;
import com.ctx.assessment_service.dto.user.CurrentUser;
import com.ctx.assessment_service.exception.custom_exceptions.DocumentProcessingException;
import com.ctx.assessment_service.model.enums.AssessmentType;
import com.ctx.assessment_service.strategy.contract.AssessmentStrategy;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Factory method responsible for routing the assessment creation & submission request
 * * <p> It dynamically selects & return the correct {@link AssessmentStrategy}( either Assignment or Quiz) based on the {@link AssessmentType}</p>
 *
 *
 * @author SudipSarkar
 * @version 1.0
 * @since 1.0
 */


@Component
@RequiredArgsConstructor
public class AssessmentFactory {
    private final List<AssessmentStrategy> assessmentStrategyList;

    /**
     * <p>Routes the student creation request to appropriate strategy </p>
     * @param teacher The Teacher creating the assignment
     * @param assessmentRequestDTO the payload
     * @return A success message
     */
    public Map<String, String> createAssessment(CurrentUser teacher, CreateAssessmentRequestDTO assessmentRequestDTO) throws BadRequestException {

        List<Map<String,String>> list = new ArrayList<>();
        for (AssessmentStrategy assessmentStrategy : assessmentStrategyList) {
            if (assessmentStrategy.supports(assessmentRequestDTO.getAssessmentType())) {
                var assessment = assessmentStrategy.createAssessment(teacher,assessmentRequestDTO);
                list.add(assessment);
            }
        }
        return list.get(0);
    }

    /**
     *
     * @param user The user(student) submitting the assignment
     * @param assessmentRequestDTO the payload
     * @return A success message
     */
    public Map<String,String> submitAssessment(CurrentUser user, AssessmentRequestDTO assessmentRequestDTO) throws BadRequestException, DocumentProcessingException {

        List<Map<String, String>> list = new ArrayList<>();
        for (AssessmentStrategy assessmentStrategy : assessmentStrategyList) {
            if (assessmentStrategy.supports(assessmentRequestDTO.getAssessmentType())) {
                Map<String, String> stringStringMap = assessmentStrategy.submitAssessment(user, assessmentRequestDTO);
                list.add(stringStringMap);
            }
        }
        return list.get(0);
    }

    public AssessmentServeDTO serveAssessment(UUID assessmentId, String assessmentType , CurrentUser user) throws BadRequestException {
        List<AssessmentServeDTO> list = new ArrayList<>();
        for (AssessmentStrategy assessmentStrategy : assessmentStrategyList) {
            if (assessmentStrategy.supports(AssessmentType.valueOf(assessmentType.toUpperCase()))) {
                AssessmentServeDTO assessmentServeDTO = assessmentStrategy.serveAssessment(assessmentId, user);
                list.add(assessmentServeDTO);
            }
        }
        return list.get(0);
    }

    public AssessmentReportDTO getReport(UUID submissionId, CurrentUser user, String assessmentType) throws BadRequestException {
        List<AssessmentReportDTO> list = new ArrayList<>();
        for (AssessmentStrategy assessmentStrategy : assessmentStrategyList) {
            if (assessmentStrategy.supports(AssessmentType.valueOf(assessmentType.toUpperCase()))) {
                AssessmentReportDTO report = assessmentStrategy.getReport(submissionId, user);
                list.add(report);
            }
        }
        return list.get(0);
    }
}

