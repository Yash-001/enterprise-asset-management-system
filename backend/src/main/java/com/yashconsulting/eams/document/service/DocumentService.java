package com.yashconsulting.eams.document.service;

import com.yashconsulting.eams.document.dto.DocumentResponse;
import com.yashconsulting.eams.document.dto.DocumentSearchRequest;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {

    DocumentResponse uploadDocument(MultipartFile file, String referenceType, Long referenceId);

    Resource downloadDocument(Long id);

    DocumentResponse getDocumentMetadata(Long id);

    Page<DocumentResponse> searchDocuments(DocumentSearchRequest request);

    List<DocumentResponse> getDocumentsByReference(String referenceType, Long referenceId);

    void deleteDocument(Long id);
}
