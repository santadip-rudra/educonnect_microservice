package com.ctx.assessment_service.model.attachment.base_class;

import com.ctx.assessment_service.model.enums.FileTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Attachment {

    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID attachmentId;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false, columnDefinition = "LONGBLOB")
    private byte[] fileData;

    private String description;

    private String fileName ;

    private FileTypeEnum fileTypeEnum;

    private String uri;

}

