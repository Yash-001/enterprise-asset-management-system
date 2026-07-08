package com.yashconsulting.eams.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoLeadingTrailingWhitespaceValidator implements ConstraintValidator<NoLeadingTrailingWhitespace, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value.equals(value.trim());
    }
}
