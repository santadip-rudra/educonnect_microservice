package com.cts.auth_service.dto;

import com.cts.auth_service.models.Action;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditLogDto {
    private UUID auditLogId;
    private UUID userId;
    @Enumerated(EnumType.STRING)
    private Action action;
    private String resource; //entity name and entity id , entity type
    @CurrentTimestamp
    private LocalDateTime timestamp ;
}
