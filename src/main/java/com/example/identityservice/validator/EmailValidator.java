package com.example.identityservice.validator;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<EmailConstraint, String> {

	private Pattern emailPattern;

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (StringUtils.isBlank(value))
			return false;

		return emailPattern.matcher(value).matches();
	}

	@Override
	public void initialize(EmailConstraint constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
		this.emailPattern = EmailConstraint.EMAIL_PATTERN;
	}

}
