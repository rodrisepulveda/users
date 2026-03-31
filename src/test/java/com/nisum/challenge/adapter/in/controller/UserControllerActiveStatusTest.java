package com.nisum.challenge.adapter.in.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
import com.nisum.challenge.adapter.in.mapper.UserRequestMapper;
import com.nisum.challenge.adapter.in.mapper.UserResponseMapper;
import com.nisum.challenge.domain.exception.NotFoundException;
import com.nisum.challenge.domain.model.User;
import com.nisum.challenge.domain.service.UserService;
import com.nisum.challenge.dto.UserActiveStatusRequest;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerActiveStatusTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRequestMapper requestMapper;

    @MockBean
    private UserResponseMapper responseMapper;
        
    @Test
    void updateActiveStatus_usuarioExistente_retorna200ConNuevoEstado() throws Exception {
        UUID userId = UUID.randomUUID();
        UserActiveStatusRequest request = new UserActiveStatusRequest(true);

        User updatedUser = User.builder()
                .id(userId)
                .email("user@example.com")
                .isActive(true)
                .build();

        Mockito.when(userService.updateActiveStatus(eq(userId), eq(true))).thenReturn(updatedUser);

        mockMvc.perform(patch("/api/users/{id}/active", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void updateActiveStatus_usuarioNoExiste_retorna404() throws Exception {
        UUID fakeId = UUID.randomUUID();
        UserActiveStatusRequest request = new UserActiveStatusRequest(true);

        when(userService.updateActiveStatus(any(UUID.class), any(Boolean.class)))
                .thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(patch("/api/users/" + fakeId + "/active")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateActiveStatus_idMalFormado_retorna400() throws Exception {
        String invalidId = "not-a-uuid";
        UserActiveStatusRequest request = new UserActiveStatusRequest(true);

        mockMvc.perform(patch("/api/users/" + invalidId + "/active")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateActiveStatus_errorInterno_retorna500() throws Exception {
        UUID id = UUID.randomUUID();
        UserActiveStatusRequest request = new UserActiveStatusRequest(true);

        when(userService.updateActiveStatus(any(UUID.class), any(Boolean.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(patch("/api/users/" + id + "/active")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }
    
    @Test
    void updateActiveStatus_conCampoActiveNulo_retorna400() throws Exception {
        // Campo active faltante en el JSON
        String body = "{}";
        UUID userId = UUID.randomUUID();

        mockMvc.perform(patch("/api/users/{id}/active", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
