package com.example.identityservice.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = { EmailValidator.class })
public @interface EmailConstraint {

	String message();

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

	Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

}
