package com.yashconsulting.eams.report.controller;

import com.yashconsulting.eams.report.dto.ReportDashboardResponse;
import com.yashconsulting.eams.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Reporting & Analytics", description = "APIs for dashboard metrics and reporting aggregates")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Operation(summary = "Get consolidated dashboard metrics", description = "Retrieve aggregate statistics for assets, work orders, maintenance schedules, inventory valuations, and vendor performance")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dashboard metrics retrieved successfully")
    })
    public ResponseEntity<ReportDashboardResponse> getDashboardMetrics() {
        return ResponseEntity.ok(reportService.getDashboardMetrics());
    }
}
