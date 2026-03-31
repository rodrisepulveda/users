package com.nisum.challenge;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

public class UserRegistrationApplicationTest {

	@Test
	void main_runsWithoutException() {
		assertDoesNotThrow(() -> UserRegistrationApplication.main(new String[] {}));
	}

}
