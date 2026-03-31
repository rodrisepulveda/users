package com.nisum.challenge.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.nisum.challenge.domain.exception.AuthenticationException;
import com.nisum.challenge.domain.model.User;
import com.nisum.challenge.domain.repository.UserRepositoryPort;
import com.nisum.challenge.infrastructure.security.JwtUtil;

class AuthenticationServiceImplTest {

	private UserRepositoryPort userRepository;
	private PasswordEncoder passwordEncoder;
	private JwtUtil jwtUtil;
	private AuthenticationServiceImpl authenticationService;

	@BeforeEach
	void setup() {
		userRepository = mock(UserRepositoryPort.class);
		passwordEncoder = mock(PasswordEncoder.class);
		jwtUtil = mock(JwtUtil.class);
		authenticationService = new AuthenticationServiceImpl(userRepository, passwordEncoder, jwtUtil);
	}

	@Test
	void authenticate_validCredentials_returnsUserWithTokenAndLastLogin() {
		// Arrange
		String email = "test@example.com";
		String rawPassword = "password123";
		String encodedPassword = "encodedPass";
		String token = "fake-jwt-token";
		UUID userId = UUID.randomUUID();

		User user = User.builder()
			.id(userId)
			.email(email)
			.password(encodedPassword)
			.isActive(true)
			.build();

		when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
		when(jwtUtil.generateToken(userId, email)).thenReturn(token);
		when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

		// Act
		User result = authenticationService.authenticate(email, rawPassword);

		// Assert
		assertEquals(email, result.getEmail());
		assertEquals(token, result.getToken());
		assertNotNull(result.getLastLogin());
		verify(userRepository).save(result);
	}

	@Test
	void shouldThrowExceptionWhenUserNotFound() {
		String email = "nonexistent@example.com";
		String password = "anyPassword";
		when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

		AuthenticationException exception = assertThrows(AuthenticationException.class,
				() -> authenticationService.authenticate(email, password));

		assertEquals("Invalid credentials", exception.getMessage());
	}

	@Test
	void shouldThrowExceptionWhenUserIsInactive() {
		// Arrange
		String email = "user@example.com";
		String password = "validPassword";

		User user = User.builder()
			.id(UUID.randomUUID())
			.name("Test")
			.email(email)
			.password(password)
			.isActive(false)
			.build();

		when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

		// Act & Assert
		AuthenticationException exception = assertThrows(AuthenticationException.class,
				() -> authenticationService.authenticate(email, password));

		assertEquals("User is inactive", exception.getMessage());
	}

	@Test
	void shouldThrowExceptionWhenPasswordIsIncorrect() {
		// Arrange
		String email = "user@example.com";
		String inputPassword = "wrongPassword";
		String storedPassword = "encodedPassword";

		User user = User.builder()
			.id(UUID.randomUUID())
			.name("Test")
			.email(email)
			.password(storedPassword)
			.isActive(true)
			.build();

		when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(inputPassword, storedPassword)).thenReturn(false);

		// Act & Assert
		AuthenticationException exception = assertThrows(AuthenticationException.class,
				() -> authenticationService.authenticate(email, inputPassword));

		assertEquals("Invalid credentials", exception.getMessage());
	}
}
