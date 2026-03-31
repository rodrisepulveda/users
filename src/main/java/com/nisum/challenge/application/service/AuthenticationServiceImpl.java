package com.nisum.challenge.application.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nisum.challenge.domain.exception.AuthenticationException;
import com.nisum.challenge.domain.model.User;
import com.nisum.challenge.domain.repository.UserRepositoryPort;
import com.nisum.challenge.domain.service.AuthenticationService;
import com.nisum.challenge.domain.service.TokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

	private final UserRepositoryPort userRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenProvider tokenProvider;

	@Override
	public User authenticate(String email, String password) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new AuthenticationException("Invalid credentials"));

		if (!user.isActive()) {
			throw new AuthenticationException("User is inactive");
		}

		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new AuthenticationException("Invalid credentials");
		}

		String token = tokenProvider.generateToken(user.getId(), user.getEmail());
		user.setLastLogin(LocalDateTime.now());
		user.setToken(token);
		userRepository.save(user); // update lastLogin and token
		return user;
	}

}
