package com.nisum.challenge.adapter.in.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nisum.challenge.adapter.in.mapper.UserRequestMapper;
import com.nisum.challenge.adapter.in.mapper.UserResponseMapper;
import com.nisum.challenge.domain.model.User;
import com.nisum.challenge.domain.service.UserService;
import com.nisum.challenge.dto.CreateUserRequest;
import com.nisum.challenge.dto.UserActiveStatusRequest;
import com.nisum.challenge.dto.UserActiveStatusResponse;
import com.nisum.challenge.dto.UserCreatedResponse;
import com.nisum.challenge.dto.UserDetailsResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final UserRequestMapper requestMapper;
	private final UserResponseMapper responseMapper;

	@PostMapping
	@Operation(summary = "Create a new user", description = "Registers a new user in the system")
	@ApiResponse(responseCode = "201", description = "User created")
	public ResponseEntity<UserCreatedResponse> registrarUsuario(@Valid @RequestBody CreateUserRequest request) {
		User user = requestMapper.toDomain(request);
		User registred = userService.registerUser(user);
		UserCreatedResponse response = responseMapper.toResponse(registred);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PatchMapping("/{id}/active")
	@Operation(summary = "Update user active status", description = "Sets a user as active or inactive")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<UserActiveStatusResponse> updateActiveStatus(@PathVariable("id") UUID id,
			@Valid @RequestBody UserActiveStatusRequest request) {

		User updatedUser = userService.updateActiveStatus(id, request.active());
		UserActiveStatusResponse response = new UserActiveStatusResponse(updatedUser.getId(), updatedUser.getEmail(),
				updatedUser.isActive());

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get user by ID", description = "Returns user details for the given ID")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<UserDetailsResponse> getUserById(@PathVariable("id") UUID id) {
		User user = userService.getById(id);
		UserDetailsResponse response = responseMapper.toUserDetailsResponse(user);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/list")
	@Operation(summary = "List users", description = "Returns a paginated list of users")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<Page<UserDetailsResponse>> getAllUsers(@Valid @RequestParam(defaultValue = "0") @Min(0) int page,
			@Valid @RequestParam(defaultValue = "10") @Min(1) int size) {

		Page<User> users = userService.getAllUsers(PageRequest.of(page, size));
		Page<UserDetailsResponse> response = users.map(responseMapper::toUserDetailsResponse);
		return ResponseEntity.ok(response);
	}

}
