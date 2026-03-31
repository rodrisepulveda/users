package com.nisum.challenge.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nisum.challenge.adapter.out.persistence.entity.UserEntity;
import com.nisum.challenge.adapter.out.persistence.jpa.JpaUserRepository;
import com.nisum.challenge.dto.CreateUserRequest;
import com.nisum.challenge.dto.PhoneRequest;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserIntegrationTest{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Test
    void registrarUsuario_debePersistirYRetornarToken() throws Exception {
   	 List<PhoneRequest> phones = new LinkedList<PhoneRequest>();
   	 PhoneRequest phoneRequest = new PhoneRequest("56874123", "9", "56");
   	 phones.add(phoneRequest);
    	
        // Arrange
        CreateUserRequest request = new CreateUserRequest("Rodrigo", "rodrigo@example.com", "Password123!", phones);

        // Act
        String response = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Assert
        UserEntity user = jpaUserRepository.findByEmail("rodrigo@example.com").orElse(null);

        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo("rodrigo@example.com");
        assertThat(user.getToken()).isNotBlank();
        assertThat(user.getId()).isInstanceOf(UUID.class);
    }
}
