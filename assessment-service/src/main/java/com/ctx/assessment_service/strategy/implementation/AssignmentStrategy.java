package com.ctx.assessment_service.strategy.implementation;

import com.ctx.assessment_service.client.CourseServiceClient;
import com.ctx.assessment_service.client.UserManagementServiceClient;
import com.ctx.assessment_service.dto.assessment.create.CreateAssessmentRequestDTO;
import com.ctx.assessment_service.dto.assessment.create.assignment.CreateAssignmentRequestDTO;
import com.ctx.assessment_service.dto.assessment.report.AssessmentReportDTO;
import com.ctx.assessment_service.dto.assessment.report.assignment.StudentAssignmentReportDTO;
import com.ctx.assessment_service.dto.assessment.serve.AssessmentServeDTO;
import com.ctx.assessment_service.dto.assessment.serve.assignment.AssignmentServeDTO;
import com.ctx.assessment_service.dto.assessment.session.quiz.QuizSessionResponseDTO;
import com.ctx.assessment_service.dto.assessment.submit.AssessmentRequestDTO;
import com.ctx.assessment_service.dto.assessment.submit.assignment.AssignmentRequestDTO;
import com.ctx.assessment_service.dto.external_response.CourseResponse;
import com.ctx.assessment_service.dto.user.CurrentUser;
import com.ctx.assessment_service.exception.custom_exceptions.DocumentProcessingException;
import com.ctx.assessment_service.exception.custom_exceptions.ResourceNotFoundException;
import com.ctx.assessment_service.model.*;
import com.ctx.assessment_service.model.attachment.assignment.AssignmentAttachment;
import com.ctx.assessment_service.model.enums.AssessmentType;
import com.ctx.assessment_service.model.enums.FileTypeEnum;
import com.ctx.assessment_service.repo.assessment.AssessmentRepo;
import com.ctx.assessment_service.repo.assessment.SubmissionRepo;
import com.ctx.assessment_service.repo.assessment.assignment.AssignmentRepo;
import com.ctx.assessment_service.repo.assessment.attachment.AssignmentAttachmentRepo;
import com.ctx.assessment_service.strategy.contract.AssessmentStrategy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class AssignmentStrategy implements AssessmentStrategy {

    private final AssessmentRepo assessmentRepo;
    private final AssignmentRepo assignmentRepo;
    private final AssignmentAttachmentRepo assignmentAttachmentRepo;
    private final SubmissionRepo submissionRepo;
    private final UserManagementServiceClient userManagementServiceClient;
    private final CourseServiceClient courseServiceClient;

    @Value("${gateway.base-url}")
    private String gatewayBaseUrl;

    private static final Map<String, FileTypeEnum> ALLOWED_TYPES = Map.of(
            ".pdf", FileTypeEnum.PDF,
            ".jpeg", FileTypeEnum.JPEG,
            ".jpg", FileTypeEnum.JPEG
    );

    private FileTypeEnum getFileType(String filename) {
        String extension = filename.toLowerCase()
                .substring(filename.lastIndexOf("."));
        return ALLOWED_TYPES.getOrDefault(extension, FileTypeEnum.BYTE_STREAM);
    }

    @Override
    public boolean supports(AssessmentType type) {
        return type == AssessmentType.ASSIGNMENT;
    }

    @Override
    @Transactional
    public Map<String, String> createAssessment(CurrentUser teacher,
                                                CreateAssessmentRequestDTO dto) throws BadRequestException {

        CourseResponse course = courseServiceClient.getcourse(dto.getCourseId());

        if (course == null) {
            throw new ResourceNotFoundException("Course not found");
        }

        if (!canCreateAssessment(teacher.getUserId(), course.getTeacherId())) {
            throw new BadRequestException("Teacher " + teacher.getUsername()
                    + " can't add assessment to this course: " + course.getTitle());
        }

        CreateAssignmentRequestDTO assignmentDTO = (CreateAssignmentRequestDTO) dto;

        Assessment assessment = new Assessment();
        assessment.setCourseId(course.getCourseId());
        assessment.setTitle(dto.getTitle());
        assessment.setType(dto.getAssessmentType());
        assessment.setMaxScore(dto.getMaxScore());
        assessment.setWeight(dto.getWeight() != null && dto.getWeight() > 0 ? dto.getWeight() : 1.0);

        Assignment assignment = new Assignment();
        assignment.setDueDate(dto.getDueDate());
        assignment.setInstruction(assignmentDTO.getInstruction());
        assignment.setNoOfDocumentsToBeUploaded(
                assignmentDTO.getNoOfDocumentsToBeUploaded() != null
                        ? assignmentDTO.getNoOfDocumentsToBeUploaded()
                        : 10
        );
        assignment.setAssessment(assessment);
        assessment.setAssignment(assignment);

        assessmentRepo.save(assessment);
        assignmentRepo.save(assignment);

        return Map.of(
                "message", "Assignment created successfully",
                "assessmentId", assessment.getAssessmentId().toString(),
                "assignmentId", assignment.getAssignmentId().toString()
        );
    }

    @Override
    public AssessmentServeDTO serveAssessment(UUID assessmentId,
                                              CurrentUser user) throws BadRequestException {

        Assignment assignment = assignmentRepo.findAssignmentAndAssessment(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

        Assessment assessment = assignment.getAssessment();

        if (user.getRole().equals("STUDENT") &&
                !courseServiceClient.isStudentEnrolled(
                        user.getUserId(), assessment.getCourseId()).getData()) {
            throw new BadRequestException("Student `" + user.getUsername()
                    + "` is not enrolled in this course");
        }

        AssignmentServeDTO serveDTO = new AssignmentServeDTO();
        serveDTO.setInstruction(assignment.getInstruction());
        serveDTO.setTitle(assessment.getTitle());
        serveDTO.setNoOfDocumentsToBeUploaded(assignment.getNoOfDocumentsToBeUploaded());
        serveDTO.setAssessmentType(AssessmentType.ASSIGNMENT);

        return serveDTO;
    }

    @Override
    @Transactional
    public Map<String, String> submitAssessment(CurrentUser student,
                                                AssessmentRequestDTO dto) throws BadRequestException, DocumentProcessingException {

        AssignmentRequestDTO assignmentDTO = (AssignmentRequestDTO) dto;

        Assessment assessment = assessmentRepo.findById(dto.getAssessmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));

        if (!courseServiceClient.isStudentEnrolled(
                student.getUserId(), assessment.getCourseId()).getData()) {
            throw new BadRequestException("Student `" + student.getUserId()
                    + "` is not enrolled in this course");
        }

        if (submissionRepo.existsByStudentIdAndAssessmentAssessmentId(
                student.getUserId(), assessment.getAssessmentId())) {
            throw new BadRequestException("Student `" + student.getUsername()
                    + "` has already submitted this assignment");
        }

        Assignment assignment = assignmentRepo.findById(assignmentDTO.getAssignmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

        List<MultipartFile> files = assignmentDTO.getFiles();

        if (files == null || files.isEmpty()) {
            throw new DocumentProcessingException("No files were attached to the submission");
        }

        int required = assignment.getNoOfDocumentsToBeUploaded();
        int uploaded = files.size();

        if (uploaded > required) {
            throw new DocumentProcessingException(
                    "Maximum of " + required + " attachments allowed");
        }
        if (uploaded < required) {
            throw new DocumentProcessingException(
                    "Please upload " + (required - uploaded) + " more attachment(s)");
        }

        Submission submission = Submission.builder()
                .assessment(assessment)
                .studentId(student.getUserId())
                .submissionStatus(SubmissionStatus.NOT_SUBMITTED)
                .build();

        submission = submissionRepo.save(submission);

        List<AssignmentAttachment> attachments = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                AssignmentAttachment attachment = new AssignmentAttachment();
                attachment.setAttachmentId(UUID.randomUUID());
                attachment.setAssignment(assignment);
                attachment.setSubmission(submission);
                attachment.setFileData(file.getBytes());
                attachment.setDescription(dto.getDescription());
                attachment.setFileName(file.getOriginalFilename());
                attachment.setFileTypeEnum(getFileType(file.getOriginalFilename()));
                attachments.add(attachment);
            } catch (Exception e) {
                log.error("Failed to process file: {}", file.getOriginalFilename(), e);
                throw new DocumentProcessingException(
                        "Failed to process file: " + file.getOriginalFilename());
            }
        }

        submission.setSubmissionStatus(SubmissionStatus.SUBMITTED);
        submissionRepo.save(submission);
        assignmentAttachmentRepo.saveAll(attachments);

        return Map.of(
                "message", "Assignment submitted successfully",
                "submissionId", submission.getSubmissionId().toString()
        );
    }

    @Override
    public AssessmentReportDTO getReport(UUID submissionId,
                                         CurrentUser user) throws BadRequestException {

        Submission submission = submissionRepo
                .findAssignmentAndAssessmentAndAttachments(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        if (user.getRole().equals("STUDENT")
                && !submission.getStudentId().equals(user.getUserId())) {
            throw new BadRequestException("Student " + user.getUsername()
                    + " is not authorized to access this report");
        }

        return buildReportDTO(submission);
    }

    @Override
    public QuizSessionResponseDTO startSession(UUID assessmentId, CurrentUser user) {
        return null;
    }

    private StudentAssignmentReportDTO buildReportDTO(Submission submission) {
        StudentAssignmentReportDTO report = new StudentAssignmentReportDTO();

        List<String> urls = submission.getAssignmentAttachmentList()
                .stream()
                .map(a -> gatewayBaseUrl + "/attachment/view/"
                        + a.getAttachmentId())
                .toList();

        report.setAttachmentUriList(urls);
        report.setTitle(submission.getAssessment().getTitle());
        report.setAssessmentType(AssessmentType.ASSIGNMENT);
        report.setNoOfDocumentsUploaded(submission.getAssignmentAttachmentList().size());

        return report;
    }
}