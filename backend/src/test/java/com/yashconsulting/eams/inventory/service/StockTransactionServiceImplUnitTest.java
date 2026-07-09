package com.yashconsulting.eams.inventory.service;

import com.yashconsulting.eams.exception.ResourceNotFoundException;
import com.yashconsulting.eams.inventory.dto.StockTransactionCreateRequest;
import com.yashconsulting.eams.inventory.entity.SparePart;
import com.yashconsulting.eams.inventory.entity.StockTransaction;
import com.yashconsulting.eams.inventory.entity.StockTransactionType;
import com.yashconsulting.eams.inventory.mapper.StockTransactionMapper;
import com.yashconsulting.eams.inventory.repository.SparePartRepository;
import com.yashconsulting.eams.inventory.repository.StockTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockTransactionServiceImplUnitTest {

    @Mock
    private StockTransactionRepository stockTransactionRepository;

    @Mock
    private SparePartRepository sparePartRepository;

    @Mock
    private StockTransactionMapper stockTransactionMapper;

    @InjectMocks
    private StockTransactionServiceImpl stockTransactionService;

    private SparePart testSparePart;

    @BeforeEach
    void setUp() {
        testSparePart = SparePart.builder()
                .id(1L)
                .partNumber("SP-TEST")
                .partName("Test Spare Part")
                .currentStock(10)
                .active(true)
                .build();
    }

    @Test
    void createStockTransaction_whenIN_thenIncrementsStock() {
        StockTransactionCreateRequest request = StockTransactionCreateRequest.builder()
                .sparePartId(1L)
                .transactionType(StockTransactionType.IN)
                .quantity(5)
                .unitCost(BigDecimal.TEN)
                .build();

        when(sparePartRepository.findById(1L)).thenReturn(Optional.of(testSparePart));
        when(stockTransactionMapper.toEntity(request)).thenReturn(new StockTransaction());
        when(stockTransactionRepository.save(any())).thenReturn(new StockTransaction());

        stockTransactionService.createStockTransaction(request);

        assertEquals(15, testSparePart.getCurrentStock());
        verify(sparePartRepository, times(1)).save(testSparePart);
    }

    @Test
    void createStockTransaction_whenOUT_thenDecrementsStock() {
        StockTransactionCreateRequest request = StockTransactionCreateRequest.builder()
                .sparePartId(1L)
                .transactionType(StockTransactionType.OUT)
                .quantity(4)
                .unitCost(BigDecimal.TEN)
                .build();

        when(sparePartRepository.findById(1L)).thenReturn(Optional.of(testSparePart));
        when(stockTransactionMapper.toEntity(request)).thenReturn(new StockTransaction());
        when(stockTransactionRepository.save(any())).thenReturn(new StockTransaction());

        stockTransactionService.createStockTransaction(request);

        assertEquals(6, testSparePart.getCurrentStock());
        verify(sparePartRepository, times(1)).save(testSparePart);
    }

    @Test
    void createStockTransaction_whenOUTExceedsCurrentStock_thenThrowsIllegalArgumentException() {
        StockTransactionCreateRequest request = StockTransactionCreateRequest.builder()
                .sparePartId(1L)
                .transactionType(StockTransactionType.OUT)
                .quantity(15) // Greater than currentStock=10
                .unitCost(BigDecimal.TEN)
                .build();

        when(sparePartRepository.findById(1L)).thenReturn(Optional.of(testSparePart));

        assertThrows(IllegalArgumentException.class, () ->
                stockTransactionService.createStockTransaction(request));

        assertEquals(10, testSparePart.getCurrentStock()); // Stock remains unchanged
        verify(sparePartRepository, never()).save(any());
    }

    @Test
    void createStockTransaction_whenNegativeADJUSTMENTExceedsStock_thenThrowsIllegalArgumentException() {
        StockTransactionCreateRequest request = StockTransactionCreateRequest.builder()
                .sparePartId(1L)
                .transactionType(StockTransactionType.ADJUSTMENT)
                .quantity(-12) // Deducts 12, causing negative stock (10 - 12 = -2)
                .unitCost(BigDecimal.TEN)
                .build();

        when(sparePartRepository.findById(1L)).thenReturn(Optional.of(testSparePart));

        assertThrows(IllegalArgumentException.class, () ->
                stockTransactionService.createStockTransaction(request));

        assertEquals(10, testSparePart.getCurrentStock());
    }

    @Test
    void createStockTransaction_whenPositiveADJUSTMENT_thenIncrementsStock() {
        StockTransactionCreateRequest request = StockTransactionCreateRequest.builder()
                .sparePartId(1L)
                .transactionType(StockTransactionType.ADJUSTMENT)
                .quantity(3)
                .unitCost(BigDecimal.TEN)
                .build();

        when(sparePartRepository.findById(1L)).thenReturn(Optional.of(testSparePart));
        when(stockTransactionMapper.toEntity(request)).thenReturn(new StockTransaction());
        when(stockTransactionRepository.save(any())).thenReturn(new StockTransaction());

        stockTransactionService.createStockTransaction(request);

        assertEquals(13, testSparePart.getCurrentStock());
    }
}
