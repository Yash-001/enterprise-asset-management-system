package com.yashconsulting.eams.maintenance.service;

import com.yashconsulting.eams.maintenance.dto.MaintenancePlanCreateRequest;
import com.yashconsulting.eams.maintenance.dto.MaintenancePlanResponse;
import com.yashconsulting.eams.maintenance.dto.MaintenancePlanSearchRequest;
import com.yashconsulting.eams.maintenance.dto.MaintenancePlanUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MaintenancePlanService {

    MaintenancePlanResponse createMaintenancePlan(MaintenancePlanCreateRequest request);

    MaintenancePlanResponse updateMaintenancePlan(Long id, MaintenancePlanUpdateRequest request);

    MaintenancePlanResponse getMaintenancePlanById(Long id);

    Page<MaintenancePlanResponse> getAllMaintenancePlans(Pageable pageable, boolean includeInactive);

    Page<MaintenancePlanResponse> searchMaintenancePlans(MaintenancePlanSearchRequest request);

    void deleteMaintenancePlan(Long id);

    MaintenancePlanResponse completeMaintenancePlan(Long id, java.time.LocalDate completionDate);
}
