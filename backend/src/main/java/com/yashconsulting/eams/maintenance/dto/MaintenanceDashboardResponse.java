package com.yashconsulting.eams.maintenance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response payload representing the maintenance metrics dashboard data")
public class MaintenanceDashboardResponse {

    @Schema(description = "Plans scheduled for maintenance in the next 30 days")
    private List<MaintenancePlanResponse> upcomingMaintenance;

    @Schema(description = "Plans that are currently overdue for maintenance")
    private List<MaintenancePlanResponse> overdueMaintenance;

    @Schema(description = "Plans completed within the current month")
    private List<MaintenancePlanResponse> completedThisMonth;

    @Schema(description = "Counts of maintenance plans grouped by status")
    private Map<String, Long> countByStatus;

    @Schema(description = "Counts of maintenance plans grouped by priority")
    private Map<String, Long> countByPriority;
}
