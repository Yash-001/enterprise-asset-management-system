package com.yashconsulting.eams.asset.repository;

import com.yashconsulting.eams.asset.entity.Asset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long>, JpaSpecificationExecutor<Asset> {

    Optional<Asset> findByAssetCode(String assetCode);

    boolean existsByAssetCode(String assetCode);

    Optional<Asset> findByIdAndActiveTrue(Long id);

    boolean existsByAssetCodeAndIdNot(String assetCode, Long id);

    Optional<Asset> findByAssetCodeAndActiveTrue(String assetCode);

    Page<Asset> findAllByActiveTrue(Pageable pageable);
}
