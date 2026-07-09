package com.yashconsulting.eams.purchase.repository;

import com.yashconsulting.eams.purchase.entity.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import com.yashconsulting.eams.report.dto.VendorPerformanceResponse;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long>, JpaSpecificationExecutor<PurchaseOrder> {

    boolean existsByPoNumber(String poNumber);

    Optional<PurchaseOrder> findByPoNumberAndActiveTrue(String poNumber);

    Page<PurchaseOrder> findAllByActiveTrue(Pageable pageable);

    @Query("SELECT po.status, COUNT(po) FROM PurchaseOrder po WHERE po.active = true GROUP BY po.status")
    List<Object[]> countPurchaseOrdersByStatus();

    @Query("SELECT new com.yashconsulting.eams.report.dto.VendorPerformanceResponse(" +
           "v.id, v.vendorCode, v.vendorName, COUNT(po), " +
           "COALESCE(SUM(CASE WHEN po.status = com.yashconsulting.eams.purchase.entity.PurchaseOrderStatus.RECEIVED THEN po.totalAmount ELSE 0.0 END), 0.0)) " +
           "FROM Vendor v LEFT JOIN PurchaseOrder po ON v.id = po.vendorId " +
           "WHERE v.active = true " +
           "GROUP BY v.id, v.vendorCode, v.vendorName")
    List<VendorPerformanceResponse> getVendorPerformanceMetrics();
}
