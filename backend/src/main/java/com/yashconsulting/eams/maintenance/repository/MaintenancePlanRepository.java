package com.yashconsulting.eams.maintenance.repository;

import com.yashconsulting.eams.maintenance.entity.MaintenancePlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MaintenancePlanRepository extends JpaRepository<MaintenancePlan, Long>, JpaSpecificationExecutor<MaintenancePlan> {

    boolean existsByPlanCode(String planCode);

    boolean existsByPlanCodeAndIdNot(String planCode, Long id);

    Optional<MaintenancePlan> findByPlanCodeAndActiveTrue(String planCode);

    Page<MaintenancePlan> findAllByActiveTrue(Pageable pageable);

    @Query("SELECT p.status, COUNT(p) FROM MaintenancePlan p WHERE p.active = true GROUP BY p.status")
    List<Object[]> countByStatus();

    @Query("SELECT p.priority, COUNT(p) FROM MaintenancePlan p WHERE p.active = true GROUP BY p.priority")
    List<Object[]> countByPriority();

    @Query("SELECT p FROM MaintenancePlan p WHERE p.active = true AND p.nextMaintenanceDate >= :today AND p.nextMaintenanceDate <= :endDate AND p.status IN (com.yashconsulting.eams.maintenance.entity.MaintenanceStatus.SCHEDULED, com.yashconsulting.eams.maintenance.entity.MaintenanceStatus.IN_PROGRESS, com.yashconsulting.eams.maintenance.entity.MaintenanceStatus.OVERDUE)")
    List<MaintenancePlan> findUpcomingMaintenance(@Param("today") LocalDate today, @Param("endDate") LocalDate endDate);

    @Query("SELECT p FROM MaintenancePlan p WHERE p.active = true AND p.nextMaintenanceDate < :today AND p.status != com.yashconsulting.eams.maintenance.entity.MaintenanceStatus.COMPLETED AND p.status != com.yashconsulting.eams.maintenance.entity.MaintenanceStatus.CANCELLED")
    List<MaintenancePlan> findOverdueMaintenance(@Param("today") LocalDate today);

    @Query("SELECT p FROM MaintenancePlan p WHERE p.active = true AND p.lastMaintenanceDate >= :startOfMonth AND p.lastMaintenanceDate <= :endOfMonth")
    List<MaintenancePlan> findCompletedWithinPeriod(@Param("startOfMonth") LocalDate startOfMonth, @Param("endOfMonth") LocalDate endOfMonth);

    @Query("SELECT COUNT(mp) FROM MaintenancePlan mp WHERE mp.active = true AND mp.nextMaintenanceDate <= :targetDate")
    long countUpcomingMaintenance(@Param("targetDate") LocalDate targetDate);
}
