package com.yashconsulting.eams.audit.controller;

import com.yashconsulting.eams.audit.dto.AuditLogResponse;
import com.yashconsulting.eams.audit.dto.AuditLogSearchRequest;
import com.yashconsulting.eams.audit.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@Tag(name = "Audit Log", description = """
        APIs for querying the audit trail of all CRUD operations.
        Every create, update, and delete operation on supported entities (User, Asset, WorkOrder, 
        PurchaseOrder, SparePart) is automatically recorded with before/after values, performer identity, 
        IP address, and timestamp.
        Access restricted to ADMIN and MANAGER roles.
        """)
@SecurityRequirement(name = "Bearer JWT")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
            summary = "Search audit logs",
            description = """
                    Paginated search with filters for entity name, entity ID, action type, 
                    performer, and date range. Results are sorted by timestamp (newest first by default).
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Audit logs retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized — invalid or missing JWT", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden — requires ADMIN or MANAGER role", content = @Content)
    })
    public ResponseEntity<Page<AuditLogResponse>> search(
            @Parameter(description = "Entity name filter (e.g., Asset, WorkOrder, User)", example = "Asset")
            @RequestParam(required = false) String entityName,
            @Parameter(description = "Entity ID filter", example = "1")
            @RequestParam(required = false) Long entityId,
            @Parameter(description = "Action filter (CREATE, UPDATE, DELETE, READ)", example = "CREATE")
            @RequestParam(required = false) String action,
            @Parameter(description = "Performer email or username filter", example = "admin@eams.com")
            @RequestParam(required = false) String performedBy,
            AuditLogSearchRequest request) {
        return ResponseEntity.ok(auditLogService.search(request));
    }

    @GetMapping("/entity/{entityName}/{entityId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
            summary = "Get complete audit history for an entity",
            description = """
                    Returns the full audit trail for a specific entity instance, ordered by most recent first.
                    Useful for compliance reporting and investigating changes to a particular record.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entity audit history retrieved successfully",
                    content = @Content(schema = @Schema(implementation = AuditLogResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden — requires ADMIN or MANAGER role", content = @Content)
    })
    public ResponseEntity<List<AuditLogResponse>> getEntityHistory(
            @Parameter(description = "Entity type name", required = true,
                    examples = {
                            @ExampleObject(name = "Asset", value = "Asset"),
                            @ExampleObject(name = "WorkOrder", value = "WorkOrder"),
                            @ExampleObject(name = "User", value = "User"),
                            @ExampleObject(name = "PurchaseOrder", value = "PurchaseOrder"),
                            @ExampleObject(name = "SparePart", value = "SparePart")
                    })
            @PathVariable String entityName,
            @Parameter(description = "Entity database ID", required = true, example = "1")
            @PathVariable Long entityId) {
        return ResponseEntity.ok(auditLogService.getEntityHistory(entityName, entityId));
    }
}
