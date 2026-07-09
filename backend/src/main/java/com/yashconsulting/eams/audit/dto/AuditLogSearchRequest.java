package com.yashconsulting.eams.audit.dto;

import com.yashconsulting.eams.audit.entity.AuditAction;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogSearchRequest {

    private String entityName;
    private Long entityId;
    private AuditAction action;
    private String performedBy;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;

    @Builder.Default
    private Integer page = 0;

    @Builder.Default
    private Integer size = 20;

    @Builder.Default
    private String sortBy = "performedAt";

    @Builder.Default
    private String sortDirection = "DESC";
}
