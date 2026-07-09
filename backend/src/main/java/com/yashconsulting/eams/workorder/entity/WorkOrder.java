package com.yashconsulting.eams.workorder.entity;

import com.yashconsulting.eams.maintenance.entity.MaintenancePriority;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_orders")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "work_order_number", nullable = false, unique = true, length = 100)
    private String workOrderNumber;

    @Column(name = "asset_id", nullable = false)
    private Long assetId;

    @Column(name = "maintenance_plan_id")
    private Long maintenancePlanId;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "assigned_technician", length = 150)
    private String assignedTechnician;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 50)
    private MaintenancePriority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private WorkOrderStatus status = WorkOrderStatus.REQUESTED;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "completion_date")
    private LocalDate completionDate;

    @Column(name = "estimated_hours")
    private BigDecimal estimatedHours;

    @Column(name = "actual_hours")
    private BigDecimal actualHours;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", nullable = false)
    private String updatedBy;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;
}
