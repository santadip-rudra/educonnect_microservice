package com.ctx.assessment_service.strategy.implementation;

import com.ctx.assessment_service.client.CourseServiceClient;
import com.ctx.assessment_service.client.UserManagementServiceClient;
import com.ctx.assessment_service.dto.assessment.create.CreateAssessmentRequestDTO;
import com.ctx.assessment_service.dto.assessment.create.quiz.CreateQuizRequestDTO;
import com.ctx.assessment_service.dto.assessment.create.quiz.QuestionOptionDTO;
import com.ctx.assessment_service.dto.assessment.create.quiz.QuizQuestionDTO;
import com.ctx.assessment_service.dto.assessment.report.AssessmentReportDTO;
import com.ctx.assessment_service.dto.assessment.report.quiz.StudentQuestionAttemptDTO;
import com.ctx.assessment_service.dto.assessment.report.quiz.StudentQuizReportDTO;
import com.ctx.assessment_service.dto.assessment.serve.AssessmentServeDTO;
import com.ctx.assessment_service.dto.assessment.serve.quiz.QuestionOptionServeDTO;
import com.ctx.assessment_service.dto.assessment.serve.quiz.QuizQuestionServeDTO;
import com.ctx.assessment_service.dto.assessment.serve.quiz.QuizServeDTO;
import com.ctx.assessment_service.dto.assessment.submit.AssessmentRequestDTO;
import com.ctx.assessment_service.dto.assessment.submit.quiz.StudentQuestionAndAnswerDTO;
import com.ctx.assessment_service.dto.assessment.submit.quiz.StudentQuizQuestionResponseDTO;
import com.ctx.assessment_service.dto.external_response.CourseResponse;
import com.ctx.assessment_service.dto.user.CurrentUser;
import com.ctx.assessment_service.exception.custom_exceptions.ResourceNotFoundException;
import com.ctx.assessment_service.model.*;
import com.ctx.assessment_service.model.enums.AssessmentType;
import com.ctx.assessment_service.repo.assessment.AssessmentRepo;
import com.ctx.assessment_service.repo.assessment.SubmissionRepo;
import com.ctx.assessment_service.repo.assessment.quiz.QuestionOptionRepo;
import com.ctx.assessment_service.repo.assessment.quiz.QuestionRepo;
import com.ctx.assessment_service.repo.assessment.quiz.QuizRepo;
import com.ctx.assessment_service.repo.assessment.quiz.StudentQuizQuestionResponseRepo;
import com.ctx.assessment_service.service.contract.result.ResultService;
import com.ctx.assessment_service.strategy.contract.AssessmentStrategy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
/**
 * Implementation of AssignmentStrategy for {@link AssessmentType} QUIZ
 * @author SudipSarkar
 * @version 1.0
 * @since 1.0
 */
public class QuizStrategy implements AssessmentStrategy {

    //private final CourseRepo courseRepo;
    private final QuizRepo quizRepo;
    private final QuestionRepo questionRepo;
    private final QuestionOptionRepo questionOptionRepo;
    private final AssessmentRepo assessmentRepo;
    private final SubmissionRepo submissionRepo;
    private final StudentQuizQuestionResponseRepo studentQuizQuestionResponseRepo;
    private final ResultService resultService;
    // private final EnrollmentRepo enrollmentRepo;

    private final CourseServiceClient courseServiceClient;
    private final UserManagementServiceClient userManagementServiceClient;

    @Override
    public boolean supports(AssessmentType type) {
        return type.toString().equals("QUIZ") || type.toString().equals("QUIZ_SUBMISSION");
    }


    @Override
    @Transactional
    public Map<String,String> createAssessment(CurrentUser teacher, CreateAssessmentRequestDTO assessmentRequestDTO) throws BadRequestException {

        CourseResponse course = courseServiceClient.getcourse(assessmentRequestDTO.getCourseId());

        if(course == null){
            throw new ResourceNotFoundException("Course not found");
        }

        if(!canCreateAssessment(teacher.getUserId(),course.getTeacherId())){
            throw new BadRequestException("Teacher " + teacher.getUsername() +" can't add assessment to this course " + course.getTitle());
        }

        Assessment assessment =
                Assessment.builder()
                        .maxScore(assessmentRequestDTO.getMaxScore())
                        .title(assessmentRequestDTO.getTitle())
                        .type(assessmentRequestDTO.getAssessmentType())
                        .courseId(course.getCourseId())
                        .build();

        assessmentRepo.save(assessment);


        Quiz quiz = new Quiz();
        quiz.setAssessment(assessment);
        quizRepo.save(quiz);


        List<QuizQuestionDTO> quizRequestDTOList =
                ((CreateQuizRequestDTO)assessmentRequestDTO).getQuestionDTOList();

        List<Question> questionList = new ArrayList<>();
        List<QuestionOption> questionOptionList = new ArrayList<>();

        for (QuizQuestionDTO quizQuestionDTO : quizRequestDTOList){

            List<QuestionOptionDTO> questionOptionDTOList = quizQuestionDTO.getQuestionOptionDTOList();

            Question question =
                    Question.builder()
                            .questionText(quizQuestionDTO.getQuestionText())
                            .quiz(quiz)
                            .build();

            questionList.add(question);

            for (QuestionOptionDTO questionOptionDTO : questionOptionDTOList){

                QuestionOption questionOption =
                        QuestionOption.builder()
                                .optionText(questionOptionDTO.getOptionText())
                                .isCorrectOption(questionOptionDTO.getIsCorrectOption())
                                .question(question)
                                .build();

                questionOptionList.add(questionOption);

            }
        }

        questionRepo.saveAll(questionList);
        questionOptionRepo.saveAll(questionOptionList);

        Map<String,String> map = new HashMap<>();
        map.put("message","Quiz created successfully");
        map.put("assessmentId",assessment.getAssessmentId().toString());
        map.put("quizId", quiz.getQuizId().toString());

        return map;
    }

