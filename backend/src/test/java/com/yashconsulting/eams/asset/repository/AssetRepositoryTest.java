package com.yashconsulting.eams.asset.repository;

import com.yashconsulting.eams.BaseIntegrationTest;
import com.yashconsulting.eams.asset.entity.Asset;
import com.yashconsulting.eams.asset.entity.AssetStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AssetRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private AssetRepository assetRepository;

    @BeforeEach
    void setUp() {
        assetRepository.deleteAll();
    }

    @Test
    void whenExistsByAssetCode_thenReturnTrue() {
        Asset asset = Asset.builder()
                .assetCode("AST-100")
                .assetName("Printer")
                .purchaseDate(LocalDate.now())
                .purchasePrice(BigDecimal.valueOf(300.00))
                .status(AssetStatus.AVAILABLE)
                .active(true)
                .build();
        assetRepository.save(asset);

        assertTrue(assetRepository.existsByAssetCode("AST-100"));
        assertFalse(assetRepository.existsByAssetCode("AST-999"));
    }

    @Test
    void whenExistsByAssetCodeAndIdNot_thenReturnTrue() {
        Asset asset1 = Asset.builder()
                .assetCode("AST-100")
                .assetName("Printer")
                .purchaseDate(LocalDate.now())
                .purchasePrice(BigDecimal.valueOf(300.00))
                .status(AssetStatus.AVAILABLE)
                .active(true)
                .build();
        Asset saved1 = assetRepository.save(asset1);

        Asset asset2 = Asset.builder()
                .assetCode("AST-200")
                .assetName("Scanner")
                .purchaseDate(LocalDate.now())
                .purchasePrice(BigDecimal.valueOf(200.00))
                .status(AssetStatus.AVAILABLE)
                .active(true)
                .build();
        Asset saved2 = assetRepository.save(asset2);

        assertTrue(assetRepository.existsByAssetCodeAndIdNot("AST-200", saved1.getId()));
        assertFalse(assetRepository.existsByAssetCodeAndIdNot("AST-100", saved1.getId()));
    }

    @Test
    void whenFindByAssetCodeAndActiveTrue_thenReturnAsset() {
        Asset assetActive = Asset.builder()
                .assetCode("AST-ACTIVE")
                .assetName("Active Asset")
                .purchaseDate(LocalDate.now())
                .purchasePrice(BigDecimal.valueOf(100.00))
                .status(AssetStatus.AVAILABLE)
                .active(true)
                .build();
        assetRepository.save(assetActive);

        Asset assetInactive = Asset.builder()
                .assetCode("AST-INACTIVE")
                .assetName("Inactive Asset")
                .purchaseDate(LocalDate.now())
                .purchasePrice(BigDecimal.valueOf(100.00))
                .status(AssetStatus.AVAILABLE)
                .active(false)
                .build();
        assetRepository.save(assetInactive);

        Optional<Asset> foundActive = assetRepository.findByAssetCodeAndActiveTrue("AST-ACTIVE");
        assertTrue(foundActive.isPresent());

        Optional<Asset> foundInactive = assetRepository.findByAssetCodeAndActiveTrue("AST-INACTIVE");
        assertFalse(foundInactive.isPresent());
    }

    @Test
    void whenFindAllByActiveTrue_thenReturnOnlyActive() {
        Asset asset1 = Asset.builder()
                .assetCode("AST-1")
                .assetName("Asset 1")
                .purchaseDate(LocalDate.now())
                .purchasePrice(BigDecimal.valueOf(100.00))
                .status(AssetStatus.AVAILABLE)
                .active(true)
                .build();
        assetRepository.save(asset1);

        Asset asset2 = Asset.builder()
                .assetCode("AST-2")
                .assetName("Asset 2")
                .purchaseDate(LocalDate.now())
                .purchasePrice(BigDecimal.valueOf(100.00))
                .status(AssetStatus.AVAILABLE)
                .active(false)
                .build();
        assetRepository.save(asset2);

        Page<Asset> activePage = assetRepository.findAllByActiveTrue(PageRequest.of(0, 10));
        assertEquals(1, activePage.getTotalElements());
        assertEquals("AST-1", activePage.getContent().get(0).getAssetCode());
    }
}
