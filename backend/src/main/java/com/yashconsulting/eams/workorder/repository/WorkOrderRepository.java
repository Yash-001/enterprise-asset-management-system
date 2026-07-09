package com.yashconsulting.eams.workorder.repository;

import com.yashconsulting.eams.workorder.entity.WorkOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long>, JpaSpecificationExecutor<WorkOrder> {

    boolean existsByWorkOrderNumber(String workOrderNumber);

    boolean existsByWorkOrderNumberAndIdNot(String workOrderNumber, Long id);

    Optional<WorkOrder> findByWorkOrderNumberAndActiveTrue(String workOrderNumber);

    Page<WorkOrder> findAllByActiveTrue(Pageable pageable);
}
