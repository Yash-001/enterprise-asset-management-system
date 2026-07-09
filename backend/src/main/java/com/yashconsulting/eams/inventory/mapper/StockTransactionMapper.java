package com.yashconsulting.eams.inventory.mapper;

import com.yashconsulting.eams.inventory.dto.StockTransactionCreateRequest;
import com.yashconsulting.eams.inventory.dto.StockTransactionResponse;
import com.yashconsulting.eams.inventory.dto.StockTransactionUpdateRequest;
import com.yashconsulting.eams.inventory.entity.StockTransaction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class StockTransactionMapper {

    public StockTransaction toEntity(StockTransactionCreateRequest request) {
        if (request == null) {
            return null;
        }

        return StockTransaction.builder()
                .sparePartId(request.getSparePartId())
                .transactionType(request.getTransactionType())
                .quantity(request.getQuantity())
                .unitCost(request.getUnitCost() != null ? request.getUnitCost() : BigDecimal.ZERO)
                .referenceType(request.getReferenceType() != null ? request.getReferenceType().trim() : null)
                .referenceId(request.getReferenceId())
                .remarks(request.getRemarks() != null ? request.getRemarks().trim() : null)
                .active(Boolean.TRUE)
                .build();
    }

    public StockTransactionResponse toResponse(StockTransaction entity) {
        if (entity == null) {
            return null;
        }

        return StockTransactionResponse.builder()
                .id(entity.getId())
                .sparePartId(entity.getSparePartId())
                .transactionType(entity.getTransactionType())
                .quantity(entity.getQuantity())
                .unitCost(entity.getUnitCost())
                .referenceType(entity.getReferenceType())
                .referenceId(entity.getReferenceId())
                .remarks(entity.getRemarks())
                .performedBy(entity.getPerformedBy())
                .transactionDate(entity.getTransactionDate())
                .active(entity.getActive())
                .build();
    }

    public void updateEntity(StockTransactionUpdateRequest request, StockTransaction entity) {
        if (request == null || entity == null) {
            return;
        }

        entity.setTransactionType(request.getTransactionType());
        entity.setQuantity(request.getQuantity());
        entity.setUnitCost(request.getUnitCost() != null ? request.getUnitCost() : BigDecimal.ZERO);
        entity.setReferenceType(request.getReferenceType() != null ? request.getReferenceType().trim() : null);
        entity.setReferenceId(request.getReferenceId());
        entity.setRemarks(request.getRemarks() != null ? request.getRemarks().trim() : null);
        entity.setActive(request.getActive());
    }
}
