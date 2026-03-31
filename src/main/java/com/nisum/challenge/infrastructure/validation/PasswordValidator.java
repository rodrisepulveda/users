package com.nisum.challenge.infrastructure.validation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

	@Value("${app.regex.password}")
	private String regex;

	@Override
	public boolean isValid(String password, ConstraintValidatorContext context) {
		return password != null && password.matches(regex);
	}
}
