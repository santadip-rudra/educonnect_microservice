package com.ctx.student_registry_service.dto.document;

import com.ctx.student_registry_service.models.StudentDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DocStreamDto{
    private InputStream inputStream;
    private StudentDocument studentDocument;
}
