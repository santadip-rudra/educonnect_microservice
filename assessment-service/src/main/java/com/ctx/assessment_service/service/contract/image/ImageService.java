package com.ctx.assessment_service.service.contract.image;

import com.ctx.assessment_service.dto.image.serve.ImageStreamDTO;
import org.apache.coyote.BadRequestException;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface ImageService {
    void uploadQuestionImage(UUID questionId, MultipartFile file) throws IOException;
    void uploadOptionImage(UUID optionId, MultipartFile file) throws IOException ;
    ImageStreamDTO getQuestionImage(UUID questionId);
    ImageStreamDTO getOptionImage(UUID optionId);
    String generateImageUri(String type, UUID id);


    default void validateImage(MultipartFile file) throws BadRequestException {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Image file must not be empty");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new BadRequestException("Only JPEG, PNG, and WebP images are allowed");
        }
    }

    List<String> ALLOWED_TYPES = List.of(
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE,
            "image/webp"
    );
}
