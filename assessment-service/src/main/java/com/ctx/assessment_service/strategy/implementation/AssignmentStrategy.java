package com.ctx.assessment_service.strategy.implementation;


import com.ctx.assessment_service.client.CourseClient;
import com.ctx.assessment_service.client.UserManagementServiceClient;
import com.ctx.assessment_service.dto.assessment.create.CreateAssessmentRequestDTO;
import com.ctx.assessment_service.dto.assessment.create.assignment.CreateAssignmentRequestDTO;
import com.ctx.assessment_service.dto.assessment.report.AssessmentReportDTO;
import com.ctx.assessment_service.dto.assessment.report.assignment.StudentAssignmentReportDTO;
import com.ctx.assessment_service.dto.assessment.serve.AssessmentServeDTO;
import com.ctx.assessment_service.dto.assessment.serve.assignment.AssignmentServeDTO;
import com.ctx.assessment_service.dto.assessment.submit.AssessmentRequestDTO;
import com.ctx.assessment_service.dto.assessment.submit.assignment.AssignmentRequestDTO;
import com.ctx.assessment_service.dto.course.CourseDTO;
import com.ctx.assessment_service.dto.user.CurrentUser;
import com.ctx.assessment_service.exception.custom_exceptions.DocumentProcessingException;
import com.ctx.assessment_service.exception.custom_exceptions.ResourceNotFoundException;
import com.ctx.assessment_service.model.*;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service

/**
 *
 * Implementation of AssignmentStrategy for {@link AssessmentType} ASSESSMENT
 * @author SudipSarkar
 * @version 1.0
 * @since 1.0
 */
public class AssignmentStrategy implements AssessmentStrategy {


    private final AssessmentRepo assessmentRepo;
    private final AssignmentRepo assignmentRepo;
    private final AssignmentAttachmentRepo assignmentAttachmentRepo;
    private final SubmissionRepo submissionRepo;
   // private final CourseRepo courseRepo; CAN'T GET IT
    // private final EnrollmentRepo enrollmentRepo;

    private final UserManagementServiceClient userManagementServiceClient;
    private final CourseClient courseClient;

    private final Map<String, FileTypeEnum> allowedTypes =
            new HashMap<>(Map.of(
                    ".pdf",FileTypeEnum.PDF,
                    ".jpeg",FileTypeEnum.JPEG,
                    ".jpg",FileTypeEnum.JPEG
            ));

    private FileTypeEnum getFileType(String filename){
        filename = filename.toLowerCase();
        String extension= filename.substring(filename.lastIndexOf("."));
        return allowedTypes.getOrDefault(extension,FileTypeEnum.BYTE_STREAM);
    }

    @Override
    public boolean supports(AssessmentType type) {
        return type.toString().equals("ASSIGNMENT");
    }

    @Override
    @Transactional
    public Map<String,String> submitAssessment(CurrentUser student, AssessmentRequestDTO dto) throws BadRequestException, DocumentProcessingException {

        Assessment assessment = assessmentRepo.findById(dto.getAssessmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));

        if(userManagementServiceClient.isStudentEnrolled(student.getUserId(),assessment.getCourseId())){
            throw new BadRequestException("Student with id `" + student.getUserId() + "` did not enroll to the course" );
        }

        List<MultipartFile> files = ((AssignmentRequestDTO)dto).getFiles();

        if (files == null || files.isEmpty()) {

            throw new DocumentProcessingException("No files were attached to the submission.");

        }

