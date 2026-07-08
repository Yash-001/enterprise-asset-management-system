package com.yashconsulting.eams.asset.validation;

import com.yashconsulting.eams.asset.repository.AssetRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class UniqueAssetCodeValidator implements ConstraintValidator<UniqueAssetCode, String> {

    private final AssetRepository assetRepository;

    @Override
    public boolean isValid(String assetCode, ConstraintValidatorContext context) {
        if (assetCode == null || assetCode.isBlank()) {
            return true;
        }
        return !assetRepository.existsByAssetCode(assetCode.trim().toUpperCase(Locale.ROOT));
    }
}
