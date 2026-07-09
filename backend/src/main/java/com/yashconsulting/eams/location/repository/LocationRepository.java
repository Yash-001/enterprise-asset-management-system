package com.yashconsulting.eams.location.repository;

import com.yashconsulting.eams.location.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long>, JpaSpecificationExecutor<Location> {

    boolean existsByLocationCode(String locationCode);

    boolean existsByLocationCodeAndIdNot(String locationCode, Long id);

    Optional<Location> findByLocationCodeAndActiveTrue(String locationCode);

    Page<Location> findAllByActiveTrue(Pageable pageable);
}
