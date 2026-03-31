package com.nisum.challenge.adapter.in.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
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
import com.nisum.challenge.adapter.in.mapper.UserRequestMapper;
import com.nisum.challenge.adapter.in.mapper.UserResponseMapper;
import com.nisum.challenge.domain.exception.EmailAlreadyExistsException;
import com.nisum.challenge.domain.model.User;
import com.nisum.challenge.domain.service.UserService;
import com.nisum.challenge.dto.CreateUserRequest;
import com.nisum.challenge.dto.PhoneRequest;
import com.nisum.challenge.dto.UserCreatedResponse;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerUserRegisterTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@MockBean
	private UserRequestMapper requestMapper;

	@MockBean
	private UserResponseMapper responseMapper;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void registrarUsuario_conDatosValidos_retorna201ConUserCreatedResponse() throws Exception {

		CreateUserRequest request = new CreateUserRequest("Rodrigo", "rodri@correo.com", "Password123!",
				List.of(new PhoneRequest("12345678", "9", "56")));

		User domainUser = User.builder().id(UUID.randomUUID()).name("Rodrigo").email("rodri@correo.com")
				.password("Password123!").isActive(true).build();

		User persistedUser = User.builder().id(domainUser.getId()).name("Rodrigo").email("rodri@correo.com")
				.created(LocalDateTime.now()).modified(LocalDateTime.now()).token("token-generado").isActive(true)
				.build();

		UserCreatedResponse response = new UserCreatedResponse(persistedUser.getId(), persistedUser.getCreated(),
				persistedUser.getModified(), persistedUser.getLastLogin(), persistedUser.getToken(), true);

		Mockito.when(requestMapper.toDomain(any(CreateUserRequest.class))).thenReturn(domainUser);
		Mockito.when(userService.registerUser(any(User.class))).thenReturn(persistedUser);
		Mockito.when(responseMapper.toResponse(any(User.class))).thenReturn(response);

		// Act & Assert
		mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(response.id().toString()))
				.andExpect(jsonPath("$.token").value(response.token())).andExpect(jsonPath("$.isActive").value(true));
	}

	@Test
	void registrarUsuario_conDatosInvalidos_retorna400YMensajesDeError() throws Exception {
	    // Arrange
	    CreateUserRequest requestInvalido = new CreateUserRequest(
	            "", // name vacío
	            "correo_invalido.com", // email sin arroba
	            "password123", // sin mayúscula
	            null // phones
	    );

	    // Act & Assert
	    mockMvc.perform(post("/api/users")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(requestInvalido)))
	        .andExpect(status().isBadRequest())
	        .andExpect(jsonPath("$.name").value("El nombre es obligatorio"))
	        .andExpect(jsonPath("$.email").value("El correo no tiene un formato válido"))
	        .andExpect(jsonPath("$.password").value("La contraseña no cumple con el formato requerido"));
	}
	
    @Test
    void registrarUsuario_conNombreVacio_retorna400() throws Exception {
        CreateUserRequest request = new CreateUserRequest("", "rodri@correo.com", "Password123!",
                List.of(new PhoneRequest("12345678", "9", "56")));

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void registrarUsuario_conEmailInvalido_retorna400() throws Exception {
        CreateUserRequest request = new CreateUserRequest("Rodrigo", "correo-invalido", "Password123!",
                List.of(new PhoneRequest("12345678", "9", "56")));

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void registrarUsuario_conPasswordDebil_retorna400() throws Exception {
        CreateUserRequest request = new CreateUserRequest("Rodrigo", "rodri@correo.com", "123",
                List.of(new PhoneRequest("12345678", "9", "56")));

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void registrarUsuario_conListaTelefonosVacia_retorna400() throws Exception {
        CreateUserRequest request = new CreateUserRequest("Rodrigo", "rodri@correo.com", "Password123!", List.of());

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void registrarUsuario_conCamposFaltantes_retorna400() throws Exception {
        String payloadIncompleto = """
            {
              "email": "rodri@correo.com"
            }
            """;

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payloadIncompleto))
            .andExpect(status().isBadRequest());
    }
    
    @Test
    void registrarUsuario_emailDuplicado_retorna409Conflict() throws Exception {
        // Arrange
        CreateUserRequest request = new CreateUserRequest(
                "Rodrigo", 
                "rodri@correo.com", 
                "Password123!",
                List.of(new PhoneRequest("12345678", "9", "56"))
        );

        Mockito.when(requestMapper.toDomain(any(CreateUserRequest.class))).thenReturn(User.builder().build());
        Mockito.when(userService.registerUser(any(User.class)))
                .thenThrow(new EmailAlreadyExistsException("Email already registered"));

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already registered"));
    }
        
}
