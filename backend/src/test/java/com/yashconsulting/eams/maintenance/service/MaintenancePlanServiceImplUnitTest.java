package com.yashconsulting.eams.maintenance.service;

import com.yashconsulting.eams.asset.repository.AssetRepository;
import com.yashconsulting.eams.maintenance.dto.MaintenancePlanResponse;
import com.yashconsulting.eams.maintenance.entity.FrequencyType;
import com.yashconsulting.eams.maintenance.entity.MaintenancePlan;
import com.yashconsulting.eams.maintenance.entity.MaintenancePriority;
import com.yashconsulting.eams.maintenance.entity.MaintenanceType;
import com.yashconsulting.eams.maintenance.mapper.MaintenancePlanMapper;
import com.yashconsulting.eams.maintenance.repository.MaintenancePlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaintenancePlanServiceImplUnitTest {

    @Mock
    private MaintenancePlanRepository maintenancePlanRepository;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private MaintenancePlanMapper maintenancePlanMapper;

    @InjectMocks
    private MaintenancePlanServiceImpl maintenancePlanService;

    private MaintenancePlan testPlan;

    @BeforeEach
    void setUp() {
        testPlan = MaintenancePlan.builder()
                .id(1L)
                .assetId(10L)
                .planCode("MP-TEST")
                .planName("Test Plan")
                .maintenanceType(MaintenanceType.PREVENTIVE)
                .frequencyValue(3)
                .nextMaintenanceDate(LocalDate.now().plusMonths(3))
                .priority(MaintenancePriority.MEDIUM)
                .active(true)
                .build();
    }

    @Test
    void calculateNextMaintenanceDate_whenDaily_thenCorrect() {
        LocalDate completionDate = LocalDate.of(2026, 7, 9);
        LocalDate nextDate = maintenancePlanService.calculateNextMaintenanceDate(completionDate, FrequencyType.DAILY, 5);
        assertEquals(LocalDate.of(2026, 7, 14), nextDate);
    }

    @Test
    void calculateNextMaintenanceDate_whenWeekly_thenCorrect() {
        LocalDate completionDate = LocalDate.of(2026, 7, 9);
        LocalDate nextDate = maintenancePlanService.calculateNextMaintenanceDate(completionDate, FrequencyType.WEEKLY, 2);
        assertEquals(LocalDate.of(2026, 7, 23), nextDate);
    }

    @Test
    void calculateNextMaintenanceDate_whenMonthly_thenCorrect() {
        LocalDate completionDate = LocalDate.of(2026, 7, 9);
        LocalDate nextDate = maintenancePlanService.calculateNextMaintenanceDate(completionDate, FrequencyType.MONTHLY, 1);
        assertEquals(LocalDate.of(2026, 8, 9), nextDate);
    }

    @Test
    void calculateNextMaintenanceDate_whenYearly_thenCorrect() {
        LocalDate completionDate = LocalDate.of(2026, 7, 9);
        LocalDate nextDate = maintenancePlanService.calculateNextMaintenanceDate(completionDate, FrequencyType.YEARLY, 1);
        assertEquals(LocalDate.of(2027, 7, 9), nextDate);
    }

    @Test
    void calculateNextMaintenanceDate_whenInvalidFrequencyValue_thenThrowsException() {
        LocalDate completionDate = LocalDate.of(2026, 7, 9);
        assertThrows(IllegalArgumentException.class, () ->
                maintenancePlanService.calculateNextMaintenanceDate(completionDate, FrequencyType.DAILY, 0));
        assertThrows(IllegalArgumentException.class, () ->
                maintenancePlanService.calculateNextMaintenanceDate(completionDate, FrequencyType.DAILY, -1));
    }

    @Test
    void calculateNextMaintenanceDate_whenNullInputs_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                maintenancePlanService.calculateNextMaintenanceDate(null, FrequencyType.DAILY, 5));
        assertThrows(IllegalArgumentException.class, () ->
                maintenancePlanService.calculateNextMaintenanceDate(LocalDate.now(), null, 5));
    }

    @Test
    void completeMaintenancePlan_whenValidPlan_thenUpdatesDatesAndSaves() {
        LocalDate completionDate = LocalDate.of(2026, 7, 9);
        testPlan.setFrequencyType(FrequencyType.MONTHLY);
        testPlan.setFrequencyValue(3);

        when(maintenancePlanRepository.findById(1L)).thenReturn(Optional.of(testPlan));
        when(maintenancePlanRepository.save(any(MaintenancePlan.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(maintenancePlanMapper.toResponse(any(MaintenancePlan.class))).thenReturn(new MaintenancePlanResponse());

        maintenancePlanService.completeMaintenancePlan(1L, completionDate);

        assertEquals(LocalDate.of(2026, 7, 9), testPlan.getLastMaintenanceDate());
        assertEquals(LocalDate.of(2026, 10, 9), testPlan.getNextMaintenanceDate());

        verify(maintenancePlanRepository, times(1)).save(testPlan);
    }
}
