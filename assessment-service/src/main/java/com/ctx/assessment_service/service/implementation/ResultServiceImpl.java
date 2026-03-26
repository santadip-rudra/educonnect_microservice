package com.ctx.assessment_service.service.implementation;

import com.ctx.assessment_service.repo.assessment.AssessmentRepo;
import com.ctx.assessment_service.repo.assessment.SubmissionRepo;
import com.ctx.assessment_service.service.contract.ResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResultServiceImpl implements ResultService {

    private final SubmissionRepo submissionRepo;
    private final AssessmentRepo assessmentRepo;
    private final StudentRepo studentRepo;
    private final ResultRepo resultRepo;
    private final StudentQuizQuestionResponseRepo studentQuizQuestionResponseRepo;

    @Override
    public String computeQuizResult(UUID assessmentId, UUID studentId) {
        Assessment assessment = assessmentRepo.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));


        Student student = null ;
        try {
            student = studentRepo.findById(studentId)
                    .orElseThrow(() -> new UserNotFoundException("Student not found"));
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }

        Submission submission = submissionRepo.findByStudentAndAssessment(student,assessment)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));


        List<StudentQuizQuestionResponse> studentQuizQuestionResponseList
                = studentQuizQuestionResponseRepo.findAllBySubmission(submission);

        int correctResponse = 0 ;
        int totalResponse = 0 ;

        for(StudentQuizQuestionResponse studentQuizQuestionResponse : studentQuizQuestionResponseList){
            totalResponse++;

            if(studentQuizQuestionResponse.getIsCorrectOptionChosen()){
                correctResponse++;
            }
        }

        Result result = new Result();

        result.setAssessment(assessment);
        result.setStudent(student);
        result.setSubmission(submission);


        double score = (totalResponse == 0) ? 0.0
                : (double) correctResponse / totalResponse;


        result.setPercentageScore(score);

        result.setStatus(score >= 0.4 ? ResultStatus.PASSED : ResultStatus.FAILED);

        resultRepo.save(result);

        return "result computed successfully";


    }

    @Override
    public String evaluateStudent(UUID assessmentId, UUID studentId, Teacher teacher, double givenScore) throws BadRequestException, UserNotFoundException {

        Assessment assessment = assessmentRepo.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));

        if(!assessment.getCourse().getTeacher().getUserId().equals(teacher.getUserId())){
            throw new BadRequestException("Teacher `" +teacher.getFullName() + "` does not have permission to evaluate");
        }

        if(resultRepo.existsByAssessmentAssessmentId(assessmentId)){
            log.info("Overwriting the assignment score...");

            Result result = resultRepo.findByAssessmentAssessmentId(assessmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Result not found"));

            double score = (assessment.getMaxScore() == 0) ? 0.0
                    : (double) givenScore / assessment.getMaxScore();


            result.setPercentageScore(score);

            result.setStatus(score >= 0.4 ? ResultStatus.PASSED : ResultStatus.FAILED);

            resultRepo.save(result);

            return "Updated the score of the assignment";
        }


        Student student= studentRepo.findById(studentId)
                .orElseThrow(() -> new UserNotFoundException("Student not found"));


        Submission submission = submissionRepo.findByStudentAndAssessment(student,assessment)
                .orElseThrow(() -> new ResourceNotFoundException("Student " + student.getFullName()
                        + " has not submitted the assignment yet"));

        if(!submission.getSubmissionStatus().toString().equals("SUBMITTED")){

            throw new BadRequestException("Student " + student.getFullName()
                    + " has not submitted the assignment yet");

        }




        Result result = new Result();

        result.setAssessment(assessment);
        result.setStudent(student);
        result.setSubmission(submission);


        double score = (assessment.getMaxScore() == 0) ? 0.0
                : (double) givenScore / assessment.getMaxScore();


        result.setPercentageScore(score);

        result.setStatus(score >= 0.4 ? ResultStatus.PASSED : ResultStatus.FAILED);

        resultRepo.save(result);

        return "Result of `" + student.getFullName() + "` evaluated by `" + teacher.getFullName() + "` saved successfully";

    }

    @Override
    public Result getResultWithId(UUID submissionId, User user) throws BadRequestException {

        Submission submission = submissionRepo.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        if(
                user.getRole().equals(Role.STUDENT) &&
                        submission.getStudent().getUserId()
                                .equals(user.getUserId())
        ){
            throw new BadRequestException("Student +[" + user.getFullName() +"] is not aut" );
        }

        return resultRepo.findBySubmissionSubmissionId(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Result not found"));
    }


}

