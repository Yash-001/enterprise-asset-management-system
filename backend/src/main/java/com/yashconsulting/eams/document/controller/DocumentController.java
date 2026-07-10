package com.yashconsulting.eams.document.controller;

import com.yashconsulting.eams.document.dto.DocumentResponse;
import com.yashconsulting.eams.document.dto.DocumentSearchRequest;
import com.yashconsulting.eams.document.service.DocumentService;
import com.yashconsulting.eams.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Tag(name = "Document Management", description = """
        APIs for uploading, downloading, and managing file attachments.
        Documents can be linked to Assets, Work Orders, Purchase Orders, and Maintenance Plans.
        Supported reference types: `ASSET`, `WORK_ORDER`, `PURCHASE_ORDER`, `MAINTENANCE_PLAN`.
        Maximum file size: 50MB.
        """)
@SecurityRequirement(name = "Bearer JWT")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(
            summary = "Upload a document",
            description = """
                    Uploads a file and links it to a reference entity.
                    The file is stored on the local filesystem (cloud storage can be configured).
                    Allowed reference types: ASSET, WORK_ORDER, PURCHASE_ORDER, MAINTENANCE_PLAN.
                    Requires any authenticated role.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Document uploaded successfully",
                    content = @Content(schema = @Schema(implementation = DocumentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request — empty file, missing params, or invalid reference type",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized — invalid or missing JWT token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden — insufficient role", content = @Content)
    })
    public ResponseEntity<DocumentResponse> uploadDocument(
            @Parameter(description = "File to upload (max 50MB)", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Entity type to link the document to", required = true,
                    examples = {
                            @ExampleObject(name = "Asset", value = "ASSET"),
                            @ExampleObject(name = "Work Order", value = "WORK_ORDER"),
                            @ExampleObject(name = "Purchase Order", value = "PURCHASE_ORDER"),
                            @ExampleObject(name = "Maintenance Plan", value = "MAINTENANCE_PLAN")
                    })
            @RequestParam("referenceType") @NotBlank String referenceType,
            @Parameter(description = "ID of the entity to link the document to", required = true, example = "1")
            @RequestParam("referenceId") @NotNull Long referenceId) {

        DocumentResponse response = documentService.uploadDocument(file, referenceType, referenceId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(
            summary = "Download a document",
            description = "Downloads the physical file by document ID. Returns the file with original content type and filename."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File downloaded successfully",
                    content = @Content(mediaType = "application/octet-stream")),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Document not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Resource> downloadDocument(
            @Parameter(description = "ID of the document to download", required = true, example = "1")
            @PathVariable Long id) {
        DocumentResponse metadata = documentService.getDocumentMetadata(id);
        Resource resource = documentService.downloadDocument(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(metadata.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + metadata.getOriginalFileName() + "\"")
                .body(resource);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(
            summary = "Get document metadata",
            description = "Returns metadata for a document (filename, size, type, upload info) without downloading the file."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Metadata retrieved successfully",
                    content = @Content(schema = @Schema(implementation = DocumentResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Document not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<DocumentResponse> getDocumentMetadata(
            @Parameter(description = "ID of the document", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocumentMetadata(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(
            summary = "Search documents",
            description = "Search and filter documents with pagination. Filter by reference type, reference ID, content type, or filename."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search results returned successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    public ResponseEntity<Page<DocumentResponse>> searchDocuments(DocumentSearchRequest request) {
        return ResponseEntity.ok(documentService.searchDocuments(request));
    }

    @GetMapping("/by-reference")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(
            summary = "Get documents by reference entity",
            description = "Returns all active documents linked to a specific entity (e.g., all documents for Asset ID 5)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Documents retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid reference type",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    public ResponseEntity<List<DocumentResponse>> getDocumentsByReference(
            @Parameter(description = "Entity type", required = true, example = "ASSET")
            @RequestParam("referenceType") @NotBlank String referenceType,
            @Parameter(description = "Entity ID", required = true, example = "5")
            @RequestParam("referenceId") @NotNull Long referenceId) {
        return ResponseEntity.ok(documentService.getDocumentsByReference(referenceType, referenceId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(
            summary = "Delete a document",
            description = "Deletes the physical file from storage and soft-deletes the metadata record. Requires ADMIN or MANAGER role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Document deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden — requires ADMIN or MANAGER role", content = @Content),
            @ApiResponse(responseCode = "404", description = "Document not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteDocument(
            @Parameter(description = "ID of the document to delete", required = true, example = "1")
            @PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
