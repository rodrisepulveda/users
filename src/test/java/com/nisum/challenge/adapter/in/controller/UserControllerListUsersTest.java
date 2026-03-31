package com.nisum.challenge.adapter.in.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import com.nisum.challenge.adapter.in.mapper.UserRequestMapper;
import com.nisum.challenge.adapter.in.mapper.UserResponseMapper;
import com.nisum.challenge.domain.model.Phone;
import com.nisum.challenge.domain.model.User;
import com.nisum.challenge.domain.service.UserService;
import com.nisum.challenge.dto.PhoneResponse;
import com.nisum.challenge.dto.UserDetailsResponse;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerListUsersTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRequestMapper requestMapper;

    @MockBean
    private UserResponseMapper responseMapper;

    @Test
    void getAllUsers_listaUsuariosRetornadaCorrectamente() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .name("Rodrigo")
                .email("rodri@correo.com")
                .created(LocalDateTime.now())
                .modified(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .isActive(true)
                .phones(List.of(Phone.builder().number("12345678").cityCode("9").countryCode("56").build()))
                .build();

        UserDetailsResponse response = new UserDetailsResponse(
                userId, "Rodrigo", user.getCreated(), user.getModified(), user.getLastLogin(), true,
                List.of(new PhoneResponse("12345678", "9", "56")));

        Page<User> userPage = new PageImpl<>(List.of(user));

        when(userService.getAllUsers(PageRequest.of(0, 10))).thenReturn(userPage);
        when(responseMapper.toUserDetailsResponse(user)).thenReturn(response);

        mockMvc.perform(get("/api/users/list?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(userId.toString()))
                .andExpect(jsonPath("$.content[0].name").value("Rodrigo"));
    }

    @Test
    void getAllUsers_sinUsuariosRetornaListaVacia() throws Exception {
        Page<User> emptyPage = new PageImpl<>(Collections.emptyList());
        when(userService.getAllUsers(PageRequest.of(0, 10))).thenReturn(emptyPage);

        mockMvc.perform(get("/api/users/list?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    void getAllUsers_conParametrosInvalidosRetorna400() throws Exception {
        mockMvc.perform(get("/api/users/list?page=-1&size=-5"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void getAllUsers_sinParametrosUsaValoresPorDefecto_retorna200() throws Exception {
        User user = User.builder()
                .id(UUID.randomUUID())
                .name("Rodrigo")
                .email("rodri@correo.com")
                .created(LocalDateTime.now())
                .modified(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .isActive(true)
                .phones(List.of(Phone.builder()
                        .number("12345678")
                        .cityCode("9")
                        .countryCode("56")
                        .build()))
                .build();

        UserDetailsResponse response = new UserDetailsResponse(
                user.getId(),
                user.getName(),
                user.getCreated(),
                user.getModified(),
                user.getLastLogin(),
                user.isActive(),
                List.of(new PhoneResponse("12345678", "9", "56"))
        );

        Page<User> page = new PageImpl<>(List.of(user));
        when(userService.getAllUsers(PageRequest.of(0, 10))).thenReturn(page);
        when(responseMapper.toUserDetailsResponse(user)).thenReturn(response);

        mockMvc.perform(get("/api/users/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(user.getId().toString()))
                .andExpect(jsonPath("$.content[0].name").value("Rodrigo"))
                .andExpect(jsonPath("$.content[0].phones[0].number").value("12345678"));
    }

}
