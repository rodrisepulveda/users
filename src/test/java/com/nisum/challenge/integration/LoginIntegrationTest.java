package com.nisum.challenge.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nisum.challenge.dto.CreateUserRequest;
import com.nisum.challenge.dto.LoginRequest;
import com.nisum.challenge.dto.PhoneRequest;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LoginIntegrationTest{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void loginUsuario_existente_debeRetornarToken() throws Exception {
    	
    	 List<PhoneRequest> phones = new LinkedList<PhoneRequest>();
    	 PhoneRequest phoneRequest = new PhoneRequest("56874123", "9", "56");
    	 phones.add(phoneRequest);
    	
        // Paso 1: Registrar el usuario
        CreateUserRequest registro = new CreateUserRequest(
                "Rodrigo",
                "rodrigo@example.com",
                "Password123!",
                phones
        );

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registro)))
                .andExpect(status().isCreated());

        // Paso 2: Login con ese usuario
        LoginRequest login = new LoginRequest(
                "rodrigo@example.com",
                "Password123!"
        );

        String response = mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Paso 3: Validar que se haya retornado el token
        assertThat(response).contains("token");
    }
}
