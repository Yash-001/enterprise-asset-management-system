package com.yashconsulting.eams.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response payload representing inventory dashboard metrics")
public class InventoryDashboardResponse {

    @Schema(description = "Total cost value valuation of all active spare parts in stock", example = "54320.00")
    private BigDecimal stockValuation;

    @Schema(description = "Active inventory counts grouped by category", example = "{\"Belts\": 12, \"Oils\": 45}")
    private Map<String, Long> countByCategory;

    @Schema(description = "Active inventory counts grouped by supplier ID", example = "{\"10\": 5, \"25\": 18}")
    private Map<Long, Long> countBySupplier;

    @Schema(description = "Active inventory counts grouped by storage location ID", example = "{\"2\": 30, \"4\": 15}")
    private Map<Long, Long> countByLocation;
}
