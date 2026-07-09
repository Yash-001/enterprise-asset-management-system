package com.yashconsulting.eams.inventory.service;

import com.yashconsulting.eams.inventory.dto.StockTransactionCreateRequest;
import com.yashconsulting.eams.inventory.dto.StockTransactionResponse;
import com.yashconsulting.eams.inventory.dto.StockTransactionSearchRequest;
import com.yashconsulting.eams.inventory.dto.StockTransactionUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StockTransactionService {

    StockTransactionResponse createStockTransaction(StockTransactionCreateRequest request);

    StockTransactionResponse updateStockTransaction(Long id, StockTransactionUpdateRequest request);

    StockTransactionResponse getStockTransactionById(Long id);

    Page<StockTransactionResponse> getAllStockTransactions(Pageable pageable, boolean includeInactive);

    Page<StockTransactionResponse> searchStockTransactions(StockTransactionSearchRequest request);

    void deleteStockTransaction(Long id);
}
