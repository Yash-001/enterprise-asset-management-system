package com.yashconsulting.eams.maintenance.entity;

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
@Table(name = "maintenance_plans")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenancePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "asset_id", nullable = false)
    private Long assetId;

    @Column(name = "plan_code", nullable = false, unique = true, length = 100)
    private String planCode;

    @Column(name = "plan_name", nullable = false, length = 150)
    private String planName;

    @Column(name = "description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "maintenance_type", nullable = false, length = 50)
    private MaintenanceType maintenanceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency_type", nullable = false, length = 50)
    private FrequencyType frequencyType;

    @Column(name = "frequency_value", nullable = false)
    private Integer frequencyValue;

    @Column(name = "next_maintenance_date", nullable = false)
    private LocalDate nextMaintenanceDate;

    @Column(name = "last_maintenance_date")
    private LocalDate lastMaintenanceDate;

    @Column(name = "estimated_duration_hours")
    private BigDecimal estimatedDurationHours;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 50)
    private MaintenancePriority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private MaintenanceStatus status = MaintenanceStatus.SCHEDULED;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", length = 100)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", length = 100)
    private String updatedBy;
}
