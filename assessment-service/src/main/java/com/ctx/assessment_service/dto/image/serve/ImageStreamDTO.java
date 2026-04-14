package com.ctx.assessment_service.dto.image.serve;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageStreamDTO {
    private InputStream inputStream;
    private String fileName;
    private String contentType;
}
