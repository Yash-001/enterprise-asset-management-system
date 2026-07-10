package com.yashconsulting.eams.document.service;

import com.yashconsulting.eams.document.dto.DocumentResponse;
import com.yashconsulting.eams.document.entity.Document;
import com.yashconsulting.eams.document.repository.DocumentRepository;
import com.yashconsulting.eams.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceImplUnitTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private DocumentServiceImpl documentService;

    @Test
    void uploadDocument_validRequest_succeeds() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "report.pdf", "application/pdf", "file-content".getBytes());

        Document saved = Document.builder()
                .id(1L)
                .fileName("uuid.pdf")
                .originalFileName("report.pdf")
                .contentType("application/pdf")
                .fileSize(12L)
                .storagePath("asset/1/uuid.pdf")
                .referenceType("ASSET")
                .referenceId(1L)
                .build();

        when(storageService.store(file, "ASSET", 1L)).thenReturn("asset/1/uuid.pdf");
        when(documentRepository.save(any(Document.class))).thenReturn(saved);

        DocumentResponse result = documentService.uploadDocument(file, "ASSET", 1L);

        assertNotNull(result);
        assertEquals("report.pdf", result.getOriginalFileName());
        assertEquals("ASSET", result.getReferenceType());
        verify(storageService).store(file, "ASSET", 1L);
        verify(documentRepository).save(any(Document.class));
    }

    @Test
    void uploadDocument_invalidReferenceType_throwsIllegalArgumentException() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "content".getBytes());

        assertThrows(IllegalArgumentException.class,
                () -> documentService.uploadDocument(file, "INVALID_TYPE", 1L));
        verify(storageService, never()).store(any(), any(), any());
    }

    @Test
    void uploadDocument_nullReferenceType_throwsIllegalArgumentException() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "content".getBytes());

        assertThrows(IllegalArgumentException.class,
                () -> documentService.uploadDocument(file, null, 1L));
    }

    @Test
    void downloadDocument_existing_returnsResource() {
        Document document = Document.builder()
                .id(1L).storagePath("asset/1/uuid.pdf").active(true).build();
        Resource mockResource = mock(Resource.class);

        when(documentRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(document));
        when(storageService.load("asset/1/uuid.pdf")).thenReturn(mockResource);

        Resource result = documentService.downloadDocument(1L);

        assertNotNull(result);
        verify(storageService).load("asset/1/uuid.pdf");
    }

    @Test
    void downloadDocument_nonExisting_throwsResourceNotFoundException() {
        when(documentRepository.findByIdAndActiveTrue(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> documentService.downloadDocument(99L));
    }

    @Test
    void getDocumentMetadata_existing_returnsResponse() {
        Document document = Document.builder()
                .id(1L).fileName("uuid.pdf").originalFileName("report.pdf")
                .contentType("application/pdf").fileSize(1024L)
                .referenceType("WORK_ORDER").referenceId(5L).active(true).build();

        when(documentRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(document));

        DocumentResponse result = documentService.getDocumentMetadata(1L);

        assertNotNull(result);
        assertEquals("report.pdf", result.getOriginalFileName());
        assertEquals("WORK_ORDER", result.getReferenceType());
    }

    @Test
    void deleteDocument_existing_deletesFileAndSoftDeletes() {
        Document document = Document.builder()
                .id(1L).storagePath("asset/1/uuid.pdf").active(true).build();

        when(documentRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(document));

        documentService.deleteDocument(1L);

        assertFalse(document.isActive());
        verify(storageService).delete("asset/1/uuid.pdf");
        verify(documentRepository).save(document);
    }

    @Test
    void getDocumentsByReference_validType_returnsList() {
        Document doc1 = Document.builder().id(1L).fileName("a.pdf").originalFileName("a.pdf")
                .contentType("application/pdf").fileSize(100L)
                .referenceType("ASSET").referenceId(1L).active(true).build();

        when(documentRepository.findAllByReferenceTypeAndReferenceIdAndActiveTrue("ASSET", 1L))
                .thenReturn(List.of(doc1));

        List<DocumentResponse> result = documentService.getDocumentsByReference("ASSET", 1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getDocumentsByReference_invalidType_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> documentService.getDocumentsByReference("UNKNOWN", 1L));
    }
}
