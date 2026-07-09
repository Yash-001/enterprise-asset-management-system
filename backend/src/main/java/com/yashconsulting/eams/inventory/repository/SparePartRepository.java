package com.yashconsulting.eams.inventory.repository;

import com.yashconsulting.eams.inventory.entity.SparePart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface SparePartRepository extends JpaRepository<SparePart, Long>, JpaSpecificationExecutor<SparePart> {

    boolean existsByPartNumber(String partNumber);

    boolean existsByPartNumberAndIdNot(String partNumber, Long id);

    Optional<SparePart> findByPartNumberAndActiveTrue(String partNumber);

    Page<SparePart> findAllByActiveTrue(Pageable pageable);

    @Query("SELECT COALESCE(SUM(s.currentStock * s.unitCost), 0) FROM SparePart s WHERE s.active = true")
    BigDecimal calculateTotalStockValuation();

    @Query("SELECT s.category, COUNT(s) FROM SparePart s WHERE s.active = true AND s.category IS NOT NULL GROUP BY s.category")
    List<Object[]> countByCategory();

    @Query("SELECT s.supplierId, COUNT(s) FROM SparePart s WHERE s.active = true AND s.supplierId IS NOT NULL GROUP BY s.supplierId")
    List<Object[]> countBySupplier();

    @Query("SELECT s.locationId, COUNT(s) FROM SparePart s WHERE s.active = true AND s.locationId IS NOT NULL GROUP BY s.locationId")
    List<Object[]> countByLocation();

    @Query("SELECT s FROM SparePart s WHERE s.active = true AND s.currentStock <= s.minimumStock")
    Page<SparePart> findLowStockItems(Pageable pageable);

    @Query("SELECT s FROM SparePart s WHERE s.active = true AND s.currentStock = 0")
    Page<SparePart> findOutOfStockItems(Pageable pageable);

    @Query("SELECT COUNT(s) FROM SparePart s WHERE s.active = true AND s.currentStock <= s.minimumStock")
    long countLowStockItems();
}
