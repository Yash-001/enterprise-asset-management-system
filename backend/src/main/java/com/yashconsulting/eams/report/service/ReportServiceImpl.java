package com.yashconsulting.eams.report.service;

import com.yashconsulting.eams.asset.repository.AssetRepository;
import com.yashconsulting.eams.inventory.repository.SparePartRepository;
import com.yashconsulting.eams.maintenance.repository.MaintenancePlanRepository;
import com.yashconsulting.eams.purchase.repository.PurchaseOrderRepository;
import com.yashconsulting.eams.report.dto.ReportDashboardResponse;
import com.yashconsulting.eams.report.dto.VendorPerformanceResponse;
import com.yashconsulting.eams.workorder.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final AssetRepository assetRepository;
    private final MaintenancePlanRepository maintenancePlanRepository;
    private final WorkOrderRepository workOrderRepository;
    private final SparePartRepository sparePartRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    @Override
    @Transactional(readOnly = true)
    public ReportDashboardResponse getDashboardMetrics() {
        log.info("Fetching Reporting & Analytics consolidated dashboard metrics");

        LocalDate targetUpcomingMaintenanceDate = LocalDate.now().plusDays(30);

        Map<String, Long> assetsByStatus = convertToMap(assetRepository.countAssetsByStatus());
        Map<String, Long> assetsByDepartment = convertToMap(assetRepository.countAssetsByDepartment());
        Map<String, Long> assetsByLocation = convertToMap(assetRepository.countAssetsByLocation());

        long upcomingMaintenanceCount = maintenancePlanRepository.countUpcomingMaintenance(targetUpcomingMaintenanceDate);
        long completedWorkOrders = workOrderRepository.countCompletedWorkOrders();
        long openWorkOrders = workOrderRepository.countOpenWorkOrders();

        var inventoryValuation = sparePartRepository.calculateTotalStockValuation();
        long lowStockCount = sparePartRepository.countLowStockItems();

        Map<String, Long> poByStatus = convertToMap(purchaseOrderRepository.countPurchaseOrdersByStatus());
        List<VendorPerformanceResponse> vendorPerf = purchaseOrderRepository.getVendorPerformanceMetrics();

        return ReportDashboardResponse.builder()
                .assetsByStatus(assetsByStatus)
                .assetsByDepartment(assetsByDepartment)
                .assetsByLocation(assetsByLocation)
                .upcomingMaintenanceCount(upcomingMaintenanceCount)
                .completedWorkOrdersCount(completedWorkOrders)
                .openWorkOrdersCount(openWorkOrders)
                .inventoryValuation(inventoryValuation)
                .lowStockItemsCount(lowStockCount)
                .purchaseOrdersByStatus(poByStatus)
                .vendorPerformance(vendorPerf)
                .build();
    }

    private Map<String, Long> convertToMap(List<Object[]> list) {
        if (list == null) {
            return Map.of();
        }
        return list.stream()
                .filter(row -> row != null && row.length >= 2 && row[0] != null && row[1] != null)
                .collect(Collectors.toMap(
                        row -> row[0].toString(),
                        row -> ((Number) row[1]).longValue(),
                        (existing, replacement) -> existing
                ));
    }
}
