package com.yashconsulting.eams.audit.annotation;

import com.yashconsulting.eams.audit.entity.AuditAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a service method for automatic audit logging via AOP.
 * 
 * Usage:
 *   @Auditable(entity = "Asset", action = AuditAction.CREATE)
 *   public AssetResponse createAsset(AssetCreateRequest request) { ... }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {

    /**
     * The entity name being audited (e.g., "User", "Asset", "WorkOrder").
     */
    String entity();

    /**
     * The CRUD action being performed.
     */
    AuditAction action();
}