        Assignment assignment = assignmentRepo.findById(((AssignmentRequestDTO)dto).getAssignmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

        int uploadedCount = files.size();
        int requiredCount = assignment.getNoOfDocumentsToBeUploaded();

        if (uploadedCount != requiredCount) {
            if (uploadedCount > requiredCount) {
                try {
                    throw new DocumentProcessingException("Maximum of " + requiredCount + " attachments allowed.");
                } catch (DocumentProcessingException e) {
                    log.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    throw new DocumentProcessingException("Please upload " + (requiredCount - uploadedCount) + " more attachments.");
                } catch (DocumentProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (submissionRepo.existsByStudentIdAndAssessmentId(student.getUserId(), assessment.getAssessmentId())) {

            throw new BadRequestException("You have already submitted this assignment.");

        }

        Submission submission = Submission.builder()
                .assessment(assessment)
                .studentId(student.getUserId())
                .submissionStatus(SubmissionStatus.NOT_SUBMITTED)
                .build();


        submission = submissionRepo.save(submission);

        List<AssignmentAttachment> attachmentList = new ArrayList<>();

        for (MultipartFile file : files) {
            try {

                AssignmentAttachment attachment = new AssignmentAttachment();

                UUID attachmentId = UUID.randomUUID();
                attachment.setAttachmentId(attachmentId);

                attachment.setAssignment(assignment);
                attachment.setSubmission(submission);
                attachment.setFileData(file.getBytes());
                attachment.setDescription(dto.getDescription());
                attachment.setFileName(file.getOriginalFilename());
                attachment.setFileTypeEnum(getFileType(file.getOriginalFilename()));

                String uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/api/attachment/view/")
                        .path(attachmentId.toString())
                        .toUriString();

                attachment.setUri(uri);

                attachmentList.add(attachment);

            } catch (Exception e) {
                log.error("Failed to read file: {}", file.getOriginalFilename(), e);
                throw new RuntimeException(e);
            }
        }

        submission.setSubmissionStatus(SubmissionStatus.SUBMITTED);

        assignmentAttachmentRepo.saveAll(attachmentList);

        Map<String,String> map = new HashMap<>();
        map.put("message","Assignment submitted successfully");
        map.put("submissionId",submission.getSubmissionId().toString());

        return map;
    }


    @Override
    @Transactional
    public Map<String,String> createAssessment(CurrentUser teacher, CreateAssessmentRequestDTO dto) throws BadRequestException {

            CourseDTO course = courseClient.getCourseById(dto.getCourseId());

            if(course == null){
                throw  new ResourceNotFoundException("Course not found");
            }

            if(!canCreateAssessment(teacher.getUserId(),course.getCourseId())){
                throw new BadRequestException("Teacher with id" + teacher.getUserId()
                        + " can't add assessment to this course " + course.getTitle());
            }

            //Assignment

            Assessment assessment = new Assessment();

            assessment.setCourseId(course.getCourseId());
            assessment.setTitle(dto.getTitle());
            assessment.setType(dto.getAssessmentType());
            assessment.setMaxScore(dto.getMaxScore());

            Assignment assignment = new Assignment();

            assignment.setDueDate(dto.getDueDate());
            assignment.setNoOfDocumentsToBeUploaded(
                    ((CreateAssignmentRequestDTO)dto).getNoOfDocumentsToBeUploaded() == null?
                            10 : ((CreateAssignmentRequestDTO)dto).getNoOfDocumentsToBeUploaded()
            );
            assignment.setInstruction(((CreateAssignmentRequestDTO) dto).getInstruction());
            assignment.setAssessment(assessment);
            assessment.setAssignment(assignment);

            assessmentRepo.save(assessment);
            assignmentRepo.save(assignment);

            Map<String,String> map = new HashMap<>();

            map.put("message","Created Assessment of type " + dto.getAssessmentType().toString());
            map.put("assessmentId",assessment.getAssessmentId().toString());
            map.put("assignmentId",assignment.getAssignmentId().toString());

            return map;
    }

    @Override
    public AssessmentServeDTO serveAssessment(UUID assessmentId, CurrentUser user) throws BadRequestException {
        Assignment assignment = assignmentRepo.findAssignmentAndAssessment(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found!!!!"));

        Assessment assessment = assignment.getAssessment();

        if(user.getRole().equals("STUDENT") &&
                !courseClient.hasEnrolledToCourse(user.getUserId(),assessment.getCourseId())){
            throw new BadRequestException("Student `" + user.getUsername() + "` did not enroll to the course" );
        }

        AssignmentServeDTO assignmentServeDTO = new AssignmentServeDTO();

        assignmentServeDTO.setInstruction(assignment.getInstruction());
        assignmentServeDTO.setTitle(assessment.getTitle());
        assignmentServeDTO.setNoOfDocumentsToBeUploaded(assignment.getNoOfDocumentsToBeUploaded());
        assignmentServeDTO.setAssessmentType(AssessmentType.ASSIGNMENT);

        return assignmentServeDTO;
    }

    @Override
    public AssessmentReportDTO getReport(UUID submissionId, CurrentUser user) throws BadRequestException {

        Submission submission = submissionRepo.findAssignmentAndAssessmentAndAttachments(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        if(user.getRole().equals("STUDENT")
                && !submission.getStudentId().equals(user.getUserId())
                ){
            throw new BadRequestException("Student " + user.getUsername()
                    + " is not authorized to access this report");
        }

        StudentAssignmentReportDTO assignmentReportDTO = new StudentAssignmentReportDTO();

        List<String> uriList = new ArrayList<>();
        for (AssignmentAttachment assignmentAttachment : submission.getAssignmentAttachmentList()){
            uriList.add(assignmentAttachment.getUri());
        }
        assignmentReportDTO.setAttachmentUriList(uriList);

        assignmentReportDTO.setTitle(submission.getAssessment().getTitle());
        assignmentReportDTO.setAssessmentType(AssessmentType.ASSIGNMENT);
        assignmentReportDTO.setNoOfDocumentsUploaded(submission.getAssessment().getAssignment().getNoOfDocumentsToBeUploaded());

        return assignmentReportDTO;

    }
}
