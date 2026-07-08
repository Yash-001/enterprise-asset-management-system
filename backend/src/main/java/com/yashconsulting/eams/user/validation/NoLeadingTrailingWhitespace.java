package com.yashconsulting.eams.user.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = NoLeadingTrailingWhitespaceValidator.class)
@Documented
public @interface NoLeadingTrailingWhitespace {

    String message() default "Must not contain leading or trailing spaces";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
