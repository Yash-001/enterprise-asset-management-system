package com.yashconsulting.eams.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yashconsulting.eams.BaseIntegrationTest;
import com.yashconsulting.eams.auth.dto.LoginRequest;
import com.yashconsulting.eams.auth.dto.LoginResponse;
import com.yashconsulting.eams.document.entity.Document;
import com.yashconsulting.eams.document.repository.DocumentRepository;
import com.yashconsulting.eams.security.Role;
import com.yashconsulting.eams.user.entity.User;
import com.yashconsulting.eams.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DocumentIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        documentRepository.deleteAll();
        userRepository.deleteAll();

        User admin = User.builder()
                .firstName("Admin")
                .lastName("Test")
                .email("admin@eams.com")
                .password(passwordEncoder.encode("Admin@123"))
                .role(Role.ADMIN)
                .active(true)
                .build();
        userRepository.save(admin);

        adminToken = obtainAccessToken("admin@eams.com", "Admin@123");
    }

    @Test
    void uploadDocument_validFile_returns201() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "report.pdf", "application/pdf", "PDF file content".getBytes());

        mockMvc.perform(multipart("/api/v1/documents")
                        .file(file)
                        .param("referenceType", "ASSET")
                        .param("referenceId", "1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.originalFileName", is("report.pdf")))
                .andExpect(jsonPath("$.contentType", is("application/pdf")))
                .andExpect(jsonPath("$.referenceType", is("ASSET")))
                .andExpect(jsonPath("$.referenceId", is(1)));
    }

    @Test
    void uploadDocument_invalidReferenceType_returns400() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "content".getBytes());

        mockMvc.perform(multipart("/api/v1/documents")
                        .file(file)
                        .param("referenceType", "INVALID")
                        .param("referenceId", "1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void uploadDocument_noAuth_returns403() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "content".getBytes());

        mockMvc.perform(multipart("/api/v1/documents")
                        .file(file)
                        .param("referenceType", "ASSET")
                        .param("referenceId", "1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getDocumentMetadata_existing_returns200() throws Exception {
        // Upload first
        MockMultipartFile file = new MockMultipartFile(
                "file", "spec.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "Word content".getBytes());

        MvcResult uploadResult = mockMvc.perform(multipart("/api/v1/documents")
                        .file(file)
                        .param("referenceType", "WORK_ORDER")
                        .param("referenceId", "5")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andReturn();

        Long docId = objectMapper.readTree(uploadResult.getResponse().getContentAsString()).get("id").asLong();

        // Get metadata
        mockMvc.perform(get("/api/v1/documents/" + docId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(docId.intValue())))
                .andExpect(jsonPath("$.originalFileName", is("spec.docx")))
                .andExpect(jsonPath("$.referenceType", is("WORK_ORDER")));
    }

    @Test
    void getDocumentMetadata_nonExisting_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/documents/99999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void downloadDocument_existing_returnsFile() throws Exception {
        byte[] content = "File binary content for download test".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file", "download-test.txt", "text/plain", content);

        MvcResult uploadResult = mockMvc.perform(multipart("/api/v1/documents")
                        .file(file)
                        .param("referenceType", "MAINTENANCE_PLAN")
                        .param("referenceId", "3")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andReturn();

        Long docId = objectMapper.readTree(uploadResult.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(get("/api/v1/documents/" + docId + "/download")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("download-test.txt")))
                .andExpect(content().bytes(content));
    }

    @Test
    void deleteDocument_existing_returns204() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "to-delete.pdf", "application/pdf", "delete me".getBytes());

        MvcResult uploadResult = mockMvc.perform(multipart("/api/v1/documents")
                        .file(file)
                        .param("referenceType", "PURCHASE_ORDER")
                        .param("referenceId", "7")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andReturn();

        Long docId = objectMapper.readTree(uploadResult.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(delete("/api/v1/documents/" + docId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        // Verify soft-deleted
        mockMvc.perform(get("/api/v1/documents/" + docId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getDocumentsByReference_returnsLinkedDocuments() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile("file", "a.pdf", "application/pdf", "a".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("file", "b.pdf", "application/pdf", "b".getBytes());

        mockMvc.perform(multipart("/api/v1/documents")
                        .file(file1).param("referenceType", "ASSET").param("referenceId", "10")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated());

        mockMvc.perform(multipart("/api/v1/documents")
                        .file(file2).param("referenceType", "ASSET").param("referenceId", "10")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/documents/by-reference")
                        .param("referenceType", "ASSET")
                        .param("referenceId", "10")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    private String obtainAccessToken(String email, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest(email, password);
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), LoginResponse.class).getAccessToken();
    }
}
