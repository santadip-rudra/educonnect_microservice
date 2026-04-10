package com.ctx.assessment_service.dto.attachment;

import com.ctx.assessment_service.model.attachment.base_class.Attachment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.InputStream;

@AllArgsConstructor
@Getter
public class AttachmentStreamDTO {
    private Attachment attachment;
    private InputStream inputStream;
}

