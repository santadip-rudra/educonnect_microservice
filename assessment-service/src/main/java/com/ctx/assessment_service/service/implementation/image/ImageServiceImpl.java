package com.ctx.assessment_service.service.implementation.image;

import com.ctx.assessment_service.dto.image.serve.ImageStreamDTO;
import com.ctx.assessment_service.exception.custom_exceptions.ResourceNotFoundException;
import com.ctx.assessment_service.model.Question;
import com.ctx.assessment_service.model.QuestionOption;
import com.ctx.assessment_service.repo.assessment.quiz.QuestionOptionRepo;
import com.ctx.assessment_service.repo.assessment.quiz.QuestionRepo;
import com.ctx.assessment_service.service.contract.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final QuestionRepo questionRepo;
    private final QuestionOptionRepo questionOptionRepo;

    @Value("${gateway.base-url}")
    private String gatewayUrl;

    @Override
    public String generateImageUri(String type, UUID id) {
        return gatewayUrl + "/assessment/quiz/view/" + type + "/image/" + id;
    }

    @Override
    public void uploadQuestionImage(UUID questionId, MultipartFile file) throws IOException {
        validateImage(file);
        Question question = questionRepo.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found: " + questionId));
        question.setImageBinData(file.getBytes());
        question.setImageFileName(file.getOriginalFilename());

        String contentType = MediaTypeFactory.getMediaType(file.getOriginalFilename())
                .map(MediaType::toString)
                .orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        question.setImageContentType(contentType);
        question.setHasImage(true);

        questionRepo.save(question);
    }

    @Override
    public void uploadOptionImage(UUID optionId, MultipartFile file) throws IOException {
        validateImage(file);
        QuestionOption option = questionOptionRepo.findById(optionId)
                .orElseThrow(() -> new ResourceNotFoundException("Option not found: " + optionId));

        option.setImageBinData(file.getBytes());
        option.setImageFileName(file.getOriginalFilename());

        String contentType = MediaTypeFactory.getMediaType(file.getOriginalFilename())
                .map(MediaType::toString)
                .orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        option.setImageContentType(contentType);
        option.setHasImage(true);

        questionOptionRepo.save(option);
    }

    @Override
    public ImageStreamDTO getQuestionImage(UUID questionId){

        Question question = questionRepo.findById(questionId)
                .orElseThrow(()->new ResourceNotFoundException("Quiz question not found"));

        byte[] imageBinary = question.getImageBinData();


        if(imageBinary == null){
            throw new ResourceNotFoundException("Image data not found");
        }
        InputStream inputStream = new ByteArrayInputStream(imageBinary);

        return new ImageStreamDTO(
                inputStream,
                question.getImageFileName(),
                question.getImageContentType()
        );
    }

    @Override
    public ImageStreamDTO getOptionImage(UUID optionId){

        QuestionOption option = questionOptionRepo.findById(optionId)
                .orElseThrow(()->new ResourceNotFoundException("Question option not found"));

        byte[] imageBinary = option.getImageBinData();


        if(imageBinary == null){
            throw new ResourceNotFoundException("Image data not found");
        }
        InputStream inputStream = new ByteArrayInputStream(imageBinary);

        return new ImageStreamDTO(
                inputStream,
                option.getImageFileName(),
                option.getImageContentType()
        );
    }
}
