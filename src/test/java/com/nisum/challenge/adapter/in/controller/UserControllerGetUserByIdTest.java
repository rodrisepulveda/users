package com.nisum.challenge.adapter.in.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.nisum.challenge.adapter.in.mapper.UserRequestMapper;
import com.nisum.challenge.adapter.in.mapper.UserResponseMapper;
import com.nisum.challenge.domain.exception.NotFoundException;
import com.nisum.challenge.domain.model.User;
import com.nisum.challenge.domain.service.UserService;
import com.nisum.challenge.dto.UserDetailsResponse;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerGetUserByIdTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRequestMapper requestMapper;

    @MockBean
    private UserResponseMapper responseMapper;
    
    @Test
    void getUserById_usuarioNoExiste_retorna404() throws Exception {
        UUID id = UUID.randomUUID();
        when(userService.getById(id)).thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(get("/api/users/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void getUserById_idMalFormado_retorna400() throws Exception {
        String invalidId = "abc-invalid-uuid";

        mockMvc.perform(get("/api/users/" + invalidId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid user ID format"));
    }
    
    @Test
    void getUserById_usuarioExiste_retorna200ConDetalles() throws Exception {
        UUID id = UUID.randomUUID();
        User user = User.builder()
            .id(id)
            .name("Rodrigo")
            .email("rodri@correo.com")
            .isActive(true)
            .build();

        UserDetailsResponse response = new UserDetailsResponse(
            id, "Rodrigo", null, null, null, true, List.of()
        );

        when(userService.getById(id)).thenReturn(user);
        when(responseMapper.toUserDetailsResponse(user)).thenReturn(response);

        mockMvc.perform(get("/api/users/" + id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.name").value("Rodrigo"))
            .andExpect(jsonPath("$.active").value(true));
    }
    
}
