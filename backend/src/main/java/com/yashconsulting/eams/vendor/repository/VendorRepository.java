package com.yashconsulting.eams.vendor.repository;

import com.yashconsulting.eams.vendor.entity.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long>, JpaSpecificationExecutor<Vendor> {

    boolean existsByVendorCode(String vendorCode);

    boolean existsByVendorCodeAndIdNot(String vendorCode, Long id);

    Optional<Vendor> findByVendorCodeAndActiveTrue(String vendorCode);

    Page<Vendor> findAllByActiveTrue(Pageable pageable);
}
