package com.yashconsulting.eams.document.controller;

import com.yashconsulting.eams.document.dto.DocumentResponse;
import com.yashconsulting.eams.document.dto.DocumentSearchRequest;
import com.yashconsulting.eams.document.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Document Management", description = "APIs for uploading, downloading, and managing file attachments linked to assets, work orders, purchase orders, and maintenance plans")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Upload a document", description = "Uploads a file and links it to a reference entity (ASSET, WORK_ORDER, PURCHASE_ORDER, MAINTENANCE_PLAN)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Document uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or empty file")
    })
    public ResponseEntity<DocumentResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("referenceType") @NotBlank String referenceType,
            @RequestParam("referenceId") @NotNull Long referenceId) {

        DocumentResponse response = documentService.uploadDocument(file, referenceType, referenceId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Download a document", description = "Downloads the file by document ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File downloaded successfully"),
            @ApiResponse(responseCode = "404", description = "Document not found")
    })
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) {
        DocumentResponse metadata = documentService.getDocumentMetadata(id);
        Resource resource = documentService.downloadDocument(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(metadata.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + metadata.getOriginalFileName() + "\"")
                .body(resource);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Get document metadata", description = "Returns metadata for a document without downloading the file")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Metadata retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Document not found")
    })
    public ResponseEntity<DocumentResponse> getDocumentMetadata(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocumentMetadata(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Search documents", description = "Search and filter documents with pagination")
    public ResponseEntity<Page<DocumentResponse>> searchDocuments(DocumentSearchRequest request) {
        return ResponseEntity.ok(documentService.searchDocuments(request));
    }

    @GetMapping("/by-reference")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Get documents by reference", description = "Returns all documents linked to a specific entity")
    public ResponseEntity<List<DocumentResponse>> getDocumentsByReference(
            @RequestParam("referenceType") @NotBlank String referenceType,
            @RequestParam("referenceId") @NotNull Long referenceId) {
        return ResponseEntity.ok(documentService.getDocumentsByReference(referenceType, referenceId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Delete a document", description = "Deletes the physical file and soft-deletes the metadata record")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Document deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Document not found")
    })
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