    @Override
    public AssessmentServeDTO serveAssessment(UUID assessmentId, CurrentUser user) throws BadRequestException {

        if (user.getRole().equals("STUDENT") &&
                submissionRepo.existsByStudentIdAndAssessmentAssessmentId(
                user.getUserId(), assessmentId
                )) {
            throw new BadRequestException("Student `" + user.getUsername()
                    + "` has already already attempted the Quiz");
        }

        Quiz quiz = quizRepo.findQuizWithQuestionAndOptions(assessmentId)
                .orElseThrow(()-> new ResourceNotFoundException("Quiz not found"));

        Assessment assessment = quiz.getAssessment();

        if(user.getRole().equals("STUDENT") &&
                !courseServiceClient.isStudentEnrolled(user.getUserId(),assessment.getCourseId()).getData()){
            throw new BadRequestException("Student `" + user.getUsername() + "` did not enroll to the course" );
        }

        return mapToQuizServeDTO(quiz);
    }

    private QuizServeDTO mapToQuizServeDTO(Quiz quiz) {
        if (quiz == null) {
            return null;
        }
        List<QuizQuestionServeDTO> questionDTOs = quiz.getQuestionList().stream()
                .map(question -> {
                    QuizQuestionServeDTO qDto
                            = new QuizQuestionServeDTO();
                    qDto.setQuizQuestionId(question.getQuestionId());
                    qDto.setQuestionText(question.getQuestionText());

                    if (question.getQuestionOptionList() != null) {

                        List<QuestionOptionServeDTO> optionDTOs = question.getQuestionOptionList()
                                .stream()
                                .map(option -> {
                                    QuestionOptionServeDTO oDto = new QuestionOptionServeDTO();
                                    oDto.setQuestionOptionId(option.getQuestionOptionId());
                                    oDto.setOptionText(option.getOptionText());
                                    return oDto;
                                })
                                .toList();
                        qDto.setQuestionOptionServeDTOList(optionDTOs);
                    }
                    return qDto;
                })
                .toList();
        QuizServeDTO quizServeDTO = new QuizServeDTO(quiz.getQuizId(), questionDTOs);

        quizServeDTO.setAssessmentType(AssessmentType.QUIZ);
        quizServeDTO.setTitle(quiz.getAssessment().getTitle());

        return quizServeDTO;
    }


    @Override
    public AssessmentReportDTO getReport(UUID submissionId, CurrentUser user) throws BadRequestException {
        List<StudentQuizQuestionResponse> studentResponseList
                = studentQuizQuestionResponseRepo.findStudentQuizResponse(submissionId);

        if(studentResponseList == null || studentResponseList.isEmpty()){
            throw new ResourceNotFoundException("Student response not found");
        }

        if (!studentResponseList.get(0).getSubmission().getStudentId().equals(user.getUserId()))
            if (user.getRole().equals("STUDENT")) {
                throw new BadRequestException("Student " + user.getUsername()
                        + " is not authorized to access this report");
            }


        StudentQuizReportDTO studentQuizReportDTO = new StudentQuizReportDTO();

        List<StudentQuestionAttemptDTO> studentQuestionAttemptDTO = new ArrayList<>();
        for(StudentQuizQuestionResponse response : studentResponseList){
            studentQuestionAttemptDTO.add(toStudentQuizReportDTO(response));
        }

        studentQuizReportDTO.setStudentQuestionAttemptDTOList(studentQuestionAttemptDTO);
        studentQuizReportDTO.setSubmissionId(submissionId);

        studentQuizReportDTO.setAssessmentType(AssessmentType.QUIZ);
        studentQuizReportDTO.setTitle(studentResponseList.get(0).getSubmission().getAssessment().getTitle());
        return studentQuizReportDTO;
    }


