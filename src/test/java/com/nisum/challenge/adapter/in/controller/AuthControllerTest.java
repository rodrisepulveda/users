package com.nisum.challenge.adapter.in.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nisum.challenge.domain.exception.AuthenticationException;
import com.nisum.challenge.domain.model.User;
import com.nisum.challenge.domain.service.AuthenticationService;
import com.nisum.challenge.dto.LoginRequest;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AuthenticationService authenticationService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void login_withValidCredentials_shouldReturnToken() throws Exception {
		String email = "usuario@correo.com";
		String password = "Password123!";
		String token = "fake.jwt.token";

		LoginRequest request = new LoginRequest(email, password);

		User mockUser = User.builder().id(UUID.randomUUID()).name("Usuario").email(email)
				.password("$2a$10$hashedPassword").isActive(true).token(token).build();

		Mockito.when(authenticationService.authenticate(email, password)).thenReturn(mockUser);

		// Act & Assert
		mockMvc.perform(post("/api/users/login").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
				.andExpect(jsonPath("$.token").value(token));
	}

	@Test
	void login_withInvalidCredentials_shouldReturnUnauthorized() throws Exception {
		String email = "invalid@correo.com";
		String password = "wrongPassword";

		LoginRequest request = new LoginRequest(email, password);

		Mockito.when(authenticationService.authenticate(email, password))
				.thenThrow(new AuthenticationException("Invalid credentials"));

		// Act & Assert
		mockMvc.perform(post("/api/users/login").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.message").value("Invalid credentials"));
	}
	
	@Test
	void login_withInactiveUser_shouldReturnUnauthorized() throws Exception {
	    String email = "inactivo@correo.com";
	    String password = "Password123!";

	    LoginRequest request = new LoginRequest(email, password);

	    Mockito.when(authenticationService.authenticate(email, password))
	            .thenThrow(new AuthenticationException("User is inactive"));

	    // Act & Assert
	    mockMvc.perform(post("/api/users/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isUnauthorized())
	            .andExpect(jsonPath("$.message").value("User is inactive"));
	}
	
	@Test
	void login_withIncorrectPassword_shouldReturnUnauthorized() throws Exception {
	    String email = "usuario@correo.com";
	    String wrongPassword = "wrongPassword";
	    LoginRequest request = new LoginRequest(email, wrongPassword);

	    Mockito.when(authenticationService.authenticate(email, wrongPassword))
	           .thenThrow(new AuthenticationException("Invalid credentials"));

	    // Act & Assert
	    mockMvc.perform(post("/api/users/login")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(request)))
	            .andExpect(status().isUnauthorized())
	            .andExpect(jsonPath("$.message").value("Invalid credentials"));
	}

}
