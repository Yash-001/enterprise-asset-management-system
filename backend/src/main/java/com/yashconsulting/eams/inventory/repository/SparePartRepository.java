package com.yashconsulting.eams.inventory.repository;

import com.yashconsulting.eams.inventory.entity.SparePart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SparePartRepository extends JpaRepository<SparePart, Long>, JpaSpecificationExecutor<SparePart> {

    boolean existsByPartNumber(String partNumber);

    boolean existsByPartNumberAndIdNot(String partNumber, Long id);

    Optional<SparePart> findByPartNumberAndActiveTrue(String partNumber);

    Page<SparePart> findAllByActiveTrue(Pageable pageable);
}
