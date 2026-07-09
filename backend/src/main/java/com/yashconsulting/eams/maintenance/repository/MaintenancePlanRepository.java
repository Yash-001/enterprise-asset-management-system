package com.yashconsulting.eams.maintenance.repository;

import com.yashconsulting.eams.maintenance.entity.MaintenancePlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MaintenancePlanRepository extends JpaRepository<MaintenancePlan, Long>, JpaSpecificationExecutor<MaintenancePlan> {

    boolean existsByPlanCode(String planCode);

    boolean existsByPlanCodeAndIdNot(String planCode, Long id);

    Optional<MaintenancePlan> findByPlanCodeAndActiveTrue(String planCode);

    Page<MaintenancePlan> findAllByActiveTrue(Pageable pageable);
}
