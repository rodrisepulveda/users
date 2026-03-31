package com.nisum.challenge.application.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nisum.challenge.domain.exception.EmailAlreadyExistsException;
import com.nisum.challenge.domain.exception.NotFoundException;
import com.nisum.challenge.domain.model.User;
import com.nisum.challenge.domain.repository.UserRepositoryPort;
import com.nisum.challenge.domain.service.TokenProvider;
import com.nisum.challenge.domain.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepositoryPort userRepository;
	private final TokenProvider tokenProvider;
	private final PasswordEncoder passwordEncoder;

	@Override
	public User registerUser(User user) {
		if (userRepository.findByEmail(user.getEmail()).isPresent()) {
			throw new EmailAlreadyExistsException("The email is already registered.");
		}

		UUID id = UUID.randomUUID();
		LocalDateTime now = LocalDateTime.now();
		String token = tokenProvider.generateToken(id, user.getEmail());

		user.setId(id);
		user.setCreated(now);
		user.setModified(now);
		user.setLastLogin(now);
		user.setToken(token);
		user.setActive(true);

		user.setPassword(passwordEncoder.encode(user.getPassword()));

		return userRepository.save(user);
	}

	@Override
	public User updateActiveStatus(UUID id, boolean active) {
		User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
		user.setActive(active);
		return userRepository.save(user);
	}

	@Override
	public User getById(UUID id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("User with ID " + id + " not found"));
	}

	@Override
	public Page<User> getAllUsers(Pageable pageable) {
		return userRepository.findAll(pageable);
	}

}
