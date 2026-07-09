package com.yashconsulting.eams.asset.repository;

import com.yashconsulting.eams.asset.entity.Asset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long>, JpaSpecificationExecutor<Asset> {

    Optional<Asset> findByAssetCode(String assetCode);

    boolean existsByAssetCode(String assetCode);

    Optional<Asset> findByIdAndActiveTrue(Long id);

    boolean existsByAssetCodeAndIdNot(String assetCode, Long id);

    Optional<Asset> findByAssetCodeAndActiveTrue(String assetCode);

    Page<Asset> findAllByActiveTrue(Pageable pageable);

    @Query("SELECT a.status, COUNT(a) FROM Asset a WHERE a.active = true GROUP BY a.status")
    List<Object[]> countAssetsByStatus();

    @Query("SELECT d.departmentName, COUNT(a) FROM Asset a JOIN Department d ON a.departmentId = d.id WHERE a.active = true AND d.active = true GROUP BY d.departmentName")
    List<Object[]> countAssetsByDepartment();

    @Query("SELECT l.locationName, COUNT(a) FROM Asset a JOIN Location l ON a.locationId = l.id WHERE a.active = true AND l.active = true GROUP BY l.locationName")
    List<Object[]> countAssetsByLocation();
}
