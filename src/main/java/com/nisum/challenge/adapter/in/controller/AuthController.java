package com.nisum.challenge.adapter.in.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nisum.challenge.domain.model.User;
import com.nisum.challenge.domain.service.AuthenticationService;
import com.nisum.challenge.dto.LoginRequest;
import com.nisum.challenge.dto.LoginResponse;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthController {

	private final AuthenticationService authenticationService;

	@PostMapping("/login")
	@Operation(summary = "Authenticate user", description = "Authenticates and returns a JWT token")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
		User usuario = authenticationService.authenticate(request.getEmail(), request.getPassword());
		return ResponseEntity.ok(new LoginResponse(usuario.getToken()));
	}
}
