package com.yashconsulting.eams.inventory.service;

import com.yashconsulting.eams.inventory.dto.SparePartCreateRequest;
import com.yashconsulting.eams.inventory.dto.SparePartResponse;
import com.yashconsulting.eams.inventory.dto.SparePartSearchRequest;
import com.yashconsulting.eams.inventory.dto.SparePartUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.yashconsulting.eams.inventory.dto.InventoryDashboardResponse;

public interface SparePartService {

    SparePartResponse createSparePart(SparePartCreateRequest request);

    SparePartResponse updateSparePart(Long id, SparePartUpdateRequest request);

    SparePartResponse getSparePartById(Long id);

    Page<SparePartResponse> getAllSpareParts(Pageable pageable, boolean includeInactive);

    Page<SparePartResponse> searchSpareParts(SparePartSearchRequest request);

    void deleteSparePart(Long id);

    InventoryDashboardResponse getInventoryDashboardMetrics();

    Page<SparePartResponse> getLowStockItems(Pageable pageable);

    Page<SparePartResponse> getOutOfStockItems(Pageable pageable);
}
