package com.ctx.student_registry_service.services;

import com.ctx.student_registry_service.client.StudentClient;
import com.ctx.student_registry_service.dto.document.DocStreamDto;
import com.ctx.student_registry_service.dto.student.StudentResponse;
import com.ctx.student_registry_service.exceptions.custom.StudentNotFoundException;
import com.ctx.student_registry_service.models.StudentDocument;
import com.ctx.student_registry_service.models.enums.FileTypeEnum;
import  com.ctx.student_registry_service.models.DocType;

import  com.ctx.student_registry_service.models.enums.DocTypeEnum;

import com.ctx.student_registry_service.repos.DocTypeRepo;
import com.ctx.student_registry_service.repos.StudentDocumentRepo;




import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentDoctypeService {

    private final StudentDocumentRepo studentDocumentRepo;
    private final StudentClient studentClient;
    private final DocTypeRepo docTypeRepo;

    private final Map<String, FileTypeEnum> allowedTypes =
            new HashMap<>(Map.of(
                    ".pdf",FileTypeEnum.PDF,
                    "jpeg",FileTypeEnum.JPEG,
                    "jpg",FileTypeEnum.JPEG
            ));


    public String saveStudentDocument(UUID studentUuid, MultipartFile file, DocTypeEnum docTypeEnum) throws IOException, StudentNotFoundException {

        if(file == null || file.isEmpty()){
            throw new RuntimeException("file not found");
        }

        StudentResponse student = studentClient.findByStudentId(studentUuid)
                .orElseThrow(()-> new StudentNotFoundException(studentUuid+ " not found"));
        StudentDocument document = new StudentDocument();

        document.setStudentId(student.getStudentId());
        document.setFileName(file.getOriginalFilename());
        FileTypeEnum fileType = getFileType(file.getOriginalFilename());
        DocType docType = docTypeRepo.findByDocTypeName(docTypeEnum).orElse(null);

        if(docType == null){
            docType = DocType.builder()
                    .docTypeName(docTypeEnum)
                    .description("LATER.....")
                    .build();
            docTypeRepo.save(docType);
        }

        document.setDocType(docType);
        document.setFileType(fileType);
        document.setFileData(file.getBytes());
        document.setStudentDocumentId(UUID.randomUUID());
        String uri =  ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/v2/api/docs/view/")
                .path(document.getStudentDocumentId().toString())
                .toUriString();
        document.setFileUri(uri);
        studentDocumentRepo.save(document);
        return uri;

    }

    private FileTypeEnum getFileType(String filename){
        filename = filename.toLowerCase();
        String extension= filename.substring(filename.lastIndexOf("."));
        return allowedTypes.getOrDefault(extension,FileTypeEnum.BYTE_STREAM);
    }


    public DocStreamDto getDocument(UUID documentUuid) {
        StudentDocument document = studentDocumentRepo
                .findByStudentDocumentId(documentUuid)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        byte[] fileByteData = document.getFileData();

        if (fileByteData == null) {
            throw new RuntimeException("Document has no data");
        }

        InputStream inputStream = new ByteArrayInputStream(fileByteData);

        return new DocStreamDto(inputStream,document);
    }
}
