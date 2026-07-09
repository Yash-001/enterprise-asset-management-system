package com.yashconsulting.eams.inventory.service;

import com.yashconsulting.eams.exception.ResourceNotFoundException;
import com.yashconsulting.eams.inventory.dto.StockTransactionCreateRequest;
import com.yashconsulting.eams.inventory.dto.StockTransactionResponse;
import com.yashconsulting.eams.inventory.dto.StockTransactionSearchRequest;
import com.yashconsulting.eams.inventory.dto.StockTransactionUpdateRequest;
import com.yashconsulting.eams.inventory.entity.SparePart;
import com.yashconsulting.eams.inventory.entity.StockTransaction;
import com.yashconsulting.eams.inventory.entity.StockTransactionType;
import com.yashconsulting.eams.inventory.mapper.StockTransactionMapper;
import com.yashconsulting.eams.inventory.repository.SparePartRepository;
import com.yashconsulting.eams.inventory.repository.StockTransactionRepository;
import com.yashconsulting.eams.inventory.specification.StockTransactionSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockTransactionServiceImpl implements StockTransactionService {

    private final StockTransactionRepository stockTransactionRepository;
    private final SparePartRepository sparePartRepository;
    private final StockTransactionMapper stockTransactionMapper;
    private final org.springframework.context.ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public StockTransactionResponse createStockTransaction(StockTransactionCreateRequest request) {
        log.info("Creating stock transaction of type {} for spare part ID {}", request.getTransactionType(), request.getSparePartId());

        SparePart sparePart = sparePartRepository.findById(request.getSparePartId())
                .orElseThrow(() -> new ResourceNotFoundException("Spare part not found with ID: " + request.getSparePartId()));

        // Apply stock adjustment and validate
        applyStockChange(sparePart, request.getTransactionType(), request.getQuantity());

        // Save updated spare part stock
        SparePart savedPart = sparePartRepository.save(sparePart);
        checkAndPublishLowStock(savedPart);

        StockTransaction entity = stockTransactionMapper.toEntity(request);
        StockTransaction saved = stockTransactionRepository.save(entity);
        return stockTransactionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public StockTransactionResponse updateStockTransaction(Long id, StockTransactionUpdateRequest request) {
        log.info("Updating stock transaction with ID {}", id);
        StockTransaction entity = getTransactionOrThrow(id);

        SparePart sparePart = sparePartRepository.findById(entity.getSparePartId())
                .orElseThrow(() -> new ResourceNotFoundException("Spare part not found with ID: " + entity.getSparePartId()));

        // Adjust stock based on active flags updates
        if (entity.getActive() && !request.getActive()) {
            // Deactivating: revert stock change
            revertStockChange(sparePart, entity.getTransactionType(), entity.getQuantity());
        } else if (!entity.getActive() && request.getActive()) {
            // Reactivating: apply stock change
            applyStockChange(sparePart, request.getTransactionType(), request.getQuantity());
        } else if (entity.getActive() && request.getActive()) {
            // Remapping properties: revert old and apply new
            revertStockChange(sparePart, entity.getTransactionType(), entity.getQuantity());
            applyStockChange(sparePart, request.getTransactionType(), request.getQuantity());
        }

        SparePart savedPart = sparePartRepository.save(sparePart);
        checkAndPublishLowStock(savedPart);

        stockTransactionMapper.updateEntity(request, entity);
        StockTransaction updated = stockTransactionRepository.save(entity);
        return stockTransactionMapper.toResponse(updated);
    }

    private void checkAndPublishLowStock(SparePart part) {
        if (part != null && part.getActive() && part.getCurrentStock() <= part.getMinimumStock()) {
            eventPublisher.publishEvent(new com.yashconsulting.eams.notification.event.LowStockEvent(
                    part.getId(),
                    part.getPartNumber(),
                    part.getPartName(),
                    part.getCurrentStock(),
                    part.getMinimumStock()
            ));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public StockTransactionResponse getStockTransactionById(Long id) {
        log.info("Fetching stock transaction with ID: {}", id);
        StockTransaction entity = getTransactionOrThrow(id);
        return stockTransactionMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StockTransactionResponse> getAllStockTransactions(Pageable pageable, boolean includeInactive) {
        log.info("Listing all stock transactions. Include inactive: {}", includeInactive);
        Page<StockTransaction> page = includeInactive
                ? stockTransactionRepository.findAll(pageable)
                : stockTransactionRepository.findAllByActiveTrue(pageable);
        return page.map(stockTransactionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StockTransactionResponse> searchStockTransactions(StockTransactionSearchRequest request) {
        log.info("Searching stock transactions dynamically");
        Specification<StockTransaction> spec = StockTransactionSpecification.build(request);

        Sort.Direction direction = Sort.Direction.fromString(
                request.getSortDirection() != null ? request.getSortDirection() : "ASC"
        );
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "id";
        Pageable pageable = PageRequest.of(
                request.getPage() != null ? request.getPage() : 0,
                request.getSize() != null ? request.getSize() : 20,
                Sort.by(direction, sortBy)
        );

        Page<StockTransaction> page = stockTransactionRepository.findAll(spec, pageable);
        return page.map(stockTransactionMapper::toResponse);
    }

    @Override
    @Transactional
    public void deleteStockTransaction(Long id) {
        log.info("Soft deleting stock transaction with ID: {}", id);
        StockTransaction entity = getTransactionOrThrow(id);

        if (entity.getActive()) {
            SparePart sparePart = sparePartRepository.findById(entity.getSparePartId())
                    .orElseThrow(() -> new ResourceNotFoundException("Spare part not found with ID: " + entity.getSparePartId()));
            // Revert stock change
            revertStockChange(sparePart, entity.getTransactionType(), entity.getQuantity());
            sparePartRepository.save(sparePart);

            entity.setActive(false);
            stockTransactionRepository.save(entity);
        }
    }

    private void applyStockChange(SparePart sparePart, StockTransactionType type, int quantity) {
        if (type != StockTransactionType.ADJUSTMENT && quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive for " + type + " transactions");
        }
        int newStock = sparePart.getCurrentStock();
        switch (type) {
            case IN:
            case RETURN:
                newStock += quantity;
                break;
            case OUT:
                newStock -= quantity;
                break;
            case ADJUSTMENT:
                newStock += quantity;
                break;
        }
        if (newStock < 0) {
            throw new IllegalArgumentException("Insufficient stock level for spare part: " + sparePart.getPartNumber());
        }
        sparePart.setCurrentStock(newStock);
    }

    private void revertStockChange(SparePart sparePart, StockTransactionType type, int quantity) {
        int newStock = sparePart.getCurrentStock();
        switch (type) {
            case IN:
            case RETURN:
                newStock -= quantity;
                break;
            case OUT:
                newStock += quantity;
                break;
            case ADJUSTMENT:
                newStock -= quantity;
                break;
        }
        if (newStock < 0) {
            throw new IllegalArgumentException("Reverting this transaction would cause a negative stock level for spare part: " + sparePart.getPartNumber());
        }
        sparePart.setCurrentStock(newStock);
    }

    private StockTransaction getTransactionOrThrow(Long id) {
        return stockTransactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock transaction not found with ID: " + id));
    }
}
