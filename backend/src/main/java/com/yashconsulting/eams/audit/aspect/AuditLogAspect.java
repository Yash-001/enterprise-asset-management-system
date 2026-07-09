package com.yashconsulting.eams.audit.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.yashconsulting.eams.audit.annotation.Auditable;
import com.yashconsulting.eams.audit.entity.AuditAction;
import com.yashconsulting.eams.audit.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogAspect {

    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Around("@annotation(auditable)")
    public Object audit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        String entityName = auditable.entity();
        AuditAction action = auditable.action();

        String performedBy = getCurrentUser();
        String ipAddress = getClientIpAddress();

        String beforeValue = null;
        Long entityId = null;

        // For UPDATE and DELETE, capture before-state
        if (action == AuditAction.UPDATE || action == AuditAction.DELETE) {
            entityId = extractEntityId(joinPoint.getArgs());
            beforeValue = captureBeforeState(joinPoint);
        }

        // Execute the target method
        Object result = joinPoint.proceed();

        // Capture after-state
        String afterValue = null;
        if (result != null && (action == AuditAction.CREATE || action == AuditAction.UPDATE)) {
            afterValue = serialize(result);
            if (action == AuditAction.CREATE) {
                entityId = extractIdFromResult(result);
            }
        }

        // For DELETE, entityId comes from method args
        if (action == AuditAction.DELETE && entityId == null) {
            entityId = extractEntityId(joinPoint.getArgs());
        }

        // Log asynchronously
        auditLogService.log(entityName, action, entityId, beforeValue, afterValue, performedBy, ipAddress);

        return result;
    }

    private String getCurrentUser() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                return auth.getName();
            }
        } catch (Exception e) {
            log.debug("Could not resolve current user: {}", e.getMessage());
        }
        return "SYSTEM";
    }

    private String getClientIpAddress() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isBlank()) {
                    return xForwardedFor.split(",")[0].trim();
                }
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            log.debug("Could not resolve IP address: {}", e.getMessage());
        }
        return null;
    }

    private Long extractEntityId(Object[] args) {
        if (args != null && args.length > 0 && args[0] instanceof Long) {
            return (Long) args[0];
        }
        return null;
    }

    private Long extractIdFromResult(Object result) {
        try {
            var method = result.getClass().getMethod("getId");
            Object id = method.invoke(result);
            if (id instanceof Long) {
                return (Long) id;
            }
        } catch (Exception e) {
            log.debug("Could not extract ID from result: {}", e.getMessage());
        }
        return null;
    }

    private String captureBeforeState(ProceedingJoinPoint joinPoint) {
        // Before state is captured from the method arguments context
        // For service methods with (Long id, Request dto), the first arg is the ID
        // The actual before-state would ideally be fetched from DB, but to keep AOP lightweight,
        // we serialize the input request as context
        try {
            Object[] args = joinPoint.getArgs();
            if (args.length > 1) {
                return serialize(args[1]);
            } else if (args.length == 1) {
                return serialize(args[0]);
            }
        } catch (Exception e) {
            log.debug("Could not capture before state: {}", e.getMessage());
        }
        return null;
    }

    private String serialize(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.debug("Could not serialize object: {}", e.getMessage());
            return obj.toString();
        }
    }
}
