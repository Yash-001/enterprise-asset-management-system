package com.yashconsulting.eams.audit.service;

import com.yashconsulting.eams.audit.dto.AuditLogResponse;
import com.yashconsulting.eams.audit.dto.AuditLogSearchRequest;
import com.yashconsulting.eams.audit.entity.AuditAction;
import com.yashconsulting.eams.audit.entity.AuditLog;
import com.yashconsulting.eams.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String entityName, AuditAction action, Long entityId,
                    String beforeValue, String afterValue, String performedBy, String ipAddress) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .entityName(entityName)
                    .action(action)
                    .entityId(entityId)
                    .beforeValue(beforeValue)
                    .afterValue(afterValue)
                    .performedBy(performedBy != null ? performedBy : "SYSTEM")
                    .performedAt(LocalDateTime.now())
                    .ipAddress(ipAddress)
                    .build();

            auditLogRepository.save(auditLog);
            log.debug("Audit log saved: {} {} on {} ID:{}", performedBy, action, entityName, entityId);
        } catch (Exception e) {
            log.error("Failed to save audit log for {} {} on {} ID:{}", 
                    performedBy, action, entityName, entityId, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> search(AuditLogSearchRequest request) {
        Sort sort = request.getSortDirection().equalsIgnoreCase("DESC")
                ? Sort.by(request.getSortBy()).descending()
                : Sort.by(request.getSortBy()).ascending();

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Specification<AuditLog> spec = Specification.where(null);

        if (request.getEntityName() != null && !request.getEntityName().isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("entityName"), request.getEntityName()));
        }
        if (request.getEntityId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("entityId"), request.getEntityId()));
        }
        if (request.getAction() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("action"), request.getAction()));
        }
        if (request.getPerformedBy() != null && !request.getPerformedBy().isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("performedBy")), "%" + request.getPerformedBy().toLowerCase() + "%"));
        }
        if (request.getFromDate() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("performedAt"), request.getFromDate()));
        }
        if (request.getToDate() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("performedAt"), request.getToDate()));
        }

        return auditLogRepository.findAll(spec, pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getEntityHistory(String entityName, Long entityId) {
        return auditLogRepository.findByEntityNameAndEntityIdOrderByPerformedAtDesc(entityName, entityId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private AuditLogResponse toResponse(AuditLog entity) {
        return AuditLogResponse.builder()
                .id(entity.getId())
                .entityName(entity.getEntityName())
                .action(entity.getAction())
                .beforeValue(entity.getBeforeValue())
                .afterValue(entity.getAfterValue())
                .performedBy(entity.getPerformedBy())
                .performedAt(entity.getPerformedAt())
                .ipAddress(entity.getIpAddress())
                .entityId(entity.getEntityId())
                .build();
    }
}
