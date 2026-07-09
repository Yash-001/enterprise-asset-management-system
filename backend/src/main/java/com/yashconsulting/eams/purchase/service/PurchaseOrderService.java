package com.yashconsulting.eams.purchase.service;

import com.yashconsulting.eams.purchase.dto.PurchaseOrderCreateRequest;
import com.yashconsulting.eams.purchase.dto.PurchaseOrderResponse;
import com.yashconsulting.eams.purchase.dto.PurchaseOrderSearchRequest;
import com.yashconsulting.eams.purchase.dto.PurchaseOrderUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PurchaseOrderService {

    PurchaseOrderResponse createPurchaseOrder(PurchaseOrderCreateRequest request);

    PurchaseOrderResponse updatePurchaseOrder(Long id, PurchaseOrderUpdateRequest request);

    PurchaseOrderResponse getPurchaseOrderById(Long id);

    Page<PurchaseOrderResponse> getAllPurchaseOrders(Pageable pageable, boolean includeInactive);

    Page<PurchaseOrderResponse> searchPurchaseOrders(PurchaseOrderSearchRequest request);

    void deletePurchaseOrder(Long id);
}
