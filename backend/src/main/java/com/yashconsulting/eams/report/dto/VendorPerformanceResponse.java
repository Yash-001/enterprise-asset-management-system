package com.yashconsulting.eams.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorPerformanceResponse {
    private Long vendorId;
    private String vendorCode;
    private String vendorName;
    private Long totalOrders;
    private BigDecimal totalSpend;
}
