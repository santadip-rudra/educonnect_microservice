package com.ctx.assessment_service.service.implementation.assessment;

import com.ctx.assessment_service.dto.assessment.general.AssessmentResponseDTO;
import com.ctx.assessment_service.exception.custom_exceptions.ResourceNotFoundException;
import com.ctx.assessment_service.model.Assessment;
import com.ctx.assessment_service.model.Submission;
import com.ctx.assessment_service.repo.assessment.AssessmentRepo;
import com.ctx.assessment_service.service.contract.assessment.AssessmentService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssessmentServiceImpl implements AssessmentService {

    private final AssessmentRepo assessmentRepo;

    @Override
    public List<AssessmentResponseDTO> getAllAssessmentUsingCourseId(UUID courseId) {

        List<Assessment> assessmentList = assessmentRepo.findByCourseId(courseId);

        if(assessmentList == null || assessmentList.isEmpty()){
            throw new ResourceNotFoundException("Assessment not found");
        }

        List<AssessmentResponseDTO> assessmentResponseDTOList = new ArrayList<>();

        for(Assessment assessment : assessmentList){

            AssessmentResponseDTO assessmentResponseDTO = AssessmentResponseDTO.builder()
                    .assessmentId(assessment.getAssessmentId())
                    .type(assessment.getType().toString())
                    .quizId(assessment.getQuiz() != null ? assessment.getQuiz().getQuizId() : null)
                    .assignmentId(assessment.getAssessmentId() != null? assessment.getAssignment().getAssignmentId() : null)
                    .title(assessment.getTitle())
                    .maxScore(assessment.getMaxScore())
                    .courseId(assessment.getCourseId())
                    .build();

            assessmentResponseDTOList.add(assessmentResponseDTO);
        }

        return assessmentResponseDTOList;
    }

    @Override
    public List<AssessmentResponseDTO> getAllAssessmentUsingListOfCourseIdsWithStudentId(List<UUID> courseIds, UUID studentId) throws BadRequestException {


        List<Assessment> assessmentList = assessmentRepo.findByCoursesId(courseIds);


//        if(assessmentList == null || assessmentList.isEmpty()){
//            throw new ResourceNotFoundException("Assessment not found");
//        }

        List<AssessmentResponseDTO> assessmentResponseDTOList = new ArrayList<>();

        for(Assessment assessment : assessmentList){

            List<Submission> studentSubmissionList =assessment.getSubmissionList().stream()
                    .filter(submission -> submission.getStudentId().equals(studentId))
                    .toList();

            if(studentSubmissionList == null || studentSubmissionList.isEmpty()){
                throw new BadRequestException("Student with id:" + studentId + " has no submission");
            }

            AssessmentResponseDTO assessmentResponseDTO = AssessmentResponseDTO.builder()
                    .assessmentId(assessment.getAssessmentId())
                    .type(assessment.getType().toString())
                    .quizId(assessment.getQuiz() != null ? assessment.getQuiz().getQuizId() : null)
                    .assignmentId(assessment.getAssignment() != null ? assessment.getAssignment().getAssignmentId() : null)
                    .title(assessment.getTitle())
                    .maxScore(assessment.getMaxScore())
                    .courseId(assessment.getCourseId())
                    .submissionStatus(studentSubmissionList.get(0).getSubmissionStatus())
                    .build();

            assessmentResponseDTOList.add(assessmentResponseDTO);
        }

        return assessmentResponseDTOList;
    }


    @Override
    public List<AssessmentResponseDTO> getAllAssessmentUsingListOfCourseIds(List<UUID> courseIds) throws BadRequestException {


        List<Assessment> assessmentList = assessmentRepo.findByCoursesId(courseIds);


//        if(assessmentList == null || assessmentList.isEmpty()){
//            throw new ResourceNotFoundException("Assessment not found");
//        }

        List<AssessmentResponseDTO> assessmentResponseDTOList = new ArrayList<>();

        for(Assessment assessment : assessmentList){

            AssessmentResponseDTO assessmentResponseDTO = AssessmentResponseDTO.builder()
                    .assessmentId(assessment.getAssessmentId())
                    .type(assessment.getType().toString())
                    .quizId(assessment.getQuiz() != null ? assessment.getQuiz().getQuizId() : null)
                    .assignmentId(assessment.getAssignment() != null ? assessment.getAssignment().getAssignmentId() : null)
                    .title(assessment.getTitle())
                    .maxScore(assessment.getMaxScore())
                    .courseId(assessment.getCourseId())
                    .build();

            assessmentResponseDTOList.add(assessmentResponseDTO);
        }

        return assessmentResponseDTOList;
    }

}