    private StudentQuestionAttemptDTO toStudentQuizReportDTO(StudentQuizQuestionResponse response){

        StudentQuestionAttemptDTO studentQuestionAttemptDTO
                = new StudentQuestionAttemptDTO();

        Question question = response.getQuestion();

        Set<QuestionOption> questionOptionSet = question.getQuestionOptionList();

        for(QuestionOption option : questionOptionSet){
            if(option.getIsCorrectOption()){
                studentQuestionAttemptDTO.setCorrectOptionId(option.getQuestionOptionId());
                studentQuestionAttemptDTO.setCorrectOptionText(option.getOptionText());
            }
            if(option.getQuestionOptionId().equals(response.getQuestionOption().getQuestionOptionId())){
                studentQuestionAttemptDTO.setChosenOptionId(option.getQuestionOptionId());
                studentQuestionAttemptDTO.setChosenOptionText(option.getOptionText());
            }
        }

        studentQuestionAttemptDTO.setIsCorrect(response.getIsCorrectOptionChosen());
        studentQuestionAttemptDTO.setQuestionId(question.getQuestionId());
        studentQuestionAttemptDTO.setQuestionText(question.getQuestionText());

        return studentQuestionAttemptDTO;
    }

    @Override
    @Transactional
    public Map<String,String> submitAssessment(CurrentUser student, AssessmentRequestDTO assessmentRequestDTO) throws BadRequestException {

        StudentQuizQuestionResponseDTO dto = (StudentQuizQuestionResponseDTO) assessmentRequestDTO;

        Assessment assessment = assessmentRepo.findById(dto.getAssessmentId())
                .orElseThrow(()-> new ResourceNotFoundException("Assessment not found"));

        if(!courseServiceClient.isStudentEnrolled(student.getUserId(),assessment.getCourseId()).getData()){
            throw new BadRequestException("Student with id `" + student.getUserId() + "` did not enroll to the course" );
        }

        if(submissionRepo.existsByStudentIdAndAssessmentAssessmentId(student.getUserId(),assessment.getAssessmentId())){
            throw new BadRequestException(
                    "Student: `" + student.getUsername() + " already submitted the course"
            );
        }

        Quiz quiz = quizRepo.findById(dto.getQuizId())
                .orElseThrow(()-> new ResourceNotFoundException("Quiz not found"));

        if(!quiz.getAssessment().getAssessmentId().equals(assessment.getAssessmentId())){
            throw new BadRequestException("Quiz does not belong to the given assessment");
        }

        Submission submission =
                Submission.builder()
                        .studentId(student.getUserId())
                        .assessment(assessment)
                        .submissionStatus(SubmissionStatus.NOT_SUBMITTED)
                        .build();

        submissionRepo.save(submission);

        List<StudentQuestionAndAnswerDTO> studentQuestionAndAnswerDTOList = dto.getStudentQuestionAndAnswerDTOList();

        List<StudentQuizQuestionResponse> studentQuizQuestionResponseList = new ArrayList<>();

        for(StudentQuestionAndAnswerDTO studentQuestionAndAnswerDTO : studentQuestionAndAnswerDTOList){

            StudentQuizQuestionResponse studentQuizQuestionResponse = new StudentQuizQuestionResponse();
            studentQuizQuestionResponse.setQuiz(quiz);
            studentQuizQuestionResponse.setSubmission(submission);

            Question question = questionRepo.findById(studentQuestionAndAnswerDTO.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Question not found!"));

            QuestionOption questionOption = questionOptionRepo.findById(studentQuestionAndAnswerDTO.getQuestionOptionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Option not found not found!"));

            if(!questionOption.getQuestion().getQuestionId().equals(question.getQuestionId())){
                throw new BadRequestException("Option does not belong to the given question");
            }

            studentQuizQuestionResponse.setQuestion(question);
            studentQuizQuestionResponse.setQuestionOption(questionOption);

            studentQuizQuestionResponse.setIsCorrectOptionChosen(questionOption.getIsCorrectOption());

            studentQuizQuestionResponseList.add(studentQuizQuestionResponse);

        }

        submission.setSubmissionStatus(SubmissionStatus.SUBMITTED);
        submissionRepo.save(submission);

        studentQuizQuestionResponseRepo.saveAll(studentQuizQuestionResponseList);

        String msg = resultService.computeQuizResult(assessment.getAssessmentId(),student.getUserId());

        log.info("Message from resultService : {}",msg );
        log.info("Result computed successfully for quiz : {}", quiz.getQuizId());

        Map<String,String> map = new HashMap<>();

        map.put("message", "Attempted the Quiz with id" + quiz.getQuizId());
        map.put("assessmentId", assessment.getAssessmentId().toString());
        map.put("quizId", quiz.getQuizId().toString());
        map.put("submissionId",submission.getSubmissionId().toString());

        return map;

    }

}