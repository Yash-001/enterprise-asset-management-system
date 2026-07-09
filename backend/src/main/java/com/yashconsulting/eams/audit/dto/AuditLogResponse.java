package com.yashconsulting.eams.audit.dto;

import com.yashconsulting.eams.audit.entity.AuditAction;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogResponse {

    private Long id;
    private String entityName;
    private AuditAction action;
    private String beforeValue;
    private String afterValue;
    private String performedBy;
    private LocalDateTime performedAt;
    private String ipAddress;
    private Long entityId;
}
