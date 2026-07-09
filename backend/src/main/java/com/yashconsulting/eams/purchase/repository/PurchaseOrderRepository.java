package com.yashconsulting.eams.purchase.repository;

import com.yashconsulting.eams.purchase.entity.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long>, JpaSpecificationExecutor<PurchaseOrder> {

    boolean existsByPoNumber(String poNumber);

    Optional<PurchaseOrder> findByPoNumberAndActiveTrue(String poNumber);

    Page<PurchaseOrder> findAllByActiveTrue(Pageable pageable);
}
