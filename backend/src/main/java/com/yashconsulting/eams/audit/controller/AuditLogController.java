package com.yashconsulting.eams.audit.controller;

import com.yashconsulting.eams.audit.dto.AuditLogResponse;
import com.yashconsulting.eams.audit.dto.AuditLogSearchRequest;
import com.yashconsulting.eams.audit.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
@Tag(name = "Audit Log", description = "APIs for querying audit trail of CRUD operations")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Search audit logs", description = "Paginated search with filters for entity, action, user, and date range")
    public ResponseEntity<Page<AuditLogResponse>> search(AuditLogSearchRequest request) {
        return ResponseEntity.ok(auditLogService.search(request));
    }

    @GetMapping("/entity/{entityName}/{entityId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get entity audit history", description = "Returns full audit trail for a specific entity instance")
    public ResponseEntity<List<AuditLogResponse>> getEntityHistory(
            @PathVariable String entityName,
            @PathVariable Long entityId) {
        return ResponseEntity.ok(auditLogService.getEntityHistory(entityName, entityId));
    }
}
