package com.yashconsulting.eams.audit.service;

import com.yashconsulting.eams.audit.dto.AuditLogResponse;
import com.yashconsulting.eams.audit.dto.AuditLogSearchRequest;
import com.yashconsulting.eams.audit.entity.AuditAction;
import com.yashconsulting.eams.audit.entity.AuditLog;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AuditLogService {

    void log(String entityName, AuditAction action, Long entityId, String beforeValue, String afterValue, String performedBy, String ipAddress);

    Page<AuditLogResponse> search(AuditLogSearchRequest request);

    List<AuditLogResponse> getEntityHistory(String entityName, Long entityId);
}
