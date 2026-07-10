package com.yashconsulting.eams.audit.service;

import com.yashconsulting.eams.audit.dto.AuditLogResponse;
import com.yashconsulting.eams.audit.dto.AuditLogSearchRequest;
import com.yashconsulting.eams.audit.entity.AuditAction;
import com.yashconsulting.eams.audit.entity.AuditLog;
import com.yashconsulting.eams.audit.repository.AuditLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceImplUnitTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogServiceImpl auditLogService;

    @Test
    void log_validParams_savesAuditLog() {
        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(inv -> inv.getArgument(0));

        auditLogService.log("Asset", AuditAction.CREATE, 1L, null, "{\"id\":1}", "admin@test.com", "192.168.1.1");

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());

        AuditLog saved = captor.getValue();
        assertEquals("Asset", saved.getEntityName());
        assertEquals(AuditAction.CREATE, saved.getAction());
        assertEquals(1L, saved.getEntityId());
        assertNull(saved.getBeforeValue());
        assertEquals("{\"id\":1}", saved.getAfterValue());
        assertEquals("admin@test.com", saved.getPerformedBy());
        assertEquals("192.168.1.1", saved.getIpAddress());
        assertNotNull(saved.getPerformedAt());
    }

    @Test
    void log_nullPerformedBy_defaultsToSystem() {
        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(inv -> inv.getArgument(0));

        auditLogService.log("User", AuditAction.DELETE, 5L, "{}", null, null, null);

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());

        assertEquals("SYSTEM", captor.getValue().getPerformedBy());
    }

    @Test
    void log_repositoryThrows_doesNotPropagate() {
        when(auditLogRepository.save(any())).thenThrow(new RuntimeException("DB error"));

        // Should not throw — errors are caught silently
        assertDoesNotThrow(() ->
                auditLogService.log("Asset", AuditAction.UPDATE, 1L, "{}", "{}", "user", "127.0.0.1"));
    }

    @Test
    void search_withEntityNameFilter_returnsPagedResults() {
        AuditLog log1 = AuditLog.builder()
                .id(1L).entityName("Asset").action(AuditAction.CREATE)
                .performedBy("admin").performedAt(LocalDateTime.now()).build();

        Page<AuditLog> page = new PageImpl<>(List.of(log1));
        when(auditLogRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        AuditLogSearchRequest request = AuditLogSearchRequest.builder()
                .entityName("Asset")
                .page(0).size(20).sortBy("performedAt").sortDirection("DESC")
                .build();

        Page<AuditLogResponse> result = auditLogService.search(request);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Asset", result.getContent().get(0).getEntityName());
    }

    @Test
    void search_withAllFilters_returnsResults() {
        Page<AuditLog> page = new PageImpl<>(List.of());
        when(auditLogRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        AuditLogSearchRequest request = AuditLogSearchRequest.builder()
                .entityName("WorkOrder")
                .entityId(5L)
                .action(AuditAction.UPDATE)
                .performedBy("admin")
                .fromDate(LocalDateTime.now().minusDays(7))
                .toDate(LocalDateTime.now())
                .page(0).size(10).sortBy("performedAt").sortDirection("ASC")
                .build();

        Page<AuditLogResponse> result = auditLogService.search(request);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getEntityHistory_returnsOrderedList() {
        AuditLog log1 = AuditLog.builder()
                .id(1L).entityName("Asset").entityId(1L)
                .action(AuditAction.CREATE).performedBy("admin")
                .performedAt(LocalDateTime.now().minusHours(2)).build();
        AuditLog log2 = AuditLog.builder()
                .id(2L).entityName("Asset").entityId(1L)
                .action(AuditAction.UPDATE).performedBy("manager")
                .performedAt(LocalDateTime.now()).build();

        when(auditLogRepository.findByEntityNameAndEntityIdOrderByPerformedAtDesc("Asset", 1L))
                .thenReturn(List.of(log2, log1));

        List<AuditLogResponse> result = auditLogService.getEntityHistory("Asset", 1L);

        assertEquals(2, result.size());
        assertEquals(AuditAction.UPDATE, result.get(0).getAction());
        assertEquals(AuditAction.CREATE, result.get(1).getAction());
    }
}
