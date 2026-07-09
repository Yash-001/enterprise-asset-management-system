package com.yashconsulting.eams.purchase.mapper;

import com.yashconsulting.eams.purchase.dto.PurchaseOrderCreateRequest;
import com.yashconsulting.eams.purchase.dto.PurchaseOrderItemCreateRequest;
import com.yashconsulting.eams.purchase.dto.PurchaseOrderItemResponse;
import com.yashconsulting.eams.purchase.dto.PurchaseOrderResponse;
import com.yashconsulting.eams.purchase.entity.PurchaseOrder;
import com.yashconsulting.eams.purchase.entity.PurchaseOrderItem;
import com.yashconsulting.eams.purchase.entity.PurchaseOrderStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class PurchaseOrderMapper {

    public PurchaseOrder toEntity(PurchaseOrderCreateRequest request) {
        if (request == null) {
            return null;
        }

        List<PurchaseOrderItem> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        PurchaseOrder entity = PurchaseOrder.builder()
                .poNumber(request.getPoNumber().trim().toUpperCase(Locale.ROOT))
                .vendorId(request.getVendorId())
                .status(PurchaseOrderStatus.DRAFT) // Creates as DRAFT by default
                .orderDate(LocalDateTime.now())
                .expectedDeliveryDate(request.getExpectedDeliveryDate())
                .remarks(request.getRemarks() != null ? request.getRemarks().trim() : null)
                .active(Boolean.TRUE)
                .items(items)
                .build();

        if (request.getItems() != null) {
            for (PurchaseOrderItemCreateRequest itemReq : request.getItems()) {
                PurchaseOrderItem item = toItemEntity(itemReq);
                if (item != null) {
                    item.setPurchaseOrder(entity);
                    items.add(item);
                    totalAmount = totalAmount.add(item.getLineTotal());
                }
            }
        }
        entity.setTotalAmount(totalAmount);
        return entity;
    }

    public PurchaseOrderItem toItemEntity(PurchaseOrderItemCreateRequest request) {
        if (request == null) {
            return null;
        }

        BigDecimal qty = BigDecimal.valueOf(request.getQuantity());
        BigDecimal lineTotal = request.getUnitPrice().multiply(qty);

        return PurchaseOrderItem.builder()
                .sparePartId(request.getSparePartId())
                .quantity(request.getQuantity())
                .unitPrice(request.getUnitPrice())
                .lineTotal(lineTotal)
                .build();
    }

    public PurchaseOrderResponse toResponse(PurchaseOrder entity) {
        if (entity == null) {
            return null;
        }

        List<PurchaseOrderItemResponse> itemResponses = new ArrayList<>();
        if (entity.getItems() != null) {
            itemResponses = entity.getItems().stream()
                    .map(this::toItemResponse)
                    .collect(Collectors.toList());
        }

        return PurchaseOrderResponse.builder()
                .id(entity.getId())
                .poNumber(entity.getPoNumber())
                .vendorId(entity.getVendorId())
                .status(entity.getStatus())
                .orderDate(entity.getOrderDate())
                .expectedDeliveryDate(entity.getExpectedDeliveryDate())
                .totalAmount(entity.getTotalAmount())
                .remarks(entity.getRemarks())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .items(itemResponses)
                .build();
    }

    public PurchaseOrderItemResponse toItemResponse(PurchaseOrderItem entity) {
        if (entity == null) {
            return null;
        }

        return PurchaseOrderItemResponse.builder()
                .id(entity.getId())
                .purchaseOrderId(entity.getPurchaseOrder() != null ? entity.getPurchaseOrder().getId() : null)
                .sparePartId(entity.getSparePartId())
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .lineTotal(entity.getLineTotal())
                .build();
    }
}
