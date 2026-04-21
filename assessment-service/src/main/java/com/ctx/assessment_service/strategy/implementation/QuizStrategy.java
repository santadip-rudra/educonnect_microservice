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
import com.ctx.assessment_service.dto.assessment.session.quiz.QuizSessionResponseDTO;
import com.ctx.assessment_service.dto.assessment.session.quiz.SavedAnswerDTO;
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
import com.ctx.assessment_service.repo.assessment.quiz.*;
import com.ctx.assessment_service.service.contract.image.ImageService;
import com.ctx.assessment_service.service.contract.result.ResultService;
import com.ctx.assessment_service.strategy.contract.AssessmentStrategy;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

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
    private final QuizDraftAnswerRepo quizDraftAnswerRepo;
    private final StudentQuizQuestionResponseRepo studentQuizQuestionResponseRepo;
    private final ResultService resultService;
    private final ImageService imageService;

    private final EntityManager entityManager;

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
                        .weight(assessmentRequestDTO.getWeight() != null && assessmentRequestDTO.getWeight() > 0
                                ? assessmentRequestDTO.getWeight()
                                : 1.0)
                        .build();

        assessmentRepo.save(assessment);


        Quiz quiz = new Quiz();
        quiz.setAssessment(assessment);
        quiz.setDurationMinutes(((CreateQuizRequestDTO)assessmentRequestDTO).getDurationMinutes());
        quizRepo.save(quiz);


        List<QuizQuestionDTO> quizRequestDTOList =
                ((CreateQuizRequestDTO)assessmentRequestDTO).getQuestionDTOList();

        List<Question> questionList = new ArrayList<>();
        List<QuestionOption> questionOptionList = new ArrayList<>();

        for (QuizQuestionDTO quizQuestionDTO : quizRequestDTOList) {

            List<QuestionOptionDTO> questionOptionDTOList = quizQuestionDTO.getQuestionOptionDTOList();

            long correctCount = questionOptionDTOList.stream()
                    .filter(option -> Boolean.TRUE.equals(option.getIsCorrectOption()))
                    .count();
            boolean isMultiOption = correctCount > 1;

            Question question = Question.builder()
                    .questionText(quizQuestionDTO.getQuestionText())
                    .quiz(quiz)
                    .marks(quizQuestionDTO.getMarks())
                    .isMultiOption(isMultiOption)
                    .isPartMarkingAllowed(
                            isMultiOption && Boolean.TRUE.equals(quizQuestionDTO.getIsPartMarkingAllowed())
                    )
                    .build();

            questionList.add(question);

            for (QuestionOptionDTO questionOptionDTO : questionOptionDTOList) {
                QuestionOption questionOption = QuestionOption.builder()
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
    @Transactional
    public AssessmentServeDTO serveAssessment(UUID assessmentId, CurrentUser user) throws BadRequestException {

        if (user.getRole().equals("STUDENT")) {
            Optional<Submission> existing =
                    submissionRepo.findByStudentIdAndAssessmentAssessmentId(user.getUserId(),assessmentId);

            if (existing.isPresent() &&
                    existing.get().getSubmissionStatus() == SubmissionStatus.SUBMITTED) {
                throw new BadRequestException("Student `" + user.getUsername()
                        + "` has already submitted this quiz");
            }
        }

        Quiz quiz = quizRepo.findQuizWithQuestionAndOptions(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        Assessment assessment = quiz.getAssessment();

        if (user.getRole().equals("STUDENT") &&
                !courseServiceClient.isStudentEnrolled(user.getUserId(), assessment.getCourseId()).getData()) {
            throw new BadRequestException("Student `" + user.getUsername() + "` is not enrolled in this course");
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
                    qDto.setMarks(question.getMarks());
                    qDto.setIsMultiOption(question.getIsMultiOption());
                    qDto.setIsPartMarkingAllowed(question.getIsPartMarkingAllowed());
                    qDto.setImageUri(
                            question.getHasImage() == null ? null :
                                    imageService.generateImageUri("question", question.getQuestionId())
                    );

                    if (question.getQuestionOptionList() != null) {

                        List<QuestionOptionServeDTO> optionDTOs = question.getQuestionOptionList()
                                .stream()
                                .map(option -> {
                                    QuestionOptionServeDTO oDto = new QuestionOptionServeDTO();
                                    oDto.setQuestionOptionId(option.getQuestionOptionId());
                                    oDto.setOptionText(option.getOptionText());
                                    oDto.setImageUri(
                                            option.getHasImage() == null?
                                                    null :
                                                    imageService.generateImageUri("option",option.getQuestionOptionId())

                                    );
                                    return oDto;
                                })
                                .toList();
                        qDto.setQuestionOptionServeDTOList(optionDTOs);
                    }
                    return qDto;
                })
                .toList();

        QuizServeDTO quizServeDTO
                = new QuizServeDTO(quiz.getQuizId(), quiz.getDurationMinutes(), questionDTOs);

        quizServeDTO.setAssessmentType(AssessmentType.QUIZ);
        quizServeDTO.setTitle(quiz.getAssessment().getTitle());
        quizServeDTO.setDurationInMinutes(quiz.getDurationMinutes());

        return quizServeDTO;
    }


    @Override
    public AssessmentReportDTO getReport(UUID submissionId, CurrentUser user) throws BadRequestException {
        List<StudentQuizQuestionResponse> studentResponseList =
                studentQuizQuestionResponseRepo.findStudentQuizResponse(submissionId);

        if (studentResponseList == null || studentResponseList.isEmpty()) {
            throw new ResourceNotFoundException("Student response not found");
        }

        if (user.getRole().equals("STUDENT") &&
                !studentResponseList.get(0).getSubmission().getStudentId().equals(user.getUserId())) {
            throw new BadRequestException("Student " + user.getUsername()
                    + " is not authorized to access this report");
        }

        // group responses by question
        Map<UUID, List<StudentQuizQuestionResponse>> byQuestion = studentResponseList.stream()
                .collect(Collectors.groupingBy(r -> r.getQuestion().getQuestionId()));

        List<StudentQuestionAttemptDTO> attempts = new ArrayList<>();

        for (Map.Entry<UUID, List<StudentQuizQuestionResponse>> entry : byQuestion.entrySet()) {
            Question question = entry.getValue().get(0).getQuestion();
            List<StudentQuizQuestionResponse> responses = entry.getValue();

            Set<UUID> correctIds = question.getQuestionOptionList().stream()
                    .filter(QuestionOption::getIsCorrectOption)
                    .map(QuestionOption::getQuestionOptionId)
                    .collect(Collectors.toSet());

            Set<UUID> chosenIds = responses.stream()
                    .map(r -> r.getQuestionOption().getQuestionOptionId())
                    .collect(Collectors.toSet());

            // compute score
            double scoreAwarded = computeQuestionScore(question, correctIds, chosenIds);

            StudentQuestionAttemptDTO dto = new StudentQuestionAttemptDTO();
            dto.setQuestionId(question.getQuestionId());
            dto.setQuestionText(question.getQuestionText());
            dto.setMarks(question.getMarks());
            dto.setIsMultiOption(question.getIsMultiOption());
            dto.setIsPartMarkingAllowed(question.getIsPartMarkingAllowed());
            dto.setScoreAwarded(scoreAwarded);

            dto.setCorrectOptionIds(new ArrayList<>(correctIds));
            dto.setCorrectOptionTexts(
                    question.getQuestionOptionList().stream()
                            .filter(o -> correctIds.contains(o.getQuestionOptionId()))
                            .map(QuestionOption::getOptionText)
                            .toList()
            );
            dto.setChosenOptionIds(new ArrayList<>(chosenIds));
            dto.setChosenOptionTexts(
                    question.getQuestionOptionList().stream()
                            .filter(o -> chosenIds.contains(o.getQuestionOptionId()))
                            .map(QuestionOption::getOptionText)
                            .toList()
            );

            attempts.add(dto);
        }

        StudentQuizReportDTO reportDTO = new StudentQuizReportDTO();
        reportDTO.setSubmissionId(submissionId);
        reportDTO.setAssessmentType(AssessmentType.QUIZ);
        reportDTO.setTitle(studentResponseList.get(0).getSubmission().getAssessment().getTitle());
        reportDTO.setStudentQuestionAttemptDTOList(attempts);

        return reportDTO;
    }

    private double computeQuestionScore(Question question, Set<UUID> correctIds, Set<UUID> chosenIds) {
        int marks = question.getMarks() != null ? question.getMarks() : 0;

        if (!question.getIsMultiOption()) {
            // full marks or zero
            return correctIds.equals(chosenIds) ? marks : 0.0;
        }

        long correctlyChosen = chosenIds.stream()
                                .filter(id -> correctIds.contains(id))
                                .count();

        long incorrectlyChosen = chosenIds.stream()
                                .filter(id -> !correctIds.contains(id))
                                .count();

        if (incorrectlyChosen > 0) {
            return 0.0;
        }

        if (correctlyChosen == correctIds.size()) {
            return marks;
        }

        if (Boolean.TRUE.equals(question.getIsPartMarkingAllowed())) {
            return ((double) correctlyChosen / correctIds.size()) * marks;
        }

        return 0.0;
    }

    @Override
    @Transactional
    public Map<String,String> submitAssessment(CurrentUser student, AssessmentRequestDTO assessmentRequestDTO) throws BadRequestException {

        StudentQuizQuestionResponseDTO dto = (StudentQuizQuestionResponseDTO) assessmentRequestDTO;

        Assessment assessment = assessmentRepo.findById(dto.getAssessmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));

        if (!courseServiceClient.isStudentEnrolled(student.getUserId(), assessment.getCourseId()).getData()) {
            throw new BadRequestException("Student with id `" + student.getUserId() + "` did not enroll to the course");
        }

        Optional<Submission> existingSubmission =
                submissionRepo.findByStudentIdAndAssessmentAssessmentId(student.getUserId(), dto.getAssessmentId());

        if (existingSubmission.isPresent()
                && existingSubmission.get().getSubmissionStatus().equals(SubmissionStatus.SUBMITTED)) {
            throw new BadRequestException(
                    "Student: `" + student.getUsername() + "` already submitted this quiz"
            );
        }

        if (existingSubmission.isEmpty()) {
            throw new BadRequestException(
                    "No active session found for student: `" + student.getUsername()
                            + "`. Please start the quiz before submitting."
            );
        }

        return getSubmitResponseData(existingSubmission.get(), student, dto, assessment);
    }

    private Map<String, String> getSubmitResponseData(
            Submission submission,
            CurrentUser student,
            StudentQuizQuestionResponseDTO dto,
            Assessment assessment) throws BadRequestException {

        Quiz quiz = quizRepo.findById(dto.getQuizId())
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        if (!quiz.getAssessment().getAssessmentId().equals(assessment.getAssessmentId())) {
            throw new BadRequestException("Quiz does not belong to the given assessment");
        }

        List<StudentQuestionAndAnswerDTO> studentQuestionAndAnswerDTOList =
                dto.getStudentQuestionAndAnswerDTOList();

        List<StudentQuizQuestionResponse> studentQuizQuestionResponseList = new ArrayList<>();

        for (StudentQuestionAndAnswerDTO studentQuestionAndAnswerDTO : studentQuestionAndAnswerDTOList) {

            Question question = questionRepo.findById(studentQuestionAndAnswerDTO.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

            // iterate over all selected option IDs for this question
            for (UUID selectedOptionId : studentQuestionAndAnswerDTO.getQuestionOptionIds()) {

                QuestionOption questionOption = questionOptionRepo.findById(selectedOptionId)
                        .orElseThrow(() -> new ResourceNotFoundException("Option not found"));

                if (!questionOption.getQuestion().getQuestionId().equals(question.getQuestionId())) {
                    throw new BadRequestException("Option does not belong to the given question");
                }

                StudentQuizQuestionResponse response = StudentQuizQuestionResponse.builder()
                        .quiz(quiz)
                        .submission(submission)
                        .question(question)
                        .questionOption(questionOption)
                        .build();

                studentQuizQuestionResponseList.add(response);
            }
        }
        // DELETE the draft
        quizDraftAnswerRepo.deleteAllBySubmissionId(submission.getSubmissionId());

         // if submission occurs twice for some reason,
        // or if a previous attempt partially committed before a rollback,
        // we don't hit the unique constraint.
        List<StudentQuizQuestionResponse> existingResponses =
                studentQuizQuestionResponseRepo.findAllBySubmission(submission);
        if (!existingResponses.isEmpty()) {
            studentQuizQuestionResponseRepo.deleteAll(existingResponses);
            studentQuizQuestionResponseRepo.flush();
        }

        studentQuizQuestionResponseRepo.saveAll(studentQuizQuestionResponseList);

        submission.setSubmissionStatus(SubmissionStatus.SUBMITTED);
        submissionRepo.save(submission);

        String msg = resultService.computeQuizResult(assessment.getAssessmentId(), student.getUserId());
        log.info("Message from resultService : {}", msg);
        log.info("Result computed successfully for quiz : {}", quiz.getQuizId());

        Map<String, String> map = new HashMap<>();
        map.put("message", "Attempted the Quiz with id " + quiz.getQuizId());
        map.put("assessmentId", assessment.getAssessmentId().toString());
        map.put("quizId", quiz.getQuizId().toString());
        map.put("submissionId", submission.getSubmissionId().toString());

        return map;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public QuizSessionResponseDTO startSession(UUID assessmentId, CurrentUser user) {

        Optional<Submission> existing =
                submissionRepo.findByStudentIdAndAssessmentAssessmentId(user.getUserId(), assessmentId);

        if (existing.isPresent()) {
            return buildSessionResponse(existing.get());
        }

        Assessment assessment = assessmentRepo.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));

        Submission submission;
        try {
            submission = Submission.builder()
                    .assessment(assessment)
                    .studentId(user.getUserId())
                    .submissionStatus(SubmissionStatus.IN_PROGRESS)
                    .startedAt(Instant.now())
                    .attemptCount(1)
                    .isLate(false)
                    .build();

            submissionRepo.saveAndFlush(submission);

        } catch (DataIntegrityViolationException e) {
            // two requests raced — the other one already inserted the row.
            // REQUIRES_NEW means this transaction is already rolled back,
            // so the re-query reads the committed row from the winning request.
            log.warn(
                    "Race condition on startSession for studentId={} assessmentId={} — fetching existing session",
                    user.getUserId(), assessmentId
            );

            entityManager.clear();

            submission = submissionRepo
                    .findByStudentIdAndAssessmentAssessmentId(user.getUserId(), assessmentId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Session could not be created or found"));
        }

        return buildSessionResponse(submission);
    }

    private QuizSessionResponseDTO buildSessionResponse(Submission s) {
        List<QuizDraftAnswer> drafts =
                quizDraftAnswerRepo.findAllBySubmissionSubmissionId(s.getSubmissionId());

        List<SavedAnswerDTO> savedAnswers = drafts.stream()
                .map(d -> SavedAnswerDTO.builder()
                        .questionId(d.getQuestionId().toString())
                        .selectedOptionIds(Arrays.asList(d.getSelectedOptionIds().split(",")))
                        .build())
                .toList();

        boolean isResumed = !savedAnswers.isEmpty()
                || s.getSubmissionStatus() == SubmissionStatus.IN_PROGRESS;

        return QuizSessionResponseDTO.builder()
                .submissionId(s.getSubmissionId())
                .startedAt(s.getStartedAt().toString())
                .durationMinutes(s.getAssessment().getQuiz().getDurationMinutes())
                .savedAnswers(savedAnswers)
                .isResumed(isResumed)
                .build();
    }


    @Transactional
    public void saveAnswer(UUID submissionId, UUID questionId, List<UUID> selectedOptionIds) throws BadRequestException {

        Submission submission = submissionRepo.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        if (submission.getSubmissionStatus() != SubmissionStatus.IN_PROGRESS) {
            throw new BadRequestException("Cannot save answer — submission is already finalized");
        }

        String joined = selectedOptionIds.stream()
                .map(UUID::toString)
                .collect(Collectors.joining(","));

        Optional<QuizDraftAnswer> existing =
                quizDraftAnswerRepo.findBySubmissionSubmissionIdAndQuestionId(submissionId, questionId);

        if (existing.isPresent()) {
            existing.get().setSelectedOptionIds(joined);
            quizDraftAnswerRepo.save(existing.get());
        } else {
            quizDraftAnswerRepo.save(
                    QuizDraftAnswer.builder()
                            .submission(submission)
                            .questionId(questionId)
                            .selectedOptionIds(joined)
                            .build()
            );
        }
    }

    @Override
    @Transactional
    public void deleteAssessment(UUID assessmentId, CurrentUser teacher) throws BadRequestException {

        Assessment assessment = assessmentRepo.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));

        CourseResponse course = courseServiceClient.getcourse(assessment.getCourseId());

        if (course == null || !canCreateAssessment(teacher.getUserId(), course.getTeacherId())) {
            throw new BadRequestException("Teacher `" + teacher.getUsername()
                    + "` is not authorized to delete this assessment");
        }

        assessmentRepo.deleteById(assessmentId);

        log.info("Assessment {} deleted by teacher {} (rollback)",
                assessmentId, teacher.getUsername());
    }
}