package com.yashconsulting.eams.document.service;

import com.yashconsulting.eams.document.dto.DocumentResponse;
import com.yashconsulting.eams.document.dto.DocumentSearchRequest;
import com.yashconsulting.eams.document.entity.Document;
import com.yashconsulting.eams.document.repository.DocumentRepository;
import com.yashconsulting.eams.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentServiceImpl implements DocumentService {

    private static final Set<String> ALLOWED_REFERENCE_TYPES = Set.of(
            "ASSET", "WORK_ORDER", "PURCHASE_ORDER", "MAINTENANCE_PLAN"
    );

    private final DocumentRepository documentRepository;
    private final StorageService storageService;

    @Override
    @Transactional
    public DocumentResponse uploadDocument(MultipartFile file, String referenceType, Long referenceId) {
        log.info("Uploading document for {} with ID: {}", referenceType, referenceId);

        validateReferenceType(referenceType);

        String storagePath = storageService.store(file, referenceType, referenceId);

        Document document = Document.builder()
                .fileName(storagePath.substring(storagePath.lastIndexOf("/") + 1))
                .originalFileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .storagePath(storagePath)
                .referenceType(referenceType.toUpperCase())
                .referenceId(referenceId)
                .build();

        Document saved = documentRepository.save(document);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource downloadDocument(Long id) {
        log.info("Downloading document with ID: {}", id);
        Document document = getDocumentOrThrow(id);
        return storageService.load(document.getStoragePath());
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentResponse getDocumentMetadata(Long id) {
        log.info("Fetching document metadata for ID: {}", id);
        Document document = getDocumentOrThrow(id);
        return toResponse(document);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentResponse> searchDocuments(DocumentSearchRequest request) {
        log.info("Searching documents");

        Sort sort = request.getSortDirection().equalsIgnoreCase("DESC")
                ? Sort.by(request.getSortBy()).descending()
                : Sort.by(request.getSortBy()).ascending();

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Specification<Document> spec = Specification.where(activeSpec());

        if (request.getReferenceType() != null && !request.getReferenceType().isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("referenceType"), request.getReferenceType().toUpperCase()));
        }
        if (request.getReferenceId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("referenceId"), request.getReferenceId()));
        }
        if (request.getContentType() != null && !request.getContentType().isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("contentType")), "%" + request.getContentType().toLowerCase() + "%"));
        }
        if (request.getOriginalFileName() != null && !request.getOriginalFileName().isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("originalFileName")), "%" + request.getOriginalFileName().toLowerCase() + "%"));
        }

        return documentRepository.findAll(spec, pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> getDocumentsByReference(String referenceType, Long referenceId) {
        log.info("Fetching documents for {} with ID: {}", referenceType, referenceId);
        validateReferenceType(referenceType);
        return documentRepository.findAllByReferenceTypeAndReferenceIdAndActiveTrue(referenceType.toUpperCase(), referenceId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteDocument(Long id) {
        log.info("Deleting document with ID: {}", id);
        Document document = getDocumentOrThrow(id);

        // Delete physical file
        storageService.delete(document.getStoragePath());

        // Soft delete record
        document.setActive(false);
        documentRepository.save(document);
    }

    private void validateReferenceType(String referenceType) {
        if (referenceType == null || !ALLOWED_REFERENCE_TYPES.contains(referenceType.toUpperCase())) {
            throw new IllegalArgumentException(
                    "Invalid reference type: " + referenceType + ". Allowed: " + ALLOWED_REFERENCE_TYPES);
        }
    }

    private Document getDocumentOrThrow(Long id) {
        return documentRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + id));
    }

    private Specification<Document> activeSpec() {
        return (root, query, cb) -> cb.isTrue(root.get("active"));
    }

    private DocumentResponse toResponse(Document document) {
        return DocumentResponse.builder()
                .id(document.getId())
                .fileName(document.getFileName())
                .originalFileName(document.getOriginalFileName())
                .contentType(document.getContentType())
                .fileSize(document.getFileSize())
                .referenceType(document.getReferenceType())
                .referenceId(document.getReferenceId())
                .uploadedBy(document.getUploadedBy())
                .uploadedAt(document.getUploadedAt())
                .build();
    }
}
