package com.yashconsulting.eams.workorder.repository;

import com.yashconsulting.eams.workorder.entity.WorkOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long>, JpaSpecificationExecutor<WorkOrder> {

    boolean existsByWorkOrderNumber(String workOrderNumber);

    boolean existsByWorkOrderNumberAndIdNot(String workOrderNumber, Long id);

    Optional<WorkOrder> findByWorkOrderNumberAndActiveTrue(String workOrderNumber);

    Page<WorkOrder> findAllByActiveTrue(Pageable pageable);

    @Query("SELECT COUNT(wo) FROM WorkOrder wo WHERE wo.active = true AND wo.status = com.yashconsulting.eams.workorder.entity.WorkOrderStatus.COMPLETED")
    long countCompletedWorkOrders();

    @Query("SELECT COUNT(wo) FROM WorkOrder wo WHERE wo.active = true AND wo.status IN (com.yashconsulting.eams.workorder.entity.WorkOrderStatus.REQUESTED, com.yashconsulting.eams.workorder.entity.WorkOrderStatus.ASSIGNED, com.yashconsulting.eams.workorder.entity.WorkOrderStatus.IN_PROGRESS)")
    long countOpenWorkOrders();

    @Query("SELECT wo FROM WorkOrder wo WHERE wo.active = true AND wo.scheduledDate < :today AND wo.status IN (com.yashconsulting.eams.workorder.entity.WorkOrderStatus.REQUESTED, com.yashconsulting.eams.workorder.entity.WorkOrderStatus.ASSIGNED, com.yashconsulting.eams.workorder.entity.WorkOrderStatus.IN_PROGRESS)")
    java.util.List<WorkOrder> findOverdueWorkOrders(@org.springframework.data.repository.query.Param("today") java.time.LocalDate today);
}
