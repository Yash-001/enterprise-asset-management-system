package com.yashconsulting.eams.purchase.service;

import com.yashconsulting.eams.exception.ResourceNotFoundException;
import com.yashconsulting.eams.exception.PurchaseOrderNumberAlreadyExistsException;
import com.yashconsulting.eams.audit.annotation.Auditable;
import com.yashconsulting.eams.audit.entity.AuditAction;
import com.yashconsulting.eams.inventory.dto.StockTransactionCreateRequest;
import com.yashconsulting.eams.inventory.entity.StockTransactionType;
import com.yashconsulting.eams.inventory.repository.SparePartRepository;
import com.yashconsulting.eams.inventory.service.StockTransactionService;
import com.yashconsulting.eams.purchase.dto.PurchaseOrderCreateRequest;
import com.yashconsulting.eams.purchase.dto.PurchaseOrderItemCreateRequest;
import com.yashconsulting.eams.purchase.dto.PurchaseOrderResponse;
import com.yashconsulting.eams.purchase.dto.PurchaseOrderSearchRequest;
import com.yashconsulting.eams.purchase.dto.PurchaseOrderUpdateRequest;
import com.yashconsulting.eams.purchase.entity.PurchaseOrder;
import com.yashconsulting.eams.purchase.entity.PurchaseOrderItem;
import com.yashconsulting.eams.purchase.entity.PurchaseOrderStatus;
import com.yashconsulting.eams.purchase.mapper.PurchaseOrderMapper;
import com.yashconsulting.eams.purchase.repository.PurchaseOrderRepository;
import com.yashconsulting.eams.purchase.specification.PurchaseOrderSpecification;
import com.yashconsulting.eams.vendor.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final VendorRepository vendorRepository;
    private final SparePartRepository sparePartRepository;
    private final StockTransactionService stockTransactionService;
    private final PurchaseOrderMapper purchaseOrderMapper;
    private final org.springframework.context.ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    @Auditable(entity = "PurchaseOrder", action = AuditAction.CREATE)
    public PurchaseOrderResponse createPurchaseOrder(PurchaseOrderCreateRequest request) {
        log.info("Creating purchase order: {}", request.getPoNumber());

        // Validate vendor exists
        if (!vendorRepository.existsById(request.getVendorId())) {
            throw new ResourceNotFoundException("Vendor not found with ID: " + request.getVendorId());
        }

        // Validate PO number uniqueness
        String poNumber = request.getPoNumber().trim().toUpperCase(Locale.ROOT);
        request.setPoNumber(poNumber);

        if (purchaseOrderRepository.existsByPoNumber(poNumber)) {
            throw new PurchaseOrderNumberAlreadyExistsException("Purchase order number " + poNumber + " already exists");
        }

        // Validate spare parts exist
        if (request.getItems() != null) {
            for (PurchaseOrderItemCreateRequest item : request.getItems()) {
                if (!sparePartRepository.existsById(item.getSparePartId())) {
                    throw new ResourceNotFoundException("Spare part not found with ID: " + item.getSparePartId());
                }
            }
        }

        PurchaseOrder entity = purchaseOrderMapper.toEntity(request);
        PurchaseOrder saved = purchaseOrderRepository.save(entity);
        return purchaseOrderMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @Auditable(entity = "PurchaseOrder", action = AuditAction.UPDATE)
    public PurchaseOrderResponse updatePurchaseOrder(Long id, PurchaseOrderUpdateRequest request) {
        log.info("Updating purchase order with ID: {}", id);
        PurchaseOrder entity = getPurchaseOrderOrThrow(id);

        PurchaseOrderStatus oldStatus = entity.getStatus();
        PurchaseOrderStatus newStatus = request.getStatus();

        // Validate status workflow transitions
        if (oldStatus != newStatus) {
            validateStatusTransition(oldStatus, newStatus);
        }

        // If items are modified, it's only allowed in DRAFT status
        if (oldStatus != PurchaseOrderStatus.DRAFT && isItemsListModified(entity, request.getItems())) {
            throw new IllegalArgumentException("Line items can only be modified when the purchase order is in DRAFT status");
        }

        // Handle items updates if PO is in DRAFT
        if (oldStatus == PurchaseOrderStatus.DRAFT) {
            entity.getItems().clear();
            BigDecimal totalAmount = BigDecimal.ZERO;
            if (request.getItems() != null) {
                for (PurchaseOrderItemCreateRequest itemReq : request.getItems()) {
                    if (!sparePartRepository.existsById(itemReq.getSparePartId())) {
                        throw new ResourceNotFoundException("Spare part not found with ID: " + itemReq.getSparePartId());
                    }
                    PurchaseOrderItem item = purchaseOrderMapper.toItemEntity(itemReq);
                    item.setPurchaseOrder(entity);
                    entity.getItems().add(item);
                    totalAmount = totalAmount.add(item.getLineTotal());
                }
            }
            entity.setTotalAmount(totalAmount);
        }

        entity.setStatus(newStatus);
        entity.setExpectedDeliveryDate(request.getExpectedDeliveryDate());
        entity.setRemarks(request.getRemarks() != null ? request.getRemarks().trim() : null);
        entity.setActive(request.getActive());

        PurchaseOrder updated = purchaseOrderRepository.save(entity);

        if (oldStatus != PurchaseOrderStatus.APPROVED && newStatus == PurchaseOrderStatus.APPROVED) {
            eventPublisher.publishEvent(new com.yashconsulting.eams.notification.event.PurchaseOrderApprovedEvent(
                    updated.getId(), updated.getPoNumber(), updated.getCreatedBy()));
        }

        // If transitioned to RECEIVED: automatically increase inventory stock
        if (oldStatus != PurchaseOrderStatus.RECEIVED && newStatus == PurchaseOrderStatus.RECEIVED) {
            receiveInventoryStock(updated);
            eventPublisher.publishEvent(new com.yashconsulting.eams.notification.event.PurchaseOrderReceivedEvent(
                    updated.getId(), updated.getPoNumber(), updated.getCreatedBy()));
        }

        return purchaseOrderMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrderResponse getPurchaseOrderById(Long id) {
        log.info("Fetching purchase order with ID: {}", id);
        PurchaseOrder entity = getPurchaseOrderOrThrow(id);
        return purchaseOrderMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PurchaseOrderResponse> getAllPurchaseOrders(Pageable pageable, boolean includeInactive) {
        log.info("Listing all purchase orders. Include inactive: {}", includeInactive);
        Page<PurchaseOrder> page = includeInactive
                ? purchaseOrderRepository.findAll(pageable)
                : purchaseOrderRepository.findAllByActiveTrue(pageable);
        return page.map(purchaseOrderMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PurchaseOrderResponse> searchPurchaseOrders(PurchaseOrderSearchRequest request) {
        log.info("Searching purchase orders dynamically");
        Specification<PurchaseOrder> spec = PurchaseOrderSpecification.build(request);

        Sort.Direction direction = Sort.Direction.fromString(
                request.getSortDirection() != null ? request.getSortDirection() : "ASC"
        );
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "id";
        Pageable pageable = PageRequest.of(
                request.getPage() != null ? request.getPage() : 0,
                request.getSize() != null ? request.getSize() : 20,
                Sort.by(direction, sortBy)
        );

        Page<PurchaseOrder> page = purchaseOrderRepository.findAll(spec, pageable);
        return page.map(purchaseOrderMapper::toResponse);
    }

    @Override
    @Transactional
    @Auditable(entity = "PurchaseOrder", action = AuditAction.DELETE)
    public void deletePurchaseOrder(Long id) {
        log.info("Soft deleting purchase order with ID: {}", id);
        PurchaseOrder entity = getPurchaseOrderOrThrow(id);
        entity.setActive(false);
        purchaseOrderRepository.save(entity);
    }

    private void validateStatusTransition(PurchaseOrderStatus from, PurchaseOrderStatus to) {
        boolean valid = false;
        switch (from) {
            case DRAFT:
                valid = (to == PurchaseOrderStatus.APPROVED || to == PurchaseOrderStatus.CANCELLED);
                break;
            case APPROVED:
                valid = (to == PurchaseOrderStatus.ORDERED || to == PurchaseOrderStatus.CANCELLED);
                break;
            case ORDERED:
                valid = (to == PurchaseOrderStatus.RECEIVED || to == PurchaseOrderStatus.CANCELLED);
                break;
            case RECEIVED:
            case CANCELLED:
                valid = false; // Terminal states
                break;
        }

        if (!valid) {
            throw new IllegalArgumentException("Invalid purchase order status transition from " + from + " to " + to);
        }
    }

    private boolean isItemsListModified(PurchaseOrder entity, List<PurchaseOrderItemCreateRequest> newItems) {
        if (entity.getItems().size() != newItems.size()) {
            return true;
        }
        for (int i = 0; i < entity.getItems().size(); i++) {
            PurchaseOrderItem existingItem = entity.getItems().get(i);
            PurchaseOrderItemCreateRequest newItem = newItems.get(i);
            if (!existingItem.getSparePartId().equals(newItem.getSparePartId())
                    || existingItem.getQuantity() != newItem.getQuantity()
                    || existingItem.getUnitPrice().compareTo(newItem.getUnitPrice()) != 0) {
                return true;
            }
        }
        return false;
    }

    private void receiveInventoryStock(PurchaseOrder po) {
        log.info("Receiving stock for purchase order: {}", po.getPoNumber());
        if (po.getItems() != null) {
            for (PurchaseOrderItem item : po.getItems()) {
                StockTransactionCreateRequest transactionRequest = StockTransactionCreateRequest.builder()
                        .sparePartId(item.getSparePartId())
                        .transactionType(StockTransactionType.IN)
                        .quantity(item.getQuantity())
                        .unitCost(item.getUnitPrice())
                        .referenceType("PURCHASE_ORDER")
                        .referenceId(po.getId())
                        .remarks("Received purchase order: " + po.getPoNumber())
                        .build();
                stockTransactionService.createStockTransaction(transactionRequest);
            }
        }
    }

    private PurchaseOrder getPurchaseOrderOrThrow(Long id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with ID: " + id));
    }
}
