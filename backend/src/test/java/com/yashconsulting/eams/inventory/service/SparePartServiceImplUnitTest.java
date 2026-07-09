package com.yashconsulting.eams.inventory.service;

import com.yashconsulting.eams.exception.ResourceNotFoundException;
import com.yashconsulting.eams.exception.SparePartNumberAlreadyExistsException;
import com.yashconsulting.eams.location.repository.LocationRepository;
import com.yashconsulting.eams.inventory.dto.SparePartCreateRequest;
import com.yashconsulting.eams.inventory.dto.SparePartUpdateRequest;
import com.yashconsulting.eams.inventory.repository.SparePartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SparePartServiceImplUnitTest {

    @Mock
    private SparePartRepository sparePartRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private com.yashconsulting.eams.inventory.mapper.SparePartMapper sparePartMapper;

    @InjectMocks
    private SparePartServiceImpl sparePartService;

    @Test
    void createSparePart_whenLocationNotFound_thenThrowsResourceNotFoundException() {
        SparePartCreateRequest request = SparePartCreateRequest.builder()
                .partNumber("SP-001")
                .partName("Test Part")
                .locationId(999L) // Non-existent location ID
                .minimumStock(1)
                .maximumStock(10)
                .currentStock(5)
                .unitCost(BigDecimal.TEN)
                .build();

        when(locationRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
                sparePartService.createSparePart(request));
    }

    @Test
    void createSparePart_whenPartNumberDuplicate_thenThrowsSparePartNumberAlreadyExistsException() {
        SparePartCreateRequest request = SparePartCreateRequest.builder()
                .partNumber("SP-DUP")
                .partName("Test Part")
                .minimumStock(1)
                .maximumStock(10)
                .currentStock(5)
                .unitCost(BigDecimal.TEN)
                .build();

        when(sparePartRepository.existsByPartNumber("SP-DUP")).thenReturn(true);

        assertThrows(SparePartNumberAlreadyExistsException.class, () ->
                sparePartService.createSparePart(request));
    }

    @Test
    void updateSparePart_whenLocationNotFound_thenThrowsResourceNotFoundException() {
        com.yashconsulting.eams.inventory.entity.SparePart part = com.yashconsulting.eams.inventory.entity.SparePart.builder()
                .id(1L)
                .partNumber("SP-001")
                .partName("Old Name")
                .build();

        SparePartUpdateRequest request = SparePartUpdateRequest.builder()
                .partName("New Name")
                .locationId(999L) // Non-existent location ID
                .minimumStock(1)
                .maximumStock(10)
                .currentStock(5)
                .unitCost(BigDecimal.TEN)
                .active(true)
                .build();

        when(sparePartRepository.findById(1L)).thenReturn(java.util.Optional.of(part));
        when(locationRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
                sparePartService.updateSparePart(1L, request));
    }
}
