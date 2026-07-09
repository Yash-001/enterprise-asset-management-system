package com.yashconsulting.eams.purchase.service;

import com.yashconsulting.eams.purchase.dto.PurchaseOrderUpdateRequest;
import com.yashconsulting.eams.purchase.entity.PurchaseOrder;
import com.yashconsulting.eams.purchase.entity.PurchaseOrderStatus;
import com.yashconsulting.eams.purchase.repository.PurchaseOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderServiceImplUnitTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @InjectMocks
    private PurchaseOrderServiceImpl purchaseOrderService;

    @Test
    void updatePurchaseOrder_whenInvalidTransitionFromDRAFTtoRECEIVED_thenThrowsIllegalArgumentException() {
        PurchaseOrder existing = PurchaseOrder.builder()
                .id(1L)
                .status(PurchaseOrderStatus.DRAFT)
                .items(new ArrayList<>())
                .build();

        PurchaseOrderUpdateRequest request = PurchaseOrderUpdateRequest.builder()
                .status(PurchaseOrderStatus.RECEIVED) // Invalid (must be APPROVED or CANCELLED)
                .active(true)
                .items(new ArrayList<>())
                .build();

        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class, () ->
                purchaseOrderService.updatePurchaseOrder(1L, request));

        verify(purchaseOrderRepository, never()).save(any());
    }

    @Test
    void updatePurchaseOrder_whenInvalidTransitionFromCANCELLED_thenThrowsIllegalArgumentException() {
        PurchaseOrder existing = PurchaseOrder.builder()
                .id(1L)
                .status(PurchaseOrderStatus.CANCELLED)
                .items(new ArrayList<>())
                .build();

        PurchaseOrderUpdateRequest request = PurchaseOrderUpdateRequest.builder()
                .status(PurchaseOrderStatus.DRAFT) // Invalid (CANCELLED is terminal)
                .active(true)
                .items(new ArrayList<>())
                .build();

        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class, () ->
                purchaseOrderService.updatePurchaseOrder(1L, request));

        verify(purchaseOrderRepository, never()).save(any());
    }
}
