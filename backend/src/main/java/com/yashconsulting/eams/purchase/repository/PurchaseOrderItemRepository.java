package com.yashconsulting.eams.purchase.repository;

import com.yashconsulting.eams.purchase.entity.PurchaseOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Long> {

    List<PurchaseOrderItem> findAllByPurchaseOrderId(Long purchaseOrderId);
}
